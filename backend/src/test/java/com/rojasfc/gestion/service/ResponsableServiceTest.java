package com.rojasfc.gestion.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rojasfc.gestion.dto.ResponsableAltaRequest;
import com.rojasfc.gestion.dto.RestablecimientoContrasenaRequest;
import com.rojasfc.gestion.exception.RecursoNoEncontradoException;
import com.rojasfc.gestion.model.Responsable;
import com.rojasfc.gestion.model.ResponsableAlumno;
import com.rojasfc.gestion.model.Vinculo;
import com.rojasfc.gestion.repository.AlumnoRepository;
import com.rojasfc.gestion.repository.PermanenciaRepository;
import com.rojasfc.gestion.repository.PersonaRepository;
import com.rojasfc.gestion.repository.ResponsableAlumnoRepository;
import com.rojasfc.gestion.repository.ResponsableRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Que pide el alta para cada responsable segun ya exista o no (RN-01, RF-06),
 * y el restablecimiento de su contrasena (RF-15).
 */
class ResponsableServiceTest {

  private static final Long ALUMNO = 45111222L;
  private static final Long DNI = 30111222L;

  private PersonaRepository personas;
  private AlumnoRepository alumnos;
  private ResponsableRepository responsables;
  private ResponsableAlumnoRepository vinculos;
  private PermanenciaRepository permanencias;
  private ResponsableService servicio;

  @BeforeEach
  void preparar() {
    personas = mock(PersonaRepository.class);
    alumnos = mock(AlumnoRepository.class);
    responsables = mock(ResponsableRepository.class);
    vinculos = mock(ResponsableAlumnoRepository.class);
    permanencias = mock(PermanenciaRepository.class);
    servicio = new ResponsableService(personas, alumnos, responsables, vinculos, permanencias,
        mock(PasswordEncoder.class));
  }

  @Test
  void unResponsableExistenteSeAsociaSinNombreTelefonoNiContrasena() {
    when(personas.existsById(DNI)).thenReturn(true);
    when(responsables.existsById(DNI)).thenReturn(true);

    servicio.asociarEnAlta(ALUMNO,
        new ResponsableAltaRequest(DNI, null, null, null, Vinculo.PADRE, null));

    verify(vinculos).saveAndFlush(any(ResponsableAlumno.class));
    verify(personas, never()).saveAndFlush(any());
    verify(responsables, never()).saveAndFlush(any());
  }

  @Test
  void unResponsableNuevoSinTelefonoNoSeRegistra() {
    when(personas.existsById(DNI)).thenReturn(false);
    when(responsables.existsById(DNI)).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> servicio.asociarEnAlta(ALUMNO,
        new ResponsableAltaRequest(DNI, "Laura", "Gómez", null, Vinculo.MADRE, "provisoria123")));

    verify(personas, never()).saveAndFlush(any());
    verify(vinculos, never()).saveAndFlush(any());
  }

  @Test
  void unaPersonaNuevaSinNombreNoSeRegistraComoResponsable() {
    when(personas.existsById(DNI)).thenReturn(false);
    when(responsables.existsById(DNI)).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> servicio.asociarEnAlta(ALUMNO,
        new ResponsableAltaRequest(DNI, null, null, "3511234567", Vinculo.MADRE, "provisoria123")));

    verify(personas, never()).saveAndFlush(any());
  }

  @Test
  void restablecerLaContrasenaLaDejaProvisoria() {
    Responsable responsable = new Responsable(DNI, "3511234567", "hash-viejo", false);
    when(responsables.findById(DNI)).thenReturn(Optional.of(responsable));

    servicio.restablecerContrasena(DNI, new RestablecimientoContrasenaRequest("provisoria456"));

    assertTrue(responsable.isContrasenaTemporal());
  }

  @Test
  void noSeRestableceLaContrasenaDeQuienNoEsResponsable() {
    when(responsables.findById(DNI)).thenReturn(Optional.empty());

    assertThrows(RecursoNoEncontradoException.class, () ->
        servicio.restablecerContrasena(DNI, new RestablecimientoContrasenaRequest("provisoria456")));
  }
}
