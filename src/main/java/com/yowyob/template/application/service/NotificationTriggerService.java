package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.*;
import com.yowyob.template.domain.ports.out.*;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.notification.NotificationCreatePullRequest;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.notification.NotificationSendRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
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

    @Value("${application.notification.templates.pull.planning-requested:}")
    private String tplPullPlanningRequested;

    @Value("${application.notification.templates.push.planning-requested:}")
    private String tplPushPlanningRequested;

    @Value("${application.notification.templates.pull.planning-accepted:}")
    private String tplPullPlanningAccepted;

    @Value("${application.notification.templates.push.planning-accepted:}")
    private String tplPushPlanningAccepted;

    @Value("${application.notification.templates.pull.planning-confirmed:}")
    private String tplPullPlanningConfirmed;

    @Value("${application.notification.templates.push.planning-confirmed:}")
    private String tplPushPlanningConfirmed;

    @Value("${application.notification.templates.pull.annonce-accepted:}")
    private String tplPullAnnonceAccepted;

    @Value("${application.notification.templates.push.annonce-accepted:}")
    private String tplPushAnnonceAccepted;

    @Value("${application.notification.templates.pull.annonce-confirmed:}")
    private String tplPullAnnonceConfirmed;

    @Value("${application.notification.templates.push.annonce-confirmed:}")
    private String tplPushAnnonceConfirmed;

    @Value("${application.notification.templates.pull.review-received:}")
    private String tplPullReviewReceived;

    @Value("${application.notification.templates.push.review-received:}")
    private String tplPushReviewReceived;

    public Mono<Void> onProductUpdated(String productType,
                                      UUID ownerUserId,
                                      UUID previousReservedByOrgId,
                                      ProductStatus previousStatus,
                                      Product updated,
                                      String jwtToken) {

        UUID newReservedByOrgId = updated.getReservedById();
        ProductStatus newStatus = updated.getStatus();

        boolean reservedChanged = (previousReservedByOrgId == null && newReservedByOrgId != null)
                || (previousReservedByOrgId != null && !previousReservedByOrgId.equals(newReservedByOrgId));

        Mono<Void> notifyOnReservation = Mono.empty();
        if (reservedChanged && newReservedByOrgId != null && newStatus == ProductStatus.PendingConfirmation) {
            if ("PLANNING".equalsIgnoreCase(productType)) {
                // Client postule au planning du driver => notifier le driver (ownerUserId)
                notifyOnReservation = notifyUserPullAndPush(
                        ownerUserId,
                        tplPullPlanningRequested,
                        tplPushPlanningRequested,
                        Map.of(
                                "productId", updated.getId().toString(),
                                "productType", productType,
                                "status", newStatus != null ? newStatus.name() : null
                        )
                );
            } else if ("ANNONCE".equalsIgnoreCase(productType)) {
                // Driver accepte une annonce (ou est sélectionné) => notifier le client (ownerUserId)
                notifyOnReservation = notifyUserPullAndPush(
                        ownerUserId,
                        tplPullAnnonceAccepted,
                        tplPushAnnonceAccepted,
                        Map.of(
                                "productId", updated.getId().toString(),
                                "productType", productType,
                                "status", newStatus != null ? newStatus.name() : null
                        )
                );
            }
        }

        boolean statusChanged = previousStatus != null && newStatus != null && previousStatus != newStatus;
        if (!statusChanged) {
            return notifyOnReservation;
        }

        Mono<Void> notifyOnStatus = Mono.empty();
        if ("PLANNING".equalsIgnoreCase(productType)) {
            if (newStatus == ProductStatus.PendingDriverConfirmation) {
                // Driver accepte la postulation => notifier le client (reservedByOrgId -> userId)
                notifyOnStatus = resolveReservedByUserId(newReservedByOrgId)
                        .flatMap(clientUserId -> notifyUserPullAndPush(
                                clientUserId,
                                tplPullPlanningAccepted,
                                tplPushPlanningAccepted,
                                Map.of(
                                        "productId", updated.getId().toString(),
                                        "productType", productType,
                                        "status", newStatus.name()
                                )
                        ));
            } else if (newStatus == ProductStatus.Confirmed) {
                // Client confirme => notifier le driver + le client
                Mono<Void> n1 = notifyUserPullAndPush(
                        ownerUserId,
                        tplPullPlanningConfirmed,
                        tplPushPlanningConfirmed,
                        Map.of(
                                "productId", updated.getId().toString(),
                                "productType", productType,
                                "status", newStatus.name()
                        )
                );
                Mono<Void> n2 = resolveReservedByUserId(newReservedByOrgId)
                        .flatMap(clientUserId -> notifyUserPullAndPush(
                                clientUserId,
                                tplPullPlanningConfirmed,
                                tplPushPlanningConfirmed,
                                Map.of(
                                        "productId", updated.getId().toString(),
                                        "productType", productType,
                                        "status", newStatus.name()
                                )
                        ));
                notifyOnStatus = n1.then(n2);
            }
        } else if ("ANNONCE".equalsIgnoreCase(productType)) {
            if (newStatus == ProductStatus.PendingDriverConfirmation) {
                // Client confirme la postulation => notifier le driver (reservedByOrgId -> userId)
                notifyOnStatus = resolveReservedByUserId(newReservedByOrgId)
                        .flatMap(driverUserId -> notifyUserPullAndPush(
                                driverUserId,
                                tplPullAnnonceConfirmed,
                                tplPushAnnonceConfirmed,
                                Map.of(
                                        "productId", updated.getId().toString(),
                                        "productType", productType,
                                        "status", newStatus.name()
                                )
                        ));
            } else if (newStatus == ProductStatus.Confirmed) {
                // Driver confirme => notifier le client + le driver
                Mono<Void> n1 = notifyUserPullAndPush(
                        ownerUserId,
                        tplPullAnnonceConfirmed,
                        tplPushAnnonceConfirmed,
                        Map.of(
                                "productId", updated.getId().toString(),
                                "productType", productType,
                                "status", newStatus.name()
                        )
                );
                Mono<Void> n2 = resolveReservedByUserId(newReservedByOrgId)
                        .flatMap(driverUserId -> notifyUserPullAndPush(
                                driverUserId,
                                tplPullAnnonceConfirmed,
                                tplPushAnnonceConfirmed,
                                Map.of(
                                        "productId", updated.getId().toString(),
                                        "productType", productType,
                                        "status", newStatus.name()
                                )
                        ));
                notifyOnStatus = n1.then(n2);
            }
        }

        return notifyOnReservation.then(notifyOnStatus);
    }

    public Mono<Void> onReviewCreated(Review review) {
        return resolveReviewTargetUserId(review)
                .flatMap(targetUserId -> notifyUserPullAndPush(
                        targetUserId,
                        tplPullReviewReceived,
                        tplPushReviewReceived,
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
                                            String pullTemplateId,
                                            String pushTemplateId,
                                            Map<String, Object> data) {

        Mono<Void> pullMono = Mono.empty();
        UUID pullTpl = parseUuidOrNull(pullTemplateId);
        if (pullTpl != null) {
            pullMono = notificationPort.createPull(new NotificationCreatePullRequest(
                    "PULL",
                    pullTpl,
                    userId,
                    data
            )).then();
        }

        Mono<Void> pushMono = Mono.empty();
        UUID pushTpl = parseUuidOrNull(pushTemplateId);
        if (pushTpl != null) {
            pushMono = userDeviceRepository.findByUserId(userId)
                    .map(UserDevice::getFcmToken)
                    .filter(token -> token != null && !token.isBlank())
                    .collectList()
                    .flatMap(tokens -> {
                        if (tokens.isEmpty()) {
                            return Mono.empty();
                        }
                        return notificationPort.send(new NotificationSendRequest(
                                "PUSH",
                                pushTpl,
                                tokens,
                                data
                        ));
                    });
        }

        return pullMono.then(pushMono);
    }

    private UUID parseUuidOrNull(String value) {
        if (value == null) {
            return null;
        }
        String v = value.trim();
        if (v.isEmpty() || "0".equals(v)) {
            return null;
        }
        try {
            return UUID.fromString(v);
        } catch (IllegalArgumentException ex) {
            log.warn("Invalid template UUID: {}", v);
            return null;
        }
    }

    private Mono<UUID> resolveReservedByUserId(UUID reservedByOrgId) {
        if (reservedByOrgId == null) {
            return Mono.error(new IllegalArgumentException("reservedByOrgId is required"));
        }
        return organisationRepository.findById(reservedByOrgId)
                .flatMap(org -> actorRepository.findById(org.getActorId()))
                .map(BusinessActor::getUserId);
    }
}
