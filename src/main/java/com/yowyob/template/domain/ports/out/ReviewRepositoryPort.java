package com.yowyob.template.domain.ports.out;

import com.yowyob.template.domain.model.Review;
import com.yowyob.template.domain.model.SubjectType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ReviewRepositoryPort {
    Mono<Review> save(Review review);
    Mono<Review> findById(UUID id);
    Flux<Review> findBySubjectIdAndSubjectType(UUID subjectId, SubjectType subjectType);
    Mono<Void> deleteById(UUID id);
}
