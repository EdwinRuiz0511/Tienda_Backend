package com.tienda.backend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<?> manejarNoEncontrado(RecursoNoEncontradoException ex) {
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<?> manejarDuplicado(RecursoDuplicadoException ex) {
        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(SolicitudInvalidaException.class)
    public ResponseEntity<?> manejarSolicitudInvalida(SolicitudInvalidaException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> manejarViolacionIntegridad(DataIntegrityViolationException ex) {
        log.warn("Violacion de restricción de integridad: {}", ex.getMessage());
        return construirRespuesta(HttpStatus.CONFLICT, "El recurso ya existe o viola una restricción de la base de datos");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> manejarErrorInesperado(Exception ex) {
        log.error("\nError inesperado al procesar la solicitud: ", ex);
        return construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }

    private ResponseEntity<?> construirRespuesta(HttpStatus status,  String mensaje) {
        Map<String, Object> body = Map.of(
                "fecha", LocalDateTime.now(),
                "codigo", status.value(),
                "error", status.getReasonPhrase(),
                "mensaje", mensaje );

        return ResponseEntity.status(status).body(body);
    }
}
