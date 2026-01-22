package com.yowyob.template.infrastructure.adapters.inbound.rest;

import com.yowyob.template.application.service.ReactionService;
import com.yowyob.template.application.service.ReviewService;
import com.yowyob.template.domain.model.Reaction;
import com.yowyob.template.domain.model.ReactionType;
import com.yowyob.template.domain.model.Review;
import com.yowyob.template.domain.model.SubjectType;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.ReactionRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.ReviewRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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
    public Mono<Reaction> addReaction(@RequestBody ReactionRequest request) {
        return reactionService.addReaction(
                request.getActorId(),
                request.getTargetId(),
                request.getTargetType(),
                request.getType()
        );
    }

    @DeleteMapping("/reactions")
    @Operation(summary = "Supprimer une réaction", description = "Supprime une réaction existante.")
    public Mono<Void> removeReaction(@RequestParam UUID actorId,
                                     @RequestParam UUID targetId,
                                     @RequestParam ReactionType type) {
        return reactionService.removeReaction(actorId, targetId, type);
    }

    @GetMapping("/reactions")
    @Operation(summary = "Lister les réactions", description = "Récupère toutes les réactions associées à une cible donnée.")
    public Flux<Reaction> getReactions(@RequestParam UUID targetId,
                                       @RequestParam SubjectType targetType) {
        return reactionService.getReactionsForTarget(targetId, targetType);
    }

    // --- Reviews ---

    @PostMapping("/reviews")
    @Operation(summary = "Ajouter un avis", description = "Permet de laisser un avis (Note + Commentaire) sur un sujet.")
    public Mono<Review> addReview(@RequestBody ReviewRequest request) {
        Review review = Review.builder()
                .authorId(request.getAuthorId())
                .subjectId(request.getSubjectId())
                .subjectType(request.getSubjectType())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
        return reviewService.createReview(review);
    }

    @GetMapping("/reviews/{id}")
    @Operation(summary = "Obtenir un avis", description = "Récupère les détails d'un avis spécifique par son ID.")
    public Mono<Review> getReview(@PathVariable UUID id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping("/reviews")
    @Operation(summary = "Lister les avis par sujet", description = "Liste tous les avis pour un sujet donné (ex: un Chauffeur, un Produit).")
    public Flux<Review> getReviewsBySubject(@RequestParam UUID subjectId,
                                            @RequestParam SubjectType subjectType) {
        return reviewService.getReviewsBySubject(subjectId, subjectType);
    }

    @PutMapping("/reviews/{id}")
    @Operation(summary = "Mettre à jour un avis", description = "Modifie la note ou le commentaire d'un avis existant.")
    public Mono<Review> updateReview(@PathVariable UUID id, @RequestBody ReviewRequest request) {
        Review review = Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
        return reviewService.updateReview(id, review);
    }

    @DeleteMapping("/reviews/{id}")
    @Operation(summary = "Supprimer un avis", description = "Supprime définitivement un avis.")
    public Mono<Void> deleteReview(@PathVariable UUID id) {
        return reviewService.deleteReview(id);
    }
}
