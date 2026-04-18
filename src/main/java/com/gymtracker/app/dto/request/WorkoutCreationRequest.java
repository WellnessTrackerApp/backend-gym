package com.gymtracker.app.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;

@Builder
public record WorkoutCreationRequest(
        Long trainingId,

        @Valid
        @NotEmpty(message = "{workout.workout-items.empty}")
        List<WorkoutItemDTO> workoutItems) {
}
