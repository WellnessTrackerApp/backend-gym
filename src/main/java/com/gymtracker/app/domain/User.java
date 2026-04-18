package com.gymtracker.app.domain;

import com.gymtracker.app.exception.DuplicatedExercisesException;
import com.gymtracker.app.exception.ExerciseAlreadyExistsException;
import com.gymtracker.app.exception.ExerciseDoesNotExistException;
import com.gymtracker.app.exception.PlanWithSameNameAlreadyExistsException;
import com.gymtracker.app.exception.TrainingDoesNotExistException;
import com.gymtracker.app.exception.TrainingPlansAmountExceededException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class User implements UserDetails {
    private static final int MAX_TRAINING_PLANS_PER_USER = 5;

    private final UUID userId;
    private String username;
    private String email;
    private Instant createdAt;

    @Setter(AccessLevel.NONE)
    private Password password;
    private Set<Exercise> exercises;
    private List<TrainingPlan> plans;

    public void updatePassword(String passwordHash) {
        this.password = new Password(passwordHash);
    }

    public Exercise createCustomExercise(String name, ExerciseCategory category) {
        boolean exerciseWithSameNameExists = exercises.stream()
                .anyMatch(exercise -> exercise.getName().equals(name));
        if (exerciseWithSameNameExists) {
            throw new ExerciseAlreadyExistsException("adding-existing-exercise-to-exercises");
        }

        Exercise newExercise = Exercise.builder()
                .name(name)
                .isCustom(true)
                .ownerId(this.userId)
                .category(category)
                .build();

        exercises.add(newExercise);

        return newExercise;
    }

    public Exercise updateCustomExercise(long existingExerciseId, String newExerciseName, ExerciseCategory exerciseCategory) {
        Exercise exercise = exercises.stream()
                .filter(ex -> ex.getExerciseId().equals(existingExerciseId))
                .findFirst()
                .orElseThrow(() -> new ExerciseDoesNotExistException("updating-exercise"));

        boolean exerciseWithSameNameExists = exercises.stream()
                .filter(ex -> !ex.getExerciseId().equals(existingExerciseId))
                .anyMatch(ex -> ex.getName().equals(newExerciseName));

        if (exerciseWithSameNameExists) {
            throw new ExerciseAlreadyExistsException("adding-existing-exercise-to-exercises");
        }

        exercise.setName(newExerciseName);
        exercise.setCategory(exerciseCategory);
        return exercise;
    }

    public void removeCustomExercise(long exerciseId) {
        if (exercises.stream().noneMatch(exercise -> exercise.getExerciseId().equals(exerciseId))) {
            throw new ExerciseDoesNotExistException("exercise-does-not-exist-exception.deleting-exercise");
        }

        exercises.removeIf(exercise -> exercise.getExerciseId().equals(exerciseId));
    }

    public TrainingPlan createCustomTrainingPlan(String planName, List<TrainingPlan.PlanItem> trainingPlanItems) {
        validateDuplicatedExercises(trainingPlanItems);

        if (plans.size() >= MAX_TRAINING_PLANS_PER_USER) {
            throw new TrainingPlansAmountExceededException("adding-training-plan", MAX_TRAINING_PLANS_PER_USER);
        }

        validateDuplicatedPlanName(planName);

        TrainingPlan newPlan = TrainingPlan.builder()
                .name(planName)
                .ownerId(this.userId)
                .isCustom(true)
                .planItems(trainingPlanItems)
                .build();

        plans.add(newPlan);

        return newPlan;
    }

    public TrainingPlan updateCustomTrainingPlan(long existingPlanId, String newName, List<TrainingPlan.PlanItem> newPlanItems) {
        TrainingPlan trainingPlan = plans.stream()
                .filter(plan -> plan.getId().equals(existingPlanId))
                .findFirst()
                .orElseThrow(() -> new TrainingDoesNotExistException("updating-training"));

        boolean planWithSameNameExists = plans.stream()
                .filter(plan -> !plan.getId().equals(existingPlanId))
                .anyMatch(plan -> plan.getName().equals(newName));

        if (planWithSameNameExists) {
            throw new PlanWithSameNameAlreadyExistsException("adding-training-plan");
        }

        validateDuplicatedExercises(newPlanItems);

        trainingPlan.setName(newName);
        trainingPlan.setPlanItems(newPlanItems);
        return trainingPlan;
    }

    public void removeCustomTrainingPlan(long planId) {
        if (plans.stream().noneMatch(plan -> plan.getId().equals(planId))) {
            throw new TrainingDoesNotExistException("deleting-training");
        }

        plans.removeIf(plan -> plan.getId().equals(planId));
    }

    private void validateDuplicatedPlanName(String planName) {
        boolean planWithSameNameExists = plans.stream()
                .anyMatch(trainingPlan -> trainingPlan.getName().equals(planName));

        if (planWithSameNameExists) {
            throw new PlanWithSameNameAlreadyExistsException("adding-training-plan");
        }
    }

    private void validateDuplicatedExercises(List<TrainingPlan.PlanItem> trainingPlanItems) {
        long distinctExercisesCount = trainingPlanItems.stream()
                .map(TrainingPlan.PlanItem::getExercise)
                .distinct()
                .count();

        if (distinctExercisesCount != trainingPlanItems.size()) {
            throw new DuplicatedExercisesException("adding-duplicated-exercises");
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password.hashedPassword();
    }

    @Override
    public String getUsername() {
        return userId.toString();
    }

    public String getDisplayUsername() {
        return username;
    }
}
