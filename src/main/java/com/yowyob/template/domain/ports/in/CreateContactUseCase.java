package com.yowyob.template.domain.ports.in;

import com.yowyob.template.domain.model.Contact;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface CreateContactUseCase {
	Mono<Contact> createContact(Contact contact);
	Mono<Contact> getContactById(UUID id);
	Flux<Contact> getAllContacts();
	Mono<Contact> updateContact(UUID id, Contact contact);
	Mono<Void> deleteContact(UUID id);
}
