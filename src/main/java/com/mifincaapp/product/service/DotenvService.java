package com.mifincaapp.product.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;


@Component
public class DotenvService {

    private final Dotenv dotenv;

    public DotenvService() {
        this.dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMissing()
                .ignoreIfMalformed()
                .load();
    }

    public String get(String key) {
        return dotenv.get(key);
    }

    public String getOrThrow(String key) {
        String value = dotenv.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Variable de entorno no encontrada: " + key);
        }
        return value;
    }
}
