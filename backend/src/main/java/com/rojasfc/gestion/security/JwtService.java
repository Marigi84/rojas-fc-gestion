package com.rojasfc.gestion.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Emite y valida los JWT (arquitectura-inicial.md, seccion 8). La clave se
 * toma de la variable de entorno JWT_SECRET, nunca del codigo.
 */
@Service
public class JwtService {

  private static final String CLAIM_ROL = "rol";
  private static final String CLAIM_TEMPORAL = "temporal";

  private final SecretKey clave;
  private final Duration duracion;

  public JwtService(
      @Value("${app.jwt.secret}") String secreto,
      @Value("${app.jwt.duracion-minutos}") long duracionMinutos) {
    byte[] bytes = secreto.getBytes(StandardCharsets.UTF_8);
    if (bytes.length < 32) {
      throw new IllegalStateException(
          "JWT_SECRET debe tener al menos 32 caracteres (ver backend/README.md)");
    }
    this.clave = Keys.hmacShaKeyFor(bytes);
    this.duracion = Duration.ofMinutes(duracionMinutos);
  }

  public String generar(UsuarioAutenticado usuario) {
    Instant ahora = Instant.now();
    return Jwts.builder()
        .subject(usuario.dni().toString())
        .claim(CLAIM_ROL, usuario.rol().name())
        .claim(CLAIM_TEMPORAL, usuario.contrasenaTemporal())
        .issuedAt(Date.from(ahora))
        .expiration(Date.from(ahora.plus(duracion)))
        .signWith(clave)
        .compact();
  }

  /**
   * Devuelve el usuario del token, o lanza JwtException si la firma no es
   * valida, si vencio o si le faltan datos.
   */
  public UsuarioAutenticado validar(String token) {
    Claims claims = Jwts.parser()
        .verifyWith(clave)
        .build()
        .parseSignedClaims(token)
        .getPayload();
    try {
      return new UsuarioAutenticado(
          Long.valueOf(claims.getSubject()),
          Rol.valueOf(claims.get(CLAIM_ROL, String.class)),
          Boolean.TRUE.equals(claims.get(CLAIM_TEMPORAL, Boolean.class)));
    } catch (RuntimeException e) {
      throw new JwtException("Token con datos invalidos", e);
    }
  }
}
