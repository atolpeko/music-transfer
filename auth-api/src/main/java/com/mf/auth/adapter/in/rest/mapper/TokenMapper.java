package com.mf.auth.adapter.in.rest.mapper;

import com.mf.auth.adapter.in.rest.entity.TokenRestEntity;
import com.mf.auth.domain.entity.Token;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TokenMapper {

    TokenRestEntity toRestEntity(Token entity);
}
