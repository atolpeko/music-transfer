package com.mf.api.boot.config.web;

import com.mf.api.boot.config.properties.SpringSwaggerProperties;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@RequiredArgsConstructor
public class SwaggerConfig {

    @Bean
    public Docket api(SpringSwaggerProperties swaggerProperties) {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getControllersPackage()))
            .paths(PathSelectors.any())
            .build()
            .apiInfo(apiInfo(swaggerProperties))
            .ignoredParameterTypes(
                java.lang.Module.class,
                org.springframework.context.ApplicationContext.class
            );
    }

    private ApiInfo apiInfo(SpringSwaggerProperties swaggerProperties) {
        return new ApiInfoBuilder()
            .title(swaggerProperties.getTitle())
            .description(swaggerProperties.getDescription())
            .version(swaggerProperties.getVersion())
            .build();
    }
}

