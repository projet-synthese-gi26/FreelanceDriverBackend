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
public class ClientOrganisation extends Organisation {
    @Override
    public Product createProduct(Map<String, Object> params) {
        String type = (String) params.get("type");
        if ("CV".equalsIgnoreCase(type)) {
            return CV.builder()
                    .orgId(this.getId())
                    .build();
        }
        return Annonce.builder()
                .orgId(this.getId())
                .build();
    }
}
