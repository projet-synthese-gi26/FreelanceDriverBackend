package com.yowyob.template.domain.ports.out;

import com.yowyob.template.domain.model.Review;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ReviewRepository {
    Mono<Review> save(Review review);

    Mono<Review> findById(UUID id);

    Flux<Review> findAll();

    Mono<Review> update(UUID id, Review review);

    Mono<Void> deleteById(UUID id);
}
