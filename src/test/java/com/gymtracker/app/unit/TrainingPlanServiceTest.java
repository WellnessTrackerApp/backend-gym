package com.gymtracker.app.unit;

import com.gymtracker.app.domain.Exercise;
import com.gymtracker.app.domain.TrainingPlan;
import com.gymtracker.app.domain.User;
import com.gymtracker.app.dto.request.TrainingPlanCreationRequest;
import com.gymtracker.app.exception.ExerciseDoesNotExistException;
import com.gymtracker.app.exception.TrainingDoesNotExistException;
import com.gymtracker.app.exception.UserDoesNotExistException;
import com.gymtracker.app.repository.ExerciseRepository;
import com.gymtracker.app.repository.TrainingPlanRepository;
import com.gymtracker.app.repository.UserRepository;
import com.gymtracker.app.service.impl.TrainingPlanServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class TrainingPlanServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private TrainingPlanRepository trainingPlanRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private TrainingPlanServiceImpl trainingPlanService;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(trainingPlanService);
    }

    @Test
    void givenExercisesNonAccessibleByTheUser_whenGenerateCustomTrainingPlanCalled_shouldThrowExerciseDoesNotExistException() {
        Mockito.when(userRepository.findById(any()))
                        .thenReturn(Optional.of(User.builder().build()));

        Mockito.when(exerciseRepository.findExerciseAccessibleByUser(any(), any()))
                .thenReturn(Optional.empty());

        TrainingPlanCreationRequest trainingPlanCreationRequest = TrainingPlanCreationRequest.builder()
                .planItems(List.of(TrainingPlanCreationRequest.PlanItem.builder().exerciseId(1L).build()))
                .build();

        Assertions.assertThrows(ExerciseDoesNotExistException.class, () -> {
            trainingPlanService.generateCustomTrainingPlan(trainingPlanCreationRequest, UUID.randomUUID());
        });
    }

    @Test
    void givenNonExistingUserId_whenGenerateCustomTrainingPlanCalled_shouldThrowUserDoesNotExistException() {
        UUID userId = UUID.randomUUID();

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        TrainingPlanCreationRequest trainingPlanCreationRequest = createEmptyTrainingPlanCreationRequest();

        Assertions.assertThrows(UserDoesNotExistException.class, () -> {
            trainingPlanService.generateCustomTrainingPlan(trainingPlanCreationRequest, userId);
        });
    }

    @Test
    void givenValidData_whenGenerateCustomTrainingPlanCalled_shouldReturnNonNullResponse() {
        ArgumentCaptor<TrainingPlan> trainingPlanArgumentCaptor = ArgumentCaptor.forClass(TrainingPlan.class);

        Mockito.when(userRepository.findById(any()))
                .thenReturn(Optional.of(User.builder().plans(new ArrayList<>()).build()));

        Mockito.when(exerciseRepository.findExerciseAccessibleByUser(any(), any()))
                .thenReturn(Optional.of(Exercise.builder().build()));

        TrainingPlanCreationRequest trainingPlanCreationRequest = TrainingPlanCreationRequest.builder()
                .planName("Plan Name")
                .planItems(List.of(TrainingPlanCreationRequest.PlanItem.builder().exerciseId(1L).build()))
                .build();

        trainingPlanService.generateCustomTrainingPlan(trainingPlanCreationRequest, UUID.randomUUID());

        Mockito.verify(trainingPlanRepository).save(trainingPlanArgumentCaptor.capture());

        TrainingPlan savedTrainingPlan = trainingPlanArgumentCaptor.getValue();
        Assertions.assertNotNull(savedTrainingPlan);
        Assertions.assertEquals(trainingPlanCreationRequest.planItems().size(), savedTrainingPlan.getPlanItems().size());
    }

    @Test
    void givenNoPredefinedTrainingPlans_whenGetAllPredefinedTrainingPlansCalled_shouldReturnEmptyList() {
        List<?> predefinedPlans = trainingPlanService.getAllPredefinedTrainingPlans();

        Assertions.assertNotNull(predefinedPlans);
        Assertions.assertTrue(predefinedPlans.isEmpty());
    }

    @Test
    void givenPredefinedTrainingPlans_whenGetAllPredefinedTrainingPlansCalled_shouldReturnNonEmptyList() {
        Mockito.when(trainingPlanRepository.findAllPredefinedPlans())
                .thenReturn(List.of(TrainingPlan.builder().build(), TrainingPlan.builder().build()));

        List<?> predefinedPlans = trainingPlanService.getAllPredefinedTrainingPlans();

        Assertions.assertNotNull(predefinedPlans);
        Assertions.assertEquals(2, predefinedPlans.size());
    }

    @Test
    void givenNonExistingUserId_whenGetUserTrainingPlansCalled_shouldThrowUserDoesNotExistException() {
        UUID userId = UUID.randomUUID();

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(UserDoesNotExistException.class, () -> {
            trainingPlanService.getUserTrainingPlans(userId);
        });
    }

    @Test
    void givenExistingUserIdWithPlans_whenGetUserTrainingPlansCalled_shouldReturnUserPlans() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .plans(List.of(TrainingPlan.builder().build(), TrainingPlan.builder().build()))
                .build();

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        List<?> userPlans = trainingPlanService.getUserTrainingPlans(userId);

        Assertions.assertNotNull(userPlans);
        Assertions.assertEquals(2, userPlans.size());
    }

    @Test
    void givenNonExistingUserId_whenGetPlanTrainingByIdCalled_shouldThrowUserDoesNotExistException() {
        UUID userId = UUID.randomUUID();
        long trainingPlanId = 1L;

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(
                UserDoesNotExistException.class,
                () -> trainingPlanService.getTrainingPlanById(trainingPlanId, userId)
        );
    }

    @Test
    void givenNonExistingTrainingPlanId_whenGetPlanTrainingByIdCalled_shouldThrowTrainingDoesNotExistException() {
        UUID userId = UUID.randomUUID();
        long trainingPlanId = 1L;

        User user = User.builder()
                .plans(List.of())
                .build();

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        Assertions.assertThrows(
                com.gymtracker.app.exception.TrainingDoesNotExistException.class,
                () -> trainingPlanService.getTrainingPlanById(trainingPlanId, userId)
        );
    }

    @Test
    void givenNonExistingUserId_whenDeleteTrainingPlanCalled_shouldThrowUserDoesNotExistException() {
        UUID userId = UUID.randomUUID();
        long trainingPlanId = 1L;

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(
                UserDoesNotExistException.class,
                () -> trainingPlanService.deleteTrainingPlan(trainingPlanId, userId)
        );
    }

    @Test
    void givenTrainingPlanNotOwnedByUser_whenDeleteTrainingPlanCalled_shouldThrowException() {
        UUID userId = UUID.randomUUID();
        long trainingPlanId = 1L;

        User user = User.builder()
                .plans(List.of())
                .build();

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        Assertions.assertThrows(
                TrainingDoesNotExistException.class,
                () -> trainingPlanService.deleteTrainingPlan(trainingPlanId, userId)
        );

        Mockito.verify(trainingPlanRepository, Mockito.never()).deleteById(trainingPlanId);
    }

    @Test
    void givenNonExistingUserId_whenUpdateCustomTrainingPlanCalled_shouldThrowUserDoesNotExistException() {
        UUID userId = UUID.randomUUID();
        long trainingPlanId = 1L;

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        TrainingPlanCreationRequest trainingPlanCreationRequest = createEmptyTrainingPlanCreationRequest();

        Assertions.assertThrows(
                UserDoesNotExistException.class,
                () -> trainingPlanService.updateCustomTrainingPlan(trainingPlanCreationRequest, userId, trainingPlanId)
        );
    }

    @Test
    void givenTrainingPlanNotOwnedByUser_whenUpdateCustomTrainingPlanCalled_shouldThrowException() {
        UUID userId = UUID.randomUUID();
        long trainingPlanId = 1L;

        User user = User.builder()
                .plans(List.of())
                .build();

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        TrainingPlanCreationRequest trainingPlanCreationRequest = createEmptyTrainingPlanCreationRequest();

        Assertions.assertThrows(
                TrainingDoesNotExistException.class,
                () -> trainingPlanService.updateCustomTrainingPlan(trainingPlanCreationRequest, userId, trainingPlanId)
        );
    }

    @Test
    void givenNonExistingExerciseId_whenUpdateCustomTrainingPlanCalled_shouldThrowExerciseDoesNotExistException() {
        UUID userId = UUID.randomUUID();
        long trainingPlanId = 1L;

        User user = User.builder()
                .plans(List.of(TrainingPlan.builder().id(trainingPlanId).build()))
                .build();

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        Mockito.when(exerciseRepository.findExerciseAccessibleByUser(any(), any()))
                .thenReturn(Optional.empty());

        TrainingPlanCreationRequest trainingPlanCreationRequest = TrainingPlanCreationRequest.builder()
                .planItems(List.of(TrainingPlanCreationRequest.PlanItem.builder().exerciseId(1L).build()))
                .build();

        Assertions.assertThrows(
                ExerciseDoesNotExistException.class,
                () -> trainingPlanService.updateCustomTrainingPlan(trainingPlanCreationRequest, userId, trainingPlanId)
        );
    }

    private TrainingPlanCreationRequest createEmptyTrainingPlanCreationRequest() {
        return TrainingPlanCreationRequest.builder()
                .planItems(List.of())
                .build();
    }
}
