package com.rojasfc.gestion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.rojasfc.gestion.repository.ReciboDetalleVista;
import com.rojasfc.gestion.repository.ReciboRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/** Reglas de PagoService que no dependen de la base (RF-24, RF-25, RF-28, RF-29). */
class PagoServiceTest {

  private static final Long ADMIN = 20111222L;
  private static final Long ALUMNO = 45111222L;
  private static final LocalDate HOY = LocalDate.of(2027, 2, 10);
  private static final LocalDate PERIODO = LocalDate.of(2027, 2, 1);
  private static final ZoneId ZONA = ZoneId.of("America/Argentina/Cordoba");

  private PagoRepository pagos;
  private ReciboRepository recibos;
  private AlumnoRepository alumnos;
  private PagoService servicio;

  @BeforeEach
  void preparar() {
    pagos = mock(PagoRepository.class);
    recibos = mock(ReciboRepository.class);
    alumnos = mock(AlumnoRepository.class);
    Clock reloj = Clock.fixed(HOY.atStartOfDay(ZONA).toInstant(), ZONA);
    servicio = new PagoService(pagos, recibos, alumnos, reloj);
    when(pagos.saveAndFlush(any(Pago.class))).thenAnswer(pedido -> pedido.getArgument(0));
    when(recibos.saveAndFlush(any(Recibo.class))).thenAnswer(pedido -> pedido.getArgument(0));
  }

  @Test
  void elPagoUsaElMontoDeLaCuotaYEmiteElRecibo() {
    when(alumnos.existsById(ALUMNO)).thenReturn(true);
    CuotaAPagarVista cuota = cuota("vencida", "1100.00");
    when(pagos.cuotaAPagar(ALUMNO, PERIODO)).thenReturn(Optional.of(cuota));

    PagoDetalle detalle = servicio.registrar(pedido("2027-02", "efectivo"), ADMIN);

    ArgumentCaptor<Pago> capturado = ArgumentCaptor.forClass(Pago.class);
    verify(pagos).saveAndFlush(capturado.capture());
    assertEquals(new BigDecimal("1100.00"), capturado.getValue().getMonto());
    assertEquals("efectivo", capturado.getValue().getMedioPago());
    verify(recibos).saveAndFlush(any(Recibo.class));
    assertEquals("2027-02", detalle.periodo());
  }

  @Test
  void noSePuedePagarUnaCuotaYaPaga() {
    when(alumnos.existsById(ALUMNO)).thenReturn(true);
    CuotaAPagarVista cuota = cuota("pagada", "1100.00");
    when(pagos.cuotaAPagar(ALUMNO, PERIODO)).thenReturn(Optional.of(cuota));

    assertThrows(ConflictoException.class,
        () -> servicio.registrar(pedido("2027-02", "efectivo"), ADMIN));

    verify(pagos, never()).saveAndFlush(any(Pago.class));
    verify(recibos, never()).saveAndFlush(any(Recibo.class));
  }

  @Test
  void noSePuedePagarUnaCuotaQueNoExiste() {
    when(alumnos.existsById(ALUMNO)).thenReturn(true);
    when(pagos.cuotaAPagar(ALUMNO, PERIODO)).thenReturn(Optional.empty());

    assertThrows(RecursoNoEncontradoException.class,
        () -> servicio.registrar(pedido("2027-02", "efectivo"), ADMIN));
  }

  @Test
  void elMedioDePagoTieneQueSerEfectivoOTransferencia() {
    assertThrows(IllegalArgumentException.class,
        () -> servicio.registrar(pedido("2027-02", "cheque"), ADMIN));

    verify(pagos, never()).saveAndFlush(any(Pago.class));
  }

  @Test
  void elPeriodoTieneQueTenerFormatoDeMes() {
    assertThrows(IllegalArgumentException.class,
        () -> servicio.registrar(pedido("2027-2", "efectivo"), ADMIN));
  }

  @Test
  void noSePuedeAnularUnPagoYaAnulado() {
    Pago pago = pagoVigente();
    pago.anular(HOY, "error de carga", ADMIN);
    when(pagos.findById(7L)).thenReturn(Optional.of(pago));

    assertThrows(ConflictoException.class,
        () -> servicio.anular(7L, "otro motivo", ADMIN));

    verify(pagos, never()).saveAndFlush(any(Pago.class));
  }

  @Test
  void anularGuardaElMotivoYLaFecha() {
    when(pagos.findById(7L)).thenReturn(Optional.of(pagoVigente()));

    servicio.anular(7L, "  se cobró dos veces  ", ADMIN);

    ArgumentCaptor<Pago> capturado = ArgumentCaptor.forClass(Pago.class);
    verify(pagos).saveAndFlush(capturado.capture());
    assertTrue(capturado.getValue().estaAnulado());
    assertEquals(HOY, capturado.getValue().getFechaAnulacion());
    assertEquals("se cobró dos veces", capturado.getValue().getMotivo());
  }

  @Test
  void elHistorialDeUnAlumnoInexistenteDevuelve404() {
    when(alumnos.existsById(ALUMNO)).thenReturn(false);

    assertThrows(RecursoNoEncontradoException.class, () -> servicio.historial(ALUMNO));
  }

  @Test
  void elReciboMuestraElAlumnoYSiEstaAnulado() {
    ReciboDetalleVista fila = mock(ReciboDetalleVista.class);
    when(fila.getNumeroRecibo()).thenReturn(5L);
    when(fila.getIdPago()).thenReturn(7L);
    when(fila.getFechaEmision()).thenReturn(HOY);
    when(fila.getDniAlumno()).thenReturn(ALUMNO);
    when(fila.getNombre()).thenReturn("Mateo");
    when(fila.getApellido()).thenReturn("Gómez");
    when(fila.getMonto()).thenReturn(new BigDecimal("1100.00"));
    when(fila.getMedioPago()).thenReturn("efectivo");
    when(fila.getPeriodo()).thenReturn(PERIODO);
    when(fila.getAnulado()).thenReturn(0);
    when(recibos.detalle(5L)).thenReturn(Optional.of(fila));

    ReciboDetalle recibo = servicio.recibo(5L);

    assertEquals("Mateo Gómez", recibo.alumno());
    assertEquals("2027-02", recibo.periodo());
    assertEquals(false, recibo.anulado());
  }

  private RegistroPagoRequest pedido(String periodo, String medioPago) {
    return new RegistroPagoRequest(ALUMNO, periodo, medioPago);
  }

  private CuotaAPagarVista cuota(String estado, String monto) {
    CuotaAPagarVista vista = mock(CuotaAPagarVista.class);
    when(vista.getEstado()).thenReturn(estado);
    when(vista.getMonto()).thenReturn(new BigDecimal(monto));
    return vista;
  }

  private Pago pagoVigente() {
    return new Pago(HOY, new BigDecimal("1100.00"), "efectivo", ADMIN, ALUMNO, PERIODO,
        "confirmado");
  }
}
