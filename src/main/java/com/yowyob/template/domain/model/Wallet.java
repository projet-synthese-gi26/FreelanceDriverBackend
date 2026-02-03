package com.yowyob.template.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {
    private UUID id;
    private UUID ownerId;
    private String ownerName;
    // Utilisation de BigDecimal pour éviter les erreurs d'arrondi sur l'argent
    private BigDecimal balance;
}