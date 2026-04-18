package com.gymtracker.app.integration.workout;

import com.gymtracker.app.domain.ExerciseCategory;
import com.gymtracker.app.dto.request.WorkoutCreationRequest;
import com.gymtracker.app.dto.request.WorkoutItemDTO;
import com.gymtracker.app.dto.request.WorkoutRepetitionItemDTO;
import com.gymtracker.app.entity.ExerciseEntity;
import com.gymtracker.app.entity.TrainingPlanEntity;
import com.gymtracker.app.entity.UserEntity;
import com.gymtracker.app.entity.workout.WorkoutEntity;
import com.gymtracker.app.entity.workout.WorkoutRepetitionItemEntity;
import com.gymtracker.app.integration.BaseIntegrationTest;
import com.gymtracker.app.repository.jpa.exercise.SpringDataJpaExerciseRepository;
import com.gymtracker.app.repository.jpa.training.SpringDataJpaTrainingPlanRepository;
import com.gymtracker.app.repository.jpa.user.SpringDataJpaUserRepository;
import com.gymtracker.app.repository.jpa.workout.SpringDataWorkoutRepository;
import com.gymtracker.app.security.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WorkoutIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private SpringDataJpaUserRepository userRepository;

    @Autowired
    private SpringDataWorkoutRepository workoutRepository;

    @Autowired
    private SpringDataJpaExerciseRepository exerciseRepository;

    @Autowired
    private SpringDataJpaTrainingPlanRepository trainingPlanRepository;

    @Autowired
    private JwtService jwtService;

    @AfterEach
    void cleanup() {
        workoutRepository.deleteAll();
        trainingPlanRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(webTestClient);
    }

    @Test
    void givenValidWorkoutRequestWithTrainingId_whenCreateWorkout_thenWorkoutIsCreated() {
        UserEntity userEntity = UserEntity.builder()
                .username("testuser")
                .email("test@example.com")
                .passwordHash("hashedpassword")
                .build();

        userEntity = userRepository.save(userEntity);

        ExerciseEntity exercise1 = ExerciseEntity.builder()
                .name("Bench Press")
                .category(ExerciseCategory.CHEST)
                .build();

        ExerciseEntity exercise2 = ExerciseEntity.builder()
                .category(ExerciseCategory.LEGS)
                .name("Squat")
                .build();

        exercise1 = exerciseRepository.save(exercise1);
        exercise2 = exerciseRepository.save(exercise2);

        List<WorkoutRepetitionItemDTO.ExerciseSet> sets1 = List.of(
                WorkoutRepetitionItemDTO.ExerciseSet.builder()
                        .reps(10)
                        .weight(BigDecimal.valueOf(50.0))
                        .build(),
                WorkoutRepetitionItemDTO.ExerciseSet.builder()
                        .reps(8)
                        .weight(BigDecimal.valueOf(55.0))
                        .build()
        );

        List<WorkoutRepetitionItemDTO.ExerciseSet> sets2 = List.of(
                WorkoutRepetitionItemDTO.ExerciseSet.builder()
                        .reps(12)
                        .weight(BigDecimal.valueOf(60.0))
                        .build(),
                WorkoutRepetitionItemDTO.ExerciseSet.builder()
                        .reps(10)
                        .weight(BigDecimal.valueOf(65.0))
                        .build()
        );

        List<WorkoutItemDTO> workoutItems = List.of(
                WorkoutRepetitionItemDTO.builder()
                        .exerciseId(exercise1.getExerciseId())
                        .sets(sets1)
                        .build(),
                WorkoutRepetitionItemDTO.builder()
                        .exerciseId(exercise2.getExerciseId())
                        .sets(sets2)
                        .build()
        );

        TrainingPlanEntity trainingPlan = TrainingPlanEntity.builder()
                .name("Test Training Plan")
                .isCustom(false)
                .planItems(List.of())
                .build();

        trainingPlan = trainingPlanRepository.save(trainingPlan);

        WorkoutCreationRequest request = WorkoutCreationRequest.builder()
                .workoutItems(workoutItems)
                .trainingId(trainingPlan.getId())
                .build();

        String token = jwtService.generateToken(userEntity.getUsername(), userEntity.getUserId().toString());

        webTestClient.post()
                .uri("/workouts")
                .header("Authorization", "Bearer " + token)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated();

        var iterator = workoutRepository.findAll().iterator();
        Assertions.assertTrue(iterator.hasNext());

        WorkoutEntity createdWorkout = iterator.next();
        Assertions.assertEquals(userEntity.getUserId(), createdWorkout.getUser().getUserId());
        Assertions.assertEquals(trainingPlan.getId(), createdWorkout.getTraining().getId());
    }

    @Test
    void givenWorkoutsForExerciseInPeriod_whenGetWorkoutExerciseHistory_thenReturnsCorrectHistory() {
        UserEntity userEntity = UserEntity.builder()
                .username("historyuser")
                .email("history@example.com")
                .passwordHash("hashedpassword")
                .build();
        userEntity = userRepository.save(userEntity);

        ExerciseEntity exercise = ExerciseEntity.builder()
                .name("Deadlift")
                .category(ExerciseCategory.GENERAL)
                .build();
        exercise = exerciseRepository.save(exercise);

        TrainingPlanEntity trainingPlan = TrainingPlanEntity.builder()
                .name("History Plan")
                .isCustom(false)
                .planItems(List.of())
                .build();
        trainingPlan = trainingPlanRepository.save(trainingPlan);

        WorkoutRepetitionItemEntity workoutItem = WorkoutRepetitionItemEntity.builder()
                .sets(List.of(
                        WorkoutRepetitionItemEntity.ExerciseSet.builder()
                                .reps(5)
                                .weight(BigDecimal.valueOf(100.0))
                                .build(),
                        WorkoutRepetitionItemEntity.ExerciseSet.builder()
                                .reps(3)
                                .weight(BigDecimal.valueOf(110.0))
                                .build()
                ))
                .exercise(exercise)
                .build();

        WorkoutEntity workout1 = WorkoutEntity.builder()
                .user(userEntity)
                .training(trainingPlan)
                .workoutItems(List.of(workoutItem))
                .build();
        workout1 = workoutRepository.save(workout1);

        String token = jwtService.generateToken(userEntity.getUsername(), userEntity.getUserId().toString());

        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to = LocalDate.now();
        Long exerciseId = exercise.getExerciseId();

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/workouts/exercises/{exerciseId}/history/period")
                        .queryParam("startDate", from)
                        .queryParam("endDate", to)
                        .build(exerciseId))
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.exerciseId").isEqualTo(exerciseId)
                .jsonPath("$.history").isArray()
                .jsonPath("$.history.length()").isEqualTo(1);
    }

    @Test
    void givenWorkoutsForTrainingInPeriod_whenGetWorkoutTrainingHistory_thenReturnsCorrectHistory() {
        UserEntity userEntity = UserEntity.builder()
                .username("traininghistoryuser")
                .email("traininghistoryuser@domain.com")
                .passwordHash("hashedpassword")
                .build();
        userEntity = userRepository.save(userEntity);

        TrainingPlanEntity trainingPlan = TrainingPlanEntity.builder()
                .name("Training History Plan")
                .isCustom(false)
                .planItems(List.of())
                .build();
        trainingPlan = trainingPlanRepository.save(trainingPlan);

        WorkoutEntity workout1 = WorkoutEntity.builder()
                .user(userEntity)
                .training(trainingPlan)
                .workoutItems(List.of())
                .build();
        workoutRepository.save(workout1);

        String token = jwtService.generateToken(userEntity.getUsername(), userEntity.getUserId().toString());
        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to = LocalDate.now();
        Long trainingId = trainingPlan.getId();

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/workouts/trainings/{trainingId}/history/period")
                        .queryParam("startDate", from)
                        .queryParam("endDate", to)
                        .build(trainingId))
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.trainingId").isEqualTo(trainingId)
                .jsonPath("$.history").isArray()
                .jsonPath("$.history.length()").isEqualTo(1);
    }

    @Test
    void givenWorkoutsInDatabase_whenGetWorkouts_thenReturnsWorkouts() {
        UserEntity userEntity = UserEntity.builder()
                .username("listworkoutsuser")
                .email("testuser@domain.com")
                .passwordHash("hashedpassword")
                .build();
        userEntity = userRepository.save(userEntity);

        TrainingPlanEntity trainingPlan = TrainingPlanEntity.builder()
                .name("List Workouts Plan")
                .isCustom(false)
                .planItems(List.of())
                .build();
        trainingPlan = trainingPlanRepository.save(trainingPlan);

        WorkoutEntity workout1 = WorkoutEntity.builder()
                .user(userEntity)
                .training(trainingPlan)
                .workoutItems(List.of())
                .build();
        workoutRepository.save(workout1);

        String token = jwtService.generateToken(userEntity.getUsername(), userEntity.getUserId().toString());
        webTestClient.get()
                .uri("/workouts")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()")
                .isEqualTo(1);
    }
}
