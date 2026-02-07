package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.Review;
import com.yowyob.template.domain.model.SubjectType;
import com.yowyob.template.domain.ports.in.ReviewUseCase;
import com.yowyob.template.domain.ports.out.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService implements ReviewUseCase {
    private final ReviewRepositoryPort repository;
    private final ProductRepositoryPort productRepository;
    private final BusinessActorRepositoryPort actorRepository;
    private final OrganisationRepositoryPort organisationRepository;
    private final NotificationTriggerService notificationTriggerService;

    @Override
    public Mono<Review> createReview(Review review) {
        review.setCreatedAt(OffsetDateTime.now());
        return repository.save(review)
                .flatMap(saved -> notificationTriggerService.onReviewCreated(saved).thenReturn(saved));
    }

    @Override
    public Mono<Review> getReviewById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Flux<Review> getAllReviews() {
        // Since the repository doesn't have findAll, we might need to add it or skip for now
        // Assuming repository.findAll() might be needed or we use a more specific query
        return Flux.empty(); 
    }

    @Override
    public Flux<Review> getReviewsBySubject(UUID subjectId, SubjectType subjectType) {
        return repository.findBySubjectIdAndSubjectType(subjectId, subjectType);
    }

    public Flux<Review> getReviewsByAuthor(UUID authorId) {
        return repository.findByAuthorId(authorId);
    }

    @Override
    public Mono<Review> updateReview(UUID id, Review review) {
        return repository.findById(id)
                .flatMap(existing -> {
                    existing.setReviewType(review.getReviewType() != null ? review.getReviewType() : existing.getReviewType());
                    existing.setRating(review.getRating() != null ? review.getRating() : existing.getRating());
                    existing.setComment(review.getComment() != null ? review.getComment() : existing.getComment());
                    existing.setReportReason(review.getReportReason() != null ? review.getReportReason() : existing.getReportReason());
                    return repository.save(existing);
                });
    }

    @Override
    public Mono<Void> deleteReview(UUID id) {
        return repository.deleteById(id);
    }
}
