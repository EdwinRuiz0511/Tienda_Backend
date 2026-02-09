package com.tienda.backend.controller;

import com.tienda.backend.dto.UsuarioDTO;
import com.tienda.backend.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/usuarios")
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;

//    @PostMapping ("/agregarUsuario")
//    public ResponseEntity<UsuarioDTO> guardarUsuario(@RequestBody UsuarioDTO usuarioDto){
//        return ResponseEntity.ok().body(usuarioService.agregarUsuario(usuarioDto));
//    }

    @GetMapping("/listarUsuarios")
    public ResponseEntity<List<UsuarioDTO>> ListarUsuarios() {
        List<UsuarioDTO> usuarioDTOS = usuarioService.listarUsuarios();

        return ResponseEntity.ok(usuarioDTOS);
    }

    @GetMapping("/listarUsuariosConFacturas/{idUsuario}")
    public ResponseEntity<UsuarioDTO> ListarUsuariosConFactuPorId(@PathVariable Long idUsuario) {
        UsuarioDTO usuarioDto = usuarioService.listar_Usuarios_Factu_PorId(idUsuario);

        return ResponseEntity.ok(usuarioDto);
    }

    @GetMapping("/listarUsuariosConFacturasYDetalles/{idUsuario}")
    public ResponseEntity<UsuarioDTO> ListarUsuariosConFactuYDetallPorId (@PathVariable Long idUsuario) {
        UsuarioDTO usuarioDto = usuarioService.listar_Usuarios_Factu_Detall_PorId(idUsuario);

        return ResponseEntity.ok(usuarioDto);
    }


    @PutMapping("/actualizar/{idUsuario}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long idUsuario, @RequestBody UsuarioDTO usuario) {
        try {
            UsuarioDTO usuarioActualizado = usuarioService.actuzalizarUsuario(idUsuario, usuario);
            return ResponseEntity.ok(usuarioActualizado);

        }catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: "+e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{idUsuario}")
    public ResponseEntity<String> eliminarUsuario (@PathVariable Long idUsuario) {
        try {
            usuarioService.eliminarUsuario(idUsuario);
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: "+e.getMessage());
        }
    }
}
