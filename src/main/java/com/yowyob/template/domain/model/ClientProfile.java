package com.yowyob.template.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientProfile {
    private User user;
    private BusinessActor actor;
    private Organisation organisation;
}
