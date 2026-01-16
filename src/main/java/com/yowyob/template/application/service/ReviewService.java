package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.Review;
import com.yowyob.template.domain.ports.in.ReviewUseCase;
import com.yowyob.template.domain.ports.out.ReviewRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Service
public class ReviewService implements ReviewUseCase {
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Mono<Review> createReview(Review review) {
        Review toSave = review;
        if (toSave.id() == null) {
            toSave = new Review(UUID.randomUUID(), toSave.rideId(), toSave.authorId(), toSave.subjectId(),
                    toSave.rating(), toSave.comment(), toSave.createdAt());
        }
        if (toSave.createdAt() == null) {
            toSave = new Review(toSave.id(), toSave.rideId(), toSave.authorId(), toSave.subjectId(), toSave.rating(),
                    toSave.comment(), Timestamp.from(Instant.now()));
        }
        return reviewRepository.save(toSave);
    }

    @Override
    public Mono<Review> getReviewById(UUID id) {
        return reviewRepository.findById(id);
    }

    @Override
    public Flux<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    public Mono<Review> updateReview(UUID id, Review review) {
        return reviewRepository.update(id, review);
    }

    @Override
    public Mono<Void> deleteReview(UUID id) {
        return reviewRepository.deleteById(id);
    }
}
