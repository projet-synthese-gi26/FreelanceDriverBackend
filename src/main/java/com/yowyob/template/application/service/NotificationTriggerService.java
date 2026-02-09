package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.*;
import com.yowyob.template.domain.ports.out.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationTriggerService {

    private final NotificationPort notificationPort;
    private final UserDeviceRepositoryPort userDeviceRepository;

    private final ProductRepositoryPort productRepository;
    private final OrganisationRepositoryPort organisationRepository;
    private final BusinessActorRepositoryPort actorRepository;

    public Mono<Void> onProductUpdated(String productType,
                                      UUID ownerUserId,
                                      UUID previousReservedByOrgId,
                                      ProductStatus previousStatus,
                                      Product updated,
                                      String jwtToken) {

        UUID newReservedByUserId = updated.getReservedById();
        ProductStatus newStatus = updated.getStatus();

        ProductStatus expectedPendingStatus = "ANNONCE".equalsIgnoreCase(productType)
                ? ProductStatus.PendingConfirmation
                : ProductStatus.PendingDriverConfirmation;

        boolean reservedChanged = (previousReservedByOrgId == null && newReservedByUserId != null)
                || (previousReservedByOrgId != null && !previousReservedByOrgId.equals(newReservedByUserId));

        Mono<Void> notifyOnReservation = Mono.empty();
        boolean pendingTransition = newReservedByUserId != null
                && newStatus == expectedPendingStatus
                && previousStatus != expectedPendingStatus;

        if ((reservedChanged || pendingTransition)
                && newReservedByUserId != null
                && newStatus == expectedPendingStatus) {
            log.info("[NOTIF] reservation requested | productType={} | productId={} | ownerOrgId={} | reservedByUserId={} | newStatus={} | previousReservedById={} | previousStatus={}",
                    productType,
                    updated.getId(),
                    updated.getOrgId(),
                    newReservedByUserId,
                    newStatus,
                    previousReservedByOrgId,
                    previousStatus);
            // Une réservation / postulation vient d'être initiée (via PUT):
            // - PLANNING: client réserve => notifier le driver propriétaire
            // - ANNONCE: driver postule => notifier le client propriétaire
            notifyOnReservation = Mono.justOrEmpty(ownerUserId)
                    .doOnNext(id -> log.info("[NOTIF] ownerUserId provided: {}", id))
                    .switchIfEmpty(resolveOwnerUserId(updated.getOrgId(), jwtToken))
                    .doOnNext(targetOwnerUserId -> log.info("[NOTIF] target owner userId resolved: {}", targetOwnerUserId))
                    .switchIfEmpty(Mono.defer(() -> {
                        log.warn("[NOTIF] cannot resolve owner userId; skipping notification | productType={} | productId={} | ownerOrgId={}",
                                productType,
                                updated.getId(),
                                updated.getOrgId());
                        return Mono.empty();
                    }))
                    .flatMap(targetOwnerUserId -> buildReservationRequestedData(productType, updated, newReservedByUserId, jwtToken)
                            .flatMap(data -> notifyUserPullAndPush(
                                    targetOwnerUserId,
                                    productType + "_REQUESTED",
                                    "Nouvelle demande",
                                    "Une demande a été effectuée. Tu peux accepter, refuser ou voir les détails.",
                                    data
                            )))
                    .onErrorResume(e -> {
                        log.warn("[NOTIF] reservation notification failed: {}", e.getMessage());
                        return Mono.empty();
                    });
        }

        boolean statusChanged = previousStatus != null && newStatus != null && previousStatus != newStatus;
        if (!statusChanged) {
            return notifyOnReservation;
        }

        Mono<Void> notifyOnStatus = Mono.empty();
        if ("PLANNING".equalsIgnoreCase(productType)) {
            if (newStatus == ProductStatus.Confirmed) {
                // Le driver accepte (PUT) => notifier le client demandeur
                notifyOnStatus = resolveReservedByUserId(newReservedByUserId)
                        .flatMap(clientUserId -> buildReservationDecisionData(productType, updated, newReservedByUserId)
                                .flatMap(data -> notifyUserPullAndPush(
                                        clientUserId,
                                        productType + "_CONFIRMED",
                                        "Demande acceptée",
                                        "Ta demande a été acceptée. Tu peux voir les détails.",
                                        data
                                )));
            }
        } else if ("ANNONCE".equalsIgnoreCase(productType)) {
            if (newStatus == ProductStatus.Confirmed) {
                // Le client accepte (PUT) => notifier le driver demandeur
                notifyOnStatus = resolveReservedByUserId(newReservedByUserId)
                        .flatMap(driverUserId -> buildReservationDecisionData(productType, updated, newReservedByUserId)
                                .flatMap(data -> notifyUserPullAndPush(
                                        driverUserId,
                                        productType + "_CONFIRMED",
                                        "Demande acceptée",
                                        "Ta demande a été acceptée. Tu peux voir les détails.",
                                        data
                                )));
            } else if (previousStatus == ProductStatus.PendingConfirmation && newStatus == ProductStatus.Published) {
                // Le client refuse (PUT) => notifier le driver demandeur
                notifyOnStatus = resolveReservedByUserId(previousReservedByOrgId)
                        .flatMap(driverUserId -> buildReservationDecisionData(productType, updated, previousReservedByOrgId)
                                .flatMap(data -> notifyUserPullAndPush(
                                        driverUserId,
                                        productType + "_REJECTED",
                                        "Demande refusée",
                                        "Ta demande a été refusée.",
                                        data
                                )));
            }
        }

        return notifyOnReservation.then(notifyOnStatus);
    }

    public Mono<Void> onReviewCreated(Review review) {
        return resolveReviewTargetUserId(review)
                .flatMap(targetUserId -> notifyUserPullAndPush(
                        targetUserId,
                        "REVIEW_RECEIVED",
                        "Nouvel avis",
                        "Tu as reçu un nouvel avis. Tu peux consulter les détails.",
                        buildReviewData(review)
                ))
                .onErrorResume(e -> {
                    log.warn("Notification review skipped: {}", e.getMessage());
                    return Mono.empty();
                });
    }

    private Map<String, Object> buildReviewData(Review review) {
        Map<String, Object> data = new HashMap<>();
        data.put("reviewId", review.getId() != null ? review.getId().toString() : null);
        data.put("authorId", review.getAuthorId() != null ? review.getAuthorId().toString() : null);
        data.put("subjectId", review.getSubjectId() != null ? review.getSubjectId().toString() : null);
        data.put("subjectType", review.getSubjectType() != null ? review.getSubjectType().name() : null);
        data.put("rating", review.getRating());
        data.put("comment", review.getComment());
        return data;
    }

    private Mono<UUID> resolveReviewTargetUserId(Review review) {
        if (review.getSubjectType() == null || review.getSubjectId() == null) {
            return Mono.error(new IllegalArgumentException("review subject is required"));
        }

        return switch (review.getSubjectType()) {
            case PRODUCT -> productRepository.findById(review.getSubjectId())
                    .map(Product::getClientId);
            case DRIVER, CLIENT -> actorRepository.findById(review.getSubjectId())
                    .map(BusinessActor::getUserId);
            case ORGANISATION -> organisationRepository.findById(review.getSubjectId())
                    .flatMap(org -> actorRepository.findById(org.getActorId()))
                    .map(BusinessActor::getUserId);
            default -> Mono.error(new IllegalArgumentException("Unsupported subjectType: " + review.getSubjectType()));
        };
    }

    private Mono<Void> notifyUserPullAndPush(UUID userId,
                                            String type,
                                            String title,
                                            String body,
                                            Map<String, Object> data) {

        Mono<Void> pullMono = notificationPort
                .notify(userId, NotificationChannel.PULL, type, title, body, data)
                .then();

        // PUSH interne: publication temps réel SSE + (optionnel) futur FCM
        Mono<Void> pushMono = userDeviceRepository.findByUserId(userId)
                .map(UserDevice::getFcmToken)
                .filter(token -> token != null && !token.isBlank())
                .hasElements()
                .flatMap(hasDevice -> {
                    if (!hasDevice) {
                        return Mono.empty();
                    }
                    return notificationPort.push(userId, type, title, body, data);
                });

        return pullMono.then(pushMono);
    }

    private Mono<UUID> resolveOwnerUserId(UUID ownerOrgId, String jwtToken) {
        if (ownerOrgId == null) {
            return Mono.error(new IllegalArgumentException("ownerOrgId is required"));
        }

        // 1) Essaye via repositories locaux (pas de token)
        Mono<UUID> local = organisationRepository.findById(ownerOrgId)
                .flatMap(org -> actorRepository.findById(org.getActorId()))
                .map(BusinessActor::getUserId);

        // 2) Fallback via appels protégés (token)
        Mono<UUID> remote = organisationRepository.findById(ownerOrgId, jwtToken)
                .flatMap(org -> actorRepository.findById(org.getActorId(), jwtToken))
                .map(BusinessActor::getUserId);

        return local
                .switchIfEmpty(remote)
                .doOnError(e -> log.warn("[NOTIF] resolveOwnerUserId failed for ownerOrgId={}: {}", ownerOrgId, e.getMessage()));
    }

    private Mono<Map<String, Object>> buildReservationRequestedData(String productType,
                                                                    Product updated,
                                                                    UUID reservedByUserId,
                                                                    String jwtToken) {
        return actorRepository.findByUserId(reservedByUserId, jwtToken)
                .map(actor -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("action", "RESPOND");
                    data.put("productId", updated.getId() != null ? updated.getId().toString() : null);
                    data.put("productType", productType);
                    data.put("status", updated.getStatus() != null ? updated.getStatus().name() : null);
                    data.put("fromUserId", reservedByUserId.toString());
                    data.put("fromActorId", actor.getId() != null ? actor.getId().toString() : null);
                    data.put("detailsActorId", actor.getId() != null ? actor.getId().toString() : null);
                    return data;
                });
    }

    private Mono<Map<String, Object>> buildReservationDecisionData(String productType,
                                                                   Product updated,
                                                                   UUID reservedByUserId) {
        Map<String, Object> data = new HashMap<>();
        data.put("action", "VIEW");
        data.put("productId", updated.getId() != null ? updated.getId().toString() : null);
        data.put("productType", productType);
        data.put("status", updated.getStatus() != null ? updated.getStatus().name() : null);
        data.put("fromOrgId", updated.getOrgId() != null ? updated.getOrgId().toString() : null);
        data.put("reservedByUserId", reservedByUserId != null ? reservedByUserId.toString() : null);
        return Mono.just(data);
    }

    private Mono<UUID> resolveReservedByUserId(UUID reservedByUserId) {
        if (reservedByUserId == null) {
            return Mono.error(new IllegalArgumentException("reservedByUserId is required"));
        }
        return Mono.just(reservedByUserId);
    }
}
