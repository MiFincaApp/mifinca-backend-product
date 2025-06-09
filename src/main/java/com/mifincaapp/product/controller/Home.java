package com.mifincaapp.product.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Home {
    
    @GetMapping("/")
    public Map<String, String> bienvenida () {
        return Map.of(
                "mensaje", "¡Hello welcome!"
        );
    }
}
