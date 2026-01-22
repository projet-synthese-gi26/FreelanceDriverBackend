package com.yowyob.template.infrastructure.adapters.outbound.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.util.UUID;

@Table("businessactor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessActorEntity {
    @Id
    private UUID id;
    private UUID userId;
    private String displayName;
    private String phoneNumber;
    private String emailAddress;
    private String avatarUrl;
}
