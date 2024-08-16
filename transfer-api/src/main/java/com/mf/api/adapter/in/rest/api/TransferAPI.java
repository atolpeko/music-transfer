package com.mf.api.adapter.in.rest.api;

import com.mf.api.adapter.in.rest.entity.ErrorResponse;
import com.mf.api.adapter.in.rest.entity.TransferResult;
import com.mf.api.adapter.in.rest.entity.MusicService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * TransferAPI interface represents the API endpoints to run
 * music transfer between streaming music services.
 * <p>
 * All methods in this interface are designed to be used over HTTP.
 */
@Validated
@Api(tags = "Transfer API", protocols = "http")
@RequestMapping(path = "/api/transfer")
public interface TransferAPI {

    @PostMapping
    @ApiOperation("Run music transfer.")
    @ApiResponses(value = {
        @ApiResponse(
            code = 200,
            message = "Successfully transferred music",
            response = TransferResult.class
        ),
        @ApiResponse(
            code = 401,
            message = "Invalid auth token provided",
            response = ErrorResponse.class
        ),
        @ApiResponse(
            code = 400,
            message = "Invalid transfer request",
            response = ErrorResponse.class
        ),
        @ApiResponse(
            code = 500,
            message = "Response in case of an unknown internal error",
            response = ErrorResponse.class
        )
    })
    TransferResult transfer(

        @RequestParam
        @ApiParam(value = "Source music service", required = true)
        MusicService source,

        @RequestParam
        @ApiParam(value = "Target music service", required = true)
        MusicService target,

        @RequestParam(required = false, defaultValue = "false")
        @ApiParam("Transfer playlists?")
        Boolean transferPlaylists,

        @RequestHeader("Authorization")
        String authToken
    );
}
