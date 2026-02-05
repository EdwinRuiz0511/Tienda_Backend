package com.tienda.backend.security.config;

import com.tienda.backend.security.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable()).
                authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter,
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Encriptador de contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager necesario para el login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

/*

🛡️ SecurityConfig:
    Esta clase define las reglas de seguridad de tu aplicación.
    Es como el portero del edificio:
    Decide quién puede entrar.
    Qué puertas están abiertas.
    Qué puertas requieren identificación.
    Cómo se validan las contraseñas.
    Qué motor se usa para autenticar usuarios.

🚫csrf -> csrf.disable():
    CSRF protege formularios tradicionales (cookies).
    Cuando usas JWT (API REST) normalmente NO lo necesitas.
    Por eso se desactiva.
    Si no lo desactivas, te puede bloquear peticiones POST, PUT, DELETE.

🔓.requestMatchers("/auth/**").permitAll():
    Cualquier endpoint que empiece por:
    /auth/login
    /auth/register
    /auth/refresh
    No necesita token, Puede entrar cualquier persona.
    Esto es obligatorio, porque si no: Nadie podría ni siquiera loguearse.

🔒 anyRequest().authenticated():
    Esto significa:
    Cualquier otra ruta: /productos, /facturas, /usuarios
    Debe venir con un usuario autenticado (token válido).

🧱 return http.build():
    Construye la configuración final y se la entrega a Spring.


    http.csrf(csrf -> csrf.disable()).
                authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll());


  "/usuarios/**"

*/
