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
public class DriverOrganisation extends Organisation {
    @Override
    public Product createProduct(Map<String, Object> params) {
        return Planning.builder()
                .orgId(this.getId())
                .build();
    }
}
