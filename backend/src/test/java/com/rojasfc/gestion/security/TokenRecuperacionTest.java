package com.rojasfc.gestion.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

/** Token del enlace de recuperacion (RF-50). */
class TokenRecuperacionTest {

  private static final String SECRETO = "clave-de-prueba-con-mas-de-32-caracteres";

  private final TokenRecuperacion tokens = new TokenRecuperacion(SECRETO, 30);

  @Test
  void conservaElDniYCoincideConLaContrasenaConLaQueSeGenero() {
    TokenRecuperacion.Datos datos = tokens.validar(tokens.generar(20111222L, "hash-actual"));

    assertEquals(20111222L, datos.dni());
    assertTrue(tokens.coincide(datos, "hash-actual"));
  }

  @Test
  void dejaDeServirCuandoCambiaLaContrasena() {
    TokenRecuperacion.Datos datos = tokens.validar(tokens.generar(20111222L, "hash-actual"));

    assertFalse(tokens.coincide(datos, "hash-nuevo"));
  }

  @Test
  void rechazaUnTokenVencido() {
    TokenRecuperacion vencidos = new TokenRecuperacion(SECRETO, -1);
    String token = vencidos.generar(20111222L, "hash-actual");

    assertThrows(JwtException.class, () -> tokens.validar(token));
  }

  @Test
  void noAceptaUnTokenDeSesionNiAlReves() {
    JwtService sesiones = new JwtService(SECRETO, 60);
    String sesion = sesiones.generar(new UsuarioAutenticado(20111222L, Rol.ADMINISTRADOR, false));
    String recuperacion = tokens.generar(20111222L, "hash-actual");

    assertThrows(JwtException.class, () -> tokens.validar(sesion));
    assertThrows(JwtException.class, () -> sesiones.validar(recuperacion));
  }
}
