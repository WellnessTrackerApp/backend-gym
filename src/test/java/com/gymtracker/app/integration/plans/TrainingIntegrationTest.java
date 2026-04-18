package com.gymtracker.app.integration.plans;

import com.gymtracker.app.domain.ExerciseCategory;
import com.gymtracker.app.dto.request.TrainingPlanCreationRequest;
import com.gymtracker.app.entity.ExerciseEntity;
import com.gymtracker.app.entity.PlanItemEntity;
import com.gymtracker.app.entity.TrainingPlanEntity;
import com.gymtracker.app.entity.UserEntity;
import com.gymtracker.app.integration.BaseIntegrationTest;
import com.gymtracker.app.repository.jpa.exercise.SpringDataJpaExerciseRepository;
import com.gymtracker.app.repository.jpa.training.SpringDataJpaTrainingPlanRepository;
import com.gymtracker.app.repository.jpa.user.SpringDataJpaUserRepository;
import com.gymtracker.app.security.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TrainingIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private SpringDataJpaTrainingPlanRepository trainingPlanRepository;

    @Autowired
    private SpringDataJpaExerciseRepository exerciseRepository;

    @Autowired
    private SpringDataJpaUserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Test
    void contextLoads() {
    }

    @AfterEach
    @BeforeEach
    void cleanUp() {
        trainingPlanRepository.deleteAll();
        exerciseRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void givenValidTrainingPlan_whenCreateTrainingPlan_thenTrainingPlanSavedInDatabase() {
        ExerciseEntity exercise1 = createTestExerciseEntity("Exercise 1");
        ExerciseEntity exercise2 = createTestExerciseEntity("Exercise 2");

        exercise1 = exerciseRepository.save(exercise1);
        exercise2 = exerciseRepository.save(exercise2);

        UserEntity user = createTestUserEntity();
        user = userRepository.save(user);

        List<TrainingPlanCreationRequest.PlanItem> planItems = List.of(
                TrainingPlanCreationRequest.PlanItem.builder()
                        .exerciseId(exercise1.getExerciseId())
                        .defaultSets(3)
                        .build(),
                TrainingPlanCreationRequest.PlanItem.builder()
                        .exerciseId(exercise2.getExerciseId())
                        .defaultSets(4)
                        .build()
        );

        TrainingPlanCreationRequest trainingPlanCreationRequest = TrainingPlanCreationRequest.builder()
                .planName("Full Body Workout")
                .planItems(planItems)
                .build();

        String authToken = jwtService.generateToken(user.getUsername(), user.getUserId().toString());

        webTestClient.post()
                .uri("/plans")
                .header("Authorization", "Bearer " + authToken)
                .bodyValue(trainingPlanCreationRequest)
                .exchange()
                .expectStatus()
                .isCreated();

        var savedTrainingPlan = trainingPlanRepository.findAll().iterator().next();
        Assertions.assertNotNull(savedTrainingPlan);
        Assertions.assertEquals(trainingPlanCreationRequest.planName(), savedTrainingPlan.getName());
    }

    @Test
    void givenInvalidExerciseId_whenCreateTrainingPlan_thenReturnsNotFound() {
        UserEntity user = createTestUserEntity();
        user = userRepository.save(user);

        List<TrainingPlanCreationRequest.PlanItem> planItems = List.of(
                TrainingPlanCreationRequest.PlanItem.builder()
                        .exerciseId(1L)
                        .defaultSets(3)
                        .build()
        );

        TrainingPlanCreationRequest trainingPlanCreationRequest = TrainingPlanCreationRequest.builder()
                .planName("Invalid Workout")
                .planItems(planItems)
                .build();

        String authToken = jwtService.generateToken(user.getUsername(), user.getUserId().toString());

        webTestClient.post()
                .uri("/plans")
                .header("Authorization", "Bearer " + authToken)
                .bodyValue(trainingPlanCreationRequest)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void givenEmptyPlanName_whenCreateTrainingPlan_thenReturnsBadRequest() {
        UserEntity user = createTestUserEntity();
        user = userRepository.save(user);

        List<TrainingPlanCreationRequest.PlanItem> planItems = List.of(
                TrainingPlanCreationRequest.PlanItem.builder()
                        .exerciseId(1L)
                        .defaultSets(3)
                        .build()
        );

        TrainingPlanCreationRequest trainingPlanCreationRequest = TrainingPlanCreationRequest.builder()
                .planName("") // Empty plan name
                .planItems(planItems)
                .build();

        String authToken = jwtService.generateToken(user.getUsername(), user.getUserId().toString());

        webTestClient.post()
                .uri("/plans")
                .header("Authorization", "Bearer " + authToken)
                .bodyValue(trainingPlanCreationRequest)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void givenGetPredefinedTrainingPlans_whenCalled_thenReturnsPredefinedPlans() {
        ExerciseEntity exercise1 = createTestExerciseEntity("Predefined Exercise 1");
        ExerciseEntity exercise2 = createTestExerciseEntity("Predefined Exercise 2");

        exercise1 = exerciseRepository.save(exercise1);
        exercise2 = exerciseRepository.save(exercise2);

        PlanItemEntity planItem1 = PlanItemEntity.builder()
                .exercise(exercise1)
                .defaultSets(3)
                .build();
        PlanItemEntity planItem2 = PlanItemEntity.builder()
                .exercise(exercise2)
                .defaultSets(4)
                .build();

        trainingPlanRepository.save(
                TrainingPlanEntity.builder()
                        .name("Predefined Plan")
                        .isCustom(false)
                        .planItems(List.of(planItem1, planItem2))
                        .build()
        );

        webTestClient.get()
                .uri("/plans")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Object.class)
                .hasSize(1);
    }

    @Test
    void givenGetUserTrainingPlans_whenCalled_thenReturnsUserPlans() {
        ExerciseEntity exercise1 = createTestExerciseEntity("User Exercise 1");
        ExerciseEntity exercise2 = createTestExerciseEntity("User Exercise 2");

        exercise1 = exerciseRepository.save(exercise1);
        exercise2 = exerciseRepository.save(exercise2);

        UserEntity user = createTestUserEntity();
        user = userRepository.save(user);

        PlanItemEntity planItem1 = PlanItemEntity.builder()
                .exercise(exercise1)
                .defaultSets(3)
                .build();
        PlanItemEntity planItem2 = PlanItemEntity.builder()
                .exercise(exercise2)
                .defaultSets(4)
                .build();

        trainingPlanRepository.save(
                TrainingPlanEntity.builder()
                        .name("User Plan")
                        .isCustom(true)
                        .owner(user)
                        .planItems(List.of(planItem1, planItem2))
                        .build()
        );

        String authToken = jwtService.generateToken(user.getUsername(), user.getUserId().toString());
        webTestClient.get()
                .uri("/plans/user")
                .header("Authorization", "Bearer " + authToken)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Object.class)
                .hasSize(1);
    }

    private ExerciseEntity createTestExerciseEntity(String name) {
        return ExerciseEntity.builder()
                .name(name)
                .category(ExerciseCategory.UNCATEGORIZED)
                .build();
    }

    private UserEntity createTestUserEntity() {
        return UserEntity.builder()
                .username("testuser")
                .email("testuser@domain.com")
                .passwordHash("testpasswordhash")
                .build();
    }
}
