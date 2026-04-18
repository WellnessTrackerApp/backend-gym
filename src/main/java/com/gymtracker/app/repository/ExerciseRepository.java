package com.gymtracker.app.repository;

import com.gymtracker.app.domain.Exercise;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ExerciseRepository {
    boolean existsByNameAndOwnerIsNull(String name);
    boolean existsInExercisesAccessibleByUser(Long exerciseId, UUID userId);
    Exercise save(Exercise exercise);
    Set<Exercise> findAllPredefinedExercises();
    Optional<Exercise> findExerciseAccessibleByUser(Long exerciseId, UUID userId);
    void deleteById(long exerciseId);
}
