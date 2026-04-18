package com.gymtracker.app.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
public record TrainingPlanCreationRequest(
        @NotBlank(message = "{training-plan.plan-name.blank}")
        @Size(min = 0, max = 50, message = "{training-plan.plan-name.size}")
        String planName,

        @Valid
        @NotNull(message = "{training-plan.plan-items.null}")
        @Size(min = 0, max = 30, message = "{training-plan.plan-items.size}")
        List<PlanItem> planItems
) {

    @Builder
    public record PlanItem(
            @NotNull(message = "{training-plan.plan-item.exercise-id.null}")
            Long exerciseId,

            @Min(value = 0, message = "{training-plan.plan-item.default-sets.min}")
            @Max(value = 50, message = "{training-plan.plan-item.default-sets.max}")
            int defaultSets
    ) { }
}
