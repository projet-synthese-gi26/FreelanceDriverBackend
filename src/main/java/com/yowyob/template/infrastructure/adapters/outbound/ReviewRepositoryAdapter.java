package com.yowyob.template.infrastructure.adapters.outbound;

import com.yowyob.template.domain.model.Review;
import com.yowyob.template.domain.ports.out.ReviewRepository;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.ReviewEntity;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.ReviewR2dbcRepository;
import com.yowyob.template.infrastructure.mappers.ReviewMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class ReviewRepositoryAdapter implements ReviewRepository {
    private final ReviewR2dbcRepository reviewR2dbcRepository;
    private final ReviewMapper reviewMapper;

    public ReviewRepositoryAdapter(ReviewR2dbcRepository reviewR2dbcRepository, ReviewMapper reviewMapper) {
        this.reviewR2dbcRepository = reviewR2dbcRepository;
        this.reviewMapper = reviewMapper;
    }

    @Override
    public Mono<Review> save(Review review) {
        ReviewEntity entity = reviewMapper.toEntityForInsert(review);
        return reviewR2dbcRepository.save(entity)
                .map(reviewMapper::toDomain);
    }

    @Override
    public Mono<Review> findById(UUID id) {
        return reviewR2dbcRepository.findById(id)
                .map(reviewMapper::toDomain);
    }

    @Override
    public Flux<Review> findAll() {
        return reviewR2dbcRepository.findAll()
                .map(reviewMapper::toDomain);
    }

    @Override
    public Mono<Review> update(UUID id, Review review) {
        return reviewR2dbcRepository.findById(id)
                .flatMap(existing -> {
                    ReviewEntity updated = reviewMapper.toEntity(review);
                    updated.setId(id);
                    return reviewR2dbcRepository.save(updated);
                })
                .map(reviewMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return reviewR2dbcRepository.deleteById(id);
    }
}
