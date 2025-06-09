package com.mifincaapp.product.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
public class DotenvTestConfig {

    @PostConstruct
    public void loadDotenv() {
        Dotenv dotenv = Dotenv.configure().load();
        dotenv.entries().forEach(entry ->
            System.setProperty(entry.getKey(), entry.getValue())
        );
    }
}