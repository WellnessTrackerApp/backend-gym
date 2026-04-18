package com.gymtracker.app.mapper;

import com.gymtracker.app.domain.TrainingPlan;
import com.gymtracker.app.dto.response.TrainingPlanDTO;
import com.gymtracker.app.entity.TrainingPlanEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = TrainingPlanItemMapper.class)
public interface TrainingPlanMapper {
    @Mapping(target = "ownerId", source = "owner.userId")
    @Mapping(target = "isCustom", source = "custom")
    TrainingPlan trainingPlanEntityToTrainingPlan(TrainingPlanEntity entity);

    @Mapping(target = "owner.userId", source = "ownerId")
    @Mapping(target = "isCustom", source = "custom")
    TrainingPlanEntity trainingPlanToTrainingPlanEntity(TrainingPlan trainingPlan);

    @Mapping(target = "isCustom", source = "custom")
    TrainingPlanDTO trainingPlanToTrainingPlanDTO(TrainingPlan trainingPlan);
}
