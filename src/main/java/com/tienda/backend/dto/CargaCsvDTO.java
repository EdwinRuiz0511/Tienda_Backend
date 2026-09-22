package com.tienda.backend.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.tienda.backend.entity.EstadoCarga;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonPropertyOrder({
        "id_Carga",
        "id_Usuario",
        "nombreUsuario",
        "nombreArchivo",
        "filasOriginales",
        "productosGuardados",
        "fechaInicio",
        "fechaFin",
        "mensajeError",
        "estado"
})
public class CargaCsvDTO {

    private Long id_Carga;
    private String nombreArchivo;
    private EstadoCarga estado;
    private Integer filasOriginales;
    private Integer productosGuardados;
    private String mensajeError;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Long id_Usuario;
    private String nombreUsuario;
}
