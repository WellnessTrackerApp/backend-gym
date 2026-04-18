package com.gymtracker.app.service;

import com.gymtracker.app.domain.Exercise;
import com.gymtracker.app.dto.request.ExerciseCreationRequest;

import java.util.Set;
import java.util.UUID;

public interface ExerciseService {
    Exercise createCustomExercise(Exercise exercise, UUID ownerId);
    Set<Exercise> getUserExercises(UUID ownerId);
    Set<Exercise> getPredefinedExercises();
    void deleteCustomExercise(long exerciseId, UUID userId);
    Exercise updateCustomExercise(long exerciseId, ExerciseCreationRequest exerciseCreationRequest, UUID userId);
}
