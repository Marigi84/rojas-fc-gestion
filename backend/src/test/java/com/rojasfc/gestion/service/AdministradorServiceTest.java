package com.rojasfc.gestion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rojasfc.gestion.dto.AdministradorAltaRequest;
import com.rojasfc.gestion.dto.AdministradorResumen;
import com.rojasfc.gestion.exception.ConflictoException;
import com.rojasfc.gestion.model.Administrador;
import com.rojasfc.gestion.model.Persona;
import com.rojasfc.gestion.repository.AdministradorRepository;
import com.rojasfc.gestion.repository.PersonaRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Alta de otro Administrador (RF-51, RN-01, RNF-01). */
class AdministradorServiceTest {

  private static final Long DNI = 28111222L;
  private static final String EMAIL = "coordinacion@rojasfc.com";

  private AdministradorRepository administradores;
  private PersonaRepository personas;
  private AdministradorService servicio;

  @BeforeEach
  void preparar() {
    administradores = mock(AdministradorRepository.class);
    personas = mock(PersonaRepository.class);
    PasswordEncoder encoder = mock(PasswordEncoder.class);
    when(encoder.encode(any())).thenReturn("hash");
    when(administradores.saveAndFlush(any(Administrador.class)))
        .thenAnswer(invocacion -> invocacion.getArgument(0));
    servicio = new AdministradorService(administradores, personas, encoder);
  }

  @Test
  void unDniQueYaEsAdministradorNoSeRegistraDeNuevo() {
    when(administradores.existsById(DNI)).thenReturn(true);

    ConflictoException error = assertThrows(ConflictoException.class,
        () -> servicio.registrar(pedido("Ana", "Gómez")));

    assertEquals("Administrador ya registrado", error.getTitulo());
    verify(administradores, never()).saveAndFlush(any());
  }

  @Test
  void unCorreoDeOtroAdministradorNoSeRepite() {
    when(administradores.existsByEmailIgnoreCase(EMAIL)).thenReturn(true);

    ConflictoException error = assertThrows(ConflictoException.class,
        () -> servicio.registrar(pedido("Ana", "Gómez")));

    assertEquals("Correo en uso", error.getTitulo());
    verify(administradores, never()).saveAndFlush(any());
  }

  @Test
  void unaPersonaNuevaSinNombreNoSeRegistra() {
    when(personas.existsById(DNI)).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> servicio.registrar(pedido(null, null)));

    verify(personas, never()).saveAndFlush(any());
    verify(administradores, never()).saveAndFlush(any());
  }

  @Test
  void unaPersonaRegistradaSeReutilizaYQuedaConContrasenaProvisoria() {
    when(personas.existsById(DNI)).thenReturn(true);
    when(personas.findById(DNI)).thenReturn(Optional.of(new Persona(DNI, "Laura", "Pérez")));

    AdministradorResumen resumen = servicio.registrar(pedido(null, null));

    verify(personas, never()).saveAndFlush(any());
    assertEquals("Laura", resumen.nombre());
    assertTrue(resumen.contrasenaTemporal());
  }

  private static AdministradorAltaRequest pedido(String nombre, String apellido) {
    return new AdministradorAltaRequest(DNI, nombre, apellido, EMAIL, "provisoria123");
  }
}
