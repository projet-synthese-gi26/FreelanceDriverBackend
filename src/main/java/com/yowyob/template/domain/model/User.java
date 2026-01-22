package com.yowyob.template.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private String username;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String photoUri;
    private Settings settings;
    
    @Builder.Default
    private List<String> permissions = new ArrayList<>();
    
    @Builder.Default
    private List<BusinessActor> roles = new ArrayList<>();

    public void addRole(BusinessActor role) {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        roles.add(role);
    }

    public BusinessActor getRole(String type) {
        return roles.stream()
                .filter(r -> r.getRoleType().equalsIgnoreCase(type))
                .findFirst()
                .orElse(null);
    }
}
