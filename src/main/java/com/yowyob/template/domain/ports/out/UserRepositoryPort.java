package com.yowyob.template.domain.ports.out;

import com.yowyob.template.domain.model.User;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepositoryPort {
    Mono<User> findById(UUID id);
    Mono<User> findById(UUID id, String jwtToken);
    Mono<User> findByEmail(String email);
    Mono<User> updateUser(UUID id, User user);
    Mono<User> updateUser(UUID id, User user, String jwtToken);
    Mono<Void> updatePassword(UUID id, String currentPassword, String newPassword);
    Mono<Void> updatePassword(UUID id, String currentPassword, String newPassword, String jwtToken);
    Mono<User> updatePicture(UUID id, FilePart file);
    Mono<User> updatePicture(UUID id, FilePart file, String jwtToken);
}
