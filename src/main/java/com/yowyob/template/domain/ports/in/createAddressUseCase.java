package com.yowyob.template.domain.ports.in;



import com.yowyob.template.domain.model.Address;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface createAddressUseCase {
    Mono<Address> createAddress(Address address);
    Mono<Address> getAddressById(UUID id);
    Flux<Address> getAllAddresses();
    Mono<Address> updateAddress(UUID id, Address address);
    Mono<Void> deleteAddress(UUID id);
}
