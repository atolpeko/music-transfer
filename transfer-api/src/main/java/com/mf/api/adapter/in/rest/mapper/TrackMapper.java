package com.mf.api.adapter.in.rest.mapper;

import com.mf.api.adapter.in.rest.entity.TrackRestEntity;
import com.mf.api.domain.entity.Track;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrackMapper {

    @Mapping(source = "id", target = "serviceId")
    Track toEntity(TrackRestEntity restEntity);

    @Mapping(source = "serviceId", target = "id")
    TrackRestEntity toRestEntity(Track track);
}
