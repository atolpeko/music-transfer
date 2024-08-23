package com.mf.serviceconfig.rest;

import com.mf.serviceconfig.rest.entity.ErrorResponse;
import com.mf.serviceconfig.rest.entity.Response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ServiceConfigAPI interface represents the API endpoints to
 * query for available services alongside their properties.
 * <p>
 * All methods in this interface are designed to be used over HTTP.
 */
@Api(tags = "Service config API", protocols = "http")
@RequestMapping(path = "/api/services")
public interface ServiceConfigAPI {

    @GetMapping
    @ApiOperation(
        value = "Query for available services and their properties",
        response = String.class
    )
    @ApiResponses(value = {
        @ApiResponse(
            code = 200,
            message = "Successfully acquired information",
            response = Response.class
        ),
        @ApiResponse(
            code = 500,
            message = "Response in case of an unknown internal error",
            response = ErrorResponse.class
        )
    })
    Response get();
}
