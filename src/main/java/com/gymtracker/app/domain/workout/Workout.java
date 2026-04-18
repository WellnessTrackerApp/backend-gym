package com.gymtracker.app.domain.workout;

import com.gymtracker.app.exception.DomainException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class Workout {
    private Long id;
    private UUID userId;
    private Long trainingId;
    private LocalDate createdAt;
    private List<WorkoutItem> workoutItems;

    public static Workout create(UUID userId, Long trainingId, List<WorkoutItem> workoutItems) {
        if (workoutItems == null || workoutItems.isEmpty()) {
            throw new DomainException("workout-exercises-minimal-amount");
        }

        return Workout.builder()
                .userId(userId)
                .trainingId(trainingId)
                .workoutItems(workoutItems)
                .build();
    }
}
