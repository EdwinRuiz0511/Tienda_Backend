package com.tienda.backend.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
    private Integer id_Factura;
    private Long id_Producto;
    private Long id_Usuario;

    //private Long id_Uasurio;
    private int cantProductos;
    private float total;             // Este lo enviamos de vuelta, no lo recibe del frontend

    // CAMPO SOLO PARA MOSTRAR DATOS DE LA TABLA PRODUCTOS
    private String nombreProducto;
    private String categoria;
    private double precio;


}
