package com.gymtracker.app.mapper;

import com.gymtracker.app.domain.TrainingPlan;
import com.gymtracker.app.domain.workout.Workout;
import com.gymtracker.app.domain.workout.WorkoutItem;
import com.gymtracker.app.domain.workout.WorkoutRepetitionItem;
import com.gymtracker.app.dto.request.WorkoutCreationRequest;
import com.gymtracker.app.dto.response.WorkoutDTO;
import com.gymtracker.app.dto.response.WorkoutExerciseHistoryDTO;
import com.gymtracker.app.dto.response.WorkoutSessionSnapshot;
import com.gymtracker.app.dto.response.WorkoutTrainingHistoryDTO;
import com.gymtracker.app.entity.workout.WorkoutEntity;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;
import java.util.function.Predicate;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {WorkoutItemMapper.class, TrainingPlanMapper.class})
public interface WorkoutMapper {
    @Mapping(target = "user.userId", source = "userId")
    @Mapping(target = "training.id", source = "trainingId")
    WorkoutEntity workoutToWorkoutEntity(Workout workout);

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "trainingId", source = "training.id")
    Workout workoutEntityToWorkout(WorkoutEntity workoutEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "userId", ignore = true)
    Workout workoutCreationRequestToWorkout(WorkoutCreationRequest workoutCreationRequest);

    @Mapping(target = "workoutId", source = "workout.id")
    @Mapping(target = "trainingPlan", source = "trainingPlan")
    WorkoutDTO workoutToWorkoutDTO(Workout workout, TrainingPlan trainingPlan);

    default WorkoutExerciseHistoryDTO toWorkoutExerciseHistoryDTO(Long exerciseId, List<Workout> workouts) {
        return WorkoutExerciseHistoryDTO.builder()
                .exerciseId(exerciseId)
                .history(workoutsToWorkoutSessionSnapshots(workouts, exerciseId))
                .build();
    }

    default WorkoutTrainingHistoryDTO toWorkoutTrainingHistoryDTO(long trainingId, List<Workout> workouts) {
        List<WorkoutSessionSnapshot> snapshots = workouts.stream()
                .map(workout -> WorkoutSessionSnapshot.builder()
                        .workoutId(workout.getId())
                        .workoutDate(workout.getCreatedAt())
                        .sets(extractSetsWithFilter(workout, workoutItem -> true))
                        .build())
                .toList();

        return WorkoutTrainingHistoryDTO.builder()
                .trainingId(trainingId)
                .history(snapshots)
                .build();
    }

    default List<WorkoutSessionSnapshot> workoutsToWorkoutSessionSnapshots(List<Workout> workouts, Long exerciseId) {
        return workouts.stream()
                .map(workout -> WorkoutSessionSnapshot.builder()
                        .workoutId(workout.getId())
                        .workoutDate(workout.getCreatedAt())
                        .sets(
                                extractSetsWithFilter(
                                        workout,
                                        workoutItem -> workoutItem.getExercise().getExerciseId().equals(exerciseId)
                                )
                        )
                        .build())
                .toList();
    }

    default List<WorkoutSessionSnapshot.SetDetail> extractSetsWithFilter(Workout workout, Predicate<WorkoutItem> predicate) {
        return workout.getWorkoutItems().stream()
                .filter(predicate)
                .filter(WorkoutRepetitionItem.class::isInstance)
                .flatMap(workoutItem -> ((WorkoutRepetitionItem) workoutItem).getSets().stream())
                .map(exerciseSet -> WorkoutSessionSnapshot.SetDetail.builder()
                        .reps(exerciseSet.reps())
                        .weight(exerciseSet.weight())
                        .build())
                .toList();
    }
}
