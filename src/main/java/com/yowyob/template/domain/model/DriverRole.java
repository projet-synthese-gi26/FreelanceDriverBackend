package com.yowyob.template.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DriverRole extends BusinessActor {
    private String licenseNumber;
    private Integer yearsExperience;

    @Override
    public String getRoleType() {
        return "DRIVER";
    }

    @Override
    public SubjectType getReviewableType() {
        return SubjectType.DRIVER;
    }

    @Override
    public SubjectType getReactableType() {
        return SubjectType.DRIVER;
    }
}
