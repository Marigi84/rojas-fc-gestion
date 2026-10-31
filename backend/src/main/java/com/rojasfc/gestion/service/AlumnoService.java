package com.rojasfc.gestion.service;

import com.rojasfc.gestion.dto.AltaAlumnoRequest;
import com.rojasfc.gestion.dto.AlumnoDetalle;
import com.rojasfc.gestion.dto.AlumnoDetalle.PermanenciaDetalle;
import com.rojasfc.gestion.dto.AlumnoDetalle.ResponsableDetalle;
import com.rojasfc.gestion.dto.AlumnoListadoItem;
import com.rojasfc.gestion.dto.ModificacionAlumnoRequest;
import com.rojasfc.gestion.dto.PaginaAlumnos;
import com.rojasfc.gestion.dto.ResponsableAltaRequest;
import com.rojasfc.gestion.exception.ConflictoException;
import com.rojasfc.gestion.exception.RecursoNoEncontradoException;
import com.rojasfc.gestion.model.Alumno;
import com.rojasfc.gestion.model.Permanencia;
import com.rojasfc.gestion.model.Persona;
import com.rojasfc.gestion.model.Responsable;
import com.rojasfc.gestion.model.ResponsableAlumno;
import com.rojasfc.gestion.repository.AlumnoListadoVista;
import com.rojasfc.gestion.repository.AlumnoRepository;
import com.rojasfc.gestion.repository.PermanenciaRepository;
import com.rojasfc.gestion.repository.PersonaRepository;
import com.rojasfc.gestion.repository.ResponsableAlumnoRepository;
import com.rojasfc.gestion.repository.ResponsableRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Gestion de alumnos (Modulo 2: RF-01 a RF-05 y RF-16).
 *
 * La baja y la reactivacion trabajan sobre permanencia: la baja cierra la
 * permanencia vigente y la reactivacion abre una nueva, sin borrar nada
 * (RN-06 a RN-09). Las reglas que controla la base (RN-03, RN-05, RN-07)
 * llegan como 409 con el mensaje del trigger (GlobalExceptionHandler).
 */
@Service
public class AlumnoService {

  private final PersonaRepository personas;
  private final AlumnoRepository alumnos;
  private final PermanenciaRepository permanencias;
  private final ResponsableRepository responsables;
  private final ResponsableAlumnoRepository vinculos;
  private final ResponsableService responsableService;
  private final Clock reloj;

  public AlumnoService(PersonaRepository personas, AlumnoRepository alumnos,
      PermanenciaRepository permanencias, ResponsableRepository responsables,
      ResponsableAlumnoRepository vinculos, ResponsableService responsableService, Clock reloj) {
    this.personas = personas;
    this.alumnos = alumnos;
    this.permanencias = permanencias;
    this.responsables = responsables;
    this.vinculos = vinculos;
    this.responsableService = responsableService;
    this.reloj = reloj;
  }

  /**
   * Alta de un alumno con al menos un responsable, en una sola transaccion
   * (RF-01, RN-05). Si el DNI ya es alumno, no crea otro: si esta inactivo,
   * avisa que se puede reactivar. Si el DNI ya es una persona registrada con
   * otro rol, reutiliza sus datos (RN-01).
   */
  @Transactional
  public AlumnoDetalle registrar(AltaAlumnoRequest pedido, Long dniAdmin) {
    Long dni = pedido.dni();
    // Primero si ya es alumno: ese caso se resuelve igual aunque el resto del pedido tenga errores.
    if (alumnos.existsById(dni)) {
      throw permanencias.findByDniAlumnoAndFechaBajaIsNull(dni).isPresent()
          ? new ConflictoException("Alumno ya registrado",
              "Ya hay un alumno activo con el DNI " + dni + ".")
          : new ConflictoException("Alumno inactivo", "El alumno con DNI " + dni
              + " ya está registrado y está dado de baja. Se puede reactivar.");
    }
    validarResponsables(pedido);
    if (!personas.existsById(dni)) {
      Nombres.exigir(pedido.nombre(), pedido.apellido(), "del alumno con DNI " + dni);
      personas.saveAndFlush(new Persona(dni, pedido.nombre().trim(), pedido.apellido().trim()));
    }
    alumnos.saveAndFlush(new Alumno(dni, pedido.fechaNacimiento(), pedido.direccion().trim()));
    permanencias.saveAndFlush(new Permanencia(dni, hoy(), dniAdmin));
    for (ResponsableAltaRequest responsable : pedido.responsables()) {
      responsableService.asociarEnAlta(dni, responsable);
    }
    return detalle(dni);
  }

  @Transactional(readOnly = true)
  public AlumnoDetalle consultar(Long dni) {
    return detalle(dni);
  }

  /**
   * Listado paginado con búsqueda y filtros combinables (RF-38, RF-39). Por
   * defecto muestra solo los alumnos activos; estado=todos quita ese filtro.
   */
  @Transactional(readOnly = true)
  public PaginaAlumnos listar(String busqueda, Integer anioNacimiento, String estado,
      Boolean moroso, int pagina, int tamano) {
    if (pagina < 1) {
      throw new IllegalArgumentException("La página tiene que ser mayor o igual a 1.");
    }
    if (tamano < 1 || tamano > 100) {
      throw new IllegalArgumentException("El tamaño de página debe estar entre 1 y 100.");
    }
    String filtroBusqueda = (busqueda == null || busqueda.isBlank()) ? null
        : escaparComodinesLike(busqueda.trim());
    String filtroEstado = normalizarEstado(estado);
    Integer filtroMoroso = moroso == null ? null : (moroso ? 1 : 0);

    Page<AlumnoListadoVista> resultado = alumnos.listar(filtroBusqueda, anioNacimiento,
        filtroEstado, filtroMoroso, PageRequest.of(pagina - 1, tamano));
    List<AlumnoListadoItem> contenido = resultado.getContent().stream()
        .map(fila -> new AlumnoListadoItem(fila.getDni(), fila.getNombre(), fila.getApellido(),
            fila.getAnioNacimiento(), fila.getEstado(),
            fila.getMoroso() != null && fila.getMoroso() == 1))
        .toList();
    return new PaginaAlumnos(contenido, pagina, tamano, resultado.getTotalElements(),
        resultado.getTotalPages());
  }

  private String normalizarEstado(String estado) {
    if (estado == null || estado.isBlank()) {
      return "activo";
    }
    String valor = estado.trim().toLowerCase(Locale.ROOT);
    if ("todos".equals(valor)) {
      return null;
    }
    if (!"activo".equals(valor) && !"inactivo".equals(valor)) {
      throw new IllegalArgumentException("El estado tiene que ser activo, inactivo o todos.");
    }
    return valor;
  }

  /**
   * Escapa los comodines de LIKE para que se busquen como texto. Usa '!' como
   * caracter de escape, el mismo que declara la consulta, para no mezclar
   * barras invertidas entre Java y MySQL.
   */
  private String escaparComodinesLike(String texto) {
    return texto.replace("!", "!!").replace("%", "!%").replace("_", "!_");
  }

  /** Modificacion de los datos del alumno (RF-03). */
  @Transactional
  public AlumnoDetalle modificar(Long dni, ModificacionAlumnoRequest pedido) {
    Alumno alumno = buscarAlumno(dni);
    Persona persona = buscarPersona(dni);
    persona.modificarNombre(pedido.nombre().trim(), pedido.apellido().trim());
    alumno.modificar(pedido.fechaNacimiento(), pedido.direccion().trim());
    return detalle(dni);
  }

  /** Baja logica: cierra la permanencia vigente (RF-04, RN-09). */
  @Transactional
  public AlumnoDetalle darDeBaja(Long dni, Long dniAdmin) {
    buscarAlumno(dni);
    Permanencia vigente = permanencias.findByDniAlumnoAndFechaBajaIsNull(dni)
        .orElseThrow(() -> new ConflictoException("Alumno inactivo",
            "El alumno con DNI " + dni + " ya está dado de baja."));
    vigente.darDeBaja(hoy(), dniAdmin);
    permanencias.flush();
    return detalle(dni);
  }

  /**
   * Reactivacion: abre una nueva permanencia y conserva las anteriores (RF-05,
   * RN-06, RN-07). Si la baja fue hoy, se toma como una baja por error y se
   * deshace, reabriendo esa misma permanencia: la clave (alumno, fecha de
   * alta) no admite dos altas el mismo dia.
   */
  @Transactional
  public AlumnoDetalle reactivar(Long dni, Long dniAdmin) {
    buscarAlumno(dni);
    if (permanencias.findByDniAlumnoAndFechaBajaIsNull(dni).isPresent()) {
      throw new ConflictoException("Alumno activo", "El alumno con DNI " + dni + " ya está activo.");
    }
    LocalDate hoy = hoy();
    Permanencia ultima = permanencias.findByDniAlumnoOrderByFechaAltaDesc(dni).get(0);
    if (hoy.equals(ultima.getFechaBaja())) {
      // RN-03: el trigger de la base controla solo el INSERT de permanencia, no esta reapertura.
      if (vinculos.existsByDniResponsable(dni)) {
        throw new ConflictoException("Regla de negocio", "Un responsable no puede ser alumno activo");
      }
      ultima.anularBaja();
      permanencias.flush();
    } else {
      permanencias.saveAndFlush(new Permanencia(dni, hoy, dniAdmin));
    }
    return detalle(dni);
  }

  private void validarResponsables(AltaAlumnoRequest pedido) {
    Set<Long> dnis = new HashSet<>();
    for (ResponsableAltaRequest responsable : pedido.responsables()) {
      if (responsable.dni().equals(pedido.dni())) {
        throw new IllegalArgumentException("Un alumno no puede ser su propio responsable.");
      }
      if (!dnis.add(responsable.dni())) {
        throw new IllegalArgumentException("El responsable con DNI " + responsable.dni()
            + " está cargado más de una vez.");
      }
    }
  }

  private AlumnoDetalle detalle(Long dni) {
    Alumno alumno = buscarAlumno(dni);
    Persona persona = buscarPersona(dni);
    List<Permanencia> historial = permanencias.findByDniAlumnoOrderByFechaAltaDesc(dni);
    boolean activo = historial.stream().anyMatch(Permanencia::isVigente);
    Long moroso = alumnos.moroso(dni);
    List<ResponsableDetalle> aCargo = vinculos.findByDniAlumno(dni).stream()
        .map(this::responsableDetalle)
        .sorted(Comparator.comparing(ResponsableDetalle::apellido)
            .thenComparing(ResponsableDetalle::nombre))
        .toList();
    return new AlumnoDetalle(dni, persona.getNombre(), persona.getApellido(),
        alumno.getFechaNacimiento(), alumno.getDireccion(), alumno.getCategoria(),
        activo ? "activo" : "inactivo", moroso != null && moroso == 1L,
        historial.stream().map(p -> new PermanenciaDetalle(p.getFechaAlta(), p.getFechaBaja()))
            .toList(),
        aCargo);
  }

  private ResponsableDetalle responsableDetalle(ResponsableAlumno vinculo) {
    Long dni = vinculo.getDniResponsable();
    Persona persona = buscarPersona(dni);
    Responsable responsable = responsables.findById(dni)
        .orElseThrow(() -> new IllegalStateException("Falta el responsable del DNI " + dni));
    return new ResponsableDetalle(dni, persona.getNombre(), persona.getApellido(),
        responsable.getTelefono(), vinculo.getVinculo());
  }

  private Alumno buscarAlumno(Long dni) {
    return alumnos.findById(dni).orElseThrow(() ->
        new RecursoNoEncontradoException("No hay ningún alumno con el DNI " + dni + "."));
  }

  private Persona buscarPersona(Long dni) {
    return personas.findById(dni)
        .orElseThrow(() -> new IllegalStateException("Falta la persona del DNI " + dni));
  }

  private LocalDate hoy() {
    return LocalDate.now(reloj);
  }
}
