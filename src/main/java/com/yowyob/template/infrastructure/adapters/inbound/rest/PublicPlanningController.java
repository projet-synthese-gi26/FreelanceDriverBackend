package com.yowyob.template.infrastructure.adapters.inbound.rest;

import com.yowyob.template.application.service.ProductService;
import com.yowyob.template.domain.model.Planning;
import com.yowyob.template.domain.model.Product;
import com.yowyob.template.domain.model.ProductStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/public/plannings")
@RequiredArgsConstructor
@Tag(name = "Public Plannings", description = "Endpoints publics pour consulter les plannings publiés")
public class PublicPlanningController {

    private final ProductService productService;

    @GetMapping("/published")
    public Flux<Product> listPublishedPlannings() {
        return productService.getAllProducts()
                .filter(product -> product instanceof Planning)
                .filter(product -> product.getStatus() == ProductStatus.Published);
    }
}
