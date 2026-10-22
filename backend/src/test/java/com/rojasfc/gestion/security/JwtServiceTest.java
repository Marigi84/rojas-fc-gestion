package com.rojasfc.gestion.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

  private static final String SECRETO = "clave-de-prueba-con-mas-de-32-caracteres";

  private final JwtService jwtService = new JwtService(SECRETO, 60);

  @Test
  void elTokenConservaDniRolYContrasenaTemporal() {
    UsuarioAutenticado usuario = new UsuarioAutenticado(30111222L, Rol.RESPONSABLE, true);

    UsuarioAutenticado leido = jwtService.validar(jwtService.generar(usuario));

    assertEquals(usuario, leido);
  }

  @Test
  void rechazaUnTokenModificado() {
    String token = jwtService.generar(new UsuarioAutenticado(1L, Rol.RESPONSABLE, false));
    String modificado = token.substring(0, token.length() - 2) + "xx";

    assertThrows(JwtException.class, () -> jwtService.validar(modificado));
  }

  @Test
  void rechazaUnTokenFirmadoConOtraClave() {
    JwtService otro = new JwtService("otra-clave-de-prueba-con-mas-de-32-caracteres", 60);
    String token = otro.generar(new UsuarioAutenticado(1L, Rol.ADMINISTRADOR, false));

    assertThrows(JwtException.class, () -> jwtService.validar(token));
  }

  @Test
  void rechazaUnTokenVencido() {
    JwtService sinDuracion = new JwtService(SECRETO, -1);
    String token = sinDuracion.generar(new UsuarioAutenticado(1L, Rol.ADMINISTRADOR, false));

    assertThrows(JwtException.class, () -> jwtService.validar(token));
  }

  @Test
  void exigeUnaClaveDeAlMenos32Caracteres() {
    assertThrows(IllegalStateException.class, () -> new JwtService("corta", 60));
  }
}
