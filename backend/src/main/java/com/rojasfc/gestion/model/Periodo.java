package com.rojasfc.gestion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Condiciones de las cuotas de un mes (tabla periodo): importe, vencimiento
 * unico e interes por mora (RF-17, RF-19, RF-20). La clave es el dia 1 del mes.
 * Una vez generadas las cuotas, el trigger periodo_condiciones_fijas no deja
 * cambiarlas (RN-22).
 */
@Entity
@Table(name = "periodo")
public class Periodo {

  @Id
  @Column(name = "periodo")
  private LocalDate periodo;

  @Column(nullable = false)
  private BigDecimal importe;

  @Column(name = "fecha_vencimiento", nullable = false)
  private LocalDate fechaVencimiento;

  @Column(name = "porcentaje_interes", nullable = false)
  private BigDecimal porcentajeInteres;

  @Column(name = "dni_admin", nullable = false)
  private Long dniAdmin;

  @Column(name = "fecha_configuracion", nullable = false)
  private LocalDate fechaConfiguracion;

  protected Periodo() {
  }

  public Periodo(LocalDate periodo, BigDecimal importe, LocalDate fechaVencimiento,
      BigDecimal porcentajeInteres, Long dniAdmin, LocalDate fechaConfiguracion) {
    this.periodo = periodo;
    this.importe = importe;
    this.fechaVencimiento = fechaVencimiento;
    this.porcentajeInteres = porcentajeInteres;
    this.dniAdmin = dniAdmin;
    this.fechaConfiguracion = fechaConfiguracion;
  }

  public void configurar(BigDecimal importe, LocalDate fechaVencimiento,
      BigDecimal porcentajeInteres, Long dniAdmin, LocalDate fechaConfiguracion) {
    this.importe = importe;
    this.fechaVencimiento = fechaVencimiento;
    this.porcentajeInteres = porcentajeInteres;
    this.dniAdmin = dniAdmin;
    this.fechaConfiguracion = fechaConfiguracion;
  }

  public LocalDate getPeriodo() {
    return periodo;
  }

  public BigDecimal getImporte() {
    return importe;
  }

  public LocalDate getFechaVencimiento() {
    return fechaVencimiento;
  }

  public BigDecimal getPorcentajeInteres() {
    return porcentajeInteres;
  }

  public Long getDniAdmin() {
    return dniAdmin;
  }

  public LocalDate getFechaConfiguracion() {
    return fechaConfiguracion;
  }
}
