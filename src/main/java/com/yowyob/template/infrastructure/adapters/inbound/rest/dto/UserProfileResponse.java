package com.yowyob.template.infrastructure.adapters.inbound.rest.dto;

import com.yowyob.template.domain.model.User;
import com.yowyob.template.domain.model.BusinessActor;
import com.yowyob.template.domain.model.Organisation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private String accessToken;
    private String refreshToken;
    private User user;
    private BusinessActor actor;
    private Organisation organisation;
}
