package com.yowyob.template.domain.ports.in;

import com.yowyob.template.domain.model.Reaction;
import com.yowyob.template.domain.model.ReactionType;
import com.yowyob.template.domain.model.SubjectType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ReactionUseCase {
    Mono<Reaction> addReaction(UUID actorId, UUID targetId, SubjectType targetType, ReactionType type);
    Mono<Void> removeReaction(UUID actorId, UUID targetId, ReactionType type);
    Flux<Reaction> getReactionsForTarget(UUID targetId, SubjectType targetType);
}
