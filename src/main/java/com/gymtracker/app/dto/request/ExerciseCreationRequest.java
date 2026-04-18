package com.gymtracker.app.dto.request;

import com.gymtracker.app.domain.ExerciseCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ExerciseCreationRequest(
        @NotBlank(message = "{exercise.name.blank}")
        @Size(min = 2, max = 100, message = "{exercise.name.size}")
        @Pattern(regexp = "[\\p{L}0-9\\-' ]+", message = "{exercise.name.pattern}")
        String name,

        @NotNull(message = "{exercise.category.null}")
        ExerciseCategory category
) {}
