package com.gymtracker.app.mapper;

import com.gymtracker.app.domain.RefreshToken;
import com.gymtracker.app.entity.RefreshTokenEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = UserMapper.class)
public interface RefreshTokenMapper {
    @Mapping(target = "user", source = "user", qualifiedByName = "userWithoutCollections")
    RefreshToken refreshTokenEntityToRefreshToken(RefreshTokenEntity refreshTokenEntity);
}
