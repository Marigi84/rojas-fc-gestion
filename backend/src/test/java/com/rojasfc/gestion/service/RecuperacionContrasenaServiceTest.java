package com.rojasfc.gestion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rojasfc.gestion.dto.NuevaContrasenaRequest;
import com.rojasfc.gestion.model.Administrador;
import com.rojasfc.gestion.model.Persona;
import com.rojasfc.gestion.repository.AdministradorRepository;
import com.rojasfc.gestion.repository.PersonaRepository;
import com.rojasfc.gestion.security.TokenRecuperacion;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Recuperacion de la contrasena del Administrador por correo (RF-50). */
class RecuperacionContrasenaServiceTest {

  private static final Long DNI = 20111222L;
  private static final String EMAIL = "marina@rojasfc.com";

  private AdministradorRepository administradores;
  private CorreoService correo;
  private TokenRecuperacion tokens;
  private Administrador administrador;
  private RecuperacionContrasenaService servicio;

  @BeforeEach
  void preparar() {
    administradores = mock(AdministradorRepository.class);
    PersonaRepository personas = mock(PersonaRepository.class);
    correo = mock(CorreoService.class);
    PasswordEncoder encoder = mock(PasswordEncoder.class);
    when(encoder.encode(any())).thenAnswer(invocacion -> "hash-" + invocacion.getArgument(0));
    tokens = new TokenRecuperacion("clave-de-prueba-con-mas-de-32-caracteres", 30);
    administrador = new Administrador(DNI, EMAIL, "hash-anterior", true);
    when(administradores.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(administrador));
    when(administradores.findById(DNI)).thenReturn(Optional.of(administrador));
    when(personas.findById(DNI)).thenReturn(Optional.of(new Persona(DNI, "Marina", "Cordero")));
    servicio = new RecuperacionContrasenaService(administradores, personas, encoder, tokens,
        correo, "http://localhost:5173/", 30);
  }

  @Test
  void elCorreoDeUnAdministradorRecibeElEnlace() {
    servicio.solicitar("  " + EMAIL + " ");

    ArgumentCaptor<String> enlace = ArgumentCaptor.forClass(String.class);
    verify(correo).enviarRecuperacion(eq(EMAIL), eq("Marina"), enlace.capture(), eq(30L));
    assertTrue(enlace.getValue().startsWith("http://localhost:5173/restablecer-contrasena#"));
  }

  @Test
  void unCorreoQueNoEsDeUnAdministradorNoRecibeNadaYNoDaError() {
    when(administradores.findByEmailIgnoreCase("otro@rojasfc.com")).thenReturn(Optional.empty());

    servicio.solicitar("otro@rojasfc.com");

    verify(correo, never()).enviarRecuperacion(anyString(), anyString(), anyString(), anyLong());
  }

  @Test
  void conElEnlaceGuardaLaContrasenaNuevaYYaNoEsProvisoria() {
    servicio.restablecer(new NuevaContrasenaRequest(token(), "nueva1234"));

    assertEquals("hash-nueva1234", administrador.getContrasenaHash());
    assertFalse(administrador.isContrasenaTemporal());
  }

  @Test
  void elEnlaceSirveUnaSolaVez() {
    String token = token();
    servicio.restablecer(new NuevaContrasenaRequest(token, "nueva1234"));

    IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
        () -> servicio.restablecer(new NuevaContrasenaRequest(token, "otra12345")));

    assertEquals(RecuperacionContrasenaService.ENLACE_INVALIDO, error.getMessage());
    assertEquals("hash-nueva1234", administrador.getContrasenaHash());
  }

  @Test
  void unEnlaceAlteradoSeRechaza() {
    String alterado = token().substring(1);

    assertThrows(IllegalArgumentException.class,
        () -> servicio.restablecer(new NuevaContrasenaRequest(alterado, "nueva1234")));
    assertEquals("hash-anterior", administrador.getContrasenaHash());
  }

  private String token() {
    return tokens.generar(DNI, administrador.getContrasenaHash());
  }
}
