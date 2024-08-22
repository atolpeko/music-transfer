package com.mf.api.adapter.in.rest.mapper;

import com.mf.api.adapter.in.rest.entity.PlaylistRestEntity;
import com.mf.api.domain.entity.Playlist;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = TrackMapper.class)
public interface PlaylistMapper {

    Playlist toEntity(PlaylistRestEntity restEntity);

    PlaylistRestEntity toRestEntity(Playlist playlist);
}
