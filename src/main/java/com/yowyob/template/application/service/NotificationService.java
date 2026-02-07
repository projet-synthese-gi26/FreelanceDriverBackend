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
 * SERVICE DE NOTIFICATION - FOCUS WHATSAPP (Template ID 5)
 * 
 * RESPONSABILITÉS :
 * 1. Préparer les données pour le Template WhatsApp validé (ID: 5).
 * 2. Déclencher l'envoi via le canal WhatsApp uniquement.
 * 3. Tracer chaque envoi avec un FlowID unique et un rendu JSON.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationGatewayPort notificationGateway;
    private final ObjectMapper objectMapper;

    private static final String LOG_PREFIX = "[NOTIF-WHATSAPP-SVC]";

    // L'ID du template que vous avez créé et testé avec succès sur Swagger
    private static final Integer VALIDATED_WHATSAPP_TEMPLATE_ID = 5;

    @PostConstruct
    public void init() {
        // Configuration de l'ObjectMapper pour des logs lisibles en console
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    // ============================================================================================
    // 1. ALERTE DE CONFIRMATION (DÉCLENCHÉ SUR STATUT "Confirmed")
    // ============================================================================================

    /**
     * Envoie une alerte WhatsApp au destinataire pour confirmer le trajet.
     */
    public Mono<Void> sendRideConfirmedAlert(UUID recipientUserId, String recipientName, String email, String phone, String rideTitle, String destination) {
        String fid = generateFlowId("CONF-WA");
        
        log.info("{} ╔══════════════════════════════════════════════════════════════════════════", LOG_PREFIX);
        log.info("{} ║ [{}] STARTING WHATSAPP NOTIFICATION: RIDE_CONFIRMED", LOG_PREFIX, fid);
        log.info("{} ║ Recipient: {} | Phone: {} | Template: {}", LOG_PREFIX, recipientName, phone, VALIDATED_WHATSAPP_TEMPLATE_ID);

        // Préparation des variables du template (doivent correspondre aux {{variables}} du template 5)
        Map<String, Object> data = new HashMap<>();
        data.put("userName", recipientName);
        data.put("rideTitle", rideTitle);
        data.put("destination", destination);
        
        logJson(fid, "WHATSAPP_DATA_PAYLOAD", data);

        // Envoi immédiat via le canal WHATSAPP
        return notificationGateway.sendImmediate(
                    NotificationType.WHATSAPP, 
                    VALIDATED_WHATSAPP_TEMPLATE_ID, 
                    List.of(phone), 
                    data
                )
                .doOnSuccess(v -> log.info("{} [{}] ✅ WhatsApp confirmation sent successfully to {}", LOG_PREFIX, fid, phone))
                .onErrorResume(e -> {
                    log.error("{} [{}] ❌ WhatsApp confirmation FAILED: {}", LOG_PREFIX, fid, e.getMessage());
                    return Mono.empty(); // On ne bloque pas le flux métier si la notif échoue
                })
                .doOnTerminate(() -> {
                    log.info("{} [{}] <<< END WHATSAPP NOTIFICATION PROCESS", LOG_PREFIX, fid);
                    log.info("{} ╚══════════════════════════════════════════════════════════════════════════", LOG_PREFIX);
                });
    }

    // ============================================================================================
    // 2. ALERTE DE PAIEMENT (DÉCLENCHÉ SUR STATUT "Terminated")
    // ============================================================================================

    /**
     * Notifie le chauffeur via WhatsApp qu'une commission a été prélevée.
     */
    public Mono<Void> sendCommissionDeductedAlert(UUID driverUserId, String driverName, String phone, String amount) {
        String fid = generateFlowId("PAY-WA");
        
        log.info("{} ╔══════════════════════════════════════════════════════════════════════════", LOG_PREFIX);
        log.info("{} ║ [{}] STARTING WHATSAPP NOTIFICATION: PAYMENT_COMMISSION", LOG_PREFIX, fid);
        log.info("{} ║ Driver: {} | Phone: {} | Amount: {}", LOG_PREFIX, driverName, phone, amount);

        // Données pour le template
        Map<String, Object> data = new HashMap<>();
        data.put("userName", driverName);
        data.put("amount", amount);
        // Note: Si le template 5 est utilisé, rideTitle et destination seront vides dans le message
        data.put("rideTitle", "Commission Trajet"); 
        data.put("destination", "Portefeuille");

        logJson(fid, "WHATSAPP_PAYMENT_PAYLOAD", data);

        return notificationGateway.sendImmediate(
                    NotificationType.WHATSAPP, 
                    VALIDATED_WHATSAPP_TEMPLATE_ID, 
                    List.of(phone), 
                    data
                )
                .doOnSuccess(v -> log.info("{} [{}] ✅ Payment notification sent successfully to {}", LOG_PREFIX, fid, phone))
                .onErrorResume(e -> {
                    log.error("{} [{}] ❌ Payment notification FAILED: {}", LOG_PREFIX, fid, e.getMessage());
                    return Mono.empty();
                })
                .doOnTerminate(() -> {
                    log.info("{} [{}] <<< END PAYMENT NOTIFICATION PROCESS", LOG_PREFIX, fid);
                    log.info("{} ╚══════════════════════════════════════════════════════════════════════════", LOG_PREFIX);
                });
    }

    // ============================================================================================
    // HELPERS (TRAÇABILITÉ)
    // ============================================================================================

    private String generateFlowId(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void logJson(String fid, String label, Object obj) {
        try {
            String json = objectMapper.writeValueAsString(obj);
            log.info("{} [{}] {}:\n{}", LOG_PREFIX, fid, label, json);
        } catch (JsonProcessingException e) {
            log.error("{} [{}] Logging error: {}", LOG_PREFIX, fid, e.getMessage());
        }
    }
}