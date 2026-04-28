package com.tienda.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tienda.backend.entity.DetalleFacturaEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductosDTO {

    private Long id_Productos;
    private String nombreProducto;
    private double precio;
    private String categoria;

    @JsonInclude(JsonInclude.Include.NON_NULL) // Solo desaparece si está en null
    private List<DetalleFacturaEntity> listaDetalleFacturaEnt;
}
