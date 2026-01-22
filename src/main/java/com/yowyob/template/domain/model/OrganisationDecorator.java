package com.yowyob.template.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class OrganisationDecorator extends Organisation {
    protected Organisation wrappedOrganisation;

    @Override
    public Product createProduct(Map<String, Object> params) {
        return wrappedOrganisation.createProduct(params);
    }
}
