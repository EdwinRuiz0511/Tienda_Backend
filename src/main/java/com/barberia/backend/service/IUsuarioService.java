package com.barberia.backend.service;

import com.barberia.backend.dto.UsuarioDTO;

import java.util.List;

public interface IUsuarioService {

    UsuarioDTO agregarUsuario(UsuarioDTO usuarioDto);

    List<UsuarioDTO> listarUsuarios();

    UsuarioDTO listar_Usuarios_Factu_PorId(Long idUsuario);

    UsuarioDTO listar_Usuarios_Factu_Detall_PorId(Long idUsuario);

    UsuarioDTO actuzalizarUsuario(Long idUsuario, UsuarioDTO usuarioDto);

    void eliminarUsuario(Long idUsuario);

}
