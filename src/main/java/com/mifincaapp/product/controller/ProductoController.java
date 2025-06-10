package com.mifincaapp.product.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mifincaapp.product.dto.ProductoConFincaDTO;
import com.mifincaapp.product.model.Finca;
import com.mifincaapp.product.model.Producto;
import com.mifincaapp.product.repository.FincaRepository;
import com.mifincaapp.product.repository.ProductoRepository;
import com.mifincaapp.product.service.R2Uploader;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private R2Uploader r2Uploader;
    private final FincaRepository fincaRepository;
    private final ProductoRepository productoRepository;

    public ProductoController(FincaRepository fincaRepository, ProductoRepository productoRepository) {
        this.fincaRepository = fincaRepository;
        this.productoRepository = productoRepository;
    }

    @GetMapping
    public List<ProductoConFincaDTO> getAllProductos() {
        return productoRepository.findAll()
                .stream()
                .map(producto -> {
                    producto.getFinca().getNombre();
                    return new ProductoConFincaDTO(producto);
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoConFincaDTO> getProductoById(@PathVariable Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Forzar carga de la finca (evita lazy exception)
        producto.getFinca().getNombre();

        return ResponseEntity.ok(new ProductoConFincaDTO(producto));
    }

    @GetMapping("/filter-producto")
    public ResponseEntity<?> listarProductos(@RequestParam(required = false) String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("El parámetro 'nombre' es obligatorio y debe contener al menos una letra");
        }

        List<Producto> productos = productoRepository.findByNombreContainingIgnoreCase(nombre);

        if (productos.isEmpty()) {
            // Retornar mensaje personalizado cuando no hay coincidencias
            return ResponseEntity.ok("Producto no se encuentra en este momento");
        }

        // Mapeo a DTO
        List<ProductoConFincaDTO> productosDTO = productos.stream()
                .map(ProductoConFincaDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(productosDTO);
    }

    @PostMapping(value = "/finca/{fincaId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProducto(
            @PathVariable Long fincaId,
            @RequestPart("producto") String productoJson,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen
    ) {
        Optional<Finca> fincaOpt = fincaRepository.findById(fincaId);
        if (fincaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Finca no encontrada");
        }

        try {
            // Convertir JSON a objeto Producto
            ObjectMapper mapper = new ObjectMapper();
            Producto producto = mapper.readValue(productoJson, Producto.class);

            // Subir la imagen si existe
            if (imagen != null && !imagen.isEmpty()) {
                String key = imagen.getOriginalFilename();
                r2Uploader.uploadFile(key, imagen);
                String imageUrl = "https://storage.mifincaapp.com/" + key;
                producto.setImagenUrl(imageUrl);
            }

            // Asignar finca al producto (asignar el objeto finca, NO el id)
            producto.setFinca(fincaOpt.get());

            Producto creado = productoRepository.save(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);

        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("JSON de producto inválido: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear producto: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProducto(
            @PathVariable Long id,
            @RequestBody Producto producto
    ) {
        Optional<Producto> existenteOpt = productoRepository.findById(id);
        if (existenteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
        }

        Producto existente = existenteOpt.get();

        // No se actualiza la finca (idFinca)
        existente.setNombre(producto.getNombre());
        existente.setPrecio(producto.getPrecio());
        existente.setDescripcion(producto.getDescripcion());
        existente.setCantidad(producto.getCantidad());

        Producto actualizado = productoRepository.save(existente);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProducto(@PathVariable Long id) {
        Optional<Producto> productoOpt = productoRepository.findById(id);
        if (productoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
        }

        Producto producto = productoOpt.get();

        // 1. Eliminar imagen en R2 si existe
        if (producto.getImagenUrl() != null && !producto.getImagenUrl().isEmpty()) {
            try {
                // Extraer la key desde la URL
                String imageUrl = producto.getImagenUrl();
                String key = imageUrl.replace("https://storage.mifincaapp.com/", "");

                // Llamar al servicio para eliminar
                r2Uploader.deleteFile(key);

                // (Opcional) purgar caché
                r2Uploader.purgeCache(imageUrl);

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                     .body("Error al eliminar imagen: " + e.getMessage());
            }
        }

        // 2. Eliminar producto de la base de datos
        productoRepository.delete(producto);

        return ResponseEntity.ok("Producto eliminado correctamente");
    }
}
