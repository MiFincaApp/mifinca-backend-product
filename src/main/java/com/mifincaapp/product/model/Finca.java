package com.mifincaapp.product.model;
import jakarta.persistence.*;

@Entity
public class Finca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuariosId;  // Solo un campo Long sin relación explícita
    private String nombre;
    private String ubicacion;

    public Finca() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuariosId() { return usuariosId; }
    public void setUsuariosId(Long usuariosId) { this.usuariosId = usuariosId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
}

