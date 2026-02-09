package com.yowyob.template.infrastructure.adapters.inbound.rest;

import com.yowyob.template.application.service.AppNotificationService;
import com.yowyob.template.domain.model.AppNotification;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.MarkNotificationReadRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Centre de notifications (PULL) + stream (push in-app)")
public class AppNotificationController {

    private final AppNotificationService service;

    @GetMapping("/me")
    @Operation(summary = "Lister mes notifications")
    public Flux<AppNotification> me(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        UUID userId = UUID.fromString(principal.getAttribute("sub"));
        return service.myNotifications(userId);
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Marquer une notification lue/non lue")
    public Mono<AppNotification> markRead(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            @PathVariable("id") UUID id,
            @Valid @RequestBody MarkNotificationReadRequest request
    ) {
        UUID userId = UUID.fromString(principal.getAttribute("sub"));
        return service.markRead(userId, id, request.read());
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Stream temps réel (SSE)")
    public Flux<AppNotification> stream(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        UUID userId = UUID.fromString(principal.getAttribute("sub"));
        return service.stream(userId);
    }
}
