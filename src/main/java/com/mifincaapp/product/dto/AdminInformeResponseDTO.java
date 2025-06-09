package com.mifincaapp.product.dto;

import com.mifincaapp.product.model.InformeVenta;
import java.util.List;


public class AdminInformeResponseDTO {
    private List<FincaConProductosDTO> fincas;
    private List<InformeVenta> informes;

    public AdminInformeResponseDTO(List<FincaConProductosDTO> fincas, List<InformeVenta> informes) {
        this.fincas = fincas;
        this.informes = informes;
    }

    // Getters y Setters
    public List<FincaConProductosDTO> getFincas() {
        return fincas;
    }

    public void setFincas(List<FincaConProductosDTO> fincas) {
        this.fincas = fincas;
    }

    public List<InformeVenta> getInformes() {
        return informes;
    }

    public void setInformes(List<InformeVenta> informes) {
        this.informes = informes;
    }
}
