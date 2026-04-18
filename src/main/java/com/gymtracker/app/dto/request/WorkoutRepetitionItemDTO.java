package com.gymtracker.app.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@Jacksonized
public class WorkoutRepetitionItemDTO extends WorkoutItemDTO {
    private static final int MAX_SETS = 100;
    private static final int MAX_REPS = 300;
    private static final int MAX_WEIGHT = 600;

    @Valid
    @NotNull(message = "{workout-repetition-item.sets.null}")
    @Size(min = 1, max = MAX_SETS, message = "{workout-repetition-item.sets.size}")
    private List<ExerciseSet> sets;

    @Builder
    public record ExerciseSet(
            @PositiveOrZero(message = "{workout-repetition-item.exercise-set.reps.positive-or-zero}")
            @Max(value = MAX_REPS, message = "{workout-repetition-item.exercise-set.reps.max}")
            int reps,

            @NotNull(message = "{workout-repetition-item.exercise-set.weight.null}")
            @PositiveOrZero(message = "{workout-repetition-item.exercise-set.weight.positive-or-zero}")
            @Max(value = MAX_WEIGHT, message = "{workout-repetition-item.exercise-set.weight.max}")
            BigDecimal weight) {}
}
