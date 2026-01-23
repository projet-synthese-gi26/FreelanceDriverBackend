package com.yowyob.template.domain.ports.in;

import com.yowyob.template.domain.model.Contact;
import reactor.core.publisher.Flux;
import java.util.UUID;

public interface GetContactsUseCase {
    Flux<Contact> getContacts(UUID contactableId);

    Flux<Contact> getContacts(UUID contactableId, String jwtToken);
}
