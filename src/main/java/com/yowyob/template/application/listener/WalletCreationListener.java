package com.yowyob.template.application.listener;

import com.yowyob.template.application.service.PaymentService;
import com.yowyob.template.domain.event.DriverOnboardedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * RÉACTEUR D'ÉVÉNEMENTS : CRÉATION AUTOMATIQUE DE WALLET
 * 
 * Ce composant écoute les événements métier de type "DriverOnboardedEvent".
 * Il assure que chaque chauffeur possède un portefeuille prêt à l'emploi
 * dès la fin de son inscription, sans bloquer le flux principal.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WalletCreationListener {

    private final PaymentService paymentService;
    private static final String LOG_PREFIX = "[EVENT-LISTENER]";

    /**
     * Gère l'événement d'onboarding d'un chauffeur.
     * Exécuté de manière asynchrone pour garantir une expérience utilisateur fluide.
     */
    @Async
    @EventListener
    public void handleDriverOnboarding(DriverOnboardedEvent event) {
        String eventId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        log.info("{} [ID:{}] 🛡️ RECEIVED: DriverOnboardedEvent", LOG_PREFIX, eventId);
        log.info("{} [ID:{}] Payload -> User: {}, Email: {}, Role: {}", 
                LOG_PREFIX, eventId, event.userId(), event.email(), event.role());

        // RÈGLE MÉTIER : Seuls les utilisateurs avec le rôle DRIVER reçoivent un Wallet
        if ("DRIVER".equalsIgnoreCase(event.role())) {
            log.info("{} [ID:{}] ✅ Role 'DRIVER' confirmed. Initiating automatic wallet provisioning...", LOG_PREFIX, eventId);

            paymentService.initializeWallet(event.userId(), event.email())
                    .subscribe(
                        wallet -> {
                            log.info("{} [ID:{}] 🎊 SUCCESS: Wallet provisioned for Driver {}.", LOG_PREFIX, eventId, event.userId());
                            log.info("{} [ID:{}] Wallet Internal ID: {}, Initial Balance: {}", 
                                    LOG_PREFIX, eventId, wallet.getId(), wallet.getBalance());
                        },
                        error -> {
                            log.error("{} [ID:{}] ❌ FAILURE: Could not create wallet for driver {}.", LOG_PREFIX, eventId, event.userId());
                            log.error("{} [ID:{}] Error Details: {}", LOG_PREFIX, eventId, error.getMessage());
                            // Note : Dans un système critique, on pourrait ici envoyer un message 
                            // dans une file d'attente (Dead Letter Queue) pour re-tentative.
                        }
                    );
        } else {
            log.info("{} [ID:{}] ℹ️ User role is '{}'. No wallet required. Skipping action.", 
                    LOG_PREFIX, eventId, event.role());
        }
    }
}