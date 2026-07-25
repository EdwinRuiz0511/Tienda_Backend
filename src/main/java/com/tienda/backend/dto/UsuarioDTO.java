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
        "id_Usuario",
        "nombre",
        "apellido",
        "sexo",
        "telefono",
        "username",
        "nombreRol",

})
public class UsuarioDTO {

    private Long id_Usuario;
    private String nombre;
    private String apellido;
    private char sexo;
    private String telefono;

    //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Se permite recibir el id del usuario en el request (POST), pero no se expone en las respuestas JSON (response).
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Se permite recibir el password del usuario en el request (POST), pero no se expone en las respuestas JSON (response).
    private  String password;

    private String nombreRol;

    @JsonInclude(JsonInclude.Include.NON_NULL) // Solo desaparece si está en null
    private Set<FacturaDTO> listaDeFacturas;
}