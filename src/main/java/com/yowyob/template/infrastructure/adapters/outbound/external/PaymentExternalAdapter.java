package com.yowyob.template.infrastructure.adapters.outbound.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yowyob.template.domain.model.PaymentTransaction;
import com.yowyob.template.domain.model.TransactionType;
import com.yowyob.template.domain.model.Wallet;
import com.yowyob.template.domain.ports.out.PaymentGatewayPort;
import com.yowyob.template.infrastructure.adapters.outbound.external.dto.payment.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Adaptateur de communication avec l'API externe de paiement.
 * AUCUN TOKEN n'est envoyé conformément aux contraintes.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentExternalAdapter implements PaymentGatewayPort {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${application.external.payment-service-url:https://payment-service.pynfi.com}")
    private String paymentServiceUrl;

    private static final String LOG_TAG = "[ADAPTER-PAY]";

    // --- Gestion des Wallets ---

    @Override
    public Mono<Wallet> createWallet(UUID ownerId, String ownerName) {
        String traceId = generateTraceId();
        String url = paymentServiceUrl + "/api/v1/wallets";
        ExternalWalletRequest request = new ExternalWalletRequest(ownerId, ownerName);

        logRequest(traceId, HttpMethod.POST, url, request);

        return webClientBuilder.baseUrl(paymentServiceUrl).build()
                .post()
                .uri("/api/v1/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res -> handleError(traceId, res))
                .bodyToMono(ExternalWalletResponse.class)
                .doOnNext(res -> logResponse(traceId, res))
                .map(this::mapToWallet)
                .doOnError(e -> log.error("{} [{}] ❌ Fatal Error: {}", LOG_TAG, traceId, e.getMessage()));
    }

    @Override
    public Mono<Wallet> getWalletByOwner(UUID ownerId) {
        String traceId = generateTraceId();
        String url = paymentServiceUrl + "/api/v1/wallets/owner/" + ownerId;

        logRequest(traceId, HttpMethod.GET, url, null);

        return webClientBuilder.baseUrl(paymentServiceUrl).build()
                .get()
                .uri("/api/v1/wallets/owner/{id}", ownerId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, res -> {
                    if (res.statusCode().value() == 404) {
                        log.warn("{} [{}] ⚠️ Wallet not found for owner {}", LOG_TAG, traceId, ownerId);
                        return Mono.empty();
                    }
                    return handleError(traceId, res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, res -> handleError(traceId, res))
                .bodyToMono(ExternalWalletResponse.class)
                .doOnNext(res -> logResponse(traceId, res))
                .map(this::mapToWallet);
    }

    // --- Gestion des Transactions ---

    @Override
    public Mono<PaymentTransaction> createTransaction(UUID walletId, BigDecimal amount, TransactionType type) {
        String traceId = generateTraceId();
        // Route spécifique pour PAYMENT sinon route standard pour RECHARGE
        String endpoint = (type == TransactionType.PAYMENT) ? "/api/v1/transactions/payment" : "/api/v1/transactions";
        String url = paymentServiceUrl + endpoint;
        
        ExternalTransactionRequest request = new ExternalTransactionRequest(walletId, amount, type.name());
        logRequest(traceId, HttpMethod.POST, url, request);

        return webClientBuilder.baseUrl(paymentServiceUrl).build()
                .post()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res -> handleError(traceId, res))
                .bodyToMono(ExternalTransactionResponse.class)
                .doOnNext(res -> logResponse(traceId, res))
                .map(this::mapToTransaction);
    }

    @Override
    public Flux<PaymentTransaction> getTransactionsHistory(UUID walletId) {
        String traceId = generateTraceId();
        String url = paymentServiceUrl + "/api/v1/transactions/Wallet/" + walletId;

        logRequest(traceId, HttpMethod.GET, url, null);

        return webClientBuilder.baseUrl(paymentServiceUrl).build()
                .get()
                .uri("/api/v1/transactions/Wallet/{walletId}", walletId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res -> handleError(traceId, res))
                .bodyToFlux(ExternalTransactionResponse.class)
                .doOnComplete(() -> log.info("{} [{}] ✅ History stream closed", LOG_TAG, traceId))
                .map(this::mapToTransaction);
    }

    // --- Helpers de Logging & Mappers ---

    private void logRequest(String tid, HttpMethod method, String url, Object body) {
        log.info("{} [{}] 🚀 Request: {} {}", LOG_TAG, tid, method, url);
        if (body != null) log.debug("{} [{}] Request Body: {}", LOG_TAG, tid, toJson(body));
    }

    private void logResponse(String tid, Object body) {
        log.info("{} [{}] ✅ Response received", LOG_TAG, tid);
        log.debug("{} [{}] Response Body: {}", LOG_TAG, tid, toJson(body));
    }

    private Mono<Throwable> handleError(String tid, org.springframework.web.reactive.function.client.ClientResponse res) {
        return res.bodyToMono(String.class).flatMap(errorBody -> {
            log.error("{} [{}] 🛑 API Error | Status: {} | Body: {}", LOG_TAG, tid, res.statusCode(), errorBody);
            return Mono.error(new RuntimeException("External API Error: " + res.statusCode()));
        });
    }

    private String toJson(Object obj) {
        try { return objectMapper.writeValueAsString(obj); } 
        catch (JsonProcessingException e) { return "error_serializing"; }
    }

    private String generateTraceId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private Wallet mapToWallet(ExternalWalletResponse dto) {
        return Wallet.builder().id(dto.id()).ownerId(dto.ownerId()).ownerName(dto.ownerName()).balance(dto.balance()).build();
    }

    private PaymentTransaction mapToTransaction(ExternalTransactionResponse dto) {
        if (dto == null) return null;
        
        log.debug("{} Mapping Transaction ID: {}, Type received: {}", LOG_TAG, dto.id(), dto.type());
        
        return PaymentTransaction.builder()
                .id(dto.id())
                .walletId(dto.walletId())
                .amount(dto.amount())
                .status(dto.status())
                // CORRECTION : On ajoute le mapping du type ici
                .type(safeValueOf(dto.type())) 
                .build();
    }

    private TransactionType safeValueOf(String typeStr) {
        if (typeStr == null) return null;
        try {
            return TransactionType.valueOf(typeStr.toUpperCase());
        } catch (Exception e) {
            log.warn("{} ⚠️ Could not map TransactionType: '{}'. Check if Enum matches API values.", LOG_TAG, typeStr);
            return null;
        }
    }
}