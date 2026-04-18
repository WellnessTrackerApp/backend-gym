package com.gymtracker.app.mapper;

import com.gymtracker.app.domain.Password;
import com.gymtracker.app.domain.User;
import com.gymtracker.app.dto.request.SignUp;
import com.gymtracker.app.dto.response.UserProfileResponse;
import com.gymtracker.app.entity.UserEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {ExerciseMapper.class, TrainingPlanMapper.class})
public interface UserMapper {
    User signUpToUser(SignUp signUp);

    @Mapping(target = "password", ignore = true)
    User userEntityToUser(UserEntity userEntity);

    @Mapping(target = "exercises", ignore = true)
    @Mapping(target = "plans", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Named("userWithoutCollections")
    User userEntityToUserWithoutCollections(UserEntity userEntity);

    @AfterMapping
    default void handlePasswordMapping(UserEntity userEntity, @MappingTarget User user) {
        user.updatePassword(userEntity.getPasswordHash());
    }

    @Mapping(target = "passwordHash", source = "password")
    @Mapping(target = "username", source = "displayUsername")
    @Mapping(target = "createdAt", ignore = true)
    UserEntity userToUserEntity(User user);

    default Password map(String value) {
        return new Password(value);
    }

    @Mapping(target = "username", source = "displayUsername")
    UserProfileResponse userToUserProfileResponse(User user);
}
