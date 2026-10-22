/*
 * CustomAuthenticationEntryPoint:
 * Spring Security invoca esta clase cuando alguien intenta acceder a un endpoint protegido
 * SIN estar autenticado (sin token, token vencido, token corrupto o con firma inválida).
 * Reemplaza el comportamiento por defecto (Http403ForbiddenEntryPoint, que siempre devuelve 403)
 * para devolver el código correcto (401 Unauthorized) junto con el mismo formato JSON
 * que usa GlobalExceptionHandler en el resto de la API.
 */

package com.tienda.backend.security.serviceSecurity;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());                                                            // 401, no 403
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> body = new LinkedHashMap<>();                                                               // Mismo formato que construirRespuesta() en GlobalExceptionHandler
        body.put("fecha", LocalDateTime.now());
        body.put("codigo", HttpStatus.UNAUTHORIZED.value());
        body.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
        body.put("mensaje", "No estás autenticado, o tu token no es válido o expiró. Iniciá sesión nuevamente");

        objectMapper.writeValue(response.getWriter(), body);
    }
}
