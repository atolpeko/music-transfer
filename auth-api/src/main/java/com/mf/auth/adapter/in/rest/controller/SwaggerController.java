package com.mf.auth.adapter.in.rest.controller;

import com.mf.auth.adapter.in.rest.RestProperties;

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
    private final RestProperties properties;

    @GetMapping
    public String swaggerRedirect() {
        return "redirect:" + properties.swaggerUrl();
    }
}
