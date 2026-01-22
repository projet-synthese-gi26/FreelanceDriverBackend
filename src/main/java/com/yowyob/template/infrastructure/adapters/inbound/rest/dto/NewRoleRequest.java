package com.yowyob.template.infrastructure.adapters.inbound.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewRoleRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    
    private String organisationName;
    private String organisationDescription;
    private String title;
    private String address;
}
