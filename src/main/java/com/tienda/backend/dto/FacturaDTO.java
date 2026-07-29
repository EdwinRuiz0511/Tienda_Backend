package com.tienda.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@JsonPropertyOrder({
        "id_Factura",
        "id_Usuario",
        "totalFactura",
        "listaDetalleFacturaDTO",

})
public class FacturaDTO {

    private int id_Factura;
    private Float totalFactura;

    @JsonInclude(JsonInclude.Include.NON_NULL) // Solo desaparece si está en null
    private Set<DetalleFacturaDTO> listaDetallesFacturas;

    //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Se permite recibir el id del usuario en el request (POST), pero no se expone en las respuestas JSON (response).
    private Long id_Usuario;
}
