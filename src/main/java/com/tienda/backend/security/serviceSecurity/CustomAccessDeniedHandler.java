/*
 * CustomAccessDeniedHandler:
 * Spring Security invoca esta clase cuando el usuario SÍ está autenticado correctamente,
 * pero no tiene el rol necesario para acceder al endpoint solicitado (ej. un USER intentando
 * entrar a una ruta exclusiva de ADMIN). Escribe la respuesta directamente sobre el
 * HttpServletResponse (en vez de usar response.sendError()) para evitar que Tomcat redirija
 * internamente a /error, lo cual perdía la autenticación y convertía el 403 en un 401 incorrecto.
 */

package com.tienda.backend.security.serviceSecurity;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("fecha", LocalDateTime.now());
        body.put("codigo", HttpStatus.FORBIDDEN.value());                                                               // Codigo 403
        body.put("error", HttpStatus.FORBIDDEN.getReasonPhrase());
        body.put("mensaje", "No tienes permiso para realizar esta acción");

        objectMapper.writeValue(response.getWriter(), body);
    }
}
