package com.yowyob.template.infrastructure.adapters.inbound.rest;

import com.yowyob.template.application.service.ReactionService;
import com.yowyob.template.application.service.ReviewService;
import com.yowyob.template.domain.model.Reaction;
import com.yowyob.template.domain.model.ReactionType;
import com.yowyob.template.domain.model.Review;
import com.yowyob.template.domain.model.ReviewType;
import com.yowyob.template.domain.model.SubjectType;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.ReactionRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.ReviewRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.UpdateReviewRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Social", description = "Interactions sociales (Réactions et Avis) entre utilisateurs et objets (Produits, Drivers, etc.)")
public class SocialController {
    private final ReactionService reactionService;
    private final ReviewService reviewService;

    // --- Reactions ---

    @PostMapping("/reactions")
    @Operation(summary = "Ajouter une réaction", description = "Ajoute une réaction (Like, Love, etc.) à une cible donnée (Produit, Driver, etc.).")
    public Mono<Reaction> addReaction(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Valid @RequestBody ReactionRequest request
    ) {
        UUID actorId = UUID.fromString(principal.getAttribute("sub"));
        return validateReactionRequest(request)
                .then(reactionService.addReaction(
                        actorId,
                        request.getTargetId(),
                        request.getTargetType(),
                        request.getType(),
                        authorization
                ));
    }

    @DeleteMapping("/reactions")
    @Operation(summary = "Supprimer une réaction", description = "Supprime une réaction existante.")
    public Mono<Void> removeReaction(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestParam UUID targetId,
            @RequestParam ReactionType type
    ) {
        UUID actorId = UUID.fromString(principal.getAttribute("sub"));
        return reactionService.removeReaction(actorId, targetId, type, authorization);
    }

    @GetMapping("/reactions")
    @Operation(summary = "Lister les réactions", description = "Récupère toutes les réactions associées à une cible donnée.")
    public Flux<Reaction> getReactions(@RequestParam UUID targetId,
                                       @Parameter(schema = @Schema(implementation = SubjectType.class))
                                       @RequestParam SubjectType targetType) {
        return reactionService.getReactionsForTarget(targetId, targetType);
    }

    @GetMapping("/reactions/{id}")
    @Operation(summary = "Obtenir une réaction", description = "Récupère les détails d'une réaction par son ID.")
    public Mono<Reaction> getReactionById(@PathVariable UUID id) {
        return reactionService.getReactionById(id);
    }

    @GetMapping("/reactions/user/{userId}")
    @Operation(summary = "Lister les réactions par utilisateur", description = "Récupère toutes les réactions faites par un utilisateur (actorId).")
    public Flux<Reaction> getReactionsByUser(@PathVariable UUID userId) {
        return reactionService.getReactionsByActor(userId);
    }

    // --- Reviews ---

    @PostMapping("/reviews")
    @Operation(summary = "Ajouter un avis", description = "Permet de laisser un avis (Note + Commentaire) sur un sujet.")
    public Mono<Review> addReview(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            @Valid @RequestBody ReviewRequest request
    ) {
        UUID authorId = UUID.fromString(principal.getAttribute("sub"));
        return validateCreateReviewRequest(request)
                .then(Mono.defer(() -> {
                    Review review = Review.builder()
                            .authorId(authorId)
                            .subjectId(request.getSubjectId())
                            .subjectType(request.getSubjectType())
                            .reviewType(request.getReviewType() != null ? request.getReviewType() : ReviewType.RATING)
                            .rating(request.getRating())
                            .comment(request.getComment())
                            .reportReason(request.getReportReason())
                            .build();
                    return reviewService.createReview(review);
                }));
    }

    @GetMapping("/reviews/{id}")
    @Operation(summary = "Obtenir un avis", description = "Récupère les détails d'un avis spécifique par son ID.")
    public Mono<Review> getReview(@PathVariable UUID id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping("/reviews")
    @Operation(summary = "Lister les avis par sujet", description = "Liste tous les avis pour un sujet donné (ex: un Chauffeur, un Produit).")
    public Flux<Review> getReviewsBySubject(@RequestParam UUID subjectId,
                                            @Parameter(schema = @Schema(implementation = SubjectType.class))
                                            @RequestParam SubjectType subjectType) {
        return reviewService.getReviewsBySubject(subjectId, subjectType);
    }

    @GetMapping("/reviews/user/{userId}")
    @Operation(summary = "Lister les avis par utilisateur", description = "Récupère tous les avis écrits par un utilisateur (authorId).")
    public Flux<Review> getReviewsByUser(@PathVariable UUID userId) {
        return reviewService.getReviewsByAuthor(userId);
    }

    @PutMapping("/reviews/{id}")
    @Operation(summary = "Mettre à jour un avis", description = "Modifie la note ou le commentaire d'un avis existant.")
    public Mono<Review> updateReview(@PathVariable UUID id, @Valid @RequestBody UpdateReviewRequest request) {
        return validateUpdateReviewRequest(request)
                .then(Mono.defer(() -> {
                    Review review = Review.builder()
                            .reviewType(request.getReviewType())
                            .rating(request.getRating())
                            .comment(request.getComment())
                            .reportReason(request.getReportReason())
                            .build();
                    return reviewService.updateReview(id, review);
                }));
    }

    private Mono<Void> validateReactionRequest(ReactionRequest request) {
        if (request.getTargetId() == null) {
            return Mono.error(new IllegalArgumentException("targetId est obligatoire"));
        }
        if (request.getTargetType() == null) {
            return Mono.error(new IllegalArgumentException("targetType est obligatoire"));
        }
        if (request.getType() == null) {
            return Mono.error(new IllegalArgumentException("type est obligatoire"));
        }
        return Mono.empty();
    }

    private Mono<Void> validateCreateReviewRequest(ReviewRequest request) {
        if (request.getSubjectId() == null) {
            return Mono.error(new IllegalArgumentException("subjectId est obligatoire"));
        }
        if (request.getSubjectType() == null) {
            return Mono.error(new IllegalArgumentException("subjectType est obligatoire"));
        }
        ReviewType reviewType = request.getReviewType() != null ? request.getReviewType() : ReviewType.RATING;
        if (reviewType == ReviewType.REPORT) {
            if (request.getReportReason() == null || request.getReportReason().isBlank()) {
                return Mono.error(new IllegalArgumentException("reportReason est obligatoire pour un signalement"));
            }
            if (request.getSubjectType() != SubjectType.DRIVER && request.getSubjectType() != SubjectType.CLIENT) {
                return Mono.error(new IllegalArgumentException("Le signalement ne supporte que DRIVER ou CLIENT"));
            }
            return Mono.empty();
        }
        if (request.getRating() == null) {
            return Mono.error(new IllegalArgumentException("rating est obligatoire"));
        }
        if (request.getRating() < 1 || request.getRating() > 5) {
            return Mono.error(new IllegalArgumentException("rating doit être entre 1 et 5"));
        }
        return Mono.empty();
    }

    private Mono<Void> validateUpdateReviewRequest(UpdateReviewRequest request) {
        if (request.getReviewType() == ReviewType.REPORT) {
            if (request.getReportReason() == null || request.getReportReason().isBlank()) {
                return Mono.error(new IllegalArgumentException("reportReason est obligatoire pour un signalement"));
            }
            return Mono.empty();
        }
        if (request.getRating() == null && (request.getComment() == null || request.getComment().isBlank())) {
            return Mono.error(new IllegalArgumentException("rating ou comment est obligatoire"));
        }
        if (request.getRating() != null && (request.getRating() < 1 || request.getRating() > 5)) {
            return Mono.error(new IllegalArgumentException("rating doit être entre 1 et 5"));
        }
        return Mono.empty();
    }

    @DeleteMapping("/reviews/{id}")
    @Operation(summary = "Supprimer un avis", description = "Supprime définitivement un avis.")
    public Mono<Void> deleteReview(@PathVariable UUID id) {
        return reviewService.deleteReview(id);
    }
}
