package com.rojasfc.gestion.service;

import com.rojasfc.gestion.dto.AsociacionAlumnoRequest;
import com.rojasfc.gestion.dto.ModificacionResponsableRequest;
import com.rojasfc.gestion.dto.ResponsableAltaRequest;
import com.rojasfc.gestion.dto.ResponsableDetalle;
import com.rojasfc.gestion.dto.ResponsableDetalle.AlumnoAsociado;
import com.rojasfc.gestion.dto.RestablecimientoContrasenaRequest;
import com.rojasfc.gestion.exception.ConflictoException;
import com.rojasfc.gestion.exception.RecursoNoEncontradoException;
import com.rojasfc.gestion.model.Alumno;
import com.rojasfc.gestion.model.Persona;
import com.rojasfc.gestion.model.Responsable;
import com.rojasfc.gestion.model.ResponsableAlumno;
import com.rojasfc.gestion.repository.AlumnoRepository;
import com.rojasfc.gestion.repository.PermanenciaRepository;
import com.rojasfc.gestion.repository.PersonaRepository;
import com.rojasfc.gestion.repository.ResponsableAlumnoRepository;
import com.rojasfc.gestion.repository.ResponsableRepository;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Responsables (Modulo 2: RF-06 a RF-10) y restablecimiento de su contrasena
 * (RF-15).
 *
 * El alta de un alumno (#39) usa {@link #asociarEnAlta} dentro de su propia
 * transaccion. El resto de las operaciones trabajan sobre un alumno existente
 * o sobre un responsable existente.
 */
@Service
public class ResponsableService {

  private final PersonaRepository personas;
  private final AlumnoRepository alumnos;
  private final ResponsableRepository responsables;
  private final ResponsableAlumnoRepository vinculos;
  private final PermanenciaRepository permanencias;
  private final PasswordEncoder passwordEncoder;

  public ResponsableService(PersonaRepository personas, AlumnoRepository alumnos,
      ResponsableRepository responsables, ResponsableAlumnoRepository vinculos,
      PermanenciaRepository permanencias, PasswordEncoder passwordEncoder) {
    this.personas = personas;
    this.alumnos = alumnos;
    this.responsables = responsables;
    this.vinculos = vinculos;
    this.permanencias = permanencias;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Agrega uno o mas responsables a un alumno existente (RF-06). Si el DNI ya
   * es persona o responsable, se reutiliza (RN-01); si es nuevo, se crea con
   * contrasena provisoria (RF-11, RNF-01). Todo ocurre en una transaccion.
   */
  @Transactional
  public void registrarEnAlumno(Long dniAlumno, List<ResponsableAltaRequest> pedidos) {
    if (!alumnos.existsById(dniAlumno)) {
      throw new RecursoNoEncontradoException("No hay ningún alumno con el DNI " + dniAlumno + ".");
    }
    validarLista(dniAlumno, pedidos);
    for (ResponsableAltaRequest pedido : pedidos) {
      asociarEnAlta(dniAlumno, pedido);
    }
  }

  /**
   * Asocia un responsable al alumno, reutilizando la persona y el rol si ya
   * existen (RF-06, RN-01) y registrando el vinculo (RN-12). Corre dentro de
   * la transaccion del alta, para que alumno y responsables se guarden juntos
   * (RN-05).
   */
  @Transactional(propagation = Propagation.MANDATORY)
  public void asociarEnAlta(Long dniAlumno, ResponsableAltaRequest pedido) {
    Long dni = pedido.dni();
    if (dni.equals(dniAlumno)) {
      throw new IllegalArgumentException("Un alumno no puede ser su propio responsable.");
    }
    boolean esResponsable = responsables.existsById(dni);
    boolean personaNueva = !personas.existsById(dni);
    if (personaNueva) {
      Nombres.exigir(pedido.nombre(), pedido.apellido(), "del responsable con DNI " + dni);
    }
    if (!esResponsable) {
      validarResponsableNuevo(pedido);
    }
    if (vinculos.existsByDniResponsableAndDniAlumno(dni, dniAlumno)) {
      throw new ConflictoException("Responsable ya asociado",
          "El responsable con DNI " + dni + " ya está asociado al alumno.");
    }
    if (personaNueva) {
      personas.saveAndFlush(new Persona(dni, pedido.nombre().trim(), pedido.apellido().trim()));
    }
    if (!esResponsable) {
      // La contrasena la asigna el Administrador y es provisoria (RF-11, RNF-01).
      responsables.saveAndFlush(new Responsable(dni, pedido.telefono().trim(),
          passwordEncoder.encode(pedido.contrasenaProvisoria()), true));
    }
    vinculos.saveAndFlush(new ResponsableAlumno(dni, dniAlumno, pedido.vinculo()));
  }

  /** Consulta un responsable y los alumnos a su cargo (RF-07). */
  @Transactional(readOnly = true)
  public ResponsableDetalle consultar(Long dni) {
    return detalle(dni);
  }

  /** Modificacion de nombre, apellido y telefono (RF-08). */
  @Transactional
  public ResponsableDetalle modificar(Long dni, ModificacionResponsableRequest pedido) {
    Responsable responsable = buscarResponsable(dni);
    Persona persona = buscarPersona(dni);
    persona.modificarNombre(pedido.nombre().trim(), pedido.apellido().trim());
    responsable.modificarTelefono(pedido.telefono().trim());
    return detalle(dni);
  }

  /** Asocia un responsable existente con otro alumno (RF-09, RN-11). */
  @Transactional
  public ResponsableDetalle asociar(Long dniResponsable, AsociacionAlumnoRequest pedido) {
    buscarResponsable(dniResponsable);
    if (dniResponsable.equals(pedido.dniAlumno())) {
      throw new IllegalArgumentException("Un alumno no puede ser su propio responsable.");
    }
    if (!alumnos.existsById(pedido.dniAlumno())) {
      throw new RecursoNoEncontradoException(
          "No hay ningún alumno con el DNI " + pedido.dniAlumno() + ".");
    }
    if (vinculos.existsByDniResponsableAndDniAlumno(dniResponsable, pedido.dniAlumno())) {
      throw new ConflictoException("Responsable ya asociado",
          "El responsable con DNI " + dniResponsable + " ya está asociado al alumno "
              + pedido.dniAlumno() + ".");
    }
    vinculos.saveAndFlush(
        new ResponsableAlumno(dniResponsable, pedido.dniAlumno(), pedido.vinculo()));
    return detalle(dniResponsable);
  }

  /**
   * Desvincula un responsable de un alumno (RF-10). El alumno debe conservar
   * al menos otro responsable (RN-05); si el responsable se queda sin alumnos,
   * se elimina su rol y, si no tiene otro rol, la persona (RN-02).
   */
  @Transactional
  public void desvincular(Long dniAlumno, Long dniResponsable) {
    ResponsableAlumno vinculo = vinculos
        .findByDniResponsableAndDniAlumno(dniResponsable, dniAlumno)
        .orElseThrow(() -> new RecursoNoEncontradoException(
            "El responsable con DNI " + dniResponsable + " no está asociado al alumno "
                + dniAlumno + "."));
    if (vinculos.countByDniAlumno(dniAlumno) <= 1) {
      throw new ConflictoException("Último responsable",
          "El alumno debe conservar al menos un responsable.");
    }
    vinculos.delete(vinculo);
    vinculos.flush();
    if (vinculos.countByDniResponsable(dniResponsable) == 0) {
      responsables.deleteById(dniResponsable);
      responsables.flush();
    }
  }

  /**
   * El Administrador le asigna al responsable una contrasena provisoria nueva,
   * por ejemplo si se olvido la suya (RF-15). En su proximo ingreso la tiene
   * que cambiar (RNF-01).
   */
  @Transactional
  public void restablecerContrasena(Long dni, RestablecimientoContrasenaRequest pedido) {
    Responsable responsable = responsables.findById(dni).orElseThrow(() ->
        new RecursoNoEncontradoException("No hay ningún responsable con el DNI " + dni + "."));
    responsable.restablecerContrasena(passwordEncoder.encode(pedido.contrasenaProvisoria()));
  }

  private void validarLista(Long dniAlumno, List<ResponsableAltaRequest> pedidos) {
    Set<Long> dnis = new HashSet<>();
    for (ResponsableAltaRequest pedido : pedidos) {
      if (pedido.dni().equals(dniAlumno)) {
        throw new IllegalArgumentException("Un alumno no puede ser su propio responsable.");
      }
      if (!dnis.add(pedido.dni())) {
        throw new IllegalArgumentException("El responsable con DNI " + pedido.dni()
            + " está cargado más de una vez.");
      }
    }
  }

  private void validarResponsableNuevo(ResponsableAltaRequest pedido) {
    if (pedido.telefono() == null || pedido.telefono().isBlank()) {
      throw new IllegalArgumentException("Falta el teléfono del responsable con DNI "
          + pedido.dni() + ", que se registra por primera vez.");
    }
    if (pedido.contrasenaProvisoria() == null) {
      throw new IllegalArgumentException("Falta la contraseña provisoria del responsable con DNI "
          + pedido.dni() + ", que se registra por primera vez.");
    }
  }

  private ResponsableDetalle detalle(Long dni) {
    Responsable responsable = buscarResponsable(dni);
    Persona persona = buscarPersona(dni);
    List<AlumnoAsociado> asociados = vinculos.findByDniResponsable(dni).stream()
        .map(this::alumnoAsociado)
        .sorted(Comparator.comparing(AlumnoAsociado::apellido)
            .thenComparing(AlumnoAsociado::nombre))
        .toList();
    return new ResponsableDetalle(dni, persona.getNombre(), persona.getApellido(),
        responsable.getTelefono(), asociados);
  }

  private AlumnoAsociado alumnoAsociado(ResponsableAlumno vinculo) {
    Long dniAlumno = vinculo.getDniAlumno();
    Alumno alumno = alumnos.findById(dniAlumno)
        .orElseThrow(() -> new IllegalStateException("Falta el alumno del DNI " + dniAlumno));
    Persona persona = buscarPersona(dniAlumno);
    boolean activo = permanencias.findByDniAlumnoAndFechaBajaIsNull(dniAlumno).isPresent();
    return new AlumnoAsociado(alumno.getDni(), persona.getNombre(), persona.getApellido(),
        vinculo.getVinculo(), activo);
  }

  private Responsable buscarResponsable(Long dni) {
    return responsables.findById(dni).orElseThrow(() ->
        new RecursoNoEncontradoException("No hay ningún responsable con el DNI " + dni + "."));
  }

  private Persona buscarPersona(Long dni) {
    return personas.findById(dni)
        .orElseThrow(() -> new IllegalStateException("Falta la persona del DNI " + dni));
  }
}
