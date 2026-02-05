package com.tienda.backend.security.filter;

import com.tienda.backend.security.serviceSecurity.CustomUserDetailsService;
import com.tienda.backend.security.serviceSecurity.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private  JwtService jwtService;
    @Autowired
    private  CustomUserDetailsService customUserDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. IGNORAR ENDPOINTS PÚBLICOS: Endpoints bajo "/auth" son públicos, se omite validación de token
        String endpoint = request.getServletPath();
        if (endpoint.startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Obtiene el header Authorization y valida que tenga formato "Bearer <token>".
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);                                                 // EXTRAER EL TOKEN (quitamos la palabra "Bearer "): "Bearer " son 7 caracteres (incluyendo el espacio).
        String username = jwtService.extraerUsername(token);                                              // Obtener username desde el token

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {         // Si hay usuario y aún no está autenticado
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);              // Cargar datos del usuario desde BD

            if (jwtService.tokenEsValido(token)) {                                                        // Validar que el token sea válido
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken   // Crear objeto de autenticación
                        (userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));         // Asociar detalles de la petición
                SecurityContextHolder.getContext().setAuthentication(authToken);                          // Guardar autenticación en el contexto
            }
        }
        filterChain.doFilter(request, response);                                                          // Continuar con la petición
    }
}

/*

request → lo que entra del cliente.
response → lo que sale hacia el cliente.
filterChain → el mecanismo para decidir si la petición sigue adelante o se corta en tu filtro.

getServletPath() → te dice qué endpoint se está llamando.
            Ejemplo: Si el cliente pide http://localhost:8080/auth/login → getServletPath() devuelve /auth/login.
                     Si pide http://localhost:8080/productos/123 → devuelve /productos/123.

startsWith("/auth") → es un método de String en Java que verifica si una cadena empieza con un texto específico.
            Ejemplo: "/auth/login".startsWith("/auth") // true
                     "/auth/register".startsWith("/auth") // true
                     "/productos/123".startsWith("/auth") // false

Authorization → el header estándar donde viaja el token.

Bearer → el esquema que define que el contenido del header es un JWT válido.

*/
