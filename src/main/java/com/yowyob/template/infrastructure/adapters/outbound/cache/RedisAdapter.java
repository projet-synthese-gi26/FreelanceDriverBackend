package com.yowyob.template.infrastructure.adapters.outbound.cache;

import com.yowyob.template.domain.model.Product;
import com.yowyob.template.domain.ports.out.ProductCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RedisAdapter implements ProductCachePort {
    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    @Override
    public Mono<Boolean> saveInCache(Product product) {
        return redisTemplate.opsForValue()
                .set("product:" + product.id(), product, Duration.ofMinutes(10));
    }

    @Override
    public Mono<Product> findInCache(UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findInCache'");
    }

    @Override
    public Mono<Void> deleteFromCache(UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteFromCache'");
    }

    @Override
    public Mono<Void> clearCache() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'clearCache'");
    }
}