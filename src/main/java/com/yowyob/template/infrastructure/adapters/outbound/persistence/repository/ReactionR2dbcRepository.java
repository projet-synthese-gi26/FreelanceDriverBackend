package com.yowyob.template.infrastructure.adapters.outbound.persistence.repository;

import com.yowyob.template.domain.model.ReactionType;
import com.yowyob.template.domain.model.SubjectType;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.entity.ReactionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ReactionR2dbcRepository extends ReactiveCrudRepository<ReactionEntity, UUID> {
    Flux<ReactionEntity> findByActorIdOrderByCreatedAtDesc(UUID actorId);
    Flux<ReactionEntity> findByTargetIdAndTargetTypeOrderByCreatedAtDesc(UUID targetId, SubjectType targetType);
    Mono<Void> deleteByActorIdAndTargetIdAndType(UUID actorId, UUID targetId, ReactionType type);
}
