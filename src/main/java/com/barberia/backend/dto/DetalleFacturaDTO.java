package com.barberia.backend.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({
        "id_DetalleFac",
        "cantProductos",
        "total",
})

public class DetalleFacturaDTO {

    private Long id_DetalleFac;
    private int cantProductos;
    private float total;
}
