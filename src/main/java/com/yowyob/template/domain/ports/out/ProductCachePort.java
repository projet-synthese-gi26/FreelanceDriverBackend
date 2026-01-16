package com.yowyob.template.domain.ports.out;

import com.yowyob.template.domain.model.Product;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface ProductCachePort {
    Mono<Boolean> saveInCache(Product product);
    Mono<Product> findInCache(UUID id);
    Mono<Void> deleteFromCache(UUID id);
    Mono<Void> clearCache();
}