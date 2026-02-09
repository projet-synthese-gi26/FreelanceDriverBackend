package com.yowyob.template.infrastructure.adapters.inbound.kafka;

import com.yowyob.template.domain.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "application.kafka", name = "enabled", havingValue = "true")
public class ProductEventConsumer {

    @Value("${application.kafka.topics.product-events}")
    private String productEventsTopic;

    @KafkaListener(topics = "${application.kafka.topics.product-events}", groupId = "template-group")
    public void consume(Product product) {
        // Utilise getTitle() pour le nom, et tente d'afficher le prix selon le type de produit
        String price = null;
        if (product instanceof com.yowyob.template.domain.model.Annonce annonce) {
            price = annonce.getCost();
        } else if (product instanceof com.yowyob.template.domain.model.Planning planning) {
            price = planning.getRegularAmount();
        }
        log.info("CONSUMER: I received an event for product with title : {} and price : {}", 
             product.getTitle(), price);
    }
}