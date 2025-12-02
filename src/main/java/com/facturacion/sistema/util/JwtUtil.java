package com.facturacion.sistema.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Se inyecta la clave secreta y el tiempo de expiración desde application.properties
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // Genera la clave de firma segura (debe ser de al menos 256 bits para HS256)
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // --- MÉTODOS DE GENERACIÓN (USADOS EN AuthController) ---

    /**
     * Genera el token JWT a partir del correo y rol del usuario.
     * @param correo Correo del usuario (Subject)
     * @param rol Rol del usuario (Claim adicional)
     * @return Token JWT generado.
     */
    public String generateToken(String correo, String rol) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", rol); // Añade el rol como claim

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(correo)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    // --- MÉTODOS DE EXTRACCIÓN Y VALIDACIÓN (USADOS EN JwtAuthenticationFilter) ---

    /**
     * Extrae un solo claim usando una función.
     * @param token El token JWT.
     * @param claimsResolver Función para resolver el claim.
     * @param <T> Tipo del claim.
     * @return El valor del claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae el Subject (correo) del token.
     * @param token El token JWT.
     * @return El correo del usuario.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae todos los claims del token.
     * @param token El token JWT.
     * @return Objeto Claims con todos los datos.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Valida si el token es válido (no expirado y firma correcta).
     * @param token El token JWT.
     * @return true si el token es válido.
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            // Manejo básico de excepciones de validación (firma incorrecta, malformado, etc.)
            return false;
        }
    }

    /**
     * Verifica si el token ha expirado.
     * @param token El token JWT.
     * @return true si la fecha de expiración es anterior a la fecha actual.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrae la fecha de expiración del token.
     * @param token El token JWT.
     * @return Fecha de expiración.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
