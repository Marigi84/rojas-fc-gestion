package com.rojasfc.gestion.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rojasfc.gestion.dto.ResponsableAltaRequest;
import com.rojasfc.gestion.model.ResponsableAlumno;
import com.rojasfc.gestion.model.Vinculo;
import com.rojasfc.gestion.repository.PersonaRepository;
import com.rojasfc.gestion.repository.ResponsableAlumnoRepository;
import com.rojasfc.gestion.repository.ResponsableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Que pide el alta para cada responsable segun ya exista o no (RN-01, RF-06). */
class ResponsableServiceTest {

  private static final Long ALUMNO = 45111222L;
  private static final Long DNI = 30111222L;

  private PersonaRepository personas;
  private ResponsableRepository responsables;
  private ResponsableAlumnoRepository vinculos;
  private ResponsableService servicio;

  @BeforeEach
  void preparar() {
    personas = mock(PersonaRepository.class);
    responsables = mock(ResponsableRepository.class);
    vinculos = mock(ResponsableAlumnoRepository.class);
    servicio = new ResponsableService(personas, responsables, vinculos, mock(PasswordEncoder.class));
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
}
