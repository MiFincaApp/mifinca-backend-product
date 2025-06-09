package com.mifincaapp.product.model;

import jakarta.persistence.*;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Double precio;

    @Column(name = "imagen_url", nullable = false, length = 500)
    private String imagenUrl;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private Integer cantidad;

    // Cambiar idFinca por relación ManyToOne
    @ManyToOne(fetch = FetchType.LAZY) // Lazy para cargar finca solo cuando se necesite
    @JoinColumn(name = "id_finca", nullable = false)
    private Finca finca;

    public Producto() {}

    // Getters y setters

    public Long getIdProducto() { 
        return idProducto; 
    }

    public void setIdProducto(Long idProducto) { 
        this.idProducto = idProducto; 
    }

    public String getNombre() { 
        return nombre; 
    }

    public void setNombre(String nombre) { 
        this.nombre = nombre; 
    }

    public Double getPrecio() { 
        return precio; 
    }

    public void setPrecio(Double precio) { 
        this.precio = precio; 
    }

    public String getImagenUrl() { 
        return imagenUrl; 
    }

    public void setImagenUrl(String imagenUrl) { 
        this.imagenUrl = imagenUrl; 
    }

    public String getDescripcion() { 
        return descripcion; 
    }

    public void setDescripcion(String descripcion) { 
        this.descripcion = descripcion; 
    }

    public Integer getCantidad() { 
        return cantidad; 
    }

    public void setCantidad(Integer cantidad) { 
        this.cantidad = cantidad; 
    }

    public Finca getFinca() { 
        return finca; 
    }

    public void setFinca(Finca finca) { 
        this.finca = finca; 
    }
}
