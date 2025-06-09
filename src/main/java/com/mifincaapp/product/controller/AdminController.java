package com.mifincaapp.product.controller;

import com.mifincaapp.product.dto.AdminInformeResponseDTO;
import com.mifincaapp.product.dto.FincaConProductosDTO;
import com.mifincaapp.product.dto.ProductoDTOPlano;
import com.mifincaapp.product.model.Finca;
import com.mifincaapp.product.model.InformeVenta;
import com.mifincaapp.product.model.Producto;
import com.mifincaapp.product.repository.FincaRepository;
import com.mifincaapp.product.repository.InformeVentasRepository;
import com.mifincaapp.product.repository.ProductoRepository;
import com.mifincaapp.product.service.UsuarioService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private final FincaRepository fincaRepository;
    private final ProductoRepository productoRepository;
    private final InformeVentasRepository informeVentasRepository;
    private final UsuarioService usuarioService;

    public AdminController(
            FincaRepository fincaRepository,
            ProductoRepository productoRepository,
            InformeVentasRepository informeVentasRepository,
            UsuarioService usuarioService) {
        this.fincaRepository = fincaRepository;
        this.productoRepository = productoRepository;
        this.informeVentasRepository = informeVentasRepository;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/informes/general")
    public ResponseEntity<?> getInformesYFincasParaAdmin(
            @RequestHeader("Authorization") String authorizationHeader) {

        try {
            // Extraer ID desde token
            String token = authorizationHeader.replace("Bearer ", "").trim();
            Long adminId = usuarioService.obtenerIdUsuarioDesdeToken(token);

            // Obtener todas las fincas
            List<Finca> fincas = fincaRepository.findAll();

            // Obtener productos por finca y mapear a DTOs
            List<FincaConProductosDTO> fincaConProductos = fincas.stream().map(finca -> {
                List<Producto> productos = productoRepository.findByFincaId(finca.getId());

                // Convertimos productos a DTO plano
                List<ProductoDTOPlano> productosDTO = productos.stream()
                    .map(ProductoDTOPlano::new)
                    .collect(Collectors.toList());

                return new FincaConProductosDTO(
                    finca.getId(),
                    finca.getUsuariosId(),
                    finca.getNombre(),
                    finca.getUbicacion(),
                    productosDTO
                );
            }).collect(Collectors.toList());

            // Obtener todos los informes
            List<InformeVenta> informes = informeVentasRepository.findAll();

            // Construir respuesta
            AdminInformeResponseDTO respuesta = new AdminInformeResponseDTO(fincaConProductos, informes);

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener datos para el admin: " + e.getMessage());
        }
    }
}
