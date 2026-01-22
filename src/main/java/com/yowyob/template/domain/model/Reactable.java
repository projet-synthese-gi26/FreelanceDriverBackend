package com.yowyob.template.domain.model;

import java.util.Map;
import java.util.UUID;

public interface Reactable {
    UUID getReactableId();
    SubjectType getReactableType();
    Map<ReactionType, Long> getReactionCounts();
}
