package com.mf.api.adapter.in.rest.api;

import com.mf.api.adapter.in.rest.entity.ErrorResponse;
import com.mf.api.adapter.in.rest.entity.PlaylistRestEntity;
import com.mf.api.adapter.in.rest.entity.TrackRestEntity;
import com.mf.api.adapter.in.rest.entity.TracksRestEntity;
import com.mf.api.adapter.in.rest.entity.TransferResultRestEntity;
import com.mf.api.adapter.in.rest.entity.MusicService;
import com.mf.api.util.Page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * TransferAPI interface represents the API endpoints to run
 * music transfer between streaming music services.
 * <p>
 * All methods in this interface are designed to be used over HTTP.
 */
@Api(tags = "Transfer API", protocols = "http")
@RequestMapping(path = "/api/transfer")
public interface TransferAPI {

    @GetMapping("/tracks")
    @ApiOperation("Get liked tracks available for transfer")
    @ApiResponses(value = {
        @ApiResponse(
            code = 200,
            message = "Successfully retrieved tracks",
            response = TransferResultRestEntity.class
        ),
        @ApiResponse(
            code = 401,
            message = "Invalid auth token provided",
            response = ErrorResponse.class
        ),
        @ApiResponse(
            code = 500,
            message = "Response in case of an unknown internal error",
            response = ErrorResponse.class
        )
    })
    Page<TrackRestEntity> availableTracks(

        @RequestParam
        @ApiParam(value = "Music service", required = true)
        MusicService service,

        @RequestHeader("Authorization")
        String authToken,

        @RequestParam(required = false)
        @ApiParam(value = "Next page identifier")
        String next
    );

    @PostMapping("/tracks")
    @ApiOperation("Transfer tracks. Limit - 25 tracks at once.")
    @ApiResponses(value = {
        @ApiResponse(
            code = 200,
            message = "Successfully transferred tracks",
            response = TransferResultRestEntity.class
        ),
        @ApiResponse(
            code = 400,
            message = "Invalid tracks number: 0 or more than 25",
            response = ErrorResponse.class
        ),
        @ApiResponse(
            code = 401,
            message = "Invalid auth token provided",
            response = ErrorResponse.class
        ),
        @ApiResponse(
            code = 500,
            message = "Response in case of an unknown internal error",
            response = ErrorResponse.class
        )
    })
    TransferResultRestEntity<List<TrackRestEntity>> transferTracks(

        @RequestParam
        @ApiParam(value = "Transfer from", required = true)
        MusicService source,

        @RequestParam
        @ApiParam(value = "Transfer to", required = true)
        MusicService target,

        @Valid
        @RequestBody
        TracksRestEntity tracks,

        @RequestHeader("Authorization")
        String authToken
    );

    @GetMapping("/playlists")
    @ApiOperation("Get playlists available for transfer")
    @ApiResponses(value = {
        @ApiResponse(
            code = 200,
            message = "Successfully retrieved playlists",
            response = TransferResultRestEntity.class
        ),
        @ApiResponse(
            code = 401,
            message = "Invalid auth token provided",
            response = ErrorResponse.class
        ),
        @ApiResponse(
            code = 500,
            message = "Response in case of an unknown internal error",
            response = ErrorResponse.class
        )
    })
    Page<PlaylistRestEntity> availablePlaylists(

        @RequestParam
        @ApiParam(value = "Music service", required = true)
        MusicService service,

        @RequestHeader("Authorization")
        String authToken,

        @RequestParam(required = false)
        @ApiParam(value = "Next page identifier")
        String next
    );

    @GetMapping("/playlists/{playlistId}/tracks")
    @ApiOperation("Get playlist tracks available for transfer")
    @ApiResponses(value = {
        @ApiResponse(
            code = 200,
            message = "Successfully retrieved tracks",
            response = TransferResultRestEntity.class
        ),
        @ApiResponse(
            code = 401,
            message = "Invalid auth token provided",
            response = ErrorResponse.class
        ),
        @ApiResponse(
            code = 500,
            message = "Response in case of an unknown internal error",
            response = ErrorResponse.class
        )
    })
    Page<TrackRestEntity> availablePlaylistTracks(

        @RequestParam
        @ApiParam(value = "Music service", required = true)
        MusicService service,

        @RequestHeader("Authorization")
        String authToken,

        @PathVariable
        @ApiParam(value = "Playlist ID", required = true)
        String playlistId,

        @RequestParam(required = false)
        @ApiParam(value = "Next page identifier")
        String next
    );

    @PostMapping("/playlists")
    @ApiOperation("Transfer playlists")
    @ApiResponses(value = {
        @ApiResponse(
            code = 200,
            message = "Successfully transferred playlists",
            response = TransferResultRestEntity.class
        ),
        @ApiResponse(
            code = 401,
            message = "Invalid auth token provided",
            response = ErrorResponse.class
        ),
        @ApiResponse(
            code = 500,
            message = "Response in case of an unknown internal error",
            response = ErrorResponse.class
        )
    })
    TransferResultRestEntity<PlaylistRestEntity> transferPlaylist(

        @RequestParam
        @ApiParam(value = "Transfer from", required = true)
        MusicService source,

        @RequestParam
        @ApiParam(value = "Transfer to", required = true)
        MusicService target,

        @Valid
        @RequestBody
        PlaylistRestEntity playlist,

        @RequestHeader("Authorization")
        String authToken
    );
}
