package com.yowyob.template.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "ReviewType",
        type = "string",
        enumAsRef = true
)
public enum ReviewType {
    RATING,
    REPORT
}
