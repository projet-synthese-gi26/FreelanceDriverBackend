package com.yowyob.template.domain.event;

import com.yowyob.template.domain.model.ProductStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * ÉVÉNEMENT DE DOMAINE : Changement de statut d'un produit (Planning ou Annonce).
 * 
 * Cet événement est publié dès qu'un trajet change d'état (ex: Confirmed, Ongoing, Terminated).
 * Il permet au système de paiement de réagir sans coupler les services.
 */
@Getter
@Builder
public class ProductStatusChangedEvent {
    private final UUID productId;
    private final String productType; // "PLANNING" ou "ANNONCE"
    private final UUID driverId;
    private final UUID clientId;
    private final BigDecimal amount;   // Montant total du trajet
    private final ProductStatus oldStatus;
    private final ProductStatus newStatus;
    private final String authToken;    // Pour les futures notifications si besoin
}