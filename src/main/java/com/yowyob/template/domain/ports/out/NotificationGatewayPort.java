package com.yowyob.template.domain.ports.out;

import com.yowyob.template.domain.model.NotificationType;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

/**
 * Interface pour la communication avec le service de notification.
 */
public interface NotificationGatewayPort {

    /**
     * Envoie une notification immédiate.
     * @param type Le canal (WHATSAPP, PUSH, etc.)
     * @param templateId L'ID du modèle créé sur l'API
     * @param recipients Liste des destinataires (Emails ou Numéros de téléphone)
     * @param data Données dynamiques pour remplir le template {{variable}}
     */
    Mono<Void> sendImmediate(NotificationType type, Integer templateId, java.util.List<String> recipients, Map<String, Object> data);

    /**
     * Enregistre le service auprès de l'API externe pour obtenir le token.
     * (Sera appelé automatiquement si le token est manquant)
     */
    Mono<String> registerService();

     /**
     * Enregistre une notification pour le centre de notifications (PULL).
     * @param userId L'ID de l'utilisateur qui verra la notification dans son app
     */
    Mono<Void> createForPull(UUID userId, NotificationType type, Integer templateId, Map<String, Object> data);

}
