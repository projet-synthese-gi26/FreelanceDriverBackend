package com.yowyob.template.infrastructure.adapters.inbound.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FullOnboardingRequest {
    private String email;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    
    // Role information: "CLIENT" or "DRIVER"
    private String roleType;
    
    // Organisation information
    private String organisationName;
    private String organisationDescription;
    private String title;
    private String address;
}
