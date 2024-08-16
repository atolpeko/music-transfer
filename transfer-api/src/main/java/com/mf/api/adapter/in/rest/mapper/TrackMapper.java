package com.mf.api.adapter.in.rest.mapper;

import com.mf.api.adapter.in.rest.entity.TrackRestEntity;
import com.mf.api.domain.entity.Track;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrackMapper {

    TrackRestEntity toRestEntity(Track track);
}
