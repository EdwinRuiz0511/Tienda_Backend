package com.tienda.backend.service;

import com.tienda.backend.dto.UsuarioDTO;

import java.util.List;

public interface IUsuarioService {

    UsuarioDTO agregarUsuario(UsuarioDTO usuarioDto);

    List<UsuarioDTO> listarUsuarios();

    UsuarioDTO listar_Usuarios_Factu_PorId(Long id_Usuario);

    UsuarioDTO listar_Usuarios_Factu_Detall_PorId(Long id_Usuario);

    UsuarioDTO actuzalizarUsuario(Long id_Usuario, UsuarioDTO usuarioDto);

    void eliminarUsuario(Long id_Usuario);

}
