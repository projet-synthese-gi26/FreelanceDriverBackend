package com.yowyob.template.application.service;

import com.yowyob.template.application.service.NotificationService;
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
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.request.CreateAnnonceRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.request.UpdateAnnonceRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



import com.yowyob.template.domain.ports.out.UserRepositoryPort;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

/**
 * SERVICE DE GESTION DES ANNONCES CLIENT
 * 
 * RESPONSABILITÉS :
 * 1. Cycle de vie des demandes de trajets (Annonces).
 * 2. Déclenchement automatique de la commission (10%) prélevée sur le CHAUFFEUR 
 *    ayant réservé la course, lors du passage à 'Terminated'.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClientAnnonceService {

    private final BusinessActorRepositoryPort actorRepository;
    private final OrganisationRepositoryPort organisationRepository;
    private final ProductRepositoryPort productRepository;
    private final PaymentUseCase paymentUseCase;
    private final ObjectMapper objectMapper;
    private final UserRepositoryPort userRepository;

    private static final String ANNONCE_TYPE = "ANNONCE";
    private static final String LOG_PREFIX = "[SERV-ANNONCE]";
    private final NotificationService notificationService;

    // ============================================================================================
    // 1. CRÉATION
    // ============================================================================================

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

    // ============================================================================================
    // 2. MISE À JOUR (LOGIQUE FINANCIÈRE COMPLEXE)
    // ============================================================================================

    public Mono<Product> updateClientAnnonce(UUID authUserId, UUID annonceId, UpdateAnnonceRequest request, String token) {
        String fid = "UPDT-ANN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("{} ╔══════════════════════════════════════════════════════════════════════════", LOG_PREFIX);
        log.info("{} ║ [{}] START UPDATE PROCESS (ANNONCE)", LOG_PREFIX, fid);
    
        return assertClient(authUserId, token)
                .then(productRepository.findByIdAndProductType(annonceId, ANNONCE_TYPE))
                .switchIfEmpty(Mono.error(new AccessDeniedException("Annonce not found")))
                .flatMap(existingProduct -> {
                    Annonce annonce = (Annonce) existingProduct;
                    ProductStatus oldStatus = annonce.getStatus();
                    ProductStatus newStatus = request.status() != null ? ProductStatus.valueOf(request.status()) : oldStatus;
    
                    // --- RÉSOLUTION DU CHAUFFEUR (Source pour Paiement et Notif) ---
                    // On prépare le flux pour trouver le chauffeur à partir de la réservation
                    Mono<com.yowyob.template.domain.model.User> driverResolver = (annonce.getReservedById() == null) 
                        ? Mono.empty() 
                        : organisationRepository.findById(annonce.getReservedById(), token)
                            .flatMap(org -> userRepository.findByEmail(org.getEmail()))
                            .cache(); // Cache car on peut l'utiliser pour Paiement ET Notif
    
                    // --- 1. ACTION SI TERMINATED (Paiement + Notif Commission) ---
                    Mono<Void> terminatedAction = Mono.empty();
                    if (newStatus == ProductStatus.Terminated && oldStatus != ProductStatus.Terminated) {
                        terminatedAction = driverResolver
                                .switchIfEmpty(Mono.error(new IllegalStateException("Cannot terminate: No driver linked.")))
                                .flatMap(driver -> {
                                    BigDecimal amount = safeParseAmount(annonce.getCost(), fid);
                                    return paymentUseCase.processRidePayment(driver.getId(), amount)
                                            .then(notificationService.sendCommissionDeductedAlert(
                                                    driver.getId(), driver.getFirstName(), driver.getPhone(), 
                                                    amount.multiply(new BigDecimal("0.1")).toString()
                                            ));
                                });
                    }
    
                    // --- 2. ACTION SI CONFIRMED (Notif Confirmation au Chauffeur) ---
                    Mono<Void> confirmedAction = Mono.empty();
                    if (newStatus == ProductStatus.Confirmed && oldStatus != ProductStatus.Confirmed) {
                        confirmedAction = driverResolver.flatMap(driver -> 
                            notificationService.sendRideConfirmedAlert(
                                    driver.getId(), driver.getFirstName(), driver.getEmail(), 
                                    driver.getPhone(), annonce.getTitle(), annonce.getDropoffLocation()
                            )
                        );
                    }
    
                    // --- 3. EXÉCUTION ET SAUVEGARDE ---
                    return Mono.when(terminatedAction, confirmedAction)
                            .then(Mono.defer(() -> {
                                applyAnnonceUpdates(annonce, request);
                                annonce.setUpdatedAt(Timestamp.from(Instant.now()));
                                return productRepository.save(annonce)
                                        .doOnNext(saved -> log.info("{} [{}] ✅ UPDATE SUCCESSFUL.", LOG_PREFIX, fid));
                            }));
                })
                .doFinally(s -> log.info("{} ╚══════════════════════════════════════════════════════════════════════════", LOG_PREFIX));
    }


    // ============================================================================================
    // 3. CONSULTATIONS & SUPPRESSION
    // ============================================================================================

    public Flux<Product> listClientAnnonces(UUID authUserId, String token) {
        return assertClient(authUserId, token)
                .thenMany(productRepository.findByProductTypeAndClientId(ANNONCE_TYPE, authUserId));
    }

    public Flux<Product> listClientAnnoncesByClientId(UUID clientId) {
        return productRepository.findByProductTypeAndClientId(ANNONCE_TYPE, clientId);
    }

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

    // ============================================================================================
    // HELPERS & LOGIQUE PRIVÉE
    // ============================================================================================

    private void applyAnnonceUpdates(Annonce annonce, UpdateAnnonceRequest request) {
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
                log.error("{} Invalid status: {}", LOG_PREFIX, request.status());
            }
        }
        
        if (request.reservedById() != null) {
            annonce.setReservedById(request.reservedById());
            if (annonce.getStatus() == ProductStatus.Published || annonce.getStatus() == ProductStatus.Draft) {
                annonce.setStatus(ProductStatus.PendingConfirmation);
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
}