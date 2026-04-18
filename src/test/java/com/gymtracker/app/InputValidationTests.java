package com.gymtracker.app;

import com.gymtracker.app.domain.ExerciseCategory;
import com.gymtracker.app.dto.request.ExerciseCreationRequest;
import com.gymtracker.app.dto.request.SignIn;
import com.gymtracker.app.dto.request.SignUp;
import com.gymtracker.app.dto.request.TrainingPlanCreationRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class InputValidationTests {
    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @ParameterizedTest
    @CsvSource(value = {
            ",123",
            "'',123",
            "email,123",
            "email@,123"
    })
    void givenInvalidSignInEmail_whenValidated_ShouldViolateRules(String email, String password) {
        SignIn signIn = new SignIn(email, password);

        Set<ConstraintViolation<SignIn>> violationSet = validator.validate(signIn);

        Assertions.assertEquals(1, violationSet.size());
        Assertions.assertEquals("email", violationSet.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "email@domain.com,",
            "email@domain.com,''"
    })
    void givenInvalidSignInPassword_whenValidated_ShouldViolateRules(String email, String password) {
        SignIn signIn = new SignIn(email, password);

        Set<ConstraintViolation<SignIn>> violationSet = validator.validate(signIn);

        Assertions.assertEquals(1, violationSet.size());
        Assertions.assertEquals("password", violationSet.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "email",
            "email@"
    })
    void givenInvalidSignUpEmail_whenValidated_ShouldViolateRules(String email) {
        SignUp signUp = new SignUp("username", email, "password");

        Set<ConstraintViolation<SignUp>> violationSet = validator.validate(signUp);

        Assertions.assertEquals(1, violationSet.size());
        Assertions.assertEquals("email", violationSet.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "123", // too short
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum." // too long
    })
    void givenInvalidSignUpPassword_whenValidated_ShouldViolateRules(String password) {
        SignUp signUp = new SignUp("username", "email@domain.com", password);

        Set<ConstraintViolation<SignUp>> violationSet = validator.validate(signUp);

        Assertions.assertFalse(violationSet.isEmpty());
        Assertions.assertEquals("password", violationSet.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "u", // too short
            "Lorem ipsum dolor sit amet" // too long
    })
    void givenInvalidSignUpUsername_whenValidated_ShouldViolateRules(String username) {
        SignUp signUp = new SignUp(username, "email@domain.com", "Password123@");

        Set<ConstraintViolation<SignUp>> violationSet = validator.validate(signUp);

        Assertions.assertFalse(violationSet.isEmpty());
        Assertions.assertEquals("username", violationSet.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "'',CHEST",
            "'   ',BACK",
            "A,ARMS", // Too short exercise name
            "!!@@##$$,SHOULDERS", // Invalid characters in exercise name
            "ThisIsAnExcessivelyLongExerciseNameThatShouldTriggerValidationErrorsBecauseItExceedsTheMaximumAllowedLength,LEGS",
            "Valid Exercise Name,", // null enum
    })
    void givenInvalidExerciseCreationRequest_whenValidated_shouldDetectRulesViolation(String exerciseName, ExerciseCategory category) {
        ExerciseCreationRequest exerciseCreationRequest = ExerciseCreationRequest.builder()
                .name(exerciseName)
                .build();

        Set<ConstraintViolation<ExerciseCreationRequest>> violations = validator.validate(exerciseCreationRequest);

        Assertions.assertNotEquals(0, violations.size());
    }

    @Test
    void givenInvalidTrainingPlanCreationRequest_whenValidated_shouldDetectRulesViolation() {
        var request = TrainingPlanCreationRequest.builder()
                .planName(" ")
                .planItems(java.util.List.of())
                .build();

        Set<ConstraintViolation<TrainingPlanCreationRequest>> violations = validator.validate(request);

        Assertions.assertNotEquals(0, violations.size());
    }

    @Test
    void givenTrainingPlanCreationRequestWithTooManyItems_whenValidated_shouldDetectRulesViolation() {
        var planItems = new ArrayList<TrainingPlanCreationRequest.PlanItem>();
        for (int i = 0; i < 31; i++) {
            planItems.add(TrainingPlanCreationRequest.PlanItem.builder()
                    .exerciseId((long) i)
                    .defaultSets(5)
                    .build());
        }

        var request = TrainingPlanCreationRequest.builder()
                .planName("Full Body Workout")
                .planItems(planItems)
                .build();

        Set<ConstraintViolation<TrainingPlanCreationRequest>> violations = validator.validate(request);

        Assertions.assertNotEquals(0, violations.size());
    }

    @Test
    void givenTrainingPlanCreationRequestWithItemHavingTooManySets_whenValidated_shouldDetectRulesViolation() {
        var planItem = TrainingPlanCreationRequest.PlanItem.builder()
                .exerciseId(1L)
                .defaultSets(100)
                .build();

        var request = TrainingPlanCreationRequest.builder()
                .planName("Full Body Workout")
                .planItems(List.of(planItem))
                .build();

        Set<ConstraintViolation<TrainingPlanCreationRequest>> violations = validator.validate(request);

        Assertions.assertNotEquals(0, violations.size());
    }

    @Test
    void givenTrainingPlanCreationRequestWithItemHavingNegativeSets_whenValidated_shouldDetectRulesViolation() {
        var planItem = TrainingPlanCreationRequest.PlanItem.builder()
                .exerciseId(1L)
                .defaultSets(-5)
                .build();

        var request = TrainingPlanCreationRequest.builder()
                .planName("Full Body Workout")
                .planItems(List.of(planItem))
                .build();

        Set<ConstraintViolation<TrainingPlanCreationRequest>> violations = validator.validate(request);

        Assertions.assertNotEquals(0, violations.size());
    }

    @Test
    void givenValidTrainingPlanCreationRequest_whenValidated_shouldNotDetectRulesViolation() {
        var planItem = TrainingPlanCreationRequest.PlanItem.builder()
                .exerciseId(1L)
                .defaultSets(5)
                .build();

        var request = TrainingPlanCreationRequest.builder()
                .planName("Full Body Workout")
                .planItems(java.util.List.of(planItem))
                .build();

        Set<ConstraintViolation<TrainingPlanCreationRequest>> violations = validator.validate(request);

        Assertions.assertEquals(0, violations.size());
    }
}
