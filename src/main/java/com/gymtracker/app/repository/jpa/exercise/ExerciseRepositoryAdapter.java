package com.gymtracker.app.repository.jpa.exercise;

import com.gymtracker.app.domain.Exercise;
import com.gymtracker.app.entity.ExerciseEntity;
import com.gymtracker.app.mapper.ExerciseMapper;
import com.gymtracker.app.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ExerciseRepositoryAdapter implements ExerciseRepository {
    private final SpringDataJpaExerciseRepository repository;
    private final ExerciseMapper mapper;

    @Override
    public boolean existsByNameAndOwnerIsNull(String name) {
        return repository.existsByNameAndOwnerIsNull(name);
    }

    @Override
    public boolean existsInExercisesAccessibleByUser(Long exerciseId, UUID userId) {
        return repository.existsByExerciseIdAndOwner_UserId(exerciseId, userId) || repository.existsByExerciseIdAndIsCustomIsFalse(exerciseId);
    }

    @Override
    public Exercise save(Exercise exercise) {
        ExerciseEntity exerciseEntity = mapper.exerciseToExerciseEntity(exercise);

        ExerciseEntity savedExerciseEntity = repository.save(exerciseEntity);

        return mapper.exerciseEntityToExercise(savedExerciseEntity);
    }

    @Override
    public Set<Exercise> findAllPredefinedExercises() {
        Set<ExerciseEntity> exerciseEntities = repository.findAllByOwnerIsNull();
        return exerciseEntities.stream()
                .map(mapper::exerciseEntityToExercise)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<Exercise> findExerciseAccessibleByUser(Long exerciseId, UUID userId) {
        Optional<ExerciseEntity> exerciseEntity = repository.findExerciseAccessibleByUser(exerciseId, userId);
        return exerciseEntity.map(mapper::exerciseEntityToExercise);
    }

    @Override
    public void deleteById(long exerciseId) {
        repository.findById(exerciseId)
                .ifPresent(exercise -> exercise.setDeleted(true));
    }
}
