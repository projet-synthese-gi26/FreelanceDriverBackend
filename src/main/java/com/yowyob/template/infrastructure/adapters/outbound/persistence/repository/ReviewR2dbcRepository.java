package com.yowyob.template.infrastructure.adapters.outbound.persistence.repository;

import com.yowyob.template.domain.model.SubjectType;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.entity.ReviewEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface ReviewR2dbcRepository extends ReactiveCrudRepository<ReviewEntity, UUID> {
    Flux<ReviewEntity> findBySubjectIdAndSubjectTypeOrderByCreatedAtDesc(UUID subjectId, SubjectType subjectType);
    Flux<ReviewEntity> findByAuthorIdOrderByCreatedAtDesc(UUID authorId);
}
