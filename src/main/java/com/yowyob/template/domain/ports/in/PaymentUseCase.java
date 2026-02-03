package com.yowyob.template.domain.ports.in;

import com.yowyob.template.domain.model.PaymentTransaction;
import com.yowyob.template.domain.model.Wallet;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Port d'entrée définissant les cas d'utilisation métier pour le paiement.
 * Ces méthodes seront appelées par les contrôleurs REST et les Listeners.
 */
public interface PaymentUseCase {

    /**
     * Initialise le portefeuille du chauffeur lors de son inscription.
     * @param driverId Identifiant unique du chauffeur
     * @param email Email pour l'identification dans le système de paiement
     */
    Mono<Wallet> initializeWallet(UUID driverId, String email);

    /**
     * Effectue un paiement pour une course. 
     * L'API externe prélèvera automatiquement 10% du montant envoyé.
     */
    Mono<PaymentTransaction> processRidePayment(UUID driverId, BigDecimal rideAmount);

    /**
     * Effectue une recharge manuelle du portefeuille.
     */
    Mono<PaymentTransaction> rechargeWallet(UUID driverId, BigDecimal amount);

    /**
     * Récupère les informations actuelles du portefeuille (solde, etc.).
     */
    Mono<Wallet> getDriverWallet(UUID driverId);

    /**
     * Récupère la liste complète des transactions (Historique).
     */
    Flux<PaymentTransaction> getWalletHistory(UUID driverId);
}