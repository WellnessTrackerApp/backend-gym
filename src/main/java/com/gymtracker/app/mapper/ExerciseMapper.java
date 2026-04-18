package com.gymtracker.app.mapper;

import com.gymtracker.app.domain.Exercise;
import com.gymtracker.app.dto.request.ExerciseCreationRequest;
import com.gymtracker.app.dto.response.ExerciseDTO;
import com.gymtracker.app.entity.ExerciseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExerciseMapper {
    Exercise exerciseCreationRequestToExercise(ExerciseCreationRequest exerciseCreationRequest);
    @Mapping(source = "custom", target = "isCustom")
    ExerciseDTO exerciseToExerciseDTO(Exercise exercise);

    @Mapping(source = "ownerId", target = "owner.userId")
    @Mapping(source = "custom", target = "isCustom")
    ExerciseEntity exerciseToExerciseEntity(Exercise exercise);

    @Mapping(source = "custom", target = "isCustom")
    @Mapping(source = "owner.userId", target = "ownerId")
    Exercise exerciseEntityToExercise(ExerciseEntity exerciseEntity);
}
