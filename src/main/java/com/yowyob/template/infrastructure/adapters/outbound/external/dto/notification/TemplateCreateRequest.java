package com.yowyob.template.infrastructure.adapters.outbound.external.dto.notification;

import lombok.Builder;

@Builder
public record TemplateCreateRequest(
    Integer templateId,
    String name,
    String subject, // Utilisé pour EMAIL
    String bodyHtml, // Utilisé pour EMAIL
    String body,    // Utilisé pour WHATSAPP/PUSH/SMS
    String title,   // Utilisé pour PUSH
    String type     // "EMAIL", "WHATSAPP", "PUSH", etc.
) {}