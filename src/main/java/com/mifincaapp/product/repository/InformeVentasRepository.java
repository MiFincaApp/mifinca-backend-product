package com.mifincaapp.product.repository;

import com.mifincaapp.product.model.InformeVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InformeVentasRepository extends JpaRepository<InformeVenta, Long> {

    List<InformeVenta> findByCompradorId(Long compradorId);
    List<InformeVenta> findByVendedorId(Long vendedorId);
}
