package com.gymtracker.app.dto.response;

import java.util.List;

public record TrainingPlanDTO(Long id, String name, boolean isCustom, List<PlanItemDTO> planItems) {
    public record PlanItemDTO(Long exerciseId, String exerciseName, int defaultSets) { }
}
