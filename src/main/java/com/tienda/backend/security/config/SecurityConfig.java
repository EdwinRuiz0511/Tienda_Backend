package com.tienda.backend.security.config;

import com.tienda.backend.security.filter.JwtAuthenticationFilter;
import com.tienda.backend.security.serviceSecurity.CustomAccessDeniedHandler;
import com.tienda.backend.security.serviceSecurity.CustomAuthenticationEntryPoint;
import com.tienda.backend.utils.RolesConstantes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Habilita CORS usando la configuración personalizada definida abajo
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Desactiva CSRF: no es necesario en una API stateless autenticada con JWT
                .csrf(csrf -> csrf.disable())

                //Se deshabilitan las HttpSession para que cada petición sea validada únicamente a través del token JWT.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Reemplaza el manejo de errores por defecto de Spring Security: distingue entre
                // "no autenticado" (token inválido o ausente → 401, CustomAuthenticationEntryPoint)
                // y "autenticado pero sin permiso" (rol insuficiente → 403, CustomAccessDeniedHandler)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))

                .authorizeHttpRequests(auth -> auth
                        // 🔓 ENDPOINTS PÚBLICOS
                        .requestMatchers("/auth/**").permitAll()

                        // 👤 USER y ADMIN
                        .requestMatchers(HttpMethod.GET,
                                "/facturas/**",
                                "/usuarios/listarUsuariosConFacturas/**",
                                "/usuarios/listarUsuariosConFacturasYDetalles/**"
                        ).hasAnyRole(RolesConstantes.USER, RolesConstantes.ADMIN)

                        // 👑 SOLO ADMIN
                        .requestMatchers("/usuarios/**",
                                "/facturas/**",
                                "/producto/**",
                                "/detalle-factura/**",
                                "/carga-csv/**"
                        ).hasRole(RolesConstantes.ADMIN)

                        // 🔒 CUALQUIER OTRA PETICIÓN
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter,
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    //Configuracion centralizada de CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));                                                 // 1. Dominios permitidos, en este caso Angular deasarrollo
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));                          // 2. Metodos HTTP permitidos
        config.setAllowedHeaders(List.of("*"));                                                                     // 3. Headers permitidos
        config.setAllowCredentials(true);                                                                               // 4. Permitir credenciales (cookies, auth headers)

        // 6. Aplicar esta configuración a TODAS las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
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

