package com.gymtracker.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
public record WorkoutDTO(Long workoutId, TrainingPlanDTO trainingPlan, LocalDate createdAt, List<WorkoutItemDTO> workoutItems) {

    @SuperBuilder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkoutItemDTO {
        protected ExerciseDTO exercise;
    }

    @SuperBuilder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkoutRepetitionItemDTO extends WorkoutItemDTO {
        private List<ExerciseSetDTO> sets;

        public record ExerciseSetDTO(int reps, BigDecimal weight) {}
    }

}
