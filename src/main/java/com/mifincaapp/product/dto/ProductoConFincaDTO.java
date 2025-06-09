package com.mifincaapp.product.dto;

import com.mifincaapp.product.model.Producto;
import com.mifincaapp.product.model.Finca;

public class ProductoConFincaDTO {
    private Long idProducto;
    private String nombre;
    private Double precio;
    private String descripcion;
    private String imagenUrl;
    private Integer cantidad;

    private Long fincaId;
    private String fincaNombre;
    private String fincaUbicacion;

    public ProductoConFincaDTO(Producto producto) {
        this.idProducto = producto.getIdProducto();
        this.nombre = producto.getNombre();
        this.precio = producto.getPrecio();
        this.descripcion = producto.getDescripcion();
        this.imagenUrl = producto.getImagenUrl();
        this.cantidad = producto.getCantidad();

        Finca finca = producto.getFinca();
        this.fincaId = finca.getId();
        this.fincaNombre = finca.getNombre();
        this.fincaUbicacion = finca.getUbicacion();
    }

    // Getters
    public Long getIdProducto() { return idProducto; }
    public String getNombre() { return nombre; }
    public Double getPrecio() { return precio; }
    public String getDescripcion() { return descripcion; }
    public String getImagenUrl() { return imagenUrl; }
    public Integer getCantidad() { return cantidad; }
    public Long getFincaId() { return fincaId; }
    public String getFincaNombre() { return fincaNombre; }
    public String getFincaUbicacion() { return fincaUbicacion; }
}