package com.yowyob.template.infrastructure.adapters.outbound.persistence;

import com.yowyob.template.domain.model.Reaction;
import com.yowyob.template.domain.model.ReactionType;
import com.yowyob.template.domain.model.SubjectType;
import com.yowyob.template.domain.ports.out.ReactionRepositoryPort;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.repository.ReactionR2dbcRepository;
import com.yowyob.template.infrastructure.mappers.ReactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReactionR2dbcAdapter implements ReactionRepositoryPort {
    private final ReactionR2dbcRepository repository;
    private final ReactionMapper mapper;

    @Override
    public Mono<Reaction> save(Reaction reaction) {
        return repository.save(mapper.toEntity(reaction))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Reaction> findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Reaction> findByActorIdAndTargetIdAndTargetType(UUID actorId, UUID targetId, SubjectType targetType) {
        return repository.findByActorIdAndTargetIdAndTargetType(actorId, targetId, targetType)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Reaction> findByActorId(UUID actorId) {
        return repository.findByActorIdOrderByCreatedAtDesc(actorId)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Reaction> findByTargetIdAndTargetType(UUID targetId, SubjectType targetType) {
        return repository.findByTargetIdAndTargetTypeOrderByCreatedAtDesc(targetId, targetType)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<Void> deleteByActorIdAndTargetIdAndType(UUID actorId, UUID targetId, ReactionType type) {
        return repository.deleteByActorIdAndTargetIdAndType(actorId, targetId, type);
    }
}
