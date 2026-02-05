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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getUsername(),
                            loginRequestDTO.getPassword()));

            // 🔐 Generar el token JWT
            String token = jwtService.generarToken(loginRequestDTO.getUsername());

            // ✅ Devolver el token
            return ResponseEntity.ok(token);

            //return ResponseEntity.ok("Login exitoso ✅ Usuario autenticado");

        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> resgistrarUsuario(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {

        usuarioService.registrarUsuario(registerRequestDTO);
        return ResponseEntity.ok("✅ Usuario registrado correctamente");
    }
}
