package com.yowyob.template.domain.ports.in;

import com.yowyob.template.domain.model.Review;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ReviewUseCase {
    Mono<Review> createReview(Review review);

    Mono<Review> getReviewById(UUID id);

    Flux<Review> getAllReviews();

    Mono<Review> updateReview(UUID id, Review review);

    Mono<Void> deleteReview(UUID id);
}
