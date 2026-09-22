package com.tienda.backend.service;

import com.tienda.backend.dto.CargaCsvDTO;
import com.tienda.backend.entity.UsuarioEntity;

public interface ICargaCsvService {

    Long crearCargaPendiente(String nombreArchivo, UsuarioEntity usuarioEnt);

    void marcarComoExitosa(Long idCarga, Integer filasOriginales, Integer productosGuardados);

    void marcarComoError(Long idCarga, String mensajeError);

    CargaCsvDTO consultarEstado(Long idCarga);
}
