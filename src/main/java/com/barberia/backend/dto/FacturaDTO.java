package com.barberia.backend.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonPropertyOrder({
        "idfactura",
        "totalFactura",
        "listaDetalleFacturaDTO",

})
public class FacturaDTO {

    private int idfactura;
    private float totalFactura;
    private UsuarioDTO usuarioDto;
    private List<DetalleFacturaDTO> listaDetalleFacturaDTO;
}
