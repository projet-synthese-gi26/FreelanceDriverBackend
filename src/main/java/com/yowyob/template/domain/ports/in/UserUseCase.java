package com.yowyob.template.domain.ports.in;

import com.yowyob.template.domain.model.User;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserUseCase {
    Mono<User> updateUserProfile(UUID id, User user);
    Mono<Void> changePassword(UUID id, String currentPassword, String newPassword);
    Mono<User> updateProfilePicture(UUID id, FilePart file);
}
