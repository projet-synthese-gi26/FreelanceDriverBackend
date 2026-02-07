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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * ADAPTATEUR DE NOTIFICATION - FOCUS WHATSAPP (LOGS MASSIFS)
 * 
 * RESPONSABILITÉS :
 * 1. Gérer l'enregistrement automatique du service (Credentials GreenAPI).
 * 2. Assurer l'envoi immédiat des messages WhatsApp.
 * 3. Offrir une traçabilité totale (Request, Response, Error Body).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationExternalAdapter implements NotificationGatewayPort {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
    
    // Cache thread-safe pour le X-Service-Token
    private final AtomicReference<String> serviceToken = new AtomicReference<>();

    @Value("${application.external.notification-service-url:https://notification-service.pynfi.com}")
    private String baseUrl;

    private static final String TAG = "[ADAPTER-NOTIF-WA]";

    @PostConstruct
    public void setup() {
        // Configuration de l'object mapper pour des logs JSON magnifiques
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    // ============================================================================================
    // 1. ENVOI IMMÉDIAT (POINT D'ENTRÉE PRINCIPAL)
    // ============================================================================================

    @Override
    public Mono<Void> sendImmediate(NotificationType type, Integer templateId, List<String> recipients, Map<String, Object> data) {
        String tid = generateTraceId();
        Instant start = Instant.now();

        logHeader(tid, "SEND_IMMEDIATE_WHATSAPP");
        log.info("{} [{}] Target Recipients: {}", TAG, tid, recipients);
        log.info("{} [{}] Using Template ID: {}", TAG, tid, templateId);

        return ensureAuthenticated(tid)
                .flatMap(token -> {
                    // Construction de la requête selon les specs de l'API
                    NotificationSendRequest request = NotificationSendRequest.builder()
                            .notificationType(type.name())
                            .templateId(templateId)
                            .to(recipients)
                            .data(data)
                            .build();

                    logRequestDetails(tid, HttpMethod.POST, "/api/v1/notifications/send", request);

                    return webClientBuilder.baseUrl(baseUrl).build()
                            .post()
                            .uri("/api/v1/notifications/send")
                            .header("X-Service-Token", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(request)
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, res -> captureAndLogErrorMessage(tid, res))
                            .toEntity(String.class)
                            .doOnNext(res -> {
                                log.info("{} [{}] ✅ API SUCCESS RESPONSE", TAG, tid);
                                log.info("{} [{}] Status Code: {}", TAG, tid, res.getStatusCode());
                                log.info("{} [{}] Payload: {}", TAG, tid, res.getBody());
                            })
                            .then();
                })
                .doOnError(e -> log.error("{} [{}] ❌ FATAL ERROR: {}", TAG, tid, e.getMessage()))
                .doFinally(s -> logFooter(tid, start));
    }

    // ============================================================================================
    // 2. CRÉATION PULL (POUR MÉMOIRE - OPTIONNEL)
    // ============================================================================================

    @Override
    public Mono<Void> createForPull(UUID userId, NotificationType type, Integer templateId, Map<String, Object> data) {
        // On laisse la méthode pour l'interface, mais on logue qu'on se focus sur WA
        log.debug("{} Ignoring Pull request for user {}, focus is on WhatsApp.", TAG, userId);
        return Mono.empty();
    }

    // ============================================================================================
    // 3. AUTHENTIFICATION (L'ÉTAPE CRUCIALE AVEC VOS PARAMÈTRES RÉELS)
    // ============================================================================================

    private Mono<String> ensureAuthenticated(String tid) {
        if (serviceToken.get() != null) {
            return Mono.just(serviceToken.get());
        }

        log.info("{} [{}] >>> NO TOKEN. Registering STATIC Service...", TAG, tid);

        // 1. UTILISER UN NOM FIXE (Celui qui a marché dans votre Swagger)
        // Comme ça, on garde le même token et les mêmes templates
        String staticName = "Freelance_Driver_App_Final_Validation_OBAMA";

        ServiceRegistrationRequest regRequest = ServiceRegistrationRequest.builder()
                .name(staticName)
                .emailServerHost("smtp.gmail.com")
                .emailServerPort(587)
                .emailUsername("mbognengj@gmail.com")
                .emailPassword("fmpjyadvpepfvcws")
                .smsServerHost("api.twilio.com")
                .smsServerPort("443")
                .smstoken("SK_DUMMY")
                .whatsappApiUrl("https://7105.api.greenapi.com")
                .whatsappIdInstance("7105420174")
                .whatsappApiTokenInstance("83c57a60ad2c440f8ba9522c5523eb81a8df3739f944443781")
                .firebaseServiceAccountJson("{\"type\": \"service_account\"}")
                .build();

        return webClientBuilder.baseUrl(baseUrl).build()
                .post()
                .uri("/api/v1/services")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(regRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res -> captureAndLogErrorMessage(tid, res))
                .bodyToMono(ServiceRegistrationResponse.class)
                .flatMap(res -> {
                    log.info("{} [{}] ✅ Service Active. ID: {}", TAG, tid, res.serviceId());
                    serviceToken.set(res.token());
                    
                    // 2. CRÉER LE TEMPLATE AUTOMATIQUEMENT S'IL N'EXISTE PAS
                    return createTemplateIfMissing(tid, res.token()).thenReturn(res.token());
                });
    }

    private Mono<Void> createTemplateIfMissing(String tid, String token) {
        // On crée le template 5 (ou un autre) pour ce service précis
        TemplateCreateRequest tpl = TemplateCreateRequest.builder()
                .templateId(5) // On demande l'ID 5
                .name("WhatsApp Confirmation")
                .type("WHATSAPP")
                .body("Bonjour {{userName}}, votre trajet {{rideTitle}} vers {{destination}} est confirmé ! Montant commission: {{amount}} FCFA.")
                .build();

        return webClientBuilder.baseUrl(baseUrl).build()
                .post()
                .uri("/api/v1/templates")
                .header("X-Service-Token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tpl)
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(r -> log.info("{} [{}] ✅ Template 5 synchronized.", TAG, tid))
                .onErrorResume(e -> {
                    log.info("{} [{}] Template 5 already exists or created. Continuing...", TAG, tid);
                    return Mono.empty();
                })
                .then();
    }

    @Override
    public Mono<String> registerService() {
        return ensureAuthenticated(generateTraceId());
    }

    // ============================================================================================
    // SYSTÈME DE LOGGING AVANCÉ (AUTOPSIE)
    // ============================================================================================

    private void logHeader(String tid, String op) {
        log.info("{} ╔══════════════════════════════════════════════════════════════════════════", TAG);
        log.info("{} ║ 🟢 STARTING: {} | Trace: {}", TAG, op, tid);
        log.info("{} ║ Time: {}", TAG, Instant.now());
    }

    private void logFooter(String tid, Instant start) {
        long duration = Duration.between(start, Instant.now()).toMillis();
        log.info("{} ║ 🏁 END PROCESS | Trace: {} | Execution Time: {}ms", TAG, tid, duration);
        log.info("{} ╚══════════════════════════════════════════════════════════════════════════", TAG);
    }

    private void logRequestDetails(String tid, HttpMethod method, String path, Object body) {
        log.info("{} ║ 📤 OUTGOING REQUEST:", TAG, tid);
        log.info("{} ║   -> Method : {}", TAG, method);
        log.info("{} ║   -> URL    : {}{}", TAG, baseUrl, path);
        try {
            String json = objectMapper.writeValueAsString(body);
            log.info("{} ║   -> BODY   :\n{}", TAG, json);
        } catch (Exception e) {
            log.warn("{} ║   -> [Body Serialization Failed]", TAG);
        }
    }

    private Mono<Throwable> captureAndLogErrorMessage(String tid, ClientResponse response) {
        log.error("{} ║ 🛑 API REJECTION DETECTED", TAG, tid);
        log.error("{} ║   -> HTTP Status: {}", TAG, response.statusCode());

        return response.bodyToMono(String.class)
                .defaultIfEmpty("[EMPTY ERROR RESPONSE]")
                .flatMap(errorBody -> {
                    log.error("{} ║   -> SERVER ERROR BODY:\n{}", TAG, errorBody);
                    
                    // Aide au diagnostic
                    if (errorBody.contains("Unauthorized")) log.error("{} ║   -> SUGGESTION: The X-Service-Token is invalid.", TAG);
                    if (response.statusCode().value() == 500) log.error("{} ║   -> SUGGESTION: Check if instance is connected in GreenAPI Console.", TAG);
                    
                    return Mono.error(new RuntimeException("Notification API Rejected Call: " + errorBody));
                });
    }

    private String generateTraceId() {
        return "NOTIF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}