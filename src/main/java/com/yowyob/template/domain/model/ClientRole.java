package com.yowyob.template.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ClientRole extends BusinessActor {
    @Override
    public String getRoleType() {
        return "CLIENT";
    }

    @Override
    public SubjectType getReviewableType() {
        return SubjectType.CLIENT;
    }

    @Override
    public SubjectType getReactableType() {
        return SubjectType.CLIENT;
    }
}
