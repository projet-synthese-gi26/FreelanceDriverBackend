package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.User;
import com.yowyob.template.domain.ports.in.UserUseCase;
import com.yowyob.template.domain.ports.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {
    private final UserRepositoryPort userRepository;

    @Override
    public Mono<User> updateUserProfile(UUID id, User user) {
        return userRepository.updateUser(id, user);
    }

    @Override
    public Mono<Void> changePassword(UUID id, String currentPassword, String newPassword) {
        return userRepository.updatePassword(id, currentPassword, newPassword);
    }

    @Override
    public Mono<User> updateProfilePicture(UUID id, FilePart file) {
        return userRepository.updatePicture(id, file);
    }
}
