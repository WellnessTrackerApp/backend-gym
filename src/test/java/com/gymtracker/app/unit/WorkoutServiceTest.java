package com.gymtracker.app.unit;

import com.gymtracker.app.domain.Exercise;
import com.gymtracker.app.domain.User;
import com.gymtracker.app.domain.workout.Workout;
import com.gymtracker.app.dto.request.WorkoutCreationRequest;
import com.gymtracker.app.dto.request.WorkoutItemDTO;
import com.gymtracker.app.dto.request.WorkoutRepetitionItemDTO;
import com.gymtracker.app.exception.DuplicatedExercisesException;
import com.gymtracker.app.exception.ExerciseDoesNotExistException;
import com.gymtracker.app.exception.InvalidPeriodException;
import com.gymtracker.app.exception.TrainingDoesNotExistException;
import com.gymtracker.app.exception.UserDoesNotExistException;
import com.gymtracker.app.mapper.WorkoutItemMapper;
import com.gymtracker.app.mapper.WorkoutMapper;
import com.gymtracker.app.repository.ExerciseRepository;
import com.gymtracker.app.repository.TrainingPlanRepository;
import com.gymtracker.app.repository.UserRepository;
import com.gymtracker.app.repository.WorkoutRepository;
import com.gymtracker.app.service.impl.WorkoutServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {
    @InjectMocks
    private WorkoutServiceImpl workoutService;

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private TrainingPlanRepository trainingPlanRepository;

    @Spy
    private WorkoutMapper workoutMapper = Mappers.getMapper(WorkoutMapper.class);

    @Spy
    private WorkoutItemMapper workoutItemMapper = Mappers.getMapper(WorkoutItemMapper.class);

    private final ArgumentCaptor<Workout> argumentCaptor = ArgumentCaptor.forClass(Workout.class);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(workoutMapper, "workoutItemMapper", workoutItemMapper);
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(workoutService);
    }

    @Test
    void givenDeletedTrainingId_whenCreateWorkout_thenTrainingDoesNotExistExceptionIsThrown() {
        WorkoutCreationRequest workoutCreationRequest = generateSampleWorkoutCreationRequest();

        User user = User.builder()
                .userId(UUID.randomUUID())
                .build();

        Mockito.when(userRepository.existsById(user.getUserId()))
                .thenReturn(true);

        Mockito.when(trainingPlanRepository.existsInUserAccessiblePlans(any(), any()))
                    .thenReturn(true);

        Mockito.when(trainingPlanRepository.isDeleted(workoutCreationRequest.trainingId()))
                .thenReturn(true);

        Assertions.assertThrows(
                TrainingDoesNotExistException.class,
                () -> workoutService.createWorkout(workoutCreationRequest, user.getUserId())
        );
    }

    @Test
    void givenValidWorkoutCreationRequest_whenCreateWorkout_thenWorkoutIsCreated() {
        WorkoutCreationRequest workoutCreationRequest = generateSampleWorkoutCreationRequest();

        UUID userId = UUID.randomUUID();

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(exerciseRepository.findExerciseAccessibleByUser(workoutCreationRequest.workoutItems().getFirst().getExerciseId(), userId))
                .thenReturn(Optional.of(Exercise.builder().build()));

        Mockito.when(trainingPlanRepository.existsInUserAccessiblePlans(workoutCreationRequest.trainingId(), userId))
                .thenReturn(true);

        workoutService.createWorkout(workoutCreationRequest, userId);

        Mockito.verify(workoutRepository).save(argumentCaptor.capture());

        Workout savedWorkout = argumentCaptor.getValue();
        Assertions.assertNotNull(savedWorkout);
        Assertions.assertEquals(userId, savedWorkout.getUserId());
    }

    @Test
    void givenNonExistingUserId_whenCreateWorkout_thenUserDoesNotExistExceptionIsThrown() {
        WorkoutCreationRequest workoutCreationRequest = generateSampleWorkoutCreationRequest();

        UUID userId = UUID.randomUUID();
        Mockito.when(userRepository.existsById(userId))
                .thenReturn(false);

        Assertions.assertThrows(UserDoesNotExistException.class, () -> {
            workoutService.createWorkout(workoutCreationRequest, userId);
        });
    }

    private WorkoutCreationRequest generateSampleWorkoutCreationRequest() {
        List<WorkoutRepetitionItemDTO.ExerciseSet> sets = List.of(
                WorkoutRepetitionItemDTO.ExerciseSet.builder()
                        .reps(10)
                        .weight(BigDecimal.valueOf(50.0))
                        .build(),
                WorkoutRepetitionItemDTO.ExerciseSet.builder()
                        .reps(8)
                        .weight(BigDecimal.valueOf(55.0))
                        .build()
        );



        List<WorkoutItemDTO> workoutItems = List.of(
                WorkoutRepetitionItemDTO.builder()
                        .exerciseId(1L)
                        .sets(sets)
                        .build()
        );

        return WorkoutCreationRequest.builder()
                .trainingId(1L)
                .workoutItems(workoutItems)
                .build();
    }

    @Test
    void givenNonExistingExerciseIdInWorkoutItem_whenCreateWorkout_thenExceptionIsThrown() {
        WorkoutCreationRequest workoutCreationRequest = generateSampleWorkoutCreationRequest();

        UUID userId = UUID.randomUUID();
        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(trainingPlanRepository.existsInUserAccessiblePlans(workoutCreationRequest.trainingId(), userId))
                        .thenReturn(true);

        Mockito.when(exerciseRepository.findExerciseAccessibleByUser(workoutCreationRequest.workoutItems().getFirst().getExerciseId(), userId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ExerciseDoesNotExistException.class, () -> {
            workoutService.createWorkout(workoutCreationRequest, userId);
        });
    }

    @Test
    void givenNonExistingTrainingId_whenCreateWorkout_thenExceptionIsThrown() {
        WorkoutCreationRequest workoutCreationRequest = generateSampleWorkoutCreationRequest();

        UUID userId = UUID.randomUUID();
        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(trainingPlanRepository.existsInUserAccessiblePlans(workoutCreationRequest.trainingId(), userId))
                .thenReturn(false);

        Assertions.assertThrows(
                TrainingDoesNotExistException.class,
                () -> workoutService.createWorkout(workoutCreationRequest, userId)
        );
    }

    @Test
    void givenDuplicatedExerciseIdsInWorkoutItems_whenCreateWorkout_thenExceptionIsThrown() {
        List<WorkoutItemDTO> workoutItems = List.of(
                WorkoutRepetitionItemDTO.builder()
                        .exerciseId(1L)
                        .sets(List.of())
                        .build(),
                WorkoutRepetitionItemDTO.builder()
                        .exerciseId(1L)
                        .sets(List.of())
                        .build()
        );

        WorkoutCreationRequest workoutCreationRequest = WorkoutCreationRequest.builder()
                .trainingId(1L)
                .workoutItems(workoutItems)
                .build();

        UUID userId = UUID.randomUUID();
        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(trainingPlanRepository.existsInUserAccessiblePlans(workoutCreationRequest.trainingId(), userId))
                .thenReturn(true);

        Assertions.assertThrows(DuplicatedExercisesException.class, () -> {
            workoutService.createWorkout(workoutCreationRequest, userId);
        });
    }

    @Test
    void givenNonExistingUserId_whenGetWorkoutExerciseHistory_thenUserDoesNotExistExceptionIsThrown() {
        UUID userId = UUID.randomUUID();
        long exerciseId = 1L;
        int previousWorkouts = 5;

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(false);

        Assertions.assertThrows(UserDoesNotExistException.class, () -> {
            workoutService.getWorkoutExerciseHistory(exerciseId, previousWorkouts, userId);
        });
    }

    @Test
    void givenNonExistingExerciseId_whenGetWorkoutExerciseHistory_thenExerciseDoesNotExistExceptionIsThrown() {
        UUID userId = UUID.randomUUID();
        long exerciseId = 1L;
        int previousWorkouts = 5;

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(exerciseRepository.existsInExercisesAccessibleByUser(exerciseId, userId))
                .thenReturn(false);

        Assertions.assertThrows(ExerciseDoesNotExistException.class, () -> {
            workoutService.getWorkoutExerciseHistory(exerciseId, previousWorkouts, userId);
        });
    }

    @Test
    void givenValidInputs_whenGetWorkoutExerciseHistory_thenWorkoutsAreReturned() {
        UUID userId = UUID.randomUUID();
        long exerciseId = 1L;
        int previousWorkouts = 5;

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(exerciseRepository.existsInExercisesAccessibleByUser(exerciseId, userId))
                .thenReturn(true);

        workoutService.getWorkoutExerciseHistory(exerciseId, previousWorkouts, userId);

        Mockito.verify(workoutRepository).findLastWorkoutsContainingExercise(exerciseId, previousWorkouts, userId);
    }

    @Test
    void givenNonExistingUserId_whenGetWorkoutExerciseHistoryByWorkoutInPeriod_thenUserDoesNotExistExceptionIsThrown() {
        UUID userId = UUID.randomUUID();
        long exerciseId = 1L;

        LocalDate startDate = LocalDate.of(2000, 1, 1);
        LocalDate endDate = LocalDate.of(2020, 2, 1);

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(false);

        Assertions.assertThrows(UserDoesNotExistException.class, () -> {
            workoutService.getWorkoutExerciseHistoryByWorkoutInPeriod(
                    exerciseId,
                    startDate,
                    endDate,
                    userId
            );
        });
    }

    @Test
    void givenNonExistingExerciseId_whenGetWorkoutExerciseHistoryByWorkoutInPeriod_thenExceptionIsThrown() {
        UUID userId = UUID.randomUUID();
        long exerciseId = 1L;

        LocalDate startDate = LocalDate.of(2000, 1, 1);
        LocalDate endDate = LocalDate.of(2020, 2, 1);

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(exerciseRepository.existsInExercisesAccessibleByUser(exerciseId, userId))
                .thenReturn(false);

        Assertions.assertThrows(ExerciseDoesNotExistException.class, () -> {
            workoutService.getWorkoutExerciseHistoryByWorkoutInPeriod(
                    exerciseId,
                    startDate,
                    endDate,
                    userId
            );
        });
    }

    @ParameterizedTest
    @CsvSource(value = {
            "2020-01-01, 2019-12-31",
            ", 2020-01-01",
            "2020-01-01,"
    })
    void givenInvalidPeriod_whenGetWorkoutExerciseHistoryByWorkoutInPeriod_thenInvalidPeriodExceptionIsThrown(LocalDate startDate, LocalDate endDate) {
        UUID userId = UUID.randomUUID();
        long exerciseId = 1L;

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(exerciseRepository.existsInExercisesAccessibleByUser(exerciseId, userId))
                .thenReturn(true);

        Assertions.assertThrows(InvalidPeriodException.class, () -> {
            workoutService.getWorkoutExerciseHistoryByWorkoutInPeriod(
                    exerciseId,
                    startDate,
                    endDate,
                    userId
            );
        });
    }

    @Test
    void givenDeletedTrainingId_whenGetWorkoutTrainingHistory_thenTrainingDoesNotExistExceptionIsThrown() {
        UUID userId = UUID.randomUUID();
        long trainingId = 1L;

        LocalDate startDate = LocalDate.of(2000, 1, 1);
        LocalDate endDate = LocalDate.of(2020, 2, 1);

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(trainingPlanRepository.existsInUserAccessiblePlans(trainingId, userId))
                .thenReturn(true);

        Mockito.when(trainingPlanRepository.isDeleted(trainingId))
                .thenReturn(true);

        Assertions.assertThrows(TrainingDoesNotExistException.class, () -> {
            workoutService.getWorkoutTrainingHistory(
                    trainingId,
                    startDate,
                    endDate,
                    userId
            );
        });
    }

    @Test
    void givenNonExistingUserId_whenGetWorkoutTrainingHistory_thenUserDoesNotExistExceptionIsThrown() {
        UUID userId = UUID.randomUUID();
        long trainingId = 1L;

        LocalDate startDate = LocalDate.of(2000, 1, 1);
        LocalDate endDate = LocalDate.of(2020, 2, 1);

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(false);

        Assertions.assertThrows(UserDoesNotExistException.class, () -> {
            workoutService.getWorkoutTrainingHistory(
                    trainingId,
                    startDate,
                    endDate,
                    userId
            );
        });
    }

    @Test
    void givenNonAccessibleTrainingId_whenGetWorkoutTrainingHistory_thenTrainingDoesNotExistExceptionIsThrown() {
        UUID userId = UUID.randomUUID();
        long trainingId = 1L;

        LocalDate startDate = LocalDate.of(2000, 1, 1);
        LocalDate endDate = LocalDate.of(2020, 2, 1);

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Assertions.assertThrows(TrainingDoesNotExistException.class, () -> {
            workoutService.getWorkoutTrainingHistory(
                    trainingId,
                    startDate,
                    endDate,
                    userId
            );
        });
    }

    @ParameterizedTest
    @CsvSource(
        {
            "2020-01-01, 2019-12-31",
            ", 2020-01-01",
            "2020-01-01,"
        }
    )
    void givenInvalidPeriod_whenGetWorkoutTrainingHistory_thenInvalidPeriodExceptionIsThrown(LocalDate startDate, LocalDate endDate) {
        UUID userId = UUID.randomUUID();
        long trainingId = 1L;

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(trainingPlanRepository.existsInUserAccessiblePlans(trainingId, userId))
                .thenReturn(true);

        Assertions.assertThrows(
                InvalidPeriodException.class,
                () -> workoutService.getWorkoutTrainingHistory(trainingId, startDate, endDate, userId)
        );
    }

    @Test
    void givenNonExistingUserId_whenGetWorkouts_thenUserDoesNotExistExceptionIsThrown() {
        UUID userId = UUID.randomUUID();

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(false);

        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2020, 2, 1);
        Long trainingPlanId = 1L;

        Assertions.assertThrows(UserDoesNotExistException.class, () -> {
            workoutService.getWorkouts(Pageable.ofSize(10), startDate, endDate, trainingPlanId, userId);
        });
    }

    @Test
    void givenNoExistingWorkouts_whenGetWorkouts_thenEmptyListIsReturned() {
        UUID userId = UUID.randomUUID();
        Long trainingPlanId = 1L;
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2020, 2, 1);

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(trainingPlanRepository.existsInUserAccessiblePlans(trainingPlanId, userId))
                .thenReturn(true);

        List<Workout> workouts = workoutService.getWorkouts(Pageable.ofSize(10), startDate, endDate, trainingPlanId, userId);

        Assertions.assertNotNull(workouts);
        Assertions.assertTrue(workouts.isEmpty());
    }

    @Test
    void givenInvalidPeriod_whenGetWorkouts_thenInvalidPeriodExceptionIsThrown() {
        UUID userId = UUID.randomUUID();

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2019, 12, 31);
        Long trainingPlanId = 1L;

        Assertions.assertThrows(InvalidPeriodException.class, () -> {
            workoutService.getWorkouts(Pageable.ofSize(10), startDate, endDate, trainingPlanId, userId);
        });
    }

    @Test
    void givenNonExistingTrainingPlanId_whenGetWorkouts_thenTrainingDoesNotExistExceptionIsThrown() {
        UUID userId = UUID.randomUUID();
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2020, 2, 1);
        Long trainingPlanId = 1L;

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(trainingPlanRepository.existsInUserAccessiblePlans(trainingPlanId, userId))
                        .thenReturn(false);

        Assertions.assertThrows(TrainingDoesNotExistException.class, () -> {
            workoutService.getWorkouts(Pageable.ofSize(10), startDate, endDate, trainingPlanId, userId);
        });
    }

}
