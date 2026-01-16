package com.yowyob.template.infrastructure.adapters.outbound.persistence;

import com.yowyob.template.domain.model.Settings;
import com.yowyob.template.domain.ports.out.SettingsRepositoryPort;
import com.yowyob.template.infrastructure.adapters.outbound.persistence.repository.SettingsR2dbcRepository;
import com.yowyob.template.infrastructure.mappers.SettingsMapper;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SettingsR2dbcAdapter implements SettingsRepositoryPort {

    private final SettingsR2dbcRepository repository;
    private final SettingsMapper mapper;

    @Override
    public Mono<Settings> save(Settings settings) {
        return repository.save(mapper.toEntity(settings))
                .map(mapper::toDomain);
    }


    @Override
    public Flux<Settings> findAll() {
        return repository.findAll()
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteAll() {
        return repository.deleteAll();
    }

    @Override
    public Mono<Settings> findById(UUID id) {
       return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }
}