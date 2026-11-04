package com.rojasfc.gestion.service;

import com.rojasfc.gestion.dto.AlumnoMoroso;
import com.rojasfc.gestion.dto.ConfiguracionPeriodoRequest;
import com.rojasfc.gestion.dto.CuotaDetalle;
import com.rojasfc.gestion.dto.GeneracionCuotasResultado;
import com.rojasfc.gestion.dto.PeriodoDetalle;
import com.rojasfc.gestion.exception.ConflictoException;
import com.rojasfc.gestion.exception.RecursoNoEncontradoException;
import com.rojasfc.gestion.model.Periodo;
import com.rojasfc.gestion.repository.AlumnoRepository;
import com.rojasfc.gestion.repository.CuotaRepository;
import com.rojasfc.gestion.repository.CuotasPorPeriodo;
import com.rojasfc.gestion.repository.PeriodoRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Periodos y cuotas (Modulo 4: RF-17 a RF-23).
 *
 * El interes por mora y el estado de cada cuota no se guardan: los calcula
 * vista_cuota, de modo que se aplican una sola vez y siempre con las
 * condiciones del periodo (RF-21, RN-19, RN-20).
 */
@Service
public class CuotaService {

  private final PeriodoRepository periodos;
  private final CuotaRepository cuotas;
  private final AlumnoRepository alumnos;
  private final Clock reloj;

  public CuotaService(PeriodoRepository periodos, CuotaRepository cuotas,
      AlumnoRepository alumnos, Clock reloj) {
    this.periodos = periodos;
    this.cuotas = cuotas;
    this.alumnos = alumnos;
    this.reloj = reloj;
  }

  /** Periodos configurados, del mas reciente al mas antiguo. */
  @Transactional(readOnly = true)
  public List<PeriodoDetalle> listarPeriodos() {
    Map<LocalDate, Long> generadas = cuotas.contarPorPeriodo().stream()
        .collect(Collectors.toMap(CuotasPorPeriodo::getPeriodo, CuotasPorPeriodo::getCantidad));
    return periodos.findAllByOrderByPeriodoDesc().stream()
        .map(periodo -> detalle(periodo, generadas.getOrDefault(periodo.getPeriodo(), 0L)))
        .toList();
  }

  /**
   * Crea o modifica las condiciones de un periodo (RF-17, RF-19, RF-20). Con
   * cuotas ya generadas no se puede cambiar (RN-22): los cambios rigen desde
   * el periodo siguiente.
   */
  @Transactional
  public PeriodoDetalle configurar(String mes, ConfiguracionPeriodoRequest pedido, Long dniAdmin) {
    LocalDate inicio = inicioDelMes(mes);
    if (pedido.fechaVencimiento().isBefore(inicio)) {
      throw new IllegalArgumentException(
          "El vencimiento no puede ser anterior al inicio del período.");
    }
    if (cuotas.existsByPeriodo(inicio)) {
      throw new ConflictoException("Regla de negocio", "El período ya tiene cuotas generadas");
    }
    LocalDate hoy = LocalDate.now(reloj);
    Periodo periodo = periodos.findById(inicio)
        .map(existente -> {
          existente.configurar(pedido.importe(), pedido.fechaVencimiento(),
              pedido.porcentajeInteres(), dniAdmin, hoy);
          return existente;
        })
        .orElseGet(() -> new Periodo(inicio, pedido.importe(), pedido.fechaVencimiento(),
            pedido.porcentajeInteres(), dniAdmin, hoy));
    return detalle(periodos.saveAndFlush(periodo), 0L);
  }

  /** Genera a pedido del Administrador las cuotas de un periodo ya configurado (RF-18). */
  @Transactional
  public GeneracionCuotasResultado generar(String mes) {
    LocalDate inicio = inicioDelMes(mes);
    if (!periodos.existsById(inicio)) {
      throw new RecursoNoEncontradoException("El período " + mes + " no está configurado.");
    }
    LocalDate hoy = LocalDate.now(reloj);
    if (inicio.isAfter(hoy)) {
      throw new IllegalArgumentException(
          "Las cuotas de " + mes + " se pueden generar recién cuando empiece el mes.");
    }
    if (cuotas.existsByPeriodo(inicio)) {
      throw new ConflictoException("Regla de negocio", "El período ya tiene cuotas generadas");
    }
    return new GeneracionCuotasResultado(mes, cuotas.generar(inicio, hoy));
  }

  /**
   * Generacion automatica del mes en curso (RF-18, RN-15). Si el mes no tiene
   * cuotas, las genera; si ademas no esta configurado, toma el ultimo valor
   * vigente (RN-17). Antes del primer periodo configurado no hace nada.
   *
   * @return la cantidad de cuotas generadas
   */
  @Transactional
  public int generarDelMes() {
    LocalDate hoy = LocalDate.now(reloj);
    LocalDate inicio = hoy.withDayOfMonth(1);
    if (cuotas.existsByPeriodo(inicio)) {
      return 0;
    }
    Optional<Periodo> configurado = periodos.findById(inicio);
    if (configurado.isEmpty()) {
      configurado = periodos.findFirstByPeriodoLessThanOrderByPeriodoDesc(inicio)
          .map(anterior -> periodos.saveAndFlush(conCondicionesDe(anterior, inicio, hoy)));
    }
    return configurado.isPresent() ? cuotas.generar(inicio, hoy) : 0;
  }

  /** Cuotas de un alumno, de la mas reciente a la mas antigua (RF-22). */
  @Transactional(readOnly = true)
  public List<CuotaDetalle> deAlumno(Long dni) {
    if (!alumnos.existsById(dni)) {
      throw new RecursoNoEncontradoException("No hay ningún alumno con el DNI " + dni + ".");
    }
    return cuotas.deAlumno(dni).stream()
        .map(cuota -> new CuotaDetalle(YearMonth.from(cuota.getPeriodo()).toString(),
            cuota.getImporte(), cuota.getFechaVencimiento(), cuota.getMontoAPagar(),
            cuota.getEstado(), cuota.getIdPago(), cuota.getFechaPago()))
        .toList();
  }

  /** Alumnos con deuda vencida y el total adeudado por cada uno (RF-23, RN-23). */
  @Transactional(readOnly = true)
  public List<AlumnoMoroso> morosos() {
    return cuotas.morosos().stream()
        .map(moroso -> new AlumnoMoroso(moroso.getDni(), moroso.getNombre(), moroso.getApellido(),
            moroso.getEstado(), moroso.getCuotasVencidas(), moroso.getTotalAdeudado()))
        .toList();
  }

  /** Mismas condiciones y mismo dia de vencimiento, ajustado si el mes es mas corto. */
  private Periodo conCondicionesDe(Periodo anterior, LocalDate inicio, LocalDate hoy) {
    int dia = Math.min(anterior.getFechaVencimiento().getDayOfMonth(), inicio.lengthOfMonth());
    return new Periodo(inicio, anterior.getImporte(), inicio.withDayOfMonth(dia),
        anterior.getPorcentajeInteres(), anterior.getDniAdmin(), hoy);
  }

  private PeriodoDetalle detalle(Periodo periodo, long generadas) {
    return new PeriodoDetalle(YearMonth.from(periodo.getPeriodo()).toString(), periodo.getImporte(),
        periodo.getFechaVencimiento(), periodo.getPorcentajeInteres(),
        periodo.getFechaConfiguracion(), generadas);
  }

  private LocalDate inicioDelMes(String mes) {
    try {
      return YearMonth.parse(mes).atDay(1);
    } catch (DateTimeParseException ex) {
      throw new IllegalArgumentException("El período tiene que tener el formato AAAA-MM.");
    }
  }
}
