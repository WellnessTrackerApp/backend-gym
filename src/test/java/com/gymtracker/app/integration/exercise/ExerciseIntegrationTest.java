package com.gymtracker.app.integration.exercise;

import com.gymtracker.app.domain.ExerciseCategory;
import com.gymtracker.app.dto.request.ExerciseCreationRequest;
import com.gymtracker.app.dto.response.ExerciseDTO;
import com.gymtracker.app.entity.ExerciseEntity;
import com.gymtracker.app.entity.UserEntity;
import com.gymtracker.app.integration.BaseIntegrationTest;
import com.gymtracker.app.repository.jpa.exercise.SpringDataJpaExerciseRepository;
import com.gymtracker.app.repository.jpa.training.SpringDataJpaTrainingPlanRepository;
import com.gymtracker.app.repository.jpa.user.SpringDataJpaUserRepository;
import com.gymtracker.app.security.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExerciseIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private SpringDataJpaExerciseRepository exerciseRepository;

    @Autowired
    private SpringDataJpaUserRepository userRepository;

    @Autowired
    private SpringDataJpaTrainingPlanRepository planRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void cleanUp() {
        planRepository.deleteAll();
        exerciseRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void givenNewExercise_whenCreateExercise_thenExerciseSavedInDatabase() {
        ExerciseCreationRequest exerciseCreationRequest = ExerciseCreationRequest.builder()
                .name("My new exercise")
                .category(ExerciseCategory.UNCATEGORIZED)
                .build();

        UserEntity user = createTestUserEntity();

        UserEntity savedUser = userRepository.save(user);

        String jwt = jwtService.generateToken(savedUser.getUsername(), savedUser.getUserId().toString());

        webTestClient.post()
                .uri("/exercises")
                .header("Authorization", "Bearer " + jwt)
                .bodyValue(exerciseCreationRequest)
                .exchange()
                .expectStatus()
                .isCreated();

        ExerciseEntity exercise = exerciseRepository.findAll().iterator().next();
        Assertions.assertEquals(1, exerciseRepository.count());
        Assertions.assertEquals(exerciseCreationRequest.name(), exercise.getName());
        Assertions.assertEquals(savedUser.getUserId(), exercise.getOwner().getUserId());
    }

    @Test
    void givenExistingUser_whenGetUserExercisesCalled_thenShouldRetrieveOnlyUserExercises() {
        UserEntity user = createTestUserEntity();
        UserEntity savedUser = userRepository.save(user);

        List<ExerciseEntity> userExercises = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            ExerciseEntity exercise = ExerciseEntity.builder()
                    .name("User Exercise " + i)
                    .isCustom(true)
                    .category(ExerciseCategory.UNCATEGORIZED)
                    .owner(savedUser)
                    .build();
            userExercises.add(exercise);
        }

        for (int i = 0; i < 2; i++) {
            ExerciseEntity exercise = ExerciseEntity.builder()
                    .name("Predefined Exercise " + i)
                    .isCustom(false)
                    .category(ExerciseCategory.UNCATEGORIZED)
                    .build();
            exerciseRepository.save(exercise);
        }

        exerciseRepository.saveAll(userExercises);

        String jwt = jwtService.generateToken(savedUser.getUsername(), savedUser.getUserId().toString());

        webTestClient.get()
                .uri("/exercises/user")
                .header("Authorization", "Bearer " + jwt)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(ExerciseDTO.class)
                .hasSize(userExercises.size());
    }

    @Test
    void givenPredefinedExercises_whenGetPredefinedExercisesCalled_thenShouldRetrieveAllPredefinedExercises() {
        List<ExerciseEntity> predefinedExercises = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            ExerciseEntity exercise = ExerciseEntity.builder()
                    .name("Predefined Exercise " + i)
                    .isCustom(false)
                    .category(ExerciseCategory.UNCATEGORIZED)
                    .build();
            predefinedExercises.add(exercise);
        }

        exerciseRepository.saveAll(predefinedExercises);

        webTestClient.get()
                .uri("/exercises")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(ExerciseDTO.class)
                .hasSize(predefinedExercises.size());
    }

    private UserEntity createTestUserEntity() {
        return UserEntity.builder()
                .username("testuser")
                .email("testuser@domain.com")
                .createdAt(Instant.now())
                .passwordHash(passwordEncoder.encode("testpassword123@"))
                .build();
    }
}
