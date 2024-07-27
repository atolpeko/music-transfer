package com.mf.auth.adapter.in.rest.api;

import com.mf.auth.adapter.in.rest.entity.ErrorResponse;
import com.mf.auth.adapter.in.rest.entity.JWTRestEntity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * TokenAPI interface represents the API endpoints to work with access tokens.
 * <p>
 * All methods in this interface are designed to be used over HTTP.
 */
@Validated
@Api(tags = "Authorization token API", protocols = "http")
@RequestMapping(path = "/api/auth/token")
public interface TokenAPI {

    @GetMapping("/get")
    @ApiOperation("Obtain a JWT using a single-use access token.")
    @ApiResponses(value = {
        @ApiResponse(
            code = 200,
            message = "Successfully acquired JWT",
            response = JWTRestEntity.class
        ),
        @ApiResponse(
            code = 401,
            message = "Invalid access token provided",
            response = JWTRestEntity.class
        ),
        @ApiResponse(
            code = 500,
            message = "Response in case of an unknown internal error",
            response = ErrorResponse.class
        )
    })
    JWTRestEntity getJwt(
        @RequestParam
        @ApiParam(value = "JWT access token", required = true)
        String accessToken
    );

    @GetMapping("/validate")
    @ApiOperation("Validate JWT")
    @ApiResponses(value = {
        @ApiResponse(
            code = 200,
            message = "JWT is valid"
        ),
        @ApiResponse(
            code = 401,
            message = "Invalid JWT provided"
        ),
        @ApiResponse(
            code = 500,
            message = "Response in case of an unknown internal error",
            response = ErrorResponse.class
        )
    })
    ResponseEntity<String> validateJwt(
        @RequestParam
        @ApiParam(value = "JWT access token", required = true)
        String accessToken
    );
}
