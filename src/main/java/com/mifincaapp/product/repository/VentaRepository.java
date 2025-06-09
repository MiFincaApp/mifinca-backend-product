package com.mifincaapp.product.repository;


import com.mifincaapp.product.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    
}
