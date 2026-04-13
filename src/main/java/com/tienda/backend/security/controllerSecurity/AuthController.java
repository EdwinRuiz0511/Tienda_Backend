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

@CrossOrigin(origins = "http://localhost:4200")
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

            //  Sacar el usuario autenticado
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            //  Obtener el rol (IMPORTANTE)
            String rol = userDetails.getAuthorities().iterator().next().getAuthority();

            //  Generar token con rol
            String token = jwtService.generarToken(userDetails.getUsername(), rol);

            //  Devolver el token
            return ResponseEntity.ok(Map.of("token", token));

            //return ResponseEntity.ok("Login exitoso ✅ Usuario autenticado");

        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> resgistrarUsuario(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {

        usuarioService.registrarUsuario(registerRequestDTO);
        return ResponseEntity.ok().body(Map.of(
                "mensaje", "✅ Usuario registrado correctamente"));
    }
}
