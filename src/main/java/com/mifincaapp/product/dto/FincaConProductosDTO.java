package com.mifincaapp.product.dto;

import java.util.List;

public class FincaConProductosDTO {

    private Long id;
    private Long usuariosId;
    private String nombre;
    private String ubicacion;
    private List<ProductoDTOPlano> productos;

    public FincaConProductosDTO(Long id, Long usuariosId, String nombre, String ubicacion, List<ProductoDTOPlano> productos) {
        this.id = id;
        this.usuariosId = usuariosId;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.productos = productos;
    }

    // Getters y setters

    public Long getId() {
        return id;
    }

    public Long getUsuariosId() {
        return usuariosId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public List<ProductoDTOPlano> getProductos() {
        return productos;
    }
}


