package com.gymtracker.app.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record WorkoutExerciseHistoryDTO(Long exerciseId, List<WorkoutSessionSnapshot> history) {
}
