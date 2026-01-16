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
        Address toSave = new Address(
                address.id(),
                address.addressableType(),
                address.type(),
                address.addressLine1(),
                address.addressLine2(),
                address.city(),
                address.state(),
                address.locality(),
                address.zipCode(),
                address.postalCode(),
                address.poBox(),
                address.isDefault(),
                address.neighborhood(),
                address.informalDescription(),
                address.latitude(),
                address.longitude(),
                address.createdAt() != null ? address.createdAt() : now,
                address.updatedAt() != null ? address.updatedAt() : now,
                address.deletedAt());
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
                    Address updated = new Address(
                            id,
                            address.addressableType() != null ? address.addressableType() : existing.addressableType(),
                            address.type() != null ? address.type() : existing.type(),
                            address.addressLine1() != null ? address.addressLine1() : existing.addressLine1(),
                            address.addressLine2() != null ? address.addressLine2() : existing.addressLine2(),
                            address.city() != null ? address.city() : existing.city(),
                            address.state() != null ? address.state() : existing.state(),
                            address.locality() != null ? address.locality() : existing.locality(),
                            address.zipCode() != null ? address.zipCode() : existing.zipCode(),
                            address.postalCode() != null ? address.postalCode() : existing.postalCode(),
                            address.poBox() != null ? address.poBox() : existing.poBox(),
                            address.isDefault() != null ? address.isDefault() : existing.isDefault(),
                            address.neighborhood() != null ? address.neighborhood() : existing.neighborhood(),
                            address.informalDescription() != null ? address.informalDescription()
                                    : existing.informalDescription(),
                            address.latitude() != null ? address.latitude() : existing.latitude(),
                            address.longitude() != null ? address.longitude() : existing.longitude(),
                            address.createdAt() != null ? address.createdAt() : existing.createdAt(),
                            address.updatedAt() != null ? address.updatedAt() : existing.updatedAt(),
                            address.deletedAt() != null ? address.deletedAt() : existing.deletedAt());
                    return repository.save(updated);
                });
    }

    @Override
    public Mono<Void> deleteAddress(UUID id) {
        return repository.deleteById(id);
    }
}
