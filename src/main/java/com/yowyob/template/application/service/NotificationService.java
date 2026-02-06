package com.yowyob.template.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.yowyob.template.domain.model.NotificationType;
import com.yowyob.template.domain.ports.out.NotificationGatewayPort;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * SERVICE DE NOTIFICATION - VERSION PUSH UNIQUEMENT (WhatsApp + Email)
 * 
 * Cette version a été épurée du mode PULL pour contourner les erreurs 500
 * du service externe et assurer la validation du flux métier.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationGatewayPort notificationGateway;
    private final ObjectMapper objectMapper;

    private static final String LOG_PREFIX = "[NOTIF-SERVICE]";

    // IDs des templates configurés
    private static final Integer TMPL_CONFIRMATION_EMAIL = 101;
    private static final Integer TMPL_CONFIRMATION_WHATSAPP = 102;
    private static final Integer TMPL_PAYMENT_WHATSAPP = 103;

    @PostConstruct
    public void init() {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    // ============================================================================================
    // 1. ALERTE DE CONFIRMATION (WhatsApp + Email)
    // ============================================================================================

    public Mono<Void> sendRideConfirmedAlert(UUID recipientUserId, String recipientName, String email, String phone, String rideTitle, String destination) {
        String fid = generateFlowId("CONF");
        
        log.info("{} ╔══════════════════════════════════════════════════════════════════════════", LOG_PREFIX);
        log.info("{} ║ [{}] STARTING PUSH NOTIFICATIONS (WA + EMAIL)", LOG_PREFIX, fid);

        Map<String, Object> data = new HashMap<>();
        data.put("userName", recipientName);
        data.put("rideTitle", rideTitle);
        data.put("destination", destination);
        
        logJson(fid, "TEMPLATE_DATA", data);

        // Canal WhatsApp
        Mono<Void> whatsapp = notificationGateway.sendImmediate(NotificationType.WHATSAPP, TMPL_CONFIRMATION_WHATSAPP, List.of(phone), data)
                .doOnSuccess(v -> log.info("{} [{}] ✅ WhatsApp alert sent.", LOG_PREFIX, fid))
                .onErrorResume(e -> {
                    log.error("{} [{}] ⚠️ WhatsApp failed but continuing: {}", LOG_PREFIX, fid, e.getMessage());
                    return Mono.empty();
                });

        // Canal Email
        Mono<Void> mail = notificationGateway.sendImmediate(NotificationType.EMAIL, TMPL_CONFIRMATION_EMAIL, List.of(email), data)
                .doOnSuccess(v -> log.info("{} [{}] ✅ Email sent.", LOG_PREFIX, fid))
                .onErrorResume(e -> {
                    log.error("{} [{}] ⚠️ Email failed but continuing: {}", LOG_PREFIX, fid, e.getMessage());
                    return Mono.empty();
                });

        return Mono.when(whatsapp, mail)
                .doOnTerminate(() -> {
                    log.info("{} [{}] <<< END PUSH NOTIFICATIONS", LOG_PREFIX, fid);
                    log.info("{} ╚══════════════════════════════════════════════════════════════════════════", LOG_PREFIX);
                });
    }

    // ============================================================================================
    // 2. ALERTE DE PAIEMENT (WhatsApp uniquement)
    // ============================================================================================

    public Mono<Void> sendCommissionDeductedAlert(UUID driverUserId, String driverName, String phone, String amount) {
        String fid = generateFlowId("PAY");
        
        log.info("{} [{}] >>> NOTIFYING DRIVER FOR PAYMENT: {}", LOG_PREFIX, fid, driverName);

        Map<String, Object> data = new HashMap<>();
        data.put("userName", driverName);
        data.put("amount", amount);

        // Envoi uniquement WhatsApp pour le paiement
        return notificationGateway.sendImmediate(NotificationType.WHATSAPP, TMPL_PAYMENT_WHATSAPP, List.of(phone), data)
                .doOnSuccess(v -> log.info("{} [{}] ✅ Payment WhatsApp sent to {}", LOG_PREFIX, fid, phone))
                .onErrorResume(e -> {
                    log.error("{} [{}] ⚠️ Payment notification failed: {}", LOG_PREFIX, fid, e.getMessage());
                    return Mono.empty();
                });
    }

    // ============================================================================================
    // HELPERS
    // ============================================================================================

    private String generateFlowId(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void logJson(String fid, String label, Object obj) {
        try {
            log.info("{} [{}] {}:\n{}", LOG_PREFIX, fid, label, objectMapper.writeValueAsString(obj));
        } catch (JsonProcessingException e) {
            log.error("{} [{}] Logging error", LOG_PREFIX, fid);
        }
    }
}