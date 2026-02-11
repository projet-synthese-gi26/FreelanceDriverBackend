package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.DriverRole;
import com.yowyob.template.domain.model.Planning;
import com.yowyob.template.domain.model.Product;
import com.yowyob.template.domain.model.ProductStatus;
import com.yowyob.template.domain.ports.out.BusinessActorRepositoryPort;
import com.yowyob.template.domain.ports.out.OrganisationRepositoryPort;
import com.yowyob.template.domain.ports.out.ProductRepositoryPort;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.request.CreatePlanningRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.request.UpdatePlanningRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverPlanningService {

    private final BusinessActorRepositoryPort actorRepository;
    private final OrganisationRepositoryPort organisationRepository;
    private final ProductRepositoryPort productRepository;
    private final NotificationTriggerService notificationTriggerService;

    private static final String PLANNING_TYPE = "PLANNING";

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

    public Flux<Product> listDriverPlanningsByDriverId(UUID driverId) {
        return productRepository.findByProductTypeAndClientId(PLANNING_TYPE, driverId);
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

    public Mono<Product> updateDriverPlanning(UUID authUserId, UUID planningId, UpdatePlanningRequest request, String token) {
        return productRepository.findByIdAndProductType(planningId, PLANNING_TYPE)
                .switchIfEmpty(Mono.error(new AccessDeniedException("Planning not found")))
                .flatMap(existing -> {
                    UUID previousReservedById = existing.getReservedById();
                    ProductStatus previousStatus = existing.getStatus();

                    boolean isOwner = existing.getClientId() != null && existing.getClientId().equals(authUserId);

                    if (!(existing instanceof Planning planning)) {
                        return Mono.error(new IllegalStateException("Product is not a Planning"));
                    }

                    // Si ce n'est pas le propriétaire, on limite strictement les champs modifiables
                    // (status / reservedById) et on vérifie que l'utilisateur correspond à reservedById.
                    Mono<Void> authorizationCheck;
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
                                || request.paymentOption() != null
                                || request.regularAmount() != null
                                || request.discountPercentage() != null
                                || request.discountedAmount() != null;

                        if (modifiesForbiddenField) {
                            return Mono.error(new AccessDeniedException("Only status/reservedById can be updated by non-owner"));
                        }

                        UUID reservedByUserId = existing.getReservedById();

                        // Cas "booking" initial: reservedById est encore null, un demandeur peut le renseigner
                        // à condition que l'organisation fournie lui appartienne.
                        if (reservedByUserId == null) {
                            if (request.reservedById() == null) {
                                return Mono.error(new AccessDeniedException("Access denied"));
                            }
                            authorizationCheck = Mono.defer(() -> {
                                log.info("[PLANNING_BOOKING] initial booking check | userId={} | requestedReservedById={}", authUserId, request.reservedById());
                                if (!authUserId.equals(request.reservedById())) {
                                    return Mono.error(new AccessDeniedException(
                                            "Access denied: reservedById must match authenticated userId. authUserId="
                                                    + authUserId + ", requestedReservedById=" + request.reservedById()));
                                }
                                return Mono.empty();
                            });
                        } else {
                            // Cas suivant: seul le demandeur (reservedById) peut mettre à jour (status)
                            authorizationCheck = Mono.defer(() -> {
                                log.info("[PLANNING_BOOKING] follow-up booking check | userId={} | reservedByUserId={}", authUserId, reservedByUserId);
                                if (!authUserId.equals(reservedByUserId)) {
                                    return Mono.error(new AccessDeniedException(
                                            "Access denied: only reserved user can update. authUserId="
                                                    + authUserId + ", reservedByUserId=" + reservedByUserId));
                                }
                                return Mono.empty();
                            });
                        }
                    }

                    return authorizationCheck.then(Mono.defer(() -> {

                    if (request.title() != null) planning.setTitle(request.title());
                    if (request.departureLocation() != null) planning.setDepartureLocation(request.departureLocation());
                    if (request.dropoffLocation() != null) planning.setDropoffLocation(request.dropoffLocation());
                    if (request.startDate() != null) planning.setStartDate(request.startDate());
                    if (request.startTime() != null) planning.setStartTime(request.startTime());
                    if (request.endDate() != null) planning.setEndDate(request.endDate());
                    if (request.endTime() != null) planning.setEndTime(request.endTime());
                    if (request.status() != null) {
                        try {
                            planning.setStatus(ProductStatus.valueOf(request.status()));
                        } catch (IllegalArgumentException ex) {
                            return Mono.error(new IllegalArgumentException("Invalid status: " + request.status()));
                        }
                    }

                    return Mono.defer(() -> {
                        if (request.reservedById() != null) {
                            planning.setReservedById(request.reservedById());
                            if (!isOwner) {
                                // Booking: dès qu'un non-propriétaire pose reservedById, on passe en attente de confirmation driver.
                                // On inclut PendingConfirmation car certains flows publient avec ce statut.
                                if (planning.getStatus() == ProductStatus.Published
                                        || planning.getStatus() == ProductStatus.Draft
                                        || planning.getStatus() == ProductStatus.PendingConfirmation) {
                                    planning.setStatus(ProductStatus.PendingDriverConfirmation);
                                }
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
                        planning.setUpdatedAt(Timestamp.from(Instant.now()));

                        return productRepository.save(planning)
                                .flatMap(saved -> notificationTriggerService
                                        .onProductUpdated(PLANNING_TYPE, existing.getClientId(), previousReservedById, previousStatus, saved, token)
                                        .thenReturn(saved));
                    });
                    }));
                });
    }

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
}