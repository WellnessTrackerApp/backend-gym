package com.gymtracker.app.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes(
        @JsonSubTypes.Type(value = WorkoutRepetitionItemDTO.class, name = "REPS")
)
@Getter
@Setter
@SuperBuilder
public abstract class WorkoutItemDTO {
    protected long exerciseId;
}
