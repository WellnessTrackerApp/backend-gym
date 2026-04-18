package com.gymtracker.app.unit;

import com.gymtracker.app.domain.Exercise;
import com.gymtracker.app.domain.ExerciseCategory;
import com.gymtracker.app.domain.User;
import com.gymtracker.app.dto.request.ExerciseCreationRequest;
import com.gymtracker.app.entity.UserEntity;
import com.gymtracker.app.exception.ExerciseAlreadyExistsException;
import com.gymtracker.app.exception.ExerciseDoesNotExistException;
import com.gymtracker.app.exception.UserDoesNotExistException;
import com.gymtracker.app.repository.ExerciseRepository;
import com.gymtracker.app.repository.UserRepository;
import com.gymtracker.app.service.impl.ExerciseServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {
    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExerciseServiceImpl exerciseService;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(exerciseService);
    }

    @Test
    void givenNewExercise_whenCreateExerciseCalled_shouldSaveCustomExercise() {
        Exercise exercise = Exercise.builder()
                .name("My new exercise")
                .build();

        Mockito.when(userRepository.findById(any()))
                .thenReturn(Optional.of(User.builder().exercises(new HashSet<>()).build()));

        exerciseService.createCustomExercise(exercise, UUID.randomUUID());

        verify(exerciseRepository).save(any());
    }

    @Test
    void givenExerciseAlreadyCreatedByUser_whenCreateCustomExerciseCalled_shouldThrowException() {
        Exercise exercise = Exercise.builder()
                .name("My new exercise")
                .build();

        User user = User.builder()
                .exercises(Set.of(exercise))
                .build();

        Mockito.when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));

        Assertions.assertThrows(ExerciseAlreadyExistsException.class, () -> exerciseService.createCustomExercise(exercise, user.getUserId()));
    }

    @Test
    void givenExerciseExistingInPredefinedExercises_whenCreateCustomExerciseCalled_shouldThrowException() {
        Exercise exercise = Exercise.builder()
                .name("My new exercise")
                .build();

        UserEntity owner = new UserEntity();
        owner.setUserId(UUID.randomUUID());

        when(exerciseRepository.existsByNameAndOwnerIsNull(exercise.getName()))
                .thenReturn(true);

        Assertions.assertThrows(ExerciseAlreadyExistsException.class, () -> exerciseService.createCustomExercise(exercise, owner.getUserId()));
    }

    @Test
    void givenOwnerId_whenGetUserExercisesCalled_shouldReturnUserExercises() {
        UUID ownerId = UUID.randomUUID();
        Set<Exercise> userExercises = Set.of(
                Exercise.builder().name("Exercise 1").build(),
                Exercise.builder().name("Exercise 2").build()
        );

        User owner = User.builder()
                .exercises(userExercises)
                .build();

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));

        var exercises = exerciseService.getUserExercises(ownerId);

        Assertions.assertEquals(2, exercises.size());
    }

    @Test
    void givenNonExistingOwnerId_whenGetUserExercisesCalled_shouldThrowUserDoesNotExistException() {
        UUID ownerId = UUID.randomUUID();
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(UserDoesNotExistException.class, () -> exerciseService.getUserExercises(ownerId));
    }

    @Test
    void givenPredefinedExercisesExist_whenGetPredefinedExercisesCalled_shouldReturnAllPredefinedExercises() {
        Set<Exercise> predefinedExercises = Set.of(
                Exercise.builder().name("Bench Press").isCustom(false).build(),
                Exercise.builder().name("Squat").isCustom(false).build()
        );

        Mockito.when(exerciseRepository.findAllPredefinedExercises())
                .thenReturn(predefinedExercises);

        Set<Exercise> result = exerciseService.getPredefinedExercises();

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.stream().anyMatch(e -> e.getName().equals("Bench Press")));
        Assertions.assertTrue(result.stream().anyMatch(e -> e.getName().equals("Squat")));
    }

    @Test
    void givenNoPredefinedExercisesExist_whenGetPredefinedExercisesCalled_shouldReturnEmptySet() {
        Mockito.when(exerciseRepository.findAllPredefinedExercises())
                .thenReturn(Set.of());

        Set<Exercise> result = exerciseService.getPredefinedExercises();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void givenNonExistingUserId_whenDeleteCustomExerciseCalled_shouldThrowUserDoesNotExistException() {
        UUID userId = UUID.randomUUID();
        long exerciseId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(UserDoesNotExistException.class, () -> exerciseService.deleteCustomExercise(exerciseId, userId));
    }

    @Test
    void givenExerciseNotOwnedByUser_whenDeleteCustomExerciseCalled_shouldThrowException() {
        UUID userId = UUID.randomUUID();
        long exerciseId = 1L;

        User user = User.builder()
                .userId(userId)
                .exercises(Set.of(Exercise.builder().exerciseId(2L).build()))
                .build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        Assertions.assertThrows(ExerciseDoesNotExistException.class, () -> exerciseService.deleteCustomExercise(exerciseId, userId));
    }

    @Test
    void givenNonExistingUserId_whenUpdateCustomExerciseCalled_shouldThrowUserDoesNotExistException() {
        UUID userId = UUID.randomUUID();
        long exerciseId = 1L;
        ExerciseCreationRequest exerciseCreationRequest = ExerciseCreationRequest.builder()
                .name("Updated Exercise")
                .category(ExerciseCategory.UNCATEGORIZED)
                .build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(UserDoesNotExistException.class, () -> exerciseService.updateCustomExercise(exerciseId, exerciseCreationRequest, userId));
    }

    @Test
    void givenExerciseNotOwnedByUser_whenUpdateCustomExerciseCalled_shouldThrowException() {
        UUID userId = UUID.randomUUID();
        long exerciseId = 1L;
        ExerciseCreationRequest exerciseCreationRequest = ExerciseCreationRequest.builder()
                .name("Updated Exercise")
                .category(ExerciseCategory.UNCATEGORIZED)
                .build();

        User owner = User.builder()
                .exercises(Set.of(Exercise.builder().exerciseId(2L).name("Exercise 2").build()))
                .build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(owner));

        Assertions.assertThrows(ExerciseDoesNotExistException.class, () -> exerciseService.updateCustomExercise(exerciseId, exerciseCreationRequest, userId));
    }
}
