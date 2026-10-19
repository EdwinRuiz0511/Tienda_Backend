package com.tienda.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@JsonPropertyOrder({                                                                                                    // Orden en que aparecen los campos en el JSON de respuesta
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

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El sexo es obligatorio")
    @Pattern(regexp = "^[MFmf]$", message = "El sexo debe ser 'M' para masculino o 'F' para femenino")
    private String sexo;

    @NotBlank(message = "El telefono es obligatorio")
    private String telefono;

    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Se permite recibir el password del usuario en el request (POST), pero no se expone en las respuestas JSON (response).
    private  String password;

    private String nombreRol;

    @JsonInclude(JsonInclude.Include.NON_NULL) // Solo desaparece si está en null
    private Set<FacturaDTO> listaDeFacturas;
}