package com.yowyob.template.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CertifiedOrganisation extends OrganisationDecorator {
    private String syndicateName;

    @Override
    public Product createProduct(Map<String, Object> params) {
        Product product = wrappedOrganisation.createProduct(params);
        product.getMetadata().add("Certified by " + syndicateName);
        return product;
    }
}
