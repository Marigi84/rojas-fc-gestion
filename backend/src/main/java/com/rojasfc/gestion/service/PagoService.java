package com.rojasfc.gestion.service;

import com.rojasfc.gestion.dto.PagoDetalle;
import com.rojasfc.gestion.dto.ReciboDetalle;
import com.rojasfc.gestion.dto.RegistroPagoRequest;
import com.rojasfc.gestion.exception.ConflictoException;
import com.rojasfc.gestion.exception.RecursoNoEncontradoException;
import com.rojasfc.gestion.model.Pago;
import com.rojasfc.gestion.model.Recibo;
import com.rojasfc.gestion.repository.AlumnoRepository;
import com.rojasfc.gestion.repository.CuotaAPagarVista;
import com.rojasfc.gestion.repository.PagoRepository;
import com.rojasfc.gestion.repository.PagoVista;
import com.rojasfc.gestion.repository.ReciboDetalleVista;
import com.rojasfc.gestion.repository.ReciboRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Pagos y recibos.
 *
 * El importe no lo manda el cliente: se calcula con el periodo y la fecha, y
 * tiene que coincidir con el total que exige el trigger pago_cuota_monto_exacto.
 * Cada pago confirmado genera su recibo en la misma operacion.
 */
@Service
public class PagoService {

  private static final String ESTADO_CONFIRMADO = "confirmado";
  private static final Set<String> MEDIOS_DE_PAGO = Set.of("efectivo", "transferencia");

  private final PagoRepository pagos;
  private final ReciboRepository recibos;
  private final AlumnoRepository alumnos;
  private final Clock reloj;

  public PagoService(PagoRepository pagos, ReciboRepository recibos, AlumnoRepository alumnos,
      Clock reloj) {
    this.pagos = pagos;
    this.recibos = recibos;
    this.alumnos = alumnos;
    this.reloj = reloj;
  }

  /**
   * Registra el pago total de una cuota y emite su recibo. La fecha es la del dia en que se registra y el
   * importe sale de vista_cuota, que ya incluye el interes si esta vencida.
   */
  @Transactional
  public PagoDetalle registrar(RegistroPagoRequest pedido, Long dniAdmin) {
    LocalDate periodo = inicioDelMes(pedido.periodo());
    String medioPago = normalizarMedio(pedido.medioPago());
    if (!alumnos.existsById(pedido.dniAlumno())) {
      throw new RecursoNoEncontradoException(
          "No hay ningún alumno con el DNI " + pedido.dniAlumno() + ".");
    }
    CuotaAPagarVista cuota = pagos.cuotaAPagar(pedido.dniAlumno(), periodo)
        .orElseThrow(() -> new RecursoNoEncontradoException(
            "El alumno no tiene una cuota del período " + pedido.periodo() + "."));
    if ("pagada".equals(cuota.getEstado())) {
      throw new ConflictoException("Cuota ya pagada",
          "La cuota del período " + pedido.periodo() + " ya está pagada.");
    }
    LocalDate hoy = LocalDate.now(reloj);
    Pago pago = pagos.saveAndFlush(new Pago(hoy, cuota.getMonto(), medioPago, dniAdmin,
        pedido.dniAlumno(), periodo, ESTADO_CONFIRMADO));
    Recibo recibo = recibos.saveAndFlush(new Recibo(pago.getIdPago(), hoy));
    return new PagoDetalle(pago.getIdPago(), pago.getFecha(), pago.getMonto(),
        pago.getMedioPago(), mes(periodo), null, null, recibo.getNumeroRecibo());
  }

  /**
   * Anula un pago conservando el registro y el motivo.
   * El recibo no se borra: queda identificado como anulado.
   */
  @Transactional
  public void anular(Long idPago, String motivo, Long dniAdmin) {
    Pago pago = pagos.findById(idPago)
        .orElseThrow(() -> new RecursoNoEncontradoException(
            "No hay ningún pago con el número " + idPago + "."));
    if (pago.estaAnulado()) {
      throw new ConflictoException("Pago ya anulado", "El pago ya estaba anulado.");
    }
    pago.anular(LocalDate.now(reloj), motivo.trim(), dniAdmin);
    pagos.saveAndFlush(pago);
  }

  /** Historial de pagos del alumno, incluidos los anulados. */
  @Transactional(readOnly = true)
  public List<PagoDetalle> historial(Long dni) {
    if (!alumnos.existsById(dni)) {
      throw new RecursoNoEncontradoException("No hay ningún alumno con el DNI " + dni + ".");
    }
    return pagos.deAlumno(dni).stream().map(this::detalle).toList();
  }

  /** Recibo emitido, con el alumno y el pago al que corresponde. */
  @Transactional(readOnly = true)
  public ReciboDetalle recibo(Long numeroRecibo) {
    ReciboDetalleVista fila = recibos.detalle(numeroRecibo)
        .orElseThrow(() -> new RecursoNoEncontradoException(
            "No hay ningún recibo con el número " + numeroRecibo + "."));
    return new ReciboDetalle(fila.getNumeroRecibo(), fila.getIdPago(), fila.getFechaEmision(),
        fila.getDniAlumno(), fila.getNombre() + " " + fila.getApellido(), mes(fila.getPeriodo()),
        fila.getMonto(), fila.getMedioPago(), fila.getAnulado() != null && fila.getAnulado() == 1);
  }

  private PagoDetalle detalle(PagoVista fila) {
    return new PagoDetalle(fila.getIdPago(), fila.getFecha(), fila.getMonto(), fila.getMedioPago(),
        mes(fila.getPeriodo()), fila.getFechaAnulacion(), fila.getMotivo(), fila.getNumeroRecibo());
  }

  /** Los medios admitidos son efectivo y transferencia. */
  private String normalizarMedio(String medioPago) {
    String valor = medioPago.trim().toLowerCase(Locale.ROOT);
    if (!MEDIOS_DE_PAGO.contains(valor)) {
      throw new IllegalArgumentException("El medio de pago tiene que ser efectivo o transferencia.");
    }
    return valor;
  }

  private LocalDate inicioDelMes(String periodo) {
    try {
      return YearMonth.parse(periodo).atDay(1);
    } catch (DateTimeParseException ex) {
      throw new IllegalArgumentException("El período tiene que tener el formato AAAA-MM.");
    }
  }

  private static String mes(LocalDate periodo) {
    return periodo == null ? null : YearMonth.from(periodo).toString();
  }
}
