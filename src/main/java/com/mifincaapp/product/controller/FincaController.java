package com.mifincaapp.product.controller;

import com.mifincaapp.product.model.Finca;                 // Para usar la entidad Finca
import com.mifincaapp.product.model.Producto;               // Para usar la entidad Producto
import com.mifincaapp.product.repository.FincaRepository;   // Para interactuar con el repositorio de Finca
import com.mifincaapp.product.repository.ProductoRepository; // Para interactuar con el repositorio de Producto
import com.mifincaapp.product.service.UsuarioService;       // Para usar el servicio que obtiene el ID del usuario desde token
import com.mifincaapp.product.dto.FincaConProductosDTO;     // DTO para devolver finca junto a sus productos
import com.mifincaapp.product.dto.ProductoDTOPlano;

import org.springframework.http.HttpStatus;                     // Para usar códigos de estado HTTP (ej. 404, 401)
import org.springframework.http.ResponseEntity;                 // Para construir respuestas HTTP con cuerpo y código de estado
import org.springframework.web.bind.annotation.CrossOrigin;     // Para manejar CORS
import org.springframework.web.bind.annotation.GetMapping;       // Para la anotación @GetMapping
import org.springframework.web.bind.annotation.RequestHeader;   // Para obtener cabeceras HTTP como Authorization
import org.springframework.web.bind.annotation.RequestMapping;  // Para la anotación @RequestMapping
import org.springframework.web.bind.annotation.RestController;  // Para marcar la clase como controlador REST

import java.util.List;                                            // Para usar listas de productos
import java.util.Optional;                                        // Para manejar valores que pueden ser nulos (Optional)
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/finca")
@CrossOrigin(origins = "*")
public class FincaController {

    private final FincaRepository fincaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioService usuarioService;

    public FincaController(FincaRepository fincaRepository, ProductoRepository productoRepository, UsuarioService usuarioService) {
        this.fincaRepository = fincaRepository;
        this.productoRepository = productoRepository;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<?> getFincaPorToken(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");
            Long idUsuario = usuarioService.obtenerIdUsuarioDesdeToken(token);

            Optional<Finca> fincaOpt = fincaRepository.findByUsuariosId(idUsuario);
            if (fincaOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Finca no encontrada para usuario");
            }

            Finca finca = fincaOpt.get();
            
            List<Producto> productos = productoRepository.findByFincaId(finca.getId());
            List<ProductoDTOPlano> productosDTO = productos.stream()
                .map(ProductoDTOPlano::new)
                .toList();

            FincaConProductosDTO dto = new FincaConProductosDTO(
                finca.getId(),
                finca.getUsuariosId(),
                finca.getNombre(),
                finca.getUbicacion(),
                productosDTO
            );

            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o error al obtener usuario");
        }
    }
    
    @PostMapping("/crear-finca")
    public ResponseEntity<?> crearFinca(@RequestHeader("Authorization") String authorizationHeader,
                                        @RequestBody Finca nuevaFinca) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");
            Long idUsuario = usuarioService.obtenerIdUsuarioDesdeToken(token); // lógica para extraer ID

            nuevaFinca.setUsuariosId(idUsuario);

            Finca fincaGuardada = fincaRepository.save(nuevaFinca);
            return ResponseEntity.status(HttpStatus.CREATED).body(fincaGuardada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o error al crear finca");
        }
    }
}
