package com.tienda.backend.security.serviceSecurity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
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
    public String generarToken(String username) {
        return Jwts.builder()
                .subject(username)                                                                          // Aquí guardamos el username del usuario autenticado.
                .issuedAt(new Date())                                                                       // Fecha en que el token fue creado.
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) //1 hora                 // Fecha de expiración.
                .signWith(getSigningKey())                                                                  // Firma el token con tu clave secreta.
                .compact();                                                                                 // Empaqueta todo en un Sprinf tipo: eyJhbGciOiJIUzI1NiJ9...
    }

    // 🧠 Extraer claims
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

    public boolean tokenEsValido(String token) {
        try {
           Claims claims = extraerClaims(token);
           return !claims.getExpiration().before(new Date());
        }catch (Exception e) {
            return false;
        }
    }
}
