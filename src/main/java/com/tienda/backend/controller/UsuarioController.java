package com.tienda.backend.controller;

import com.tienda.backend.dto.UsuarioDTO;
import com.tienda.backend.service.IUsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping ("/usuarios")
public class UsuarioController {

    @Autowired
    private IUsuarioService iUsuarioService;

//    @PostMapping ("/agregarUsuario")
//    public ResponseEntity<UsuarioDTO> guardarUsuario(@RequestBody UsuarioDTO usuarioDto){
//        return ResponseEntity.ok().body(usuarioService.agregarUsuario(usuarioDto));
//    }

    @GetMapping("/listarUsuarios")
    public ResponseEntity<List<UsuarioDTO>> ListarUsuarios() {
        List<UsuarioDTO> usuarioDTOS = iUsuarioService.listarUsuarios();
        return ResponseEntity.ok(usuarioDTOS);
    }

    @GetMapping("/listarUsuariosConFacturas/{idUsuario}")
    public ResponseEntity<UsuarioDTO> ListarUsuariosConFactuPorId(@PathVariable Long idUsuario) {
        UsuarioDTO usuarioDto = iUsuarioService.listar_Usuarios_Factu_PorId(idUsuario);
        return ResponseEntity.ok(usuarioDto);
    }

    @GetMapping("/listarUsuariosConFacturasYDetalles/{id_Usuario}")
    public ResponseEntity<UsuarioDTO> ListarUsuariosConFactuYDetallPorId (@PathVariable Long id_Usuario) {
       UsuarioDTO usuarioDto = iUsuarioService.listar_Usuarios_Factu_Detall_PorId(id_Usuario);
       return ResponseEntity.ok(usuarioDto);
    }


    @PutMapping("/actualizar/{id_Usuario}")
    public ResponseEntity<UsuarioDTO> actualizarUsuario(@PathVariable Long id_Usuario, @Valid @RequestBody UsuarioDTO usuarioDto) {
        UsuarioDTO usuarioActualizado = iUsuarioService.actuzalizarUsuario(id_Usuario, usuarioDto);
        return  ResponseEntity.ok(usuarioActualizado);
    }

    @DeleteMapping("/eliminar/{id_Usuario}")
    public ResponseEntity<Void> eliminarUsuario (@PathVariable Long id_Usuario) {
        iUsuarioService.eliminarUsuario(id_Usuario);
        return ResponseEntity.noContent().build();
    }
}
