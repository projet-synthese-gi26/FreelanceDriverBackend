package com.yowyob.template.infrastructure.adapters.inbound.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yowyob.template.domain.model.PaymentTransaction;
import com.yowyob.template.domain.model.Wallet;
import com.yowyob.template.domain.ports.in.PaymentUseCase;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.RechargeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * CONTRÔLEUR DE GESTION FINANCIÈRE (CHAUFFEUR)
 * 
 * Ce contrôleur expose les services de Wallet pour les utilisateurs authentifiés.
 * Sécurité : Protégé par JWT (OAuth2). 
 * Traçabilité : Logging JSON exhaustif de chaque requête entrante et sortante.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/driver/wallet")
@RequiredArgsConstructor
@Tag(name = "Driver Wallet", description = "Opérations financières sur le portefeuille Chauffeur")
public class DriverWalletController {

    private final PaymentUseCase paymentUseCase;
    private final ObjectMapper objectMapper;

    private static final String LOG_PREFIX = "[REST-WALLET]";

    // ============================================================================================
    // 1. CONSULTER LE SOLDE
    // ============================================================================================

    @GetMapping
    @Operation(summary = "Consulter le solde", description = "Récupère le montant disponible et les infos du wallet.")
    public Mono<Wallet> getWallet(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            ServerWebExchange exchange) {
        
        String reqId = generateRequestId();
        Instant start = Instant.now();
        UUID driverId = extractUserId(principal);

        logRequest(reqId, "GET_BALANCE", driverId, exchange.getRequest(), null);

        return paymentUseCase.getDriverWallet(driverId)
                .doOnNext(w -> logResponse(reqId, "GET_BALANCE", w, start))
                .doOnError(e -> logError(reqId, "GET_BALANCE", e, start));
    }

    // ============================================================================================
    // 2. HISTORIQUE DES TRANSACTIONS
    // ============================================================================================

    @GetMapping("/transactions")
    @Operation(summary = "Historique des transactions", description = "Liste tous les mouvements d'argent (Flux réactif).")
    public Flux<PaymentTransaction> getHistory(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            ServerWebExchange exchange) {

        String reqId = generateRequestId();
        Instant start = Instant.now();
        UUID driverId = extractUserId(principal);

        logRequest(reqId, "GET_HISTORY", driverId, exchange.getRequest(), null);

        return paymentUseCase.getWalletHistory(driverId)
                .index() // On indexe pour compter les transactions dans les logs
                .doOnNext(tuple -> {
                    if (tuple.getT1() == 0) log.info("{} [{}] 📥 First transaction batch received from service.", LOG_PREFIX, reqId);
                })
                .map(reactor.util.function.Tuple2::getT2)
                .doOnComplete(() -> log.info("{} [{}] ✅ History stream delivered in {}ms", LOG_PREFIX, reqId, Duration.between(start, Instant.now()).toMillis()))
                .doOnError(e -> logError(reqId, "GET_HISTORY", e, start));
    }

    // ============================================================================================
    // 3. RECHARGER LE COMPTE
    // ============================================================================================

    @PostMapping("/recharge")
    @Operation(summary = "Recharger le compte", description = "Crée une transaction de recharge pour augmenter le solde.")
    public Mono<PaymentTransaction> recharge(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            @Valid @RequestBody RechargeRequest request,
            ServerWebExchange exchange) {

        String reqId = generateRequestId();
        Instant start = Instant.now();
        UUID driverId = extractUserId(principal);

        logRequest(reqId, "RECHARGE_POST", driverId, exchange.getRequest(), request);

        return paymentUseCase.rechargeWallet(driverId, request.amount())
                .doOnNext(tx -> logResponse(reqId, "RECHARGE_POST", tx, start))
                .doOnError(e -> logError(reqId, "RECHARGE_POST", e, start));
    }

    // ============================================================================================
    // MÉTHODES DE LOGGING & UTILITAIRES
    // ============================================================================================

    private String generateRequestId() {
        return "REQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private UUID extractUserId(OAuth2AuthenticatedPrincipal principal) {
        try {
            return UUID.fromString(principal.getAttribute("sub"));
        } catch (Exception e) {
            log.error("{} 🚨 FAILED TO EXTRACT USER ID FROM TOKEN!", LOG_PREFIX);
            throw new RuntimeException("Invalid authentication principal");
        }
    }

    private void logRequest(String id, String op, UUID user, ServerHttpRequest req, Object body) {
        log.info("{} ╔══════════════════════════════════════════════════════════════════════════", LOG_PREFIX);
        log.info("{} ║ ID: {} | OP: {} | USER: {}", LOG_PREFIX, id, op, user);
        log.info("{} ║ URI: {} {}", LOG_PREFIX, req.getMethod(), req.getURI());
        log.info("{} ║ IP: {}", LOG_PREFIX, req.getRemoteAddress());
        
        if (body != null) {
            try {
                String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
                log.info("{} ║ BODY:\n{}", LOG_PREFIX, json);
            } catch (JsonProcessingException e) {
                log.warn("{} ║ [Body serialization failed]", LOG_PREFIX);
            }
        }
        log.info("{} ╚══════════════════════════════════════════════════════════════════════════", LOG_PREFIX);
    }

    private void logResponse(String id, String op, Object response, Instant start) {
        long time = Duration.between(start, Instant.now()).toMillis();
        try {
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
            log.info("{} ✅ SUCCESS [{}] | OP: {} | TIME: {}ms | RESPONSE:\n{}", LOG_PREFIX, id, op, time, json);
        } catch (JsonProcessingException e) {
            log.info("{} ✅ SUCCESS [{}] | OP: {} | TIME: {}ms", LOG_PREFIX, id, op, time);
        }
    }

    private void logError(String id, String op, Throwable e, Instant start) {
        long time = Duration.between(start, Instant.now()).toMillis();
        log.error("{} ❌ FAILED [{}] | OP: {} | TIME: {}ms | ERROR: {}", LOG_PREFIX, id, op, time, e.getMessage());
        // En cas d'erreur 401 ou 500, le log massif permettra de voir si c'est un problème de token 
        // ou de communication avec l'API externe.
    }
}