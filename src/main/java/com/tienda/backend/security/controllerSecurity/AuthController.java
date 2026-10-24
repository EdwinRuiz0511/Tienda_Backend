package com.tienda.backend.security.controllerSecurity;

import com.tienda.backend.security.dtoSecurity.LoginRequestDTO;
import com.tienda.backend.security.dtoSecurity.RegisterRequestDTO;
import com.tienda.backend.security.serviceSecurity.JwtService;
import com.tienda.backend.service.IUsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {

        Authentication authentication = authenticationManager.authenticate(                                                 // Si las credenciales son inválidas, lanza BadCredentialsException (la atrapa GlobalExceptionHandler)
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getUsername(),
                        loginRequestDTO.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();                                              // Sacar el usuario autenticado
        String rol = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");   // Obtener el rol (IMPORTANTE)
        String token = jwtService.generarToken(userDetails.getUsername(), rol);                                             // Generar token con rol

        return ResponseEntity.ok(Map.of("token", token));                                                               // Devolver el token
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> resgistrarUsuario(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {

        usuarioService.registrarUsuario(registerRequestDTO);

        return ResponseEntity.ok().body(Map.of("mensaje", "Usuario registrado correctamente"));
    }
}

