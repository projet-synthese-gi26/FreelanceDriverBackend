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
    private String userId;
    private String name;
    private String phoneNumber;
    private String emailAddress;
}
