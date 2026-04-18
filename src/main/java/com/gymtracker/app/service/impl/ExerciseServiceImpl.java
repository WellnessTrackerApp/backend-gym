package com.gymtracker.app.service.impl;

import com.gymtracker.app.domain.Exercise;
import com.gymtracker.app.domain.User;
import com.gymtracker.app.dto.request.ExerciseCreationRequest;
import com.gymtracker.app.exception.ExerciseAlreadyExistsException;
import com.gymtracker.app.exception.UserDoesNotExistException;
import com.gymtracker.app.repository.ExerciseRepository;
import com.gymtracker.app.repository.UserRepository;
import com.gymtracker.app.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    @Override
    public Exercise createCustomExercise(Exercise exercise, UUID ownerId) {
        if (exerciseRepository.existsByNameAndOwnerIsNull(exercise.getName()))
            throw new ExerciseAlreadyExistsException("predefined-exercise", exercise.getName());

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserDoesNotExistException("owner-not-found"));

        Exercise customExercise = owner.createCustomExercise(exercise.getName(), exercise.getCategory());

        return exerciseRepository.save(customExercise);
    }

    @Override
    public Set<Exercise> getUserExercises(UUID ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserDoesNotExistException("owner-not-found"));

        return owner.getExercises();
    }

    @Override
    public Set<Exercise> getPredefinedExercises() {
        return exerciseRepository.findAllPredefinedExercises();
    }

    @Override
    @Transactional
    public void deleteCustomExercise(long exerciseId, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("deleting-exercise-for-non-existing-user"));

        user.removeCustomExercise(exerciseId);

        exerciseRepository.deleteById(exerciseId);
    }

    @Override
    @Transactional
    public Exercise updateCustomExercise(long exerciseId, ExerciseCreationRequest exerciseCreationRequest, UUID userId) {
        if (exerciseRepository.existsByNameAndOwnerIsNull(exerciseCreationRequest.name()))
            throw new ExerciseAlreadyExistsException("predefined-exercise", exerciseCreationRequest.name());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("updating-exercise-for-non-existing-user"));

        Exercise updatedExercise = user.updateCustomExercise(exerciseId, exerciseCreationRequest.name(), exerciseCreationRequest.category());

        return exerciseRepository.save(updatedExercise);
    }
}
