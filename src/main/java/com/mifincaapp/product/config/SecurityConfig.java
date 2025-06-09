package com.mifincaapp.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final ApiKeyFilter apiKeyFilter;

    public SecurityConfig(ApiKeyFilter apiKeyFilter) {
        this.apiKeyFilter = apiKeyFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                    // Permitir GET y POST en /finca/**
                    .requestMatchers(HttpMethod.GET, "/finca/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/finca/**").permitAll()

                    // Permitir todos los métodos en /productos/**
                    .requestMatchers(HttpMethod.GET, "/productos/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/productos/**").permitAll()
                    .requestMatchers(HttpMethod.PUT, "/productos/**").permitAll()
                    .requestMatchers(HttpMethod.DELETE, "/productos/**").permitAll()


                    // Otras rutas públicas generales si las necesitas
                    .requestMatchers(HttpMethod.GET, "/", "/api/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/upload").permitAll()
                    .requestMatchers(HttpMethod.DELETE, "/api/**").permitAll()
                    
                    // Rutas de Ventas
                    .requestMatchers(HttpMethod.GET, "/ventas/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/ventas/**").permitAll()
                        
                    // Rutas del Admin
                    .requestMatchers(HttpMethod.GET, "/Admin/**").permitAll()

                    // Todo lo demás requiere autenticación
                    .anyRequest().authenticated()
                )
                .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}