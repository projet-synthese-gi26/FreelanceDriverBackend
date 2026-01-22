package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.Address;
import com.yowyob.template.domain.ports.in.createAddressUseCase;
import com.yowyob.template.domain.ports.out.AddressRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressService implements createAddressUseCase {
    private final AddressRepositoryPort repository;

    @Override
    public Mono<Address> createAddress(Address address) {
        java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
        Address toSave = Address.builder()
                .id(address.getId())
                .addressableType(address.getAddressableType())
                .type(address.getType())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .locality(address.getLocality())
                .zipCode(address.getZipCode())
                .postalCode(address.getPostalCode())
                .poBox(address.getPoBox())
                .isDefault(address.getIsDefault())
                .neighborhood(address.getNeighborhood())
                .informalDescription(address.getInformalDescription())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .createdAt(address.getCreatedAt() != null ? address.getCreatedAt() : now)
                .updatedAt(address.getUpdatedAt() != null ? address.getUpdatedAt() : now)
                .deletedAt(address.getDeletedAt())
                .build();
        return repository.save(toSave);
    }

    @Override
    public Mono<Address> getAddressById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Flux<Address> getAllAddresses() {
        return repository.findAll();
    }

    @Override
    public Mono<Address> updateAddress(UUID id, Address address) {
        return repository.findById(id)
                .flatMap(existing -> {
                    Address updated = Address.builder()
                            .id(id)
                            .addressableType(address.getAddressableType() != null ? address.getAddressableType() : existing.getAddressableType())
                            .type(address.getType() != null ? address.getType() : existing.getType())
                            .addressLine1(address.getAddressLine1() != null ? address.getAddressLine1() : existing.getAddressLine1())
                            .addressLine2(address.getAddressLine2() != null ? address.getAddressLine2() : existing.getAddressLine2())
                            .city(address.getCity() != null ? address.getCity() : existing.getCity())
                            .state(address.getState() != null ? address.getState() : existing.getState())
                            .locality(address.getLocality() != null ? address.getLocality() : existing.getLocality())
                            .zipCode(address.getZipCode() != null ? address.getZipCode() : existing.getZipCode())
                            .postalCode(address.getPostalCode() != null ? address.getPostalCode() : existing.getPostalCode())
                            .poBox(address.getPoBox() != null ? address.getPoBox() : existing.getPoBox())
                            .isDefault(address.getIsDefault() != null ? address.getIsDefault() : existing.getIsDefault())
                            .neighborhood(address.getNeighborhood() != null ? address.getNeighborhood() : existing.getNeighborhood())
                            .informalDescription(address.getInformalDescription() != null ? address.getInformalDescription()
                                    : existing.getInformalDescription())
                            .latitude(address.getLatitude() != null ? address.getLatitude() : existing.getLatitude())
                            .longitude(address.getLongitude() != null ? address.getLongitude() : existing.getLongitude())
                            .createdAt(existing.getCreatedAt())
                            .updatedAt(new java.sql.Timestamp(System.currentTimeMillis()))
                            .deletedAt(address.getDeletedAt() != null ? address.getDeletedAt() : existing.getDeletedAt())
                            .build();
                    return repository.save(updated);
                });
    }

    @Override
    public Mono<Void> deleteAddress(UUID id) {
        return repository.deleteById(id);
    }
}
