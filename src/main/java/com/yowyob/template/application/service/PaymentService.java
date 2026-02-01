package com.yowyob.template.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.yowyob.template.domain.exception.PaymentFailedException;
import com.yowyob.template.domain.model.PaymentTransaction;
import com.yowyob.template.domain.model.TransactionType;
import com.yowyob.template.domain.model.Wallet;
import com.yowyob.template.domain.ports.in.PaymentUseCase;
import com.yowyob.template.domain.ports.out.PaymentGatewayPort;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * SERVICE DE PAIEMENT - VERSION RÉSILIENTE
 * 
 * STRATÉGIE : Suite au bug 500 détecté sur l'API externe lors de la vérification (GET),
 * cette version tente la CRÉATION DIRECTE (POST) lors de l'initialisation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService implements PaymentUseCase {

    private final PaymentGatewayPort paymentGateway;
    private final ObjectMapper objectMapper;

    private static final String LOG_PREFIX = "[CORE-PAYMENT]";

    @PostConstruct
    public void setup() {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    // ============================================================================================
    // 1. INITIALISATION DU PORTEFEUILLE (CORRIGÉ : CRÉATION DIRECTE)
    // ============================================================================================

    @Override
    public Mono<Wallet> initializeWallet(UUID driverId, String email) {
        String fid = generateFlowId("INIT");
        Instant start = Instant.now();

        log.info("{} ╔══════════════════════════════════════════════════════════════════════════", LOG_PREFIX);
        log.info("{} ║ [{}] STRATEGY: DIRECT CREATION (Bypassing GET to avoid External 500)", LOG_PREFIX, fid);
        log.info("{} ║ Target Driver: {} | Email: {}", LOG_PREFIX, driverId, email);

        // Au lieu de faire un GET d'abord, on tente le POST directement.
        // Si le wallet existe déjà, l'API renverra une erreur, que nous traiterons.
        return paymentGateway.createWallet(driverId, email)
                .doOnSubscribe(s -> log.info("{} [{}] Step 1: Sending POST request to create wallet...", LOG_PREFIX, fid))
                .doOnNext(newWallet -> {
                    log.info("{} [{}] ✅ SUCCESS: Wallet created and confirmed.", LOG_PREFIX, fid);
                    logObject(fid, "WALLET_DATA", newWallet);
                })
                .onErrorResume(e -> {
                    log.warn("{} [{}] ⚠️ POST Failed. Checking if it was because wallet already exists...", LOG_PREFIX, fid);
                    
                    // Si la création échoue, on tente un dernier GET au cas où le wallet aurait été créé entre temps
                    return paymentGateway.getWalletByOwner(driverId)
                            .doOnNext(w -> log.info("{} [{}] ✅ RECOVERED: Wallet already existed.", LOG_PREFIX, fid))
                            .onErrorResume(e2 -> {
                                log.error("{} [{}] ❌ FATAL: Direct creation failed AND check failed.", LOG_PREFIX, fid);
                                return Mono.error(new PaymentFailedException("Wallet initialization failed after multiple attempts: " + e.getMessage()));
                            });
                })
                .doOnTerminate(() -> logCompletion(fid, "InitializeWallet", start));
    }

    // ============================================================================================
    // 2. PAIEMENT D'UNE COURSE (COMMISSION 10%)
    // ============================================================================================

    @Override
    public Mono<PaymentTransaction> processRidePayment(UUID driverId, BigDecimal rideAmount) {
        String fid = generateFlowId("PAY");
        Instant start = Instant.now();

        log.info("{} [{}] >>> INITIATING Ride Payment. Driver: {}, Ride Price: {}", LOG_PREFIX, fid, driverId, rideAmount);

        if (rideAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("{} [{}] ❌ REJECTED: Payment amount must be positive. Received: {}", LOG_PREFIX, fid, rideAmount);
            return Mono.error(new IllegalArgumentException("Invalid ride amount"));
        }

        return paymentGateway.getWalletByOwner(driverId)
                .doOnSubscribe(s -> log.info("{} [{}] Step 1: Resolving Wallet ID...", LOG_PREFIX, fid))
                .switchIfEmpty(Mono.error(new PaymentFailedException("Execution aborted: No wallet found for driver " + driverId)))
                .flatMap(wallet -> {
                    log.info("{} [{}] Wallet Resolved (ID: {}). Balance: {}. Sending to API/payment.", LOG_PREFIX, fid, wallet.getId(), wallet.getBalance());
                    
                    return paymentGateway.createTransaction(wallet.getId(), rideAmount, TransactionType.PAYMENT)
                            .doOnNext(tx -> {
                                log.info("{} [{}] ✅ Transaction APPROVED. Status: {}", LOG_PREFIX, fid, tx.getStatus());
                                logObject(fid, "TX_DETAILS", tx);
                            });
                })
                .onErrorResume(e -> {
                    log.error("{} [{}] ❌ PAYMENT FAILED: {}", LOG_PREFIX, fid, e.getMessage());
                    return Mono.error(new PaymentFailedException("Ride payment failed: " + e.getMessage()));
                })
                .doOnTerminate(() -> logCompletion(fid, "ProcessRidePayment", start));
    }

    // ============================================================================================
    // 3. RECHARGE DU PORTEFEUILLE
    // ============================================================================================

    @Override
    public Mono<PaymentTransaction> rechargeWallet(UUID driverId, BigDecimal amount) {
        String fid = generateFlowId("RECH");
        Instant start = Instant.now();

        log.info("{} [{}] >>> INITIATING Recharge for Driver: {}, Amount: {}", LOG_PREFIX, fid, driverId, amount);

        return paymentGateway.getWalletByOwner(driverId)
                .doOnSubscribe(s -> log.info("{} [{}] Step 1: Fetching wallet handle...", LOG_PREFIX, fid))
                .switchIfEmpty(Mono.error(new PaymentFailedException("Cannot recharge: Driver has no wallet record.")))
                .flatMap(wallet -> {
                    log.info("{} [{}] Wallet Found. Proceeding with RECHARGE transaction.", LOG_PREFIX, fid);
                    return paymentGateway.createTransaction(wallet.getId(), amount, TransactionType.RECHARGE);
                })
                .doOnNext(tx -> {
                    log.info("{} [{}] ✅ Recharge transaction successful.", LOG_PREFIX, fid);
                    logObject(fid, "RECHARGE_TX_DETAILS", tx);
                })
                .doOnTerminate(() -> logCompletion(fid, "RechargeWallet", start));
    }

    // ============================================================================================
    // 4. CONSULTATIONS (SOLDE & HISTORIQUE)
    // ============================================================================================

    @Override
    public Mono<Wallet> getDriverWallet(UUID driverId) {
        String fid = generateFlowId("VIEW");
        log.info("{} [{}] Fetching wallet state for driver: {}", LOG_PREFIX, fid, driverId);

        return paymentGateway.getWalletByOwner(driverId)
                .doOnNext(w -> log.info("{} [{}] ✅ Wallet state retrieved. Current Balance: {}", LOG_PREFIX, fid, w.getBalance()))
                .switchIfEmpty(Mono.error(new PaymentFailedException("Wallet record missing.")));
    }

    @Override
    public Flux<PaymentTransaction> getWalletHistory(UUID driverId) {
        String fid = generateFlowId("HIST");
        log.info("{} [{}] Accessing transaction history for driver: {}", LOG_PREFIX, fid, driverId);

        return paymentGateway.getWalletByOwner(driverId)
                .switchIfEmpty(Mono.error(new PaymentFailedException("History unavailable: Wallet not found.")))
                .flatMapMany(wallet -> {
                    log.info("{} [{}] Streaming transactions from Gateway for Wallet: {}", LOG_PREFIX, fid, wallet.getId());
                    return paymentGateway.getTransactionsHistory(wallet.getId());
                })
                .doOnComplete(() -> log.info("{} [{}] ✅ History stream transfer completed.", LOG_PREFIX, fid));
    }

    // ============================================================================================
    // HELPERS
    // ============================================================================================

    private String generateFlowId(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void logObject(String fid, String label, Object obj) {
        try {
            String json = objectMapper.writeValueAsString(obj);
            log.info("{} [{}] {}:\n{}", LOG_PREFIX, fid, label, json);
        } catch (JsonProcessingException e) {
            log.error("{} [{}] Failed to serialize object for logs: {}", LOG_PREFIX, fid, e.getMessage());
        }
    }

    private void logCompletion(String fid, String operation, Instant start) {
        long duration = Duration.between(start, Instant.now()).toMillis();
        log.info("{} [{}] <<< END OF OPERATION: {} (Total Duration: {} ms)", LOG_PREFIX, fid, operation, duration);
        log.info("{} ╚══════════════════════════════════════════════════════════════════════════", LOG_PREFIX);
    }
}