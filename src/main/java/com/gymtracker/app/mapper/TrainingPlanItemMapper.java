package com.gymtracker.app.mapper;

import com.gymtracker.app.domain.TrainingPlan;
import com.gymtracker.app.dto.response.TrainingPlanDTO;
import com.gymtracker.app.entity.PlanItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = ExerciseMapper.class)
public interface TrainingPlanItemMapper {
    PlanItemEntity trainingPlanItemToPlanItemEntity(TrainingPlan.PlanItem trainingPlanItem);
    TrainingPlan.PlanItem planItemEntityToTrainingPlanItem(PlanItemEntity planItemEntity);

    @Mapping(target = "exerciseId", source = "exercise.exerciseId")
    @Mapping(target = "exerciseName", source = "exercise.name")
    TrainingPlanDTO.PlanItemDTO planItemToPlanItemDTO(TrainingPlan.PlanItem planItem);
}
