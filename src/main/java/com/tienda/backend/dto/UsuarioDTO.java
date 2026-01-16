package com.tienda.backend.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonPropertyOrder({
        "id_Usuario",
        "nombre",
        "apellido",
        "sexo",
        "telefono",
        "listFacturaDTO",

})
public class UsuarioDTO {

    private Long id_Usuario;
    private String nombre;
    private String apellido;
    private char sexo;
    private int telefono;
    private List<FacturaDTO> listFacturaDTO;
}