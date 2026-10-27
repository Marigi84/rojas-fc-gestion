package com.rojasfc.gestion.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Token del enlace para recuperar la contrasena del Administrador (RF-50).
 *
 * - Se firma con una clave derivada de JWT_SECRET distinta de la de las
 *   sesiones, asi este token nunca sirve para ingresar al sistema.
 * - Vence a los 30 minutos.
 * - Lleva una huella de la contrasena actual: cuando la contrasena cambia, la
 *   huella deja de coincidir y el enlace no se puede volver a usar. Por eso
 *   no hace falta guardar nada en la base. La huella se calcula con la clave
 *   del token, asi que no revela nada del hash de la contrasena.
 */
@Component
public class TokenRecuperacion {

  private static final String CLAIM_HUELLA = "huella";
  private static final String USO = "recuperacion-contrasena";

  private final byte[] claveBytes;
  private final SecretKey clave;
  private final Duration duracion;

  public TokenRecuperacion(
      @Value("${app.jwt.secret}") String secreto,
      @Value("${app.recuperacion.duracion-minutos}") long duracionMinutos) {
    this.claveBytes = derivar(secreto);
    this.clave = Keys.hmacShaKeyFor(claveBytes);
    this.duracion = Duration.ofMinutes(duracionMinutos);
  }

  public String generar(Long dni, String contrasenaHash) {
    Instant ahora = Instant.now();
    return Jwts.builder()
        .subject(dni.toString())
        .claim(CLAIM_HUELLA, huella(contrasenaHash))
        .issuedAt(Date.from(ahora))
        .expiration(Date.from(ahora.plus(duracion)))
        .signWith(clave)
        .compact();
  }

  /**
   * Devuelve el DNI y la huella del token, o lanza JwtException si la firma no
   * es valida, si vencio o si le faltan datos.
   */
  public Datos validar(String token) {
    Claims claims = Jwts.parser()
        .verifyWith(clave)
        .build()
        .parseSignedClaims(token)
        .getPayload();
    try {
      String huella = claims.get(CLAIM_HUELLA, String.class);
      if (huella == null) {
        throw new IllegalArgumentException("Sin huella");
      }
      return new Datos(Long.valueOf(claims.getSubject()), huella);
    } catch (RuntimeException e) {
      throw new JwtException("Token con datos invalidos", e);
    }
  }

  /** true si la huella del token corresponde a la contrasena actual. */
  public boolean coincide(Datos datos, String contrasenaHash) {
    return MessageDigest.isEqual(
        datos.huella().getBytes(StandardCharsets.UTF_8),
        huella(contrasenaHash).getBytes(StandardCharsets.UTF_8));
  }

  private String huella(String contrasenaHash) {
    byte[] resumen = hmac(claveBytes, contrasenaHash);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(resumen);
  }

  private static byte[] derivar(String secreto) {
    return hmac(secreto.getBytes(StandardCharsets.UTF_8), USO);
  }

  private static byte[] hmac(byte[] clave, String dato) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(clave, "HmacSHA256"));
      return mac.doFinal(dato.getBytes(StandardCharsets.UTF_8));
    } catch (GeneralSecurityException e) {
      throw new IllegalStateException(e);
    }
  }

  public record Datos(Long dni, String huella) {
  }
}
