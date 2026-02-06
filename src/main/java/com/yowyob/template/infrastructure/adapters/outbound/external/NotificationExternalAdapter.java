package com.yowyob.template.infrastructure.adapters.outbound.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.yowyob.template.domain.model.NotificationType;
import com.yowyob.template.domain.ports.out.NotificationGatewayPort;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.notification.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * ADAPTATEUR DE NOTIFICATION - VERSION AUTOPSIE (DIAGNOSTIC TOTAL)
 * 
 * Ce composant trace chaque détail de la communication avec l'API externe.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationExternalAdapter implements NotificationGatewayPort {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
    
    // Stockage du token en mémoire
    private final AtomicReference<String> serviceToken = new AtomicReference<>();

    @Value("${application.external.notification-service-url}")
    private String baseUrl;

    @Value("${application.external.notification-app-name:Freelance_Driver_App_Diagnostic}")
    private String appName;

    private static final String TAG = "[NOTIF-DEBUG]";

    @PostConstruct
    public void init() {
        // Force l'affichage JSON propre dans la console
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    // ============================================================================================
    // 1. ENVOI IMMÉDIAT (WhatsApp / Email)
    // ============================================================================================

    @Override
    public Mono<Void> sendImmediate(NotificationType type, Integer templateId, List<String> recipients, Map<String, Object> data) {
        String tid = generateTraceId();
        logHeader(tid, "SEND_IMMEDIATE");

        return ensureAuthenticated(tid)
                .flatMap(token -> {
                    NotificationSendRequest request = NotificationSendRequest.builder()
                            .notificationType(type.name())
                            .templateId(templateId)
                            .to(recipients)
                            .data(data)
                            .build();

                    logRequest(tid, HttpMethod.POST, "/api/v1/notifications/send", request);

                    return webClientBuilder.baseUrl(baseUrl).build()
                            .post()
                            .uri("/api/v1/notifications/send")
                            .header("X-Service-Token", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(request)
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, res -> logAndInterrogateError(tid, res))
                            .toBodilessEntity()
                            .doOnNext(res -> log.info("{} [{}] ✅ SUCCESS: Message Accepted (Status: {})", TAG, tid, res.getStatusCode()))
                            .then();
                })
                .doOnError(e -> log.error("{} [{}] 💥 CRITICAL FAILURE: {}", TAG, tid, e.getMessage()))
                .doFinally(s -> logFooter(tid));
    }

    // ============================================================================================
    // 2. CRÉATION POUR PULL (Historique App)
    // ============================================================================================

    @Override
    public Mono<Void> createForPull(UUID userId, NotificationType type, Integer templateId, Map<String, Object> data) {
        String tid = generateTraceId();
        logHeader(tid, "CREATE_PULL");

        return ensureAuthenticated(tid)
                .flatMap(token -> {
                    NotificationPullRequest request = NotificationPullRequest.builder()
                            .notificationType(type.name())
                            .templateId(templateId)
                            .userId(userId)
                            .data(data)
                            .build();

                    logRequest(tid, HttpMethod.POST, "/api/v1/notifications", request);

                    return webClientBuilder.baseUrl(baseUrl).build()
                            .post()
                            .uri("/api/v1/notifications")
                            .header("X-Service-Token", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(request)
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, res -> logAndInterrogateError(tid, res))
                            .toBodilessEntity()
                            .doOnNext(res -> log.info("{} [{}] ✅ SUCCESS: Pull Notif Created", TAG, tid))
                            .then();
                })
                .doFinally(s -> logFooter(tid));
    }

    // ============================================================================================
    // 3. AUTHENTIFICATION & ENREGISTREMENT (CIBLE DU BUG 500)
    // ============================================================================================

    private Mono<String> ensureAuthenticated(String tid) {
        if (serviceToken.get() != null) {
            return Mono.just(serviceToken.get());
        }

        log.info("{} [{}] >>> NO TOKEN. Starting Registration with FULL BODY...", TAG, tid);

        // Nom unique pour éviter les collisions
        String uniqueName = appName + "_" + UUID.randomUUID().toString().substring(0, 5);

        // Corps complet pour satisfaire la validation stricte de l'API externe
        ServiceRegistrationRequest regRequest = ServiceRegistrationRequest.builder()
                .name(uniqueName)
                // Configuration Email (Réelle)
                .emailServerHost("smtp.gmail.com")
                .emailServerPort(587)
                .emailUsername("mbognengj@gmail.com")
                .emailPassword("fmpjyadvpepfvcws")
                // Configuration SMS (Dummy - Requis pour éviter Erreur 500)
                .smsServerHost("api.twilio.com")
                .smsServerPort("443")
                .smstoken("SK_DUMMY_TOKEN_123456")
                // Configuration WhatsApp (Dummy - Requis)
                .whatsappApiUrl("https://7103.api.greenapi.com/")
                .whatsappIdInstance("1101123456")
                .whatsappApiTokenInstance("dummy-token-wa-123456")
                // Configuration Firebase (JSON Dummy - Requis)
                .firebaseServiceAccountJson("{\"type\": \"service_account\", \"project_id\": \"dummy-id\"}")
                .build();

        logRequest(tid, HttpMethod.POST, "/api/v1/services", regRequest);

        return webClientBuilder.baseUrl(baseUrl).build()
                .post()
                .uri("/api/v1/services")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(regRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res -> logAndInterrogateError(tid, res))
                .bodyToMono(ServiceRegistrationResponse.class)
                .flatMap(res -> {
                    log.info("{} [{}] ✅ REGISTRATION SUCCESS. ServiceID: {}, Token received.", TAG, tid, res.serviceId());
                    serviceToken.set(res.token());
                    // Création des templates une fois le service enregistré
                    return createTemplates(tid, res.token()).thenReturn(res.token());
                })
                .doOnError(e -> log.error("{} [{}] 💥 FINAL REGISTRATION ATTEMPT FAILED: {}", TAG, tid, e.getMessage()));
    }

    private Mono<Void> createTemplates(String tid, String token) {
        log.info("{} [{}] Initializing Templates...", TAG, tid);

        List<TemplateCreateRequest> tpls = List.of(
            TemplateCreateRequest.builder().templateId(101).type("EMAIL").name("ConfEmail").subject("Ride Confirmed").bodyHtml("Hi {{userName}}, ride {{rideTitle}} is confirmed.").build(),
            TemplateCreateRequest.builder().templateId(102).type("WHATSAPP").name("ConfWA").body("Hi {{userName}}, ride {{rideTitle}} to {{destination}} is confirmed!").build(),
            TemplateCreateRequest.builder().templateId(103).type("WHATSAPP").name("PayWA").body("Hi {{userName}}, commission of {{amount}} FCFA deducted.").build(),
            TemplateCreateRequest.builder().templateId(104).type("PULL").name("AppNotif").body("Ride {{rideTitle}} updated.").build()
        );

        return Flux.fromIterable(tpls)
                .flatMap(t -> {
                    log.info("{} [{}] Creating Template {} ({})", TAG, tid, t.templateId(), t.type());
                    return webClientBuilder.baseUrl(baseUrl).build()
                            .post()
                            .uri("/api/v1/templates")
                            .header("X-Service-Token", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(t)
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, r -> Mono.empty()) // Silencieux si déjà existant
                            .toBodilessEntity();
                }).then();
    }

    @Override
    public Mono<String> registerService() {
        return ensureAuthenticated(generateTraceId());
    }

    // ============================================================================================
    // SYSTÈME D'AUTOPSIE (LOGS RICHES)
    // ============================================================================================

    private void logHeader(String tid, String op) {
        log.info("{} ╔══════════════════════════════════════════════════════════════════════════", TAG);
        log.info("{} ║ PROCESS: {} | TRACE: {}", TAG, op, tid);
    }

    private void logFooter(String tid) {
        log.info("{} ║ END PROCESS | TRACE: {}", TAG, tid);
        log.info("{} ╚══════════════════════════════════════════════════════════════════════════", TAG);
    }

    private void logRequest(String tid, HttpMethod method, String path, Object body) {
        log.info("{} ║ OUTGOING REQUEST:", TAG, tid);
        log.info("{} ║   -> Method: {}", TAG, method);
        log.info("{} ║   -> URL: {}{}", TAG, baseUrl, path);
        try {
            String json = objectMapper.writeValueAsString(body);
            log.info("{} ║   -> BODY PAYLOAD:\n{}", TAG, json);
        } catch (Exception e) {
            log.warn("{} ║   -> [Serialization Failed]", TAG);
        }
    }

    /**
     * Intercepte la réponse d'erreur et lit le Body pour le logger avant de lancer l'exception.
     */
    private Mono<Throwable> logAndInterrogateError(String tid, ClientResponse response) {
        log.error("{} ║ 🛑 API REJECTION DETECTED", TAG, tid);
        log.error("{} ║   -> HTTP Status: {}", TAG, response.statusCode());

        return response.bodyToMono(String.class)
                .defaultIfEmpty("[EMPTY RESPONSE BODY]")
                .flatMap(body -> {
                    log.error("{} ║   -> SERVER RESPONSE BODY:\n{}", TAG, body);
                    
                    // Analyse automatique
                    if (body.contains("already exists")) log.error("{} ║   -> ANALYSIS: Unique Constraint Violation on Server.", TAG);
                    if (response.statusCode().value() == 500) log.error("{} ║   -> ANALYSIS: Internal Server Crash (Professor's API issue).", TAG);

                    return Mono.error(new RuntimeException("Notification Service Error: " + body));
                });
    }

    private String generateTraceId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}