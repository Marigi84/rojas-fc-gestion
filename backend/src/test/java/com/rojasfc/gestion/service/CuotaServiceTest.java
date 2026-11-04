package com.rojasfc.gestion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rojasfc.gestion.dto.ConfiguracionPeriodoRequest;
import com.rojasfc.gestion.exception.ConflictoException;
import com.rojasfc.gestion.exception.RecursoNoEncontradoException;
import com.rojasfc.gestion.model.Periodo;
import com.rojasfc.gestion.repository.AlumnoRepository;
import com.rojasfc.gestion.repository.CuotaRepository;
import com.rojasfc.gestion.repository.PeriodoRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/** Reglas de CuotaService que no dependen de la base. */
class CuotaServiceTest {

  private static final Long ADMIN = 20111222L;
  private static final LocalDate HOY = LocalDate.of(2027, 2, 10);
  private static final LocalDate FEBRERO = LocalDate.of(2027, 2, 1);
  private static final ZoneId ZONA = ZoneId.of("America/Argentina/Cordoba");

  private PeriodoRepository periodos;
  private CuotaRepository cuotas;
  private CuotaService servicio;

  @BeforeEach
  void preparar() {
    periodos = mock(PeriodoRepository.class);
    cuotas = mock(CuotaRepository.class);
    Clock reloj = Clock.fixed(HOY.atStartOfDay(ZONA).toInstant(), ZONA);
    servicio = new CuotaService(periodos, cuotas, mock(AlumnoRepository.class), reloj);
    when(periodos.saveAndFlush(any())).thenAnswer(pedido -> pedido.getArgument(0));
  }

  @Test
  void elVencimientoNoPuedeSerAnteriorAlPeriodo() {
    assertThrows(IllegalArgumentException.class,
        () -> servicio.configurar("2027-02", condiciones(LocalDate.of(2027, 1, 31)), ADMIN));

    verify(periodos, never()).saveAndFlush(any());
  }

  @Test
  void conCuotasGeneradasNoSeModificaElPeriodo() {
    when(cuotas.existsByPeriodo(FEBRERO)).thenReturn(true);

    assertThrows(ConflictoException.class,
        () -> servicio.configurar("2027-02", condiciones(LocalDate.of(2027, 2, 13)), ADMIN));

    verify(periodos, never()).saveAndFlush(any());
  }

  @Test
  void unPeriodoConFormatoInvalidoSeRechaza() {
    assertThrows(IllegalArgumentException.class,
        () -> servicio.configurar("febrero", condiciones(LocalDate.of(2027, 2, 13)), ADMIN));
    assertThrows(IllegalArgumentException.class, () -> servicio.generar("2027-13"));
  }

  @Test
  void noSeGeneraUnPeriodoSinConfigurar() {
    when(periodos.existsById(FEBRERO)).thenReturn(false);

    assertThrows(RecursoNoEncontradoException.class, () -> servicio.generar("2027-02"));

    verify(cuotas, never()).generar(any(), any());
  }

  @Test
  void noSeGeneraUnPeriodoQueTodaviaNoEmpezo() {
    when(periodos.existsById(LocalDate.of(2027, 3, 1))).thenReturn(true);

    assertThrows(IllegalArgumentException.class, () -> servicio.generar("2027-03"));

    verify(cuotas, never()).generar(any(), any());
  }

  @Test
  void noSeGeneraDosVecesElMismoPeriodo() {
    when(periodos.existsById(FEBRERO)).thenReturn(true);
    when(cuotas.existsByPeriodo(FEBRERO)).thenReturn(true);

    assertThrows(ConflictoException.class, () -> servicio.generar("2027-02"));

    verify(cuotas, never()).generar(any(), any());
  }

  @Test
  void laGeneracionAutomaticaNoRepiteUnMesYaGenerado() {
    when(cuotas.existsByPeriodo(FEBRERO)).thenReturn(true);

    assertEquals(0, servicio.generarDelMes());

    verify(cuotas, never()).generar(any(), any());
  }

  @Test
  void laGeneracionAutomaticaUsaElPeriodoConfigurado() {
    Periodo configurado = periodo(FEBRERO, LocalDate.of(2027, 2, 13));
    when(periodos.findById(FEBRERO)).thenReturn(Optional.of(configurado));
    when(cuotas.generar(FEBRERO, HOY)).thenReturn(140);

    assertEquals(140, servicio.generarDelMes());

    verify(periodos, never()).saveAndFlush(any());
  }

  @Test
  void sinConfiguracionTomaElValorVigenteYAjustaElVencimientoAlMes() {
    Periodo enero = periodo(LocalDate.of(2027, 1, 1), LocalDate.of(2027, 1, 31));
    when(periodos.findById(FEBRERO)).thenReturn(Optional.empty());
    when(periodos.findFirstByPeriodoLessThanOrderByPeriodoDesc(FEBRERO))
        .thenReturn(Optional.of(enero));
    when(cuotas.generar(FEBRERO, HOY)).thenReturn(140);

    assertEquals(140, servicio.generarDelMes());

    ArgumentCaptor<Periodo> nuevo = ArgumentCaptor.forClass(Periodo.class);
    verify(periodos).saveAndFlush(nuevo.capture());
    assertEquals(FEBRERO, nuevo.getValue().getPeriodo());
    assertEquals(LocalDate.of(2027, 2, 28), nuevo.getValue().getFechaVencimiento());
    assertEquals(new BigDecimal("1000.00"), nuevo.getValue().getImporte());
    assertEquals(new BigDecimal("10.00"), nuevo.getValue().getPorcentajeInteres());
    assertEquals(ADMIN, nuevo.getValue().getDniAdmin());
    assertEquals(HOY, nuevo.getValue().getFechaConfiguracion());
  }

  @Test
  void sinNingunPeriodoConfiguradoNoGeneraNada() {
    when(periodos.findById(FEBRERO)).thenReturn(Optional.empty());
    when(periodos.findFirstByPeriodoLessThanOrderByPeriodoDesc(FEBRERO))
        .thenReturn(Optional.empty());

    assertEquals(0, servicio.generarDelMes());

    verify(cuotas, never()).generar(any(), any());
  }

  private static ConfiguracionPeriodoRequest condiciones(LocalDate vencimiento) {
    return new ConfiguracionPeriodoRequest(new BigDecimal("1000.00"), vencimiento,
        new BigDecimal("10.00"));
  }

  private static Periodo periodo(LocalDate inicio, LocalDate vencimiento) {
    return new Periodo(inicio, new BigDecimal("1000.00"), vencimiento, new BigDecimal("10.00"),
        ADMIN, inicio);
  }
}
