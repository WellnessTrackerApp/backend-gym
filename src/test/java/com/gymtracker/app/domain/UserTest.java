package com.gymtracker.app.domain;

import com.gymtracker.app.exception.DuplicatedExercisesException;
import com.gymtracker.app.exception.ExerciseAlreadyExistsException;
import com.gymtracker.app.exception.PlanWithSameNameAlreadyExistsException;
import com.gymtracker.app.exception.TrainingPlansAmountExceededException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    @Test
    void givenExceedingMaxPlans_whenCreateCustomTrainingPlan_thenThrowException() {
        User user = User.builder()
                .userId(UUID.randomUUID())
                .plans(new ArrayList<>())
                .build();

        for (int i = 0; i < 5; i++) {
            user.getPlans().add(TrainingPlan.builder().name("Plan " + i).build());
        }

        assertThrows(TrainingPlansAmountExceededException.class, () -> {
            user.createCustomTrainingPlan("New Plan", new ArrayList<>());
        });
    }

    @Test
    void givenDuplicatePlanName_whenCreateCustomTrainingPlan_thenThrowException() {
        User user = User.builder()
                .userId(UUID.randomUUID())
                .plans(new ArrayList<>())
                .build();
        user.getPlans().add(TrainingPlan.builder().name("Duplicate Plan").build());

        assertThrows(PlanWithSameNameAlreadyExistsException.class, () -> {
            user.createCustomTrainingPlan("Duplicate Plan", new ArrayList<>());
        });
    }

    @Test
    void givenDuplicatedExercises_whenCreateCustomTrainingPlan_thenThrowException() {
        User user = User.builder()
                .userId(UUID.randomUUID())
                .plans(new ArrayList<>())
                .build();

        Exercise duplicatedExercise = Exercise.builder()
                .exerciseId(1L)
                .name("Push Up")
                .build();

        List<TrainingPlan.PlanItem> planItems = List.of(
                TrainingPlan.PlanItem.builder().exercise(duplicatedExercise).defaultSets(2).build(),
                TrainingPlan.PlanItem.builder().exercise(duplicatedExercise).defaultSets(1).build()
        );

        assertThrows(DuplicatedExercisesException.class, () -> {
            user.createCustomTrainingPlan("Plan with Duplicates", planItems);
        });
    }

    @Test
    void givenValidInput_whenCreateCustomTrainingPlan_thenCreatePlanSuccessfully() {
        User user = User.builder()
                .userId(UUID.randomUUID())
                .plans(new ArrayList<>())
                .build();

        TrainingPlan newPlan = user.createCustomTrainingPlan("Unique Plan", new ArrayList<>());

        assertNotNull(newPlan);
        assertEquals("Unique Plan", newPlan.getName());
        assertTrue(user.getPlans().contains(newPlan));
    }

    @Test
    void givenDuplicateExercises_whenUpdateCustomTrainingPlan_thenThrowException() {
        User user = User.builder()
                .userId(UUID.randomUUID())
                .plans(new ArrayList<>())
                .build();

        TrainingPlan existingPlan = TrainingPlan.builder()
                .id(1L)
                .name("Existing Plan")
                .build();
        user.getPlans().add(existingPlan);

        Exercise duplicatedExercise = Exercise.builder()
                .exerciseId(1L)
                .name("Squat")
                .build();

        List<TrainingPlan.PlanItem> planItems = List.of(
                TrainingPlan.PlanItem.builder().exercise(duplicatedExercise).defaultSets(3).build(),
                TrainingPlan.PlanItem.builder().exercise(duplicatedExercise).defaultSets(2).build()
        );

        assertThrows(DuplicatedExercisesException.class, () -> {
            user.updateCustomTrainingPlan(1L, "Updated Plan", planItems);
        });
    }

    @Test
    void givenValidInput_whenUpdateCustomTrainingPlan_thenUpdatePlanSuccessfully() {
        User user = User.builder()
                .userId(UUID.randomUUID())
                .plans(new ArrayList<>())
                .build();

        TrainingPlan existingPlan = TrainingPlan.builder()
                .id(1L)
                .name("Existing Plan")
                .build();
        user.getPlans().add(existingPlan);

        List<TrainingPlan.PlanItem> planItems = List.of(TrainingPlan.PlanItem.builder()
                .exercise(Exercise.builder().exerciseId(2L).name("Lunge").build())
                .defaultSets(3)
                .build());

        TrainingPlan updatedPlan = user.updateCustomTrainingPlan(1L, "Updated Plan", planItems);

        assertNotNull(updatedPlan);
        assertEquals("Updated Plan", updatedPlan.getName());
        assertTrue(user.getPlans().contains(updatedPlan));
        assertEquals(planItems.size(), updatedPlan.getPlanItems().size());
    }

    @Test
    void givenDuplicatedExercise_whenCreateCustomExercise_thenThrowException() {
        User user = User.builder()
                .userId(UUID.randomUUID())
                .exercises(new HashSet<>())
                .build();

        Exercise existingExercise = Exercise.builder()
                .exerciseId(1L)
                .name("Deadlift")
                .build();
        user.getExercises().add(existingExercise);

        Exercise newExercise = Exercise.builder()
                .name("Deadlift")
                .build();

        assertThrows(ExerciseAlreadyExistsException.class, () -> {
            user.createCustomExercise(newExercise.getName(), newExercise.getCategory());
        });
    }

    @Test
    void givenAlreadyExistingExercise_whenUpdateCustomExercise_thenThrowException() {
        User user = User.builder()
                .userId(UUID.randomUUID())
                .exercises(new HashSet<>())
                .build();

        Exercise exercise1 = Exercise.builder()
                .exerciseId(1L)
                .name("Pull Up")
                .build();
        Exercise exercise2 = Exercise.builder()
                .exerciseId(2L)
                .name("Chin Up")
                .build();
        user.getExercises().add(exercise1);
        user.getExercises().add(exercise2);

        assertThrows(ExerciseAlreadyExistsException.class, () -> {
            user.updateCustomExercise(1L, "Chin Up", exercise1.getCategory());
        });
    }

    @Test
    void givenValidInput_whenCreateAndUpdateCustomExercise_thenSucceed() {
        User user = User.builder()
                .userId(UUID.randomUUID())
                .exercises(new HashSet<>())
                .build();

        Exercise createdExercise = user.createCustomExercise("Burpee", ExerciseCategory.UNCATEGORIZED);
        assertNotNull(createdExercise);
        assertEquals("Burpee", createdExercise.getName());
        assertTrue(user.getExercises().contains(createdExercise));
        createdExercise.setExerciseId(1L);

        Exercise updatedExercise = user.updateCustomExercise(createdExercise.getExerciseId(), "Modified Burpee", ExerciseCategory.UNCATEGORIZED);
        assertNotNull(updatedExercise);
        assertEquals("Modified Burpee", updatedExercise.getName());
        assertEquals(ExerciseCategory.UNCATEGORIZED, updatedExercise.getCategory());
    }

    @Test
    void givenValidInput_whenDeleteCustomExercise_thenSucceed() {
        User user = User.builder()
                .userId(UUID.randomUUID())
                .exercises(new HashSet<>())
                .build();

        Exercise exercise = Exercise.builder()
                .exerciseId(1L)
                .name("Mountain Climber")
                .build();
        user.getExercises().add(exercise);

        user.removeCustomExercise(exercise.getExerciseId());

        assertTrue(user.getExercises().isEmpty());
    }
}