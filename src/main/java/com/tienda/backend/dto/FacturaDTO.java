package com.tienda.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@JsonPropertyOrder({                                                                                                    // Orden en que aparecen los campos en el JSON de respuesta
        "id_Factura",
        "id_Usuario",
        "nombre",
        "apellido",
        "telefono",
        "totalFactura",
        "listaDetalleFacturaDTO",

})
public class FacturaDTO {

    private int id_Factura;

    private Float totalFactura;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<DetalleFacturaDTO> listaDetallesFacturas;

    @NotNull(message = "El Id de Usuario es obligatorio, para saber a quien pertenece la factura")
    private Long id_Usuario;

    // Estos datos los usamos para mostrarlos al momento de agregarFactura
    @JsonInclude(JsonInclude.Include.NON_NULL)                                                                          // Se omiten del JSON si son null.
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)                                                               // Solo lectura: el cliente no lo envía
    private String nombre;

    @JsonInclude(JsonInclude.Include.NON_NULL)                                                                          // Se omiten del JSON si son null.
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)                                                               // Solo lectura: el cliente no lo envía
    private String apellido;

    @JsonInclude(JsonInclude.Include.NON_NULL)                                                                          // Se omiten del JSON si son null.
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)                                                               // Solo lectura: el cliente no lo envía
    private String telefono;
}
