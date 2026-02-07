package com.yowyob.template.application.service;

import com.yowyob.template.application.service.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yowyob.template.domain.model.DriverRole;
import com.yowyob.template.domain.model.Planning;
import com.yowyob.template.domain.model.Product;
import com.yowyob.template.domain.model.ProductStatus;
import com.yowyob.template.domain.ports.out.BusinessActorRepositoryPort;
import com.yowyob.template.domain.ports.out.OrganisationRepositoryPort;
import com.yowyob.template.domain.ports.out.ProductRepositoryPort;
import com.yowyob.template.domain.ports.out.UserRepositoryPort;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.request.CreatePlanningRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.request.UpdatePlanningRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// NOUVEAUX IMPORTS POUR LA MISE À JOUR
import com.yowyob.template.domain.ports.in.PaymentUseCase;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverPlanningService {

    private final BusinessActorRepositoryPort actorRepository;
    private final OrganisationRepositoryPort organisationRepository;
    private final ProductRepositoryPort productRepository;
    private final UserRepositoryPort userRepository;

    
    // INJECTIONS POUR LA MISE À JOUR ET TRAÇABILITÉ
    private final PaymentUseCase paymentUseCase;
    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    private static final String PLANNING_TYPE = "PLANNING";
    private static final String LOG_PREFIX = "[SERV-PLANNING]";

    // ============================================================================================
    // PARTIE IDENTIQUE (SANS CHANGEMENT)
    // ============================================================================================

    public Mono<Product> createDriverPlanning(UUID authUserId, CreatePlanningRequest request, String token) {
        
        // 1. Trouver le BusinessActor associé à l'utilisateur connecté
        return actorRepository.findByUserId(authUserId, token)
            .switchIfEmpty(Mono.error(new AccessDeniedException("No Business Actor found for this user")))
            .flatMap(actor -> {
                
                // 2. Vérifier que c'est bien un Driver
                if (!(actor instanceof DriverRole)) {
                    return Mono.error(new AccessDeniedException("User is not a Driver"));
                }

                // 3. Trouver son Organisation
                return organisationRepository.findByActorId(actor.getId(), token)
                    .switchIfEmpty(Mono.error(new RuntimeException("Driver has no Organisation")))
                    .flatMap(organisation -> {
                        
                        // 4. Utiliser la Factory Method pour créer le Planning
                        // Ici, on utilise un Builder pour construire l'objet à partir de la requête
                        Planning planning = Planning.builder()
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
                            .status(ProductStatus.Draft)
                            .tripType(request.tripType())
                            .meetupPoint(request.meetupPoint())
                            .tripIntention(request.tripIntention())
                            .pricingMethod(request.pricingMethod())
                            .isNegotiable(request.isNegotiable() != null && request.isNegotiable())
                            .paymentOption(request.paymentOption())
                            .regularAmount(request.regularAmount())
                            .discountPercentage(request.discountPercentage())
                            .discountedAmount(request.discountedAmount())
                            .createdAt(Timestamp.from(Instant.now()))
                            .build();

                        // 5. Sauvegarder via le ProductRepositoryPort
                        return productRepository.save(planning);
                    });
            });
    }

    public Flux<Product> listDriverPlannings(UUID authUserId, String token) {
        return assertDriver(authUserId, token)
                .thenMany(productRepository.findByProductTypeAndClientId(PLANNING_TYPE, authUserId));
    }

    public Mono<Product> getDriverPlanning(UUID authUserId, UUID planningId, String token) {
        return assertDriver(authUserId, token)
                .then(productRepository.findByIdAndProductType(planningId, PLANNING_TYPE))
                .switchIfEmpty(Mono.error(new AccessDeniedException("Planning not found")))
                .flatMap(product -> {
                    if (product.getClientId() == null || !product.getClientId().equals(authUserId)) {
                        return Mono.error(new AccessDeniedException("Access denied"));
                    }
                    return Mono.just(product);
                });
    }

    // ============================================================================================
    // PARTIE MODIFIÉE (UPDATE AVEC LOGS MASSIFS ET PAIEMENT)
    // ============================================================================================

    public Mono<Product> updateDriverPlanning(UUID authUserId, UUID planningId, UpdatePlanningRequest request, String token) {
        String fid = "UPDT-PLAN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        log.info("{} ╔══════════════════════════════════════════════════════════════════════════", LOG_PREFIX);
        log.info("{} ║ [{}] START UPDATE PROCESS (PLANNING)", LOG_PREFIX, fid);
    
        return assertDriver(authUserId, token)
                .then(productRepository.findByIdAndProductType(planningId, PLANNING_TYPE))
                .switchIfEmpty(Mono.error(new AccessDeniedException("Planning not found")))
                .flatMap(existing -> {
                    Planning planning = (Planning) existing;
                    ProductStatus oldStatus = planning.getStatus();
                    ProductStatus newStatus = request.status() != null ? ProductStatus.valueOf(request.status()) : oldStatus;
    
                    // --- RÉSOLUTION DU CHAUFFEUR (Pour avoir son téléphone réel) ---
                    Mono<com.yowyob.template.domain.model.User> driverResolver = userRepository.findById(authUserId, token).cache();
    
                    // --- 1. ACTION SI TERMINATED (Paiement + Notif Chauffeur) ---
                    Mono<Void> terminatedAction = Mono.empty();
                    if (newStatus == ProductStatus.Terminated && oldStatus != ProductStatus.Terminated) {
                        BigDecimal amount = safeParseBigDecimal(planning.getRegularAmount(), fid);
                        
                        terminatedAction = driverResolver.flatMap(driver -> 
                            paymentUseCase.processRidePayment(authUserId, amount)
                                .then(notificationService.sendCommissionDeductedAlert(
                                        authUserId, 
                                        driver.getFirstName(), 
                                        driver.getPhone(), // <--- CORRECTION : On utilise le vrai téléphone
                                        amount.multiply(new BigDecimal("0.1")).toString()
                                ))
                        ).onErrorResume(e -> Mono.empty());
                    }
    
                    // --- 2. ACTION SI CONFIRMED (Notif au Client) ---
                    Mono<Void> confirmedAction = Mono.empty();
                    if (newStatus == ProductStatus.Confirmed && oldStatus != ProductStatus.Confirmed) {
                        UUID clientId = (request.reservedById() != null) ? request.reservedById() : planning.getReservedById();
                        
                        if (clientId != null) {
                            confirmedAction = organisationRepository.findById(clientId, token)
                                .flatMap(org -> userRepository.findByEmail(org.getEmail()))
                                .flatMap(clientUser -> notificationService.sendRideConfirmedAlert(
                                        clientUser.getId(), clientUser.getFirstName(), clientUser.getEmail(), 
                                        clientUser.getPhone(), planning.getTitle(), planning.getDropoffLocation()
                                )).onErrorResume(e -> Mono.empty());
                        }
                    }
    
                    // --- 3. SAUVEGARDE ---
                    return Mono.when(terminatedAction, confirmedAction)
                            .then(Mono.defer(() -> {
                                mapUpdateToPlanning(planning, request);
                                planning.setUpdatedAt(Timestamp.from(Instant.now()));
                                return productRepository.save(planning);
                            }));
                })
                .doFinally(s -> log.info("{} ╚══════════════════════════════════════════════════════════════════════════", LOG_PREFIX));
    }
    // ============================================================================================
    // SUITE PARTIE IDENTIQUE
    // ============================================================================================

    public Mono<Void> deleteDriverPlanning(UUID authUserId, UUID planningId, String token) {
        return assertDriver(authUserId, token)
                .then(productRepository.findByIdAndProductType(planningId, PLANNING_TYPE))
                .switchIfEmpty(Mono.error(new AccessDeniedException("Planning not found")))
                .flatMap(existing -> {
                    if (existing.getClientId() == null || !existing.getClientId().equals(authUserId)) {
                        return Mono.error(new AccessDeniedException("Access denied"));
                    }
                    return productRepository.deleteById(planningId);
                });
    }

    private Mono<Void> assertDriver(UUID authUserId, String token) {
        return actorRepository.findByUserId(authUserId, token)
                .switchIfEmpty(Mono.error(new AccessDeniedException("No Business Actor found for this user")))
                .flatMap(actor -> {
                    if (!(actor instanceof DriverRole)) {
                        return Mono.error(new AccessDeniedException("User is not a Driver"));
                    }
                    return Mono.empty();
                });
    }

    // --- NOUVEAUX HELPERS DE TRAÇABILITÉ ---

    private void mapUpdateToPlanning(Planning planning, UpdatePlanningRequest request) {
        if (request.title() != null) planning.setTitle(request.title());
        if (request.departureLocation() != null) planning.setDepartureLocation(request.departureLocation());
        if (request.dropoffLocation() != null) planning.setDropoffLocation(request.dropoffLocation());
        if (request.startDate() != null) planning.setStartDate(request.startDate());
        if (request.startTime() != null) planning.setStartTime(request.startTime());
        if (request.endDate() != null) planning.setEndDate(request.endDate());
        if (request.endTime() != null) planning.setEndTime(request.endTime());

        if (request.reservedById() != null) planning.setReservedById(request.reservedById());
        
        if (request.status() != null) {
            try {
                planning.setStatus(ProductStatus.valueOf(request.status()));
            } catch (IllegalArgumentException ex) {
                log.error("Invalid status value: {}", request.status());
            }
        }
        
        if (request.tripType() != null) planning.setTripType(request.tripType());
        if (request.meetupPoint() != null) planning.setMeetupPoint(request.meetupPoint());
        if (request.tripIntention() != null) planning.setTripIntention(request.tripIntention());
        if (request.pricingMethod() != null) planning.setPricingMethod(request.pricingMethod());
        if (request.isNegotiable() != null) planning.setNegotiable(request.isNegotiable());
        if (request.paymentOption() != null) planning.setPaymentOption(request.paymentOption());
        if (request.regularAmount() != null) planning.setRegularAmount(request.regularAmount());
        if (request.discountPercentage() != null) planning.setDiscountPercentage(request.discountPercentage());
        if (request.discountedAmount() != null) planning.setDiscountedAmount(request.discountedAmount());
    }

    private BigDecimal safeParseBigDecimal(String val, String flowId) {
        if (val == null || val.isBlank()) return BigDecimal.ZERO;
        try {
            return new BigDecimal(val.replaceAll("[^\\d.]", ""));
        } catch (Exception e) {
            log.error("{} [{}] Could not parse amount '{}'. Defaulting to 0.", LOG_PREFIX, flowId, val);
            return BigDecimal.ZERO;
        }
    }

    private void logJson(String flowId, String label, Object obj) {
        try {
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            log.info("{} [{}] {}:\n{}", LOG_PREFIX, flowId, label, json);
        } catch (JsonProcessingException e) {
            log.error("{} [{}] Failed to log JSON for {}: {}", LOG_PREFIX, flowId, label, e.getMessage());
        }
    }
}