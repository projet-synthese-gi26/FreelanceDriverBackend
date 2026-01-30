package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.Product;
import com.yowyob.template.domain.ports.in.CreateProductUseCase; // Assure-toi que cette interface existe et correspond
import com.yowyob.template.domain.ports.out.ProductRepositoryPort;
import com.yowyob.template.domain.ports.out.ProductEventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService implements CreateProductUseCase {
    @Override
    public Mono<Product> createProductForOrganisation(UUID organisationId, java.util.Map<String, Object> params) {
        // Exemple d'implémentation générique, à adapter selon la logique métier
        Product product = null;
        // Ici, il faudrait instancier le bon type de Product (Annonce, Planning, etc.) selon params
        // et remplir les champs nécessaires. Pour l'instant, on laisse null ou on peut lever une exception.
        return Mono.error(new UnsupportedOperationException("createProductForOrganisation n'est pas encore implémenté"));
    }

    private final ProductRepositoryPort productRepository;
    private final ProductEventPublisherPort eventPublisher;

    // Cette méthode reste pour des cas d'usage génériques ou internes si nécessaire,
    // mais la création principale se fait via les services spécifiques (Driver/Client)
    @Override
    public Mono<Product> createProduct(Product product) {
        product.setCreatedAt(Timestamp.from(Instant.now()));
        return productRepository.save(product)
                .flatMap(saved -> eventPublisher.publishProductCreated(saved).thenReturn(saved));
    }
    
    @Override
    public Mono<Product> getProductById(UUID id) {
        return productRepository.findById(id);
    }

    @Override
    public Flux<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Note : Cette méthode de mise à jour est générique.
    // Pour une mise à jour sécurisée, il faudrait une méthode qui vérifie la propriété,
    // comme on l'a fait dans les services Driver/Client.
    @Override
    public Mono<Product> updateProduct(UUID id, Product product) {
        return productRepository.findById(id)
                .flatMap(existing -> {
                    // Mappe les champs modifiables. Attention, ceci est un exemple simple.
                    // Une vraie implémentation utiliserait un DTO pour ne pas écraser les champs non modifiables.
                    existing.setTitle(product.getTitle() != null ? product.getTitle() : existing.getTitle());
                    existing.setStatus(product.getStatus() != null ? product.getStatus() : existing.getStatus());
                    existing.setUpdatedAt(Timestamp.from(Instant.now()));
                    return productRepository.save(existing)
                            .flatMap(saved -> eventPublisher.publishProductUpdated(saved).thenReturn(saved));
                });
    }

    @Override
    public Mono<Void> deleteProduct(UUID id) {
        return productRepository.findById(id)
                .flatMap(product -> productRepository.deleteById(id)
                        .then(eventPublisher.publishProductDeleted(product)));
    }
    
    // La méthode createProductForOrganisation n'est plus dans cette interface/service
    // car elle est maintenant gérée par DriverPlanningService et ClientAnnonceService.
    // Si ton interface CreateProductUseCase la contient encore, tu devrais la retirer.
}