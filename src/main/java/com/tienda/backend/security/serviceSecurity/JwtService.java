package com.tienda.backend.security.serviceSecurity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    // Minimo 32 acaractereres
    private static final String SECRET_KEY = "mi_clave_secreta_super_segura_para_jwt_123456";

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // 🔐 Generar token
    public String generarToken(String username, String rol) {
        return Jwts.builder()
                .subject(username)                                                                          // Aquí guardamos el username del usuario autenticado.
                .claim("role", rol)
                .issuedAt(new Date())                                                                       // Fecha en que el token fue creado.
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) //1 hora                 // Fecha de expiración.
                .signWith(getSigningKey())                                                                  // Firma el token con tu clave secreta.
                .compact();                                                                                 // Empaqueta todo en un Sprinf tipo: eyJhbGciOiJIUzI1NiJ9...
    }

    // Método que recibe un JWT y devuelve sus datos internos (claims)
    private Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
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
            System.out.println("JWT expirado: " + e.getMessage());

        } catch (MalformedJwtException e) {                                                 // Token corrupto o manipulado
            System.out.println("JWT mal formado: " + e.getMessage());

        } catch (SignatureException e) {                                                    // Intento de falsificación
            System.out.println("Firma JWT inválida: " + e.getMessage());

        } catch (IllegalArgumentException e) {                                              // Error de cliente
            System.out.println("Token vacío o null: " + e.getMessage());

        } catch (Exception e) {                                                             // Error interno
            System.out.println("Error inesperado validando JWT: " + e.getMessage());
        }

        return false;
    }
}
