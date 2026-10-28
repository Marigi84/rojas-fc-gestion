package com.rojasfc.gestion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rojasfc.gestion.dto.AsociacionAlumnoRequest;
import com.rojasfc.gestion.dto.ModificacionResponsableRequest;
import com.rojasfc.gestion.dto.ResponsableAltaRequest;
import com.rojasfc.gestion.dto.ResponsableDetalle;
import com.rojasfc.gestion.exception.ConflictoException;
import com.rojasfc.gestion.exception.RecursoNoEncontradoException;
import com.rojasfc.gestion.model.Persona;
import com.rojasfc.gestion.model.Responsable;
import com.rojasfc.gestion.model.ResponsableAlumno;
import com.rojasfc.gestion.model.Vinculo;
import com.rojasfc.gestion.repository.AlumnoRepository;
import com.rojasfc.gestion.repository.PermanenciaRepository;
import com.rojasfc.gestion.repository.PersonaRepository;
import com.rojasfc.gestion.repository.ResponsableAlumnoRepository;
import com.rojasfc.gestion.repository.ResponsableRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class ResponsableService40Test {

  @Mock
  private PersonaRepository personas;

  @Mock
  private AlumnoRepository alumnos;

  @Mock
  private ResponsableRepository responsables;

  @Mock
  private ResponsableAlumnoRepository vinculos;

  @Mock
  private PermanenciaRepository permanencias;

  @Mock
  private PasswordEncoder passwordEncoder;

  private ResponsableService servicio;

  @BeforeEach
  void setUp() {
    servicio = new ResponsableService(personas, alumnos, responsables, vinculos,
        permanencias, passwordEncoder);
  }

  @Test
  void consultarUnDniSinResponsableDevuelve404() {
    when(responsables.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> servicio.consultar(99L))
        .isInstanceOf(RecursoNoEncontradoException.class);
  }

  @Test
  void altaDeResponsableNuevoReutilizaLaPersonaExistente() {
    ResponsableAltaRequest pedido = new ResponsableAltaRequest(10L, "Laura", "Gómez",
        "3511234567", Vinculo.MADRE, "provisoria123");
    when(alumnos.existsById(20L)).thenReturn(true);
    when(responsables.existsById(10L)).thenReturn(false);
    when(vinculos.existsByDniResponsableAndDniAlumno(10L, 20L)).thenReturn(false);
    when(personas.existsById(10L)).thenReturn(true);
    when(passwordEncoder.encode("provisoria123")).thenReturn("hash");

    servicio.registrarEnAlumno(20L, List.of(pedido));

    verify(personas, never()).saveAndFlush(any(Persona.class));
    verify(responsables).saveAndFlush(any(Responsable.class));
    verify(vinculos).saveAndFlush(any(ResponsableAlumno.class));
  }

  @Test
  void altaDeResponsableExistenteSoloAsocia() {
    ResponsableAltaRequest pedido = new ResponsableAltaRequest(10L, "Laura", "Gómez",
        null, Vinculo.MADRE, null);
    when(responsables.existsById(10L)).thenReturn(true);
    when(vinculos.existsByDniResponsableAndDniAlumno(10L, 20L)).thenReturn(false);
    when(personas.existsById(10L)).thenReturn(true);

    servicio.asociarEnAlta(20L, pedido);

    verify(responsables, never()).saveAndFlush(any(Responsable.class));
    verify(vinculos).saveAndFlush(any(ResponsableAlumno.class));
  }

  @Test
  void noSePuedeDesvincularAlUltimoResponsableDelAlumno() {
    ResponsableAlumno vinculo = new ResponsableAlumno(10L, 20L, Vinculo.MADRE);
    when(vinculos.findByDniResponsableAndDniAlumno(10L, 20L))
        .thenReturn(Optional.of(vinculo));
    when(vinculos.countByDniAlumno(20L)).thenReturn(1L);

    assertThatThrownBy(() -> servicio.desvincular(20L, 10L))
        .isInstanceOf(ConflictoException.class);

    verify(vinculos, never()).delete(any());
    verify(responsables, never()).deleteById(any());
  }

  @Test
  void alDesvincularEliminaElResponsableSiNoLeQuedanAlumnos() {
    ResponsableAlumno vinculo = new ResponsableAlumno(10L, 20L, Vinculo.MADRE);
    when(vinculos.findByDniResponsableAndDniAlumno(10L, 20L))
        .thenReturn(Optional.of(vinculo));
    when(vinculos.countByDniAlumno(20L)).thenReturn(2L);
    when(vinculos.countByDniResponsable(10L)).thenReturn(0L);

    servicio.desvincular(20L, 10L);

    verify(vinculos).delete(vinculo);
    verify(responsables).deleteById(10L);
  }

  @Test
  void modificarActualizaNombreYTelefono() {
    Persona persona = new Persona(10L, "Laura", "Gómez");
    Responsable responsable = new Responsable(10L, "3511234567", "hash", false);
    when(responsables.findById(10L)).thenReturn(Optional.of(responsable));
    when(personas.findById(10L)).thenReturn(Optional.of(persona));
    when(vinculos.findByDniResponsable(10L)).thenReturn(List.of());

    ResponsableDetalle detalle = servicio.modificar(10L,
        new ModificacionResponsableRequest("Lau", "Gómez", "3517654321"));

    assertThat(detalle.nombre()).isEqualTo("Lau");
    assertThat(detalle.telefono()).isEqualTo("3517654321");
  }

  @Test
  void noSePuedeAsociarDosVecesElMismoAlumno() {
    when(responsables.findById(10L))
        .thenReturn(Optional.of(new Responsable(10L, "3511234567", "hash", false)));
    when(alumnos.existsById(20L)).thenReturn(true);
    when(vinculos.existsByDniResponsableAndDniAlumno(10L, 20L)).thenReturn(true);

    assertThatThrownBy(() -> servicio.asociar(10L,
        new AsociacionAlumnoRequest(20L, Vinculo.PADRE)))
        .isInstanceOf(ConflictoException.class);
  }

  @Test
  void noSePuedeAsociarUnAlumnoConsigoMismo() {
    when(responsables.findById(10L))
        .thenReturn(Optional.of(new Responsable(10L, "3511234567", "hash", false)));

    assertThatThrownBy(() -> servicio.asociar(10L,
        new AsociacionAlumnoRequest(10L, Vinculo.MADRE)))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
