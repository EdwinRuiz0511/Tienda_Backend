package com.tienda.backend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class  GlobalExceptionHandler {

    // Se lanza cuando el service busca algo por ID (findById) y no lo encuentra → 404
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<?> manejarNoEncontrado(RecursoNoEncontradoException ex) {
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // Se lanza cuando el service detecta manualmente que algo ya existe (ej. producto por nombre) → 409
    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<?> manejarDuplicado(RecursoDuplicadoException ex) {
        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage());
    }

    // Se lanza cuando el service detecta una regla de negocio inválida que no es un simple @NotNull
    // (ej. "todos los detalles deben pertenecer a la misma factura") → 400
    @ExceptionHandler(SolicitudInvalidaException.class)
    public ResponseEntity<?> manejarSolicitudInvalida(SolicitudInvalidaException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    // Red de seguridad a nivel de base de datos: salta si algo viola una restricción SQL
    // (unique=true, nullable=false) que no fue atrapada antes por el DTO → 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> manejarViolacionIntegridad(DataIntegrityViolationException ex) {
        log.warn("Violacion de restricción de integridad: {}", ex.getMessage());
        return construirRespuesta(HttpStatus.CONFLICT, "El recurso ya existe o viola una restricción de la base de datos");
    }

    // Salta cuando falla una anotación de Bean Validation (@NotNull, @Positive, etc.) sobre
    // un DTO individual recibido con @RequestBody (ej. POST /detalle-factura/crear) → 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> manejarValidacion(MethodArgumentNotValidException ex) {

        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errores.putIfAbsent(error.getField(), error.getDefaultMessage()));

        log.warn("Validación fallida: {}", errores);

        Map<String, Object> body = new LinkedHashMap<>();                                                               // Usamos LinkedHashMap, para garantizar el orden en la respuesta JSON
        body.put("fecha", LocalDateTime.now());
        body.put("codigo", HttpStatus.BAD_REQUEST.value());
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put("mensaje", "Datos de entrada inválidos");
        body.put("errores", errores);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Salta cuando falla una anotación de Bean Validation DENTRO de elementos de un List<DTO>
    // recibido con @RequestBody (ej. POST /detalle-factura/crear-lista) → 400
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<?> manejarValidacionParametros(HandlerMethodValidationException ex) {

        Map<String, String> errores = new LinkedHashMap<>();

        for (ParameterValidationResult resultado : ex.getParameterValidationResults()) {
            for (MessageSourceResolvable error : resultado.getResolvableErrors()) {

                String campo;
                if (error instanceof FieldError) {
                    FieldError fieldError = (FieldError) error;
                    campo = fieldError.getField();
                } else {
                    campo = resultado.getMethodParameter().getParameterName();
                }
                errores.putIfAbsent(campo, error.getDefaultMessage());
            }
        }

        log.warn("Validación fallida: {}", errores);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("fecha", LocalDateTime.now());
        body.put("codigo", HttpStatus.BAD_REQUEST.value());
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put("mensaje", "Datos de entrada inválidos");
        body.put("errores", errores);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Salta cuando Jackson no puede ni siquiera construir el objeto desde el JSON
    // (ej. mandar texto donde se espera un número) → 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> manejarFormatoInvalido(HttpMessageNotReadableException ex) {
        log.warn("No se pudo leer el cuerpo de la solicitd: {}", ex.getMessage());
        return construirRespuesta(HttpStatus.BAD_REQUEST, "El formato de los datos enviados no es valido");
    }

    // Red de seguridad final: cualquier excepción no prevista por los handlers de arriba → 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> manejarErrorInesperado(Exception ex) {
        log.error("Error inesperado al procesar la solicitud: ", ex);
        return construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }

    // Método auxiliar privado: arma el JSON de respuesta estándar (fecha, código, error, mensaje)
    // para todos los handlers que no necesitan el mapa extra de "errores" por campo
    private ResponseEntity<?> construirRespuesta(HttpStatus status,  String mensaje) {

        Map<String, Object> body = new LinkedHashMap<>();                                                               // Usamos LinkedHashMap, para garantizar el orden en la respuesta JSON
        body.put("fecha", LocalDateTime.now());
        body.put("codigo", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("mensaje", mensaje);

        return ResponseEntity.status(status).body(body);
    }
}
