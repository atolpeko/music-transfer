package com.mf.serviceconfig.rest.controller;

import com.mf.serviceconfig.rest.properties.SwaggerProperties;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
@RequestMapping("/")
@RequiredArgsConstructor
public class SwaggerController {

    private final SwaggerProperties properties;

    @GetMapping
    public String swaggerRedirect() {
        return "redirect:" + properties.swaggerUrl();
    }
}
