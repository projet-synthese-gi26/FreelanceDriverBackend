package com.yowyob.template.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "SubjectType",
        type = "string",
        enumAsRef = true
)
public enum SubjectType {
    PRODUCT,
    DRIVER,
    CLIENT,
    ORGANISATION,
    VEHICLE,
    PLATFORM,
    REVIEW
}
