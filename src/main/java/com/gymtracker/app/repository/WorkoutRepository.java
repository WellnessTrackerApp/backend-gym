package com.gymtracker.app.repository;

import com.gymtracker.app.domain.workout.Workout;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface WorkoutRepository {
    Workout save(Workout workout);
    List<Workout> findLastWorkoutsContainingExercise(long exerciseId, int previousWorkouts, UUID userId);
    List<Workout> findWorkoutsContainingExerciseInPeriod(long exerciseId, LocalDate startDate, LocalDate endDate, UUID userId);
    List<Workout> findWorkoutsByTrainingIdAndPeriod(long trainingId, LocalDate startDate, LocalDate endDate, UUID userId);
    List<Workout> findUserWorkouts(Pageable pageable, LocalDate startDate, LocalDate endDate, Long trainingPlanId, UUID userId);
}
