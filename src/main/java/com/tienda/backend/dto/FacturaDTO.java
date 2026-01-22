package com.tienda.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonPropertyOrder({
        "id_Factura",
        "totalFactura",
        "listaDetalleFacturaDTO",

})
public class FacturaDTO {

    private int id_Factura;
    private float totalFactura;
    private List<DetalleFacturaDTO> listaDetalleFacturaDTO;
    @JsonIgnore
    private Long id_Usuario;
}
