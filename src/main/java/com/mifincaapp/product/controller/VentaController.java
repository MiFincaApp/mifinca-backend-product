package com.mifincaapp.product.controller;

import com.mifincaapp.product.dto.VentaRequest;
import com.mifincaapp.product.model.DetalleVenta;
import com.mifincaapp.product.model.InformeVenta;
import com.mifincaapp.product.model.Producto;
import com.mifincaapp.product.model.Venta;
import com.mifincaapp.product.repository.DetalleVentaRepository;
import com.mifincaapp.product.repository.InformeVentasRepository;
import com.mifincaapp.product.repository.ProductoRepository;
import com.mifincaapp.product.repository.VentaRepository;
import com.mifincaapp.product.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/ventas")
@CrossOrigin(origins = "*")
public class VentaController {
    
    @Autowired
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final InformeVentasRepository informeVentasRepository;
    private final UsuarioService usuarioService;
    private final ProductoRepository productoRepository;

    public VentaController(
            VentaRepository ventaRepository,
            DetalleVentaRepository detalleVentaRepository,
            UsuarioService usuarioService,
            InformeVentasRepository informeVentasRepository,
            ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.usuarioService = usuarioService;
        this.informeVentasRepository = informeVentasRepository;
        this.productoRepository = productoRepository;
    }

    @PostMapping("/crear-ventas")
    public ResponseEntity<?> registrarVenta(
            @RequestBody VentaRequest request,
            @RequestHeader("Authorization") String authorizationHeader) {

        try {
            // Extraer token sin "Bearer "
            String token = authorizationHeader.replace("Bearer ", "").trim();

            // Obtener el id cliente desde token
            Long clienteId = usuarioService.obtenerIdUsuarioDesdeToken(token);

            // Crear objeto venta
            Venta venta = new Venta();
            venta.setClienteId(clienteId);
            venta.setTotal(request.getTotal());
            venta.setFecha(LocalDateTime.now());
            venta.setEstado("COMPLETADA");

            // Guardar venta en DB
            venta = ventaRepository.save(venta);

            // Guardar detalles de venta
            for (VentaRequest.DetalleProducto p : request.getProductos()) {
                Producto producto = productoRepository.findById(p.getProductoId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

                DetalleVenta detalle = new DetalleVenta();
                detalle.setVenta(venta);
                detalle.setProducto(producto);
                detalle.setCantidad(p.getCantidad());

                if (p.getPrecioUnitario() == null) {
                    throw new RuntimeException("El precio unitario no puede ser null");
                }

                // ✅ Solución aquí
                detalle.setPrecioUnitario(p.getPrecioUnitario());

                detalleVentaRepository.save(detalle);
            }

            return ResponseEntity.ok("Venta registrada exitosamente");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar venta: " + e.getMessage());
        }
    }

    @GetMapping("/informes/cliente")
    public ResponseEntity<?> getInformesDelCliente(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "").trim();
        Long clienteId = usuarioService.obtenerIdUsuarioDesdeToken(token);
        List<InformeVenta> informes = informeVentasRepository.findByCompradorId(clienteId);
        if (informes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body("No has realizado ninguna compra");
        }
        return ResponseEntity.ok(informes);
    }

    @GetMapping("/informes/vendedor")
    public ResponseEntity<?> getInformesDelVendedor(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "").trim();
        Long vendedorId = usuarioService.obtenerIdUsuarioDesdeToken(token);
        List<InformeVenta> informes = informeVentasRepository.findByVendedorId(vendedorId);
        if (informes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body("No has realizado ninguna venta");
        }
        return ResponseEntity.ok(informes);
    }
}

