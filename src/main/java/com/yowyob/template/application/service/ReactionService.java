package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.Reaction;
import com.yowyob.template.domain.model.ReactionType;
import com.yowyob.template.domain.model.SubjectType;
import com.yowyob.template.domain.ports.in.ReactionUseCase;
import com.yowyob.template.domain.ports.out.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReactionService implements ReactionUseCase {
    private final ReactionRepositoryPort repository;
    private final ProductRepositoryPort productRepository;
    private final BusinessActorRepositoryPort actorRepository;
    private final OrganisationRepositoryPort organisationRepository;
    private final ReviewRepositoryPort reviewRepository;

    @Override
    public Mono<Reaction> addReaction(UUID actorId, UUID targetId, SubjectType targetType, ReactionType type) {
        return verifyTargetExists(targetId, targetType)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new IllegalArgumentException("Target " + targetId + " of type " + targetType + " does not exist"));
                    }
                    Reaction reaction = Reaction.builder()
                            .id(UUID.randomUUID())
                            .actorId(actorId)
                            .targetId(targetId)
                            .targetType(targetType)
                            .type(type)
                            .createdAt(new Timestamp(System.currentTimeMillis()))
                            .build();
                    return repository.save(reaction);
                });
    }

    private Mono<Boolean> verifyTargetExists(UUID targetId, SubjectType targetType) {
        return switch (targetType) {
            case PRODUCT -> productRepository.findById(targetId).map(p -> true).defaultIfEmpty(false);
            case DRIVER, CLIENT -> actorRepository.findById(targetId).map(a -> true).defaultIfEmpty(false);
            case ORGANISATION -> organisationRepository.findById(targetId).map(o -> true).defaultIfEmpty(false);
            case REVIEW -> reviewRepository.findById(targetId).map(r -> true).defaultIfEmpty(false);
            default -> Mono.just(true); // Platforms or others assume always exist or handle later
        };
    }

    @Override
    public Mono<Void> removeReaction(UUID actorId, UUID targetId, ReactionType type) {
        return repository.deleteByActorIdAndTargetIdAndType(actorId, targetId, type);
    }

    @Override
    public Flux<Reaction> getReactionsForTarget(UUID targetId, SubjectType targetType) {
        return repository.findByTargetIdAndTargetType(targetId, targetType);
    }
}
