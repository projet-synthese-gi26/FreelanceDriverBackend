package com.yowyob.template.application.service;

import com.yowyob.template.domain.model.User;
import com.yowyob.template.domain.ports.out.UserRepositoryPort;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.FullOnboardingRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.OtpRegisterVerifyRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.RegisterInitRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.OffsetDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthRegistrationService {
    private static final int OTP_TTL_MINUTES = 5;

    private final UserRepositoryPort userRepository;
    private final OtpVerificationService otpVerificationService;
    private final OnboardingService onboardingService;
    private final JavaMailSender mailSender;

    public Mono<Void> registerInit(RegisterInitRequest request) {
        return findUserByEmail(request.email())
                .flatMap(existing -> Mono.<Void>error(new IllegalArgumentException("Email already in use")))
                .switchIfEmpty(Mono.defer(() -> {
                    String otp = generateOtp();
                    OffsetDateTime expiresAt = OffsetDateTime.now().plusMinutes(OTP_TTL_MINUTES);
                    return otpVerificationService.saveOtp(request.email(), otp, expiresAt)
                            .flatMap(saved -> sendOtpEmail(request.email(), otp))
                            .then();
                }));
    }

    public Mono<UserProfileResponse> verifyOtpAndRegister(OtpRegisterVerifyRequest request) {
        return otpVerificationService.verifyOtp(request.email(), request.otp())
                .then(onboardingService.registerAndOnboard(buildOnboardingRequest(request)));
    }

    private Mono<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> Mono.empty());
    }

    private String generateOtp() {
        int code = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return String.valueOf(code);
    }

    private Mono<Void> sendOtpEmail(String email, String otp) {
        return Mono.fromRunnable(() -> {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo(email);
                    message.setSubject("Your verification code");
                    message.setText("Your OTP is: " + otp + ". It expires in " + OTP_TTL_MINUTES + " minutes.");
                    mailSender.send(message);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(ex -> {
                    log.error("Failed to send OTP email to {}", email, ex);
                    return Mono.error(new IllegalArgumentException("Failed to send OTP"));
                })
                .then();
    }

    private FullOnboardingRequest buildOnboardingRequest(OtpRegisterVerifyRequest request) {
        FullOnboardingRequest onboardingRequest = new FullOnboardingRequest();
        onboardingRequest.setEmail(request.email());
        onboardingRequest.setUsername(request.email());
        onboardingRequest.setPassword(request.password());
        onboardingRequest.setFirstName(request.firstName());
        onboardingRequest.setLastName(request.lastName());
        onboardingRequest.setPhone(request.phone());
        onboardingRequest.setRoleType(request.role().toUpperCase());
        onboardingRequest.setOrganisationName(request.organisationName());
        onboardingRequest.setOrganisationDescription(request.organisationDescription());
        onboardingRequest.setTitle(request.title());
        onboardingRequest.setAddress(request.address());
        return onboardingRequest;
    }
}
