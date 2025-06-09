package com.mifincaapp.product.dto;

import com.mifincaapp.product.model.Producto;

public class ProductoDTOPlano {
    private Long idProducto;
    private String nombre;
    private Double precio;
    private String descripcion;
    private String imagenUrl;
    private Integer cantidad;

    public ProductoDTOPlano(Producto producto) {
        this.idProducto = producto.getIdProducto();
        this.nombre = producto.getNombre();
        this.precio = producto.getPrecio();
        this.descripcion = producto.getDescripcion();
        this.imagenUrl = producto.getImagenUrl();
        this.cantidad = producto.getCantidad();
    }

    // Getters
    public Long getIdProducto() { return idProducto; }
    public String getNombre() { return nombre; }
    public Double getPrecio() { return precio; }
    public String getDescripcion() { return descripcion; }
    public String getImagenUrl() { return imagenUrl; }
    public Integer getCantidad() { return cantidad; }
}
