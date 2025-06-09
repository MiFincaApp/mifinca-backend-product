package com.mifincaapp.product.repository;

import com.mifincaapp.product.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByFincaId(Long fincaId);
    
    // Buscar productos por nombre ignorando mayúsculas/minúsculas
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}
