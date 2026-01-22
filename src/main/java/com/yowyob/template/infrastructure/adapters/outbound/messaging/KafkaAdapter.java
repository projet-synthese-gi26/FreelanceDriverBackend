package com.yowyob.template.infrastructure.adapters.outbound.messaging;

import com.yowyob.template.domain.model.Product;
import com.yowyob.template.domain.ports.out.ProductEventPublisherPort;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class KafkaAdapter implements ProductEventPublisherPort {

    private final ReactiveKafkaProducerTemplate<String, Object> kafkaTemplate;

    @Value("${application.kafka.topics.product-events}")
    private String productEventsTopic;

    @Override
    public Mono<Void> publishProductCreated(Product product) {
        return kafkaTemplate.send(productEventsTopic, product.getId() != null ? product.getId().toString() : "", product)
                .then();
    }

    @Override
    public Mono<Void> publishProductUpdated(Product product) {
        // Basic implementation: just log or send to Kafka if needed
        return kafkaTemplate.send(productEventsTopic, product.getId() != null ? product.getId().toString() : "", product)
                .then();
    }

    @Override
    public Mono<Void> publishProductDeleted(Product product) {
        // Basic implementation: just log or send to Kafka if needed
        return kafkaTemplate.send(productEventsTopic, product.getId() != null ? product.getId().toString() : "", product)
                .then();
    }
}