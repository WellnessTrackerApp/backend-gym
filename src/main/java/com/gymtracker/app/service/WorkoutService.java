package com.gymtracker.app.service;

import com.gymtracker.app.domain.workout.Workout;
import com.gymtracker.app.dto.request.WorkoutCreationRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface WorkoutService {
    void createWorkout(WorkoutCreationRequest workoutCreationRequest, UUID userId);
    List<Workout> getWorkoutExerciseHistory(long exerciseId, int previousWorkouts, UUID userId);
    List<Workout> getWorkoutExerciseHistoryByWorkoutInPeriod(long exerciseId, LocalDate startDate, LocalDate endDate, UUID userId);
    List<Workout> getWorkoutTrainingHistory(long trainingId, LocalDate startDate, LocalDate endDate, UUID userId);
    List<Workout> getWorkouts(Pageable pageable, LocalDate startDate, LocalDate endDate, Long trainingPlanId, UUID userId);
}
