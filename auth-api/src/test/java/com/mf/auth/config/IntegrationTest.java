package com.mf.auth.config;

import com.mf.auth.boot.Application;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Tag;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ActiveProfiles("test")
@Tag("category.IntegrationTest")
@SpringBootTest(classes = Application.class)
@Import(WireMockConfig.class)
@AutoConfigureMockMvc
public @interface IntegrationTest {

}
