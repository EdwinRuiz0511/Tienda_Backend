package com.tienda.backend.security.serviceSecurity;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.time.expiration}")
    private long timeExpiration;

    // 🔐 Generar token
    public String generarToken(String username, String rol) {
        return Jwts.builder()
                .subject(username)                                                                          // Aquí guardamos el username del usuario autenticado.
                .claim("role", rol)
                .issuedAt(new Date())                                                                       // Fecha en que el token fue creado.
                .expiration(new Date(System.currentTimeMillis() + timeExpiration))//30min                   // Fecha de expiración.
                .signWith(obtenerClaveSecreta(), SignatureAlgorithm.HS256)                                  // Firma el token con tu clave secreta.
                .compact();                                                                                 // Empaqueta todo en un Sprinf tipo: eyJhbGciOiJIUzI1NiJ9...
    }

    // Obtener firma del token
    public SecretKey obtenerClaveSecreta() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);                                                // Convierte el string Base64 a bytes
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Método que recibe un JWT y devuelve sus datos internos (claims)
    private Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(obtenerClaveSecreta())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extraerUsername(String token) {
        return extraerClaims(token).getSubject();
    }

    public String extraerRol(String token) {
        return extraerClaims(token).get("role", String.class);
    }

    public boolean tokenEsValido(String token) {
        try {
            Claims claims = extraerClaims(token);
            return !claims.getExpiration().before(new Date());

        } catch (ExpiredJwtException e) {                                                   // El usuario debe volver a loguearse
            log.warn("JWT expirado: {}", e.getMessage());

        } catch (MalformedJwtException e) {                                                 // Token corrupto o manipulado
            log.error("JWT mal formado: {}", e.getMessage());

        } catch (SignatureException e) {                                                    // Intento de falsificación
            log.error("Firma invalida: {}", e.getMessage());

        } catch (IllegalArgumentException e) {                                              // Error de cliente
            log.error("Token vacio o null: {}", e.getMessage());

        } catch (Exception e) {                                                             // Error interno
            log.error("Error inesperado validando JWT: {}", e);
        }

        return false;
    }
}
