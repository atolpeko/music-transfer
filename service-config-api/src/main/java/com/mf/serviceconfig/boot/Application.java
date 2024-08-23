package com.mf.serviceconfig.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.mf.serviceconfig")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
