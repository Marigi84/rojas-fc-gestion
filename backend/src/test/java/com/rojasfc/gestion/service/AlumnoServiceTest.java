package com.rojasfc.gestion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rojasfc.gestion.dto.AltaAlumnoRequest;
import com.rojasfc.gestion.dto.ResponsableAltaRequest;
import com.rojasfc.gestion.exception.ConflictoException;
import com.rojasfc.gestion.model.Alumno;
import com.rojasfc.gestion.model.Permanencia;
import com.rojasfc.gestion.model.Persona;
import com.rojasfc.gestion.model.Vinculo;
import com.rojasfc.gestion.repository.AlumnoRepository;
import com.rojasfc.gestion.repository.PermanenciaRepository;
import com.rojasfc.gestion.repository.PersonaRepository;
import com.rojasfc.gestion.repository.ResponsableAlumnoRepository;
import com.rojasfc.gestion.repository.ResponsableRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/** Reglas de AlumnoService que no dependen de la base (orden de validaciones y reactivacion). */
class AlumnoServiceTest {

  private static final Long DNI = 45111222L;
  private static final Long ADMIN = 20111222L;
  private static final LocalDate HOY = LocalDate.of(2026, 10, 27);

  private PersonaRepository personas;
  private AlumnoRepository alumnos;
  private PermanenciaRepository permanencias;
  private ResponsableAlumnoRepository vinculos;
  private ResponsableService responsableService;
  private AlumnoService servicio;

  @BeforeEach
  void preparar() {
    personas = mock(PersonaRepository.class);
    alumnos = mock(AlumnoRepository.class);
    permanencias = mock(PermanenciaRepository.class);
    vinculos = mock(ResponsableAlumnoRepository.class);
    responsableService = mock(ResponsableService.class);
    Clock reloj = Clock.fixed(HOY.atStartOfDay(ZoneId.of("America/Argentina/Cordoba")).toInstant(),
        ZoneId.of("America/Argentina/Cordoba"));
    servicio = new AlumnoService(personas, alumnos, permanencias, mock(ResponsableRepository.class),
        vinculos, responsableService, reloj);
  }

  @Test
  void unAlumnoExistenteSeInformaAntesDeValidarLosResponsables() {
    when(alumnos.existsById(DNI)).thenReturn(true);
    when(permanencias.findByDniAlumnoAndFechaBajaIsNull(DNI))
        .thenReturn(Optional.of(new Permanencia(DNI, HOY.minusDays(30), ADMIN)));
    ResponsableAltaRequest repetido = new ResponsableAltaRequest(
        30111222L, null, null, null, Vinculo.MADRE, null);

    ConflictoException error = assertThrows(ConflictoException.class,
        () -> servicio.registrar(alta("Mateo", List.of(repetido, repetido)), ADMIN));

    assertEquals("Alumno ya registrado", error.getTitulo());
    verify(responsableService, never()).asociarEnAlta(any(), any());
  }

  @Test
  void unaPersonaNuevaSinNombreNoSeRegistra() {
    when(alumnos.existsById(DNI)).thenReturn(false);
    when(personas.existsById(DNI)).thenReturn(false);
    ResponsableAltaRequest responsable = new ResponsableAltaRequest(
        30111222L, "Laura", "Gómez", "3511234567", Vinculo.MADRE, "provisoria123");

    assertThrows(IllegalArgumentException.class,
        () -> servicio.registrar(alta(null, List.of(responsable)), ADMIN));

    verify(personas, never()).saveAndFlush(any());
    verify(alumnos, never()).saveAndFlush(any());
  }

  @Test
  void reactivarElMismoDiaDeLaBajaDeshaceLaBaja() {
    Permanencia ultima = conBaja(HOY.minusDays(30), HOY);
    prepararReactivacion(ultima);
    when(vinculos.existsByDniResponsable(DNI)).thenReturn(false);

    servicio.reactivar(DNI, ADMIN);

    assertTrue(ultima.isVigente());
    verify(permanencias, never()).saveAndFlush(any());
  }

  @Test
  void reactivarElMismoDiaRespetaRn03SiAhoraEsResponsable() {
    Permanencia ultima = conBaja(HOY.minusDays(30), HOY);
    prepararReactivacion(ultima);
    when(vinculos.existsByDniResponsable(DNI)).thenReturn(true);

    ConflictoException error = assertThrows(ConflictoException.class,
        () -> servicio.reactivar(DNI, ADMIN));

    assertEquals("Regla de negocio", error.getTitulo());
    assertFalse(ultima.isVigente());
  }

  @Test
  void reactivarDespuesDeUnaBajaAnteriorAbreUnaNuevaPermanencia() {
    Permanencia ultima = conBaja(HOY.minusDays(60), HOY.minusDays(10));
    prepararReactivacion(ultima);

    servicio.reactivar(DNI, ADMIN);

    ArgumentCaptor<Permanencia> nueva = ArgumentCaptor.forClass(Permanencia.class);
    verify(permanencias).saveAndFlush(nueva.capture());
    assertEquals(HOY, nueva.getValue().getFechaAlta());
    assertTrue(nueva.getValue().isVigente());
    assertFalse(ultima.isVigente());
  }

  private AltaAlumnoRequest alta(String nombre, List<ResponsableAltaRequest> responsables) {
    return new AltaAlumnoRequest(DNI, nombre, "Gómez", LocalDate.of(2015, 3, 10),
        "San Martín 123", responsables);
  }

  private static Permanencia conBaja(LocalDate alta, LocalDate baja) {
    Permanencia permanencia = new Permanencia(DNI, alta, ADMIN);
    permanencia.darDeBaja(baja, ADMIN);
    return permanencia;
  }

  private void prepararReactivacion(Permanencia ultima) {
    when(alumnos.findById(DNI)).thenReturn(
        Optional.of(new Alumno(DNI, LocalDate.of(2015, 3, 10), "San Martín 123")));
    when(personas.findById(DNI)).thenReturn(Optional.of(new Persona(DNI, "Mateo", "Gómez")));
    when(permanencias.findByDniAlumnoAndFechaBajaIsNull(DNI)).thenReturn(Optional.empty());
    when(permanencias.findByDniAlumnoOrderByFechaAltaDesc(DNI)).thenReturn(List.of(ultima));
    when(alumnos.moroso(DNI)).thenReturn(0L);
    when(vinculos.findByDniAlumno(DNI)).thenReturn(List.of());
  }
}
