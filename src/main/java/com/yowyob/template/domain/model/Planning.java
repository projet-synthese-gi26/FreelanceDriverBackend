package com.yowyob.template.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalTime;
import java.time.OffsetDateTime;

@Getter 
@Setter 
@SuperBuilder
@NoArgsConstructor 
@AllArgsConstructor
public class Planning extends Product {
    // Les champs comme departureLocation sont maintenant dans la classe Product (en String)
    private String paymentOption;
    private String regularAmount;
    private String discountPercentage;
    private String discountedAmount;
}
