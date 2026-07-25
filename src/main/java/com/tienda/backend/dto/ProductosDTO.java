package com.tienda.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductosDTO {

    private Long id_Producto;
    private String nombreProducto;
    private double precio;
    private String categoria;
}
