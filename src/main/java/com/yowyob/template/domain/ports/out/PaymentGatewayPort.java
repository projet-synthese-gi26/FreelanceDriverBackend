package com.yowyob.template.domain.ports.out;

import com.yowyob.template.domain.model.PaymentTransaction;
import com.yowyob.template.domain.model.TransactionType;
import com.yowyob.template.domain.model.Wallet;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Interface définissant les opérations autorisées vers le service de paiement externe.
 * AUCUN token n'est requis par l'API de destination.
 */
public interface PaymentGatewayPort {

    /**
     * Crée un nouveau wallet (Appelé à l'onboarding).
     */
    Mono<Wallet> createWallet(UUID ownerId, String ownerName);

    /**
     * Trouve un wallet à partir de l'ID du Chauffeur (ownerId).
     */
    Mono<Wallet> getWalletByOwner(UUID ownerId);

    /**
     * Crée une transaction (RECHARGE ou PAYMENT) via le walletId.
     */
    Mono<PaymentTransaction> createTransaction(UUID walletId, BigDecimal amount, TransactionType type);

    /**
     * Récupère la liste des transactions pour un walletId.
     */
    Flux<PaymentTransaction> getTransactionsHistory(UUID walletId);
}