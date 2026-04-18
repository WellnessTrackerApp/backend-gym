package com.gymtracker.app.mapper;

import com.gymtracker.app.domain.workout.WorkoutItem;
import com.gymtracker.app.domain.workout.WorkoutRepetitionItem;
import com.gymtracker.app.dto.request.WorkoutItemDTO;
import com.gymtracker.app.dto.request.WorkoutRepetitionItemDTO;
import com.gymtracker.app.dto.response.WorkoutDTO;
import com.gymtracker.app.entity.workout.WorkoutItemEntity;
import com.gymtracker.app.entity.workout.WorkoutRepetitionItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION,
        uses = ExerciseMapper.class
)
public interface WorkoutItemMapper {
    @SubclassMapping(source = WorkoutRepetitionItemDTO.class, target = WorkoutRepetitionItem.class)
    WorkoutItem workoutItemDTOToWorkoutItem(WorkoutItemDTO workoutItemDTO);

    @Mapping(target = "exercise.exerciseId", source = "exerciseId")
    WorkoutRepetitionItem workoutRepetitionItemDTOToWorkoutRepetitionItem(WorkoutRepetitionItemDTO workoutRepetitionItemDTO);

    @SubclassMapping(source = WorkoutRepetitionItem.class, target = WorkoutRepetitionItemEntity.class)
    WorkoutItemEntity workoutItemToWorkoutItemEntity(WorkoutItem workoutItem);

    @Mapping(target = "id", ignore = true)
    WorkoutRepetitionItemEntity workoutRepetitionItemToWorkoutRepetitionItemEntity(WorkoutRepetitionItem workoutRepetitionItem);

    @SubclassMapping(source = WorkoutRepetitionItemEntity.class, target = WorkoutRepetitionItem.class)
    WorkoutItem workoutItemEntityToWorkoutItem(WorkoutItemEntity workoutItemEntity);
    WorkoutRepetitionItem workoutRepetitionItemEntityToWorkoutRepetitionItem(WorkoutRepetitionItemEntity workoutRepetitionItemEntity);

    @SubclassMapping(source = WorkoutRepetitionItem.class, target = WorkoutDTO.WorkoutRepetitionItemDTO.class)
    WorkoutDTO.WorkoutItemDTO workoutItemToWorkoutItemDTO(WorkoutItem workoutItem);
}
