package com.gymtracker.app.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record WorkoutTrainingHistoryDTO(Long trainingId, List<WorkoutSessionSnapshot> history) {
}
