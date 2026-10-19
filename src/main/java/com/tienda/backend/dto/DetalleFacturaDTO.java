package com.tienda.backend.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({
        "id_DetalleFac",
        "nombreProducto",
        "categoria",
        "precio",
        "cantProductos",
        "total",
        "id_Producto",
        "id_Factura",
        "id_Usuario"
})

public class DetalleFacturaDTO {

    private Long id_DetalleFac;

    @Positive(message = "La cantidad de productos debe ser mayor a cero")
    private int cantProductos;

    private float total;                                                                                                // Este lo enviamos de vuelta, no lo recibe del frontend

    @NotNull(message = "El ID de la factura es obligatorio")
    private Integer id_Factura;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long id_Producto;

    // CAMPO SOLO PARA MOSTRAR DATOS DE LA TABLA PRODUCTOS Y USUARIOS
    private Long id_Usuario;
    private String nombreProducto;
    private String categoria;
    private double precio;


}
