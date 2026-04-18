package com.gymtracker.app.dto.response;

import com.gymtracker.app.domain.ExerciseCategory;

public record ExerciseDTO(Long exerciseId, ExerciseCategory category, String name, boolean isCustom) {
}
