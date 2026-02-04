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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientAnnonceService {

    private final BusinessActorRepositoryPort actorRepository;
    private final OrganisationRepositoryPort organisationRepository;
    private final ProductRepositoryPort productRepository;

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
        return assertClient(authUserId, token)
                .then(productRepository.findByIdAndProductType(annonceId, ANNONCE_TYPE))
                .switchIfEmpty(Mono.error(new AccessDeniedException("Annonce not found")))
                .flatMap(existing -> {
                    if (existing.getClientId() == null || !existing.getClientId().equals(authUserId)) {
                        return Mono.error(new AccessDeniedException("Access denied"));
                    }
                    if (!(existing instanceof Annonce annonce)) {
                        return Mono.error(new IllegalStateException("Product is not an Annonce"));
                    }

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
                        if (annonce.getStatus() == ProductStatus.Published
                                || annonce.getStatus() == ProductStatus.Draft) {
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

                    annonce.setUpdatedAt(Timestamp.from(Instant.now()));
                    return productRepository.save(annonce);
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