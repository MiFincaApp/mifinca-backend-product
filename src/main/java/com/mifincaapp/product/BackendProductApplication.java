package com.mifincaapp.product;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendProductApplication {

    public static void main(String[] args) {
        // Detectar si estamos en entorno local
        String envType = System.getenv("ENV_TYPE");

        if ("local".equalsIgnoreCase(envType)) {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
            });

            System.out.println("Entorno local: variables de .env cargadas.");
        } else {
            System.out.println("Entorno producción: no se carga .env.");
        }

        SpringApplication.run(BackendProductApplication.class, args);
    }
}
