package com.mf.auth.adapter.in.rest.mapper;

import com.mf.auth.adapter.in.rest.entity.JWTRestEntity;
import com.mf.auth.domain.entity.JWT;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JWTMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accessToken", ignore = true)
    JWT toEntity(JWTRestEntity restEntity);

    JWTRestEntity toRestEntity(JWT entity);
}
