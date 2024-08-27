package com.mf.api.adapter.in.rest.mapper;

import com.mf.api.adapter.in.rest.entity.PlaylistRestEntity;
import com.mf.api.domain.entity.Playlist;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = TrackMapper.class)
public interface PlaylistMapper {

    @Mapping(source = "id", target = "serviceId")
    Playlist toEntity(PlaylistRestEntity restEntity);

    @Mapping(source = "serviceId", target = "id")
    PlaylistRestEntity toRestEntity(Playlist playlist);
}
