package com.mf.auth.adapter.in.rest.api;

import com.mf.auth.adapter.in.rest.entity.ErrorResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * YTMusicAuthAPI interface represents the API endpoints to authenticate into YouTube Music.
 * <p>
 * All methods in this interface are designed to be used over HTTP.
 */
@Api(tags = "YouTube Music authentication API", protocols = "http")
@RequestMapping(path = "/api/auth/ytmusic")
public interface YTMusicAuthAPI {

    @GetMapping("/redirect")
    @ResponseStatus(HttpStatus.FOUND)
    @ApiOperation(
        value = "Redirect to YouTube Music OAuth2 consent page. "
            + "YouTube Music is going to redirect to /api/auth/ytmusic/callback when user logs in.",
        response = String.class
    )
    @ApiResponses(value = {
        @ApiResponse(
            code = 200,
            message = "Successfully acquired redirect URL",
            response = String.class
        ),
        @ApiResponse(
            code = 401,
            message = "Invalid JWT provided",
            response = ErrorResponse.class
        ),
        @ApiResponse(
            code = 500,
            message = "Response in case of an unknown internal error",
            response = ErrorResponse.class
        )
    })
    String redirectToAuth(
        @RequestParam
        @ApiParam(value = "Used to redirect back to client"
            + " application after successfully login", required = true)
        String redirectUrl,

        @RequestParam(required = false)
        @ApiParam("JWT")
        String jwtToUpdate
    ) throws Exception;

    @GetMapping("/callback")
    @ResponseStatus(HttpStatus.FOUND)
    @ApiOperation(value = "Handle YouTube Music OAuth2 callback. "
        + "This endpoint is triggered by YouTube Music during OAuth2 authorization flow."
        + "Redirects user back to client application setting "
        + "JWT single-use access token as a URL parameter."
    )
    @ApiResponses(value = {
        @ApiResponse(
            code = 200,
            message = "Successfully handled OAuth2 callback",
            response = String.class
        ),
        @ApiResponse(
            code = 401,
            message = "Invalid UUID or JWT provided",
            response = ErrorResponse.class
        ),
        @ApiResponse(
            code = 403,
            message = "User didn't give permission to access his YouTube Music account",
            response = ErrorResponse.class
        ),
        @ApiResponse(
            code = 500,
            message = "Response in case of an unknown internal error",
            response = ErrorResponse.class
        )
    })
    String callback(
        @RequestParam(required = false)
        @ApiParam("OAuth2 access code issued by YouTube Music")
        String code,

        @RequestParam(required = false)
        @ApiParam("Error status")
        String error,

        @RequestParam
        @ApiParam(value = "OAuth2 state", required = true)
        String state
    ) throws Exception;
}
