package com.yowyob.template.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Getter 
@Setter 
@SuperBuilder 
@NoArgsConstructor 
@AllArgsConstructor
public class Annonce extends Product {
    // Les champs comme departureLocation sont maintenant dans la classe Product (en String)
    private String cost;
    private String baggageInfo;
}