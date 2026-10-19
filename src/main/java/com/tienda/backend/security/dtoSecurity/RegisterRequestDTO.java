package com.tienda.backend.security.dtoSecurity;

import com.tienda.backend.utils.Constantes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {

    @NotBlank(message = "|nombre| ---> " + Constantes.NOMBRE)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El sexo es obligatorio")
    @Pattern(regexp = "^[MFmf]$", message = "El sexo debe ser 'M' para masculino o 'F' para femenino")
    private String  sexo;

    @NotBlank(message = "El telefono es obligatorio")
    private String  telefono;

    @NotBlank(message = "El username es obligatorio")
    private String username;

    @NotBlank(message = "El password es obligatorio")
    private String password;
}
