package com.yowyob.template.domain.ports.out;

import com.yowyob.template.domain.model.Reaction;
import com.yowyob.template.domain.model.ReactionType;
import com.yowyob.template.domain.model.SubjectType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ReactionRepositoryPort {
    Mono<Reaction> save(Reaction reaction);
    Mono<Reaction> findById(UUID id);
    Flux<Reaction> findByActorId(UUID actorId);
    Flux<Reaction> findByTargetIdAndTargetType(UUID targetId, SubjectType targetType);
    Mono<Void> deleteById(UUID id);
    Mono<Void> deleteByActorIdAndTargetIdAndType(UUID actorId, UUID targetId, ReactionType type);
}
