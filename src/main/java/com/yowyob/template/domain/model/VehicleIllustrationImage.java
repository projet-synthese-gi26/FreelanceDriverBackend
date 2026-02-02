package com.yowyob.template.domain.model;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleIllustrationImage {
    private UUID vehicleIllustrationImageId;
    private UUID vehicleId;
    private String imagePath;
}
