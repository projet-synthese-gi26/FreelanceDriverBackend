package com.yowyob.template.infrastructure.adapters.outbound.persistence.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.yowyob.template.infrastructure.adapters.outbound.persistence.entity.AddressEntity;


public interface AddressR2dbcRepository extends ReactiveCrudRepository<AddressEntity, UUID>{}
