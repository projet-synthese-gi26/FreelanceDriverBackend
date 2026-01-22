package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.CertifiedOrganisation;
import com.yowyob.template.domain.model.Organisation;
import com.yowyob.template.domain.model.OrganisationBuilder;
import com.yowyob.template.domain.model.Product;
import com.yowyob.template.domain.ports.in.OrganisationUseCase;
import com.yowyob.template.domain.ports.out.OrganisationRepositoryPort;
import com.yowyob.template.domain.ports.out.ProductEventPublisherPort;
import com.yowyob.template.domain.ports.out.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganisationService implements OrganisationUseCase {
    private final OrganisationRepositoryPort repository;
    private final ProductRepositoryPort productRepository;
    private final ProductEventPublisherPort eventPublisher;

    @Override
    public Mono<Organisation> createOrganisation(String name, UUID actorId, OrganisationBuilder.OrgType type) {
        Organisation org = new OrganisationBuilder()
                .withName(name)
                .withActorId(actorId)
                .asDriver()
                .build();
        
        if (type == OrganisationBuilder.OrgType.CLIENT) {
            org = new OrganisationBuilder()
                    .withName(name)
                    .withActorId(actorId)
                    .asClient()
                    .build();
        }
        
        org.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        org.setIsActive(true);
        org.setStatus("ACTIVE");
        
        return repository.save(org);
    }

    @Override
    public Mono<Organisation> getOrganisationById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Flux<Organisation> getAllOrganisations() {
        return repository.findAll();
    }

    @Override
    public Mono<Organisation> certifyOrganisation(UUID id, String syndicateName) {
        return repository.findById(id)
                .flatMap(org -> {
                    CertifiedOrganisation certified = CertifiedOrganisation.builder()
                            .wrappedOrganisation(org)
                            .syndicateName(syndicateName)
                            .id(org.getId())
                            .name(org.getName())
                            .actorId(org.getActorId())
                            .status("CERTIFIED")
                            .build();
                    
                    return repository.save(certified);
                });
    }

    @Override
    public Mono<Void> deleteOrganisation(UUID id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<Product> createProductForOrganisation(UUID id, Map<String, Object> params) {
        return repository.findById(id)
                .flatMap(org -> {
                    Product product = org.createProduct(params);
                    product.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                    return productRepository.save(product)
                            .flatMap(savedProduct -> eventPublisher.publishProductCreated(savedProduct)
                                    .thenReturn(savedProduct));
                });
    }
}
