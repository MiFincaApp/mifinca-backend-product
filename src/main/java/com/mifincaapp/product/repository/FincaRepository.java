package com.mifincaapp.product.repository;

import com.mifincaapp.product.model.Finca;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FincaRepository extends JpaRepository<Finca, Long> {
    Optional<Finca> findByUsuariosId(Long usuariosId);
}

