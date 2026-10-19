package com.tienda.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductosDTO {

    private Long id_Producto;

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombreProducto;

    @Positive(message = "El precio debe ser mayor a cero")
    private double precio;

    @NotBlank(message = "La categoria es obligatoria")
    private String categoria;
}
