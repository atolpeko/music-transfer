package com.mf.auth.adapter.in.rest.api;

import com.mf.auth.adapter.in.rest.entity.ErrorResponse;
import com.mf.auth.adapter.in.rest.valueobject.MusicService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.hibernate.validator.constraints.URL;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Authentication interface represents the API endpoints to authenticate into a music service.
 * <p>
 * All methods in this interface are designed to be used over HTTP.
 */
@Validated
@Api(tags = "Authentication API", protocols = "http")
@RequestMapping(path = "/api/auth")
public interface AuthAPI {

    @GetMapping("/redirect")
    @ResponseStatus(HttpStatus.FOUND)
    @ApiOperation(
        value = "Redirect to the music service OAuth2 consent page. "
            + "When following the redirect a JWT access token is going to be "
            + "returned as a URL parameter of the provided redirectUrl",
        response = String.class
    )
    @ApiResponses(value = {
        @ApiResponse(
            code = 200,
            message = "Successfully acquired redirect URL",
            response = String.class
        ),
        @ApiResponse(
            code = 400,
            message = "Invalid music service provided",
            response = ErrorResponse.class
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
        @ApiParam(value = "Music service to authenticate into", required = true)
        MusicService service,

        @Valid
        @URL(message = "Redirect URl should be valid")
        @RequestParam
        @ApiParam(value = "Redirect URL", required = true)
        String redirectUrl,

        @RequestParam(required = false)
        @ApiParam("JWT")
        String jwt
    );
}
