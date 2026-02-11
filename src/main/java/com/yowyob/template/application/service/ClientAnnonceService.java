package com.yowyob.template.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yowyob.template.domain.model.Annonce;
import com.yowyob.template.domain.model.ClientRole;
import com.yowyob.template.domain.model.Product;
import com.yowyob.template.domain.model.ProductStatus;
import com.yowyob.template.domain.ports.in.PaymentUseCase;
import com.yowyob.template.domain.ports.out.BusinessActorRepositoryPort;
import com.yowyob.template.domain.ports.out.OrganisationRepositoryPort;
import com.yowyob.template.domain.ports.out.ProductRepositoryPort;
import com.yowyob.template.domain.ports.out.UserRepositoryPort;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.request.CreateAnnonceRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.request.UpdateAnnonceRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientAnnonceService {

    private final BusinessActorRepositoryPort actorRepository;
    private final OrganisationRepositoryPort organisationRepository;
    private final ProductRepositoryPort productRepository;
    private final NotificationTriggerService notificationTriggerService;

    // ========== Dépendances supplémentaires ==========
    private final PaymentUseCase paymentUseCase;
    private final ObjectMapper objectMapper;
    private final UserRepositoryPort userRepository;
    private final NotificationService notificationService;
    private static final String LOG_PREFIX = "[SERV-ANNONCE]";
    // ==================================================

    private static final String ANNONCE_TYPE = "ANNONCE";

    // ─────────────────────────────────────────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────────────────────────────────────────

    public Mono<Product> createClientAnnonce(UUID authUserId, CreateAnnonceRequest request, String token) {

        return actorRepository.findByUserId(authUserId, token)
                .switchIfEmpty(Mono.error(new AccessDeniedException("No Business Actor found for this user")))
                .flatMap(actor -> {

                    if (!(actor instanceof ClientRole)) {
                        return Mono.error(new AccessDeniedException("User is not a Client"));
                    }

                    return organisationRepository.findByActorId(actor.getId(), token)
                            .switchIfEmpty(Mono.error(new RuntimeException("Client has no Organisation")))
                            .flatMap(organisation -> {

                                Annonce annonce = Annonce.builder()
                                        .orgId(organisation.getId())
                                        .clientId(authUserId)
                                        .clientName(actor.getDisplayName())
                                        .clientPhoneNumber(actor.getPhoneNumber())
                                        .profileImageUrl(actor.getAvatarUrl())
                                        .title(request.title())
                                        .departureLocation(request.departureLocation())
                                        .dropoffLocation(request.dropoffLocation())
                                        .startDate(request.startDate())
                                        .startTime(request.startTime())
                                        .endDate(request.endDate())
                                        .endTime(request.endTime())
                                        .cost(request.cost())
                                        .baggageInfo(request.baggageInfo())
                                        .status(ProductStatus.Draft)
                                        .tripType(request.tripType())
                                        .meetupPoint(request.meetupPoint())
                                        .tripIntention(request.tripIntention())
                                        .pricingMethod(request.pricingMethod())
                                        .isNegotiable(request.isNegotiable() != null && request.isNegotiable())
                                        .paymentMethod(request.paymentMethod())
                                        .createdAt(Timestamp.from(Instant.now()))
                                        .build();

                                return productRepository.save(annonce);
                            });
                });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LIST
    // ─────────────────────────────────────────────────────────────────────────

    public Flux<Product> listClientAnnonces(UUID authUserId, String token) {
        return assertClient(authUserId, token)
                .thenMany(productRepository.findByProductTypeAndClientId(ANNONCE_TYPE, authUserId));
    }

    public Flux<Product> listClientAnnoncesByClientId(UUID clientId) {
        return productRepository.findByProductTypeAndClientId(ANNONCE_TYPE, clientId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET
    // ─────────────────────────────────────────────────────────────────────────

    public Mono<Product> getClientAnnonce(UUID authUserId, UUID annonceId, String token) {
        return assertClient(authUserId, token)
                .then(productRepository.findByIdAndProductType(annonceId, ANNONCE_TYPE))
                .switchIfEmpty(Mono.error(new AccessDeniedException("Annonce not found")))
                .flatMap(product -> {
                    if (product.getClientId() == null || !product.getClientId().equals(authUserId)) {
                        return Mono.error(new AccessDeniedException("Access denied"));
                    }
                    return Mono.just(product);
                });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UPDATE
    // ─────────────────────────────────────────────────────────────────────────

    public Mono<Product> updateClientAnnonce(UUID authUserId, UUID annonceId, UpdateAnnonceRequest request, String token) {

        String fid = "UPDT-ANN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("{} ╔══════════════════════════════════════════════════════════════════════════", LOG_PREFIX);
        log.info("{} ║ [{}] START UPDATE PROCESS (ANNONCE)", LOG_PREFIX, fid);

        return productRepository.findByIdAndProductType(annonceId, ANNONCE_TYPE)
                .switchIfEmpty(Mono.error(new AccessDeniedException("Annonce not found")))
                .flatMap(existing -> {

                    final UUID previousReservedById = existing.getReservedById();
                    final ProductStatus previousStatus = existing.getStatus();
                    final boolean isOwner = existing.getClientId() != null && existing.getClientId().equals(authUserId);

                    if (!(existing instanceof Annonce annonce)) {
                        return Mono.error(new IllegalStateException("Product is not an Annonce"));
                    }

                    final ProductStatus oldStatus = annonce.getStatus();
                    final ProductStatus newStatus = request.status() != null
                            ? ProductStatus.valueOf(request.status())
                            : oldStatus;

                    // ── Vérification d'autorisation ───────────────────────────────────────
                    final Mono<Void> authorizationCheck;
                    if (isOwner) {
                        authorizationCheck = Mono.empty();
                    } else {
                        boolean modifiesForbiddenField = request.title() != null
                                || request.departureLocation() != null
                                || request.dropoffLocation() != null
                                || request.startDate() != null
                                || request.startTime() != null
                                || request.endDate() != null
                                || request.endTime() != null
                                || request.tripType() != null
                                || request.meetupPoint() != null
                                || request.tripIntention() != null
                                || request.pricingMethod() != null
                                || request.isNegotiable() != null
                                || request.paymentMethod() != null
                                || request.cost() != null
                                || request.baggageInfo() != null;

                        if (modifiesForbiddenField) {
                            return Mono.error(new AccessDeniedException("Only status/reservedById can be updated by non-owner"));
                        }

                        final UUID reservedByUserId = existing.getReservedById();

                        if (reservedByUserId == null) {
                            if (request.reservedById() == null) {
                                return Mono.error(new AccessDeniedException("Access denied"));
                            }
                            authorizationCheck = Mono.defer(() -> {
                                log.info("[ANNONCE_BOOKING] initial booking check | userId={} | requestedReservedById={}",
                                        authUserId, request.reservedById());
                                if (!authUserId.equals(request.reservedById())) {
                                    return Mono.error(new AccessDeniedException(
                                            "Access denied: reservedById must match authenticated userId. authUserId="
                                                    + authUserId + ", requestedReservedById=" + request.reservedById()));
                                }
                                return Mono.empty();
                            });
                        } else {
                            authorizationCheck = Mono.defer(() -> {
                                log.info("[ANNONCE_BOOKING] follow-up booking check | userId={} | reservedByUserId={}",
                                        authUserId, reservedByUserId);
                                if (!authUserId.equals(reservedByUserId)) {
                                    return Mono.error(new AccessDeniedException(
                                            "Access denied: only reserved user can update. authUserId="
                                                    + authUserId + ", reservedByUserId=" + reservedByUserId));
                                }
                                return Mono.empty();
                            });
                        }
                    }

                    // ── Résolution du chauffeur (cachée pour réutilisation) ───────────────
                    final UUID effectiveDriverId = (request.reservedById() != null) ? request.reservedById() : annonce.getReservedById();

                    final Mono<com.yowyob.template.domain.model.User> driverResolver =
                            (effectiveDriverId == null)
                                    ? Mono.empty()
                                    : userRepository.findById(effectiveDriverId, token) // Appel direct au service Auth
                                            .cache();

                    // ── Actions conditionnelles — assignation UNIQUE (effectively final) ──
                    //
                    // FIX: l'opérateur ternaire remplace l'ancien pattern "init + if (réassignation)"
                    // qui rendait les variables non effectively final et empêchait leur capture
                    // dans les lambdas imbriquées (erreur Java 536871575).

                    final Mono<Void> terminatedAction =
                            (newStatus == ProductStatus.Terminated && oldStatus != ProductStatus.Terminated)
                                    ? driverResolver
                                    .switchIfEmpty(Mono.error(new IllegalStateException("Cannot terminate: No driver linked.")))
                                    .flatMap(driver -> {
                                        BigDecimal amount = safeParseAmount(annonce.getCost(), fid);
                                        return paymentUseCase.processRidePayment(driver.getId(), amount)
                                                .then(notificationService.sendCommissionDeductedAlert(
                                                        driver.getId(),
                                                        driver.getFirstName(),
                                                        driver.getPhone(),
                                                        amount.multiply(new BigDecimal("0.1")).toString(),
                                                        annonce.getTitle(),           // <--- PASSAGE DU VRAI TITRE
                                                        annonce.getDropoffLocation()  // <--- PASSAGE DE LA VRAIE DESTINATION
                                                ));
                                    })
                                    : Mono.empty();

                    final Mono<Void> confirmedAction =
                            (newStatus == ProductStatus.Confirmed && oldStatus != ProductStatus.Confirmed)
                                    ? driverResolver.flatMap(driver ->
                                            notificationService.sendRideConfirmedAlert(
                                                    driver.getId(),
                                                    driver.getFirstName(),
                                                    driver.getEmail(),
                                                    driver.getPhone(),
                                                    annonce.getTitle(),
                                                    annonce.getDropoffLocation()
                                            ))
                                    : Mono.empty();

                    // ── Application des modifications puis sauvegarde ─────────────────────
                    return authorizationCheck.then(Mono.defer(() -> {

                        if (request.title() != null) annonce.setTitle(request.title());
                        if (request.departureLocation() != null) annonce.setDepartureLocation(request.departureLocation());
                        if (request.dropoffLocation() != null) annonce.setDropoffLocation(request.dropoffLocation());
                        if (request.startDate() != null) annonce.setStartDate(request.startDate());
                        if (request.startTime() != null) annonce.setStartTime(request.startTime());
                        if (request.endDate() != null) annonce.setEndDate(request.endDate());
                        if (request.endTime() != null) annonce.setEndTime(request.endTime());

                        if (request.status() != null) {
                            try {
                                annonce.setStatus(ProductStatus.valueOf(request.status()));
                            } catch (IllegalArgumentException ex) {
                                return Mono.error(new IllegalArgumentException("Invalid status: " + request.status()));
                            }
                        }

                        if (request.reservedById() != null) {
                            annonce.setReservedById(request.reservedById());
                            if (!isOwner) {
                                log.info("[ANNONCE_BOOKING] booking update | annonceId={} | authUserId={} | ownerClientId={} | ownerOrgId={} | requestedReservedById={} | previousReservedById={} | previousStatus={} | currentStatus(before)={}",
                                        existing.getId(),
                                        authUserId,
                                        existing.getClientId(),
                                        existing.getOrgId(),
                                        request.reservedById(),
                                        previousReservedById,
                                        previousStatus,
                                        annonce.getStatus());

                                if (annonce.getStatus() == ProductStatus.Published
                                        || annonce.getStatus() == ProductStatus.Draft
                                        || annonce.getStatus() == ProductStatus.PendingConfirmation) {
                                    annonce.setStatus(ProductStatus.PendingConfirmation);
                                }

                                log.info("[ANNONCE_BOOKING] booking update applied | annonceId={} | ownerClientId={} | reservedById(after)={} | status(after)={}",
                                        existing.getId(),
                                        existing.getClientId(),
                                        annonce.getReservedById(),
                                        annonce.getStatus());
                            }
                        }

                        if (request.tripType() != null) annonce.setTripType(request.tripType());
                        if (request.meetupPoint() != null) annonce.setMeetupPoint(request.meetupPoint());
                        if (request.tripIntention() != null) annonce.setTripIntention(request.tripIntention());
                        if (request.pricingMethod() != null) annonce.setPricingMethod(request.pricingMethod());
                        if (request.isNegotiable() != null) annonce.setNegotiable(request.isNegotiable());
                        if (request.paymentMethod() != null) annonce.setPaymentMethod(request.paymentMethod());
                        if (request.cost() != null) annonce.setCost(request.cost());
                        if (request.baggageInfo() != null) annonce.setBaggageInfo(request.baggageInfo());

                        annonce.setUpdatedAt(Timestamp.from(Instant.now()));

                        return Mono.when(terminatedAction, confirmedAction)
                                .then(Mono.defer(() -> productRepository.save(annonce)
                                        .doOnNext(saved -> log.info("{} [{}] ✅ UPDATE SUCCESSFUL.", LOG_PREFIX, fid))
                                        .flatMap(saved -> notificationTriggerService
                                                .onProductUpdated(ANNONCE_TYPE, saved.getClientId(), previousReservedById, previousStatus, saved, token)
                                                .thenReturn(saved))));
                    }));
                })
                .doFinally(s -> log.info("{} ╚══════════════════════════════════════════════════════════════════════════", LOG_PREFIX));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE
    // ─────────────────────────────────────────────────────────────────────────

    public Mono<Void> deleteClientAnnonce(UUID authUserId, UUID annonceId, String token) {
        return assertClient(authUserId, token)
                .then(productRepository.findByIdAndProductType(annonceId, ANNONCE_TYPE))
                .switchIfEmpty(Mono.error(new AccessDeniedException("Annonce not found")))
                .flatMap(existing -> {
                    if (existing.getClientId() == null || !existing.getClientId().equals(authUserId)) {
                        return Mono.error(new AccessDeniedException("Access denied"));
                    }
                    return productRepository.deleteById(annonceId);
                });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS PRIVÉS
    // ─────────────────────────────────────────────────────────────────────────

    private Mono<Void> assertClient(UUID authUserId, String token) {
        return actorRepository.findByUserId(authUserId, token)
                .switchIfEmpty(Mono.error(new AccessDeniedException("No Business Actor found for this user")))
                .flatMap(actor -> {
                    if (!(actor instanceof ClientRole)) {
                        return Mono.error(new AccessDeniedException("User is not a Client"));
                    }
                    return Mono.empty();
                });
    }

    private BigDecimal safeParseAmount(String amountStr, String fid) {
        if (amountStr == null || amountStr.isBlank()) return BigDecimal.ZERO;
        try {
            return new BigDecimal(amountStr.replaceAll("[^\\d.]", ""));
        } catch (Exception e) {
            log.error("{} [{}] 🚨 Amount parse error: {}", LOG_PREFIX, fid, amountStr);
            return BigDecimal.ZERO;
        }
    }

    private void logJson(String fid, String label, Object obj) {
        try {
            log.info("{} [{}] {}:\n{}", LOG_PREFIX, fid, label, objectMapper.writeValueAsString(obj));
        } catch (JsonProcessingException e) {
            log.warn("{} Logging error", LOG_PREFIX);
        }
    }
}