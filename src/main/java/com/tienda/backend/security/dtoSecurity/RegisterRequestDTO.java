package com.tienda.backend.security.dtoSecurity;

import com.tienda.backend.utils.Constantes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {

    @NotBlank(message = "|nombre|" + Constantes.NOMBRE)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotNull(message = "El sexo es obligatorio")
    private Character  sexo;

    @NotNull(message = "El telefono es obligatorio")
    private int  telefono;

    @NotBlank(message = "El username es obligatorio")
    private String username;

    @NotBlank(message = "El password es obligatorio")
    private String password;
}
