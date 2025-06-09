package com.mifincaapp.product.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UsuarioService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final String usuarioApiUrl;

    public UsuarioService(RestTemplate restTemplate, ObjectMapper objectMapper, DotenvService dotenvService) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.usuarioApiUrl = dotenvService.get("USUARIO_API_URL");
    }

    public Long obtenerIdUsuarioDesdeToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("USER-MIFINCA-CLIENT", "mifincaapp-mobile-android");
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
            usuarioApiUrl,
            HttpMethod.GET,
            entity,
            String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                String jsonBody = response.getBody();
                JsonNode root = objectMapper.readTree(jsonBody);
                Long idUsuario = root.path("id").asLong();
                return idUsuario;
            } catch (Exception e) {
                throw new RuntimeException("Error al parsear id_usuario del JSON", e);
            }
        } else {
            throw new RuntimeException("Error al obtener perfil usuario desde API externa");
        }
    }
}

