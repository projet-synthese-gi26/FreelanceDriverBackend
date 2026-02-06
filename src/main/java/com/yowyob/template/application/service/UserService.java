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
    public Mono<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public Mono<User> updateUserProfile(UUID id, User user, String jwtToken) {
        return userRepository.updateUser(id, user, jwtToken);
    }

    @Override
    public Mono<Void> changePassword(UUID id, String currentPassword, String newPassword, String jwtToken) {
        return userRepository.updatePassword(id, currentPassword, newPassword, jwtToken);
    }

    @Override
    public Mono<User> updateProfilePicture(UUID id, FilePart file, String jwtToken) {
        return userRepository.updatePicture(id, file, jwtToken);
    }
}
