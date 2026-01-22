package com.yowyob.template.infrastructure.adapters.outbound.persistence;

import com.yowyob.template.domain.model.Review;
import com.yowyob.template.domain.model.SubjectType;
import com.yowyob.template.domain.ports.out.ReviewRepositoryPort;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.repository.ReviewR2dbcRepository;
import com.yowyob.template.infrastructure.mappers.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReviewR2dbcAdapter implements ReviewRepositoryPort {
    private final ReviewR2dbcRepository repository;
    private final ReviewMapper mapper;

    @Override
    public Mono<Review> save(Review review) {
        return repository.save(mapper.toEntity(review))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Review> findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Review> findBySubjectIdAndSubjectType(UUID subjectId, SubjectType subjectType) {
        return repository.findBySubjectIdAndSubjectType(subjectId, subjectType)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }
}
