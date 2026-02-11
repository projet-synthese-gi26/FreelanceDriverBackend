package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.Annonce;
import com.yowyob.template.domain.model.ClientRole;
import com.yowyob.template.domain.model.Product;
import com.yowyob.template.domain.model.ProductStatus;
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

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientAnnonceService {

    private final BusinessActorRepositoryPort actorRepository;
    private final OrganisationRepositoryPort organisationRepository;
    private final ProductRepositoryPort productRepository;
    private final NotificationTriggerService notificationTriggerService;

    private static final String ANNONCE_TYPE = "ANNONCE";
    
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

    public Mono<Product> updateClientAnnonce(UUID authUserId, UUID annonceId, UpdateAnnonceRequest request, String token) {
        return productRepository.findByIdAndProductType(annonceId, ANNONCE_TYPE)
                .switchIfEmpty(Mono.error(new AccessDeniedException("Annonce not found")))
                .flatMap(existing -> {
                    UUID previousReservedById = existing.getReservedById();
                    ProductStatus previousStatus = existing.getStatus();

                    boolean isOwner = existing.getClientId() != null && existing.getClientId().equals(authUserId);

                    if (!(existing instanceof Annonce annonce)) {
                        return Mono.error(new IllegalStateException("Product is not an Annonce"));
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
                                || request.paymentMethod() != null
                                || request.cost() != null
                                || request.baggageInfo() != null;

                        if (modifiesForbiddenField) {
                            return Mono.error(new AccessDeniedException("Only status/reservedById can be updated by non-owner"));
                        }

                        UUID reservedByUserId = existing.getReservedById();

                        // Cas "postulation" initiale: reservedById est encore null, un demandeur peut le renseigner
                        // à condition que l'utilisateur corresponde à reservedById.
                        if (reservedByUserId == null) {
                            if (request.reservedById() == null) {
                                return Mono.error(new AccessDeniedException("Access denied"));
                            }
                            authorizationCheck = Mono.defer(() -> {
                                log.info("[ANNONCE_BOOKING] initial booking check | userId={} | requestedReservedById={}", authUserId, request.reservedById());
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
                                log.info("[ANNONCE_BOOKING] follow-up booking check | userId={} | reservedByUserId={}", authUserId, reservedByUserId);
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
                    return productRepository.save(annonce)
                            .flatMap(saved -> notificationTriggerService
                                    .onProductUpdated(ANNONCE_TYPE, saved.getClientId(), previousReservedById, previousStatus, saved, token)
                                    .thenReturn(saved));
                    }));
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