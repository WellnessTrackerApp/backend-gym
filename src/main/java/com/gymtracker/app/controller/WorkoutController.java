package com.gymtracker.app.controller;

import com.gymtracker.app.domain.TrainingPlan;
import com.gymtracker.app.domain.workout.Workout;
import com.gymtracker.app.dto.request.WorkoutCreationRequest;
import com.gymtracker.app.dto.response.MessageResponse;
import com.gymtracker.app.dto.response.WorkoutDTO;
import com.gymtracker.app.dto.response.WorkoutExerciseHistoryDTO;
import com.gymtracker.app.dto.response.WorkoutTrainingHistoryDTO;
import com.gymtracker.app.mapper.WorkoutMapper;
import com.gymtracker.app.service.TrainingPlanService;
import com.gymtracker.app.service.WorkoutService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workouts")
@RequiredArgsConstructor
@Validated
public class WorkoutController {
    private final WorkoutService workoutService;
    private final TrainingPlanService trainingPlanService;
    private final WorkoutMapper workoutMapper;
    private final MessageSource messageSource;

    @GetMapping
    public ResponseEntity<List<WorkoutDTO>> getWorkouts(
            Pageable pageable,
            @RequestParam(name = "startDate", required = false) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) LocalDate endDate,
            @RequestParam(name = "trainingPlanId", required = false) Long trainingPlanId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<Workout> workouts = workoutService.getWorkouts(pageable, startDate, endDate, trainingPlanId, UUID.fromString(userDetails.getUsername()));
        List<WorkoutDTO> workoutDTOS = workouts.stream()
                .map(workout -> {
                    TrainingPlan trainingPlan = trainingPlanService.getTrainingPlanByIdForWorkoutHistory(workout.getTrainingId(), UUID.fromString(userDetails.getUsername()));
                    return workoutMapper.workoutToWorkoutDTO(workout, trainingPlan);
                })
                .toList();

        return ResponseEntity.ok(workoutDTOS);
    }

    @PostMapping
    public ResponseEntity<MessageResponse> createWorkout(
            @Valid @RequestBody WorkoutCreationRequest workoutCreationRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        workoutService.createWorkout(workoutCreationRequest, UUID.fromString(userDetails.getUsername()));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse(
                        messageSource.getMessage("message-response.workout-created-successfully", null, LocaleContextHolder.getLocale())
                ));
    }

    @GetMapping("/exercises/{exerciseId}/history")
    public ResponseEntity<WorkoutExerciseHistoryDTO> getWorkoutExerciseHistory(
            @PathVariable("exerciseId") long exerciseId,

            @Valid
            @Positive(message = "{previous-workouts-parameter.positive}")
            @Max(value = 10, message = "{previous-workouts-parameter.max}")
            @RequestParam(name = "previousWorkouts", defaultValue = "3") int previousWorkouts,

            @AuthenticationPrincipal UserDetails userDetails) {
        List<Workout> lastWorkoutsContainingExercise = workoutService.getWorkoutExerciseHistory(
                exerciseId,
                previousWorkouts,
                UUID.fromString(userDetails.getUsername())
        );

        WorkoutExerciseHistoryDTO workoutExerciseHistoryDTO = workoutMapper.toWorkoutExerciseHistoryDTO(exerciseId, lastWorkoutsContainingExercise);
        return ResponseEntity.status(HttpStatus.OK)
                .body(workoutExerciseHistoryDTO);
    }

    @GetMapping("/exercises/{exerciseId}/history/period")
    public ResponseEntity<WorkoutExerciseHistoryDTO> getWorkoutExerciseHistoryByWorkoutInPeriod(
            @PathVariable("exerciseId") long exerciseId,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<Workout> workouts = workoutService.getWorkoutExerciseHistoryByWorkoutInPeriod(
                exerciseId,
                startDate,
                endDate,
                UUID.fromString(userDetails.getUsername())
        );

        WorkoutExerciseHistoryDTO workoutExerciseHistoryDTO = workoutMapper.toWorkoutExerciseHistoryDTO(exerciseId, workouts);
        return ResponseEntity.status(HttpStatus.OK)
                .body(workoutExerciseHistoryDTO);
    }

    @GetMapping("/trainings/{trainingId}/history/period")
    public ResponseEntity<WorkoutTrainingHistoryDTO> getWorkoutTrainingHistory(
            @PathVariable("trainingId") long trainingId,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<Workout> workouts = workoutService.getWorkoutTrainingHistory(
                trainingId,
                startDate,
                endDate,
                UUID.fromString(userDetails.getUsername())
        );

        WorkoutTrainingHistoryDTO workoutTrainingHistoryDTO = workoutMapper.toWorkoutTrainingHistoryDTO(trainingId, workouts);
        return ResponseEntity.status(HttpStatus.OK)
                .body(workoutTrainingHistoryDTO);
    }
}
