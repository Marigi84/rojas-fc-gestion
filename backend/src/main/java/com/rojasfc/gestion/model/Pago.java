package com.rojasfc.gestion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Pago de una cuota (tabla pago). Un pago no se modifica ni se borra: se anula
 * con un motivo y se conserva (RN-30, RN-31). La columna vigente la calcula la
 * base y sirve para que cada cuota tenga un solo pago vigente (RN-29).
 */
@Entity
@Table(name = "pago")
public class Pago {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_pago")
  private Long idPago;

  @Column(nullable = false)
  private LocalDate fecha;

  @Column(nullable = false)
  private BigDecimal monto;

  @Column(name = "nombre_medio_pago", nullable = false)
  private String medioPago;

  @Column(name = "dni_admin", nullable = false)
  private Long dniAdmin;

  @Column(name = "dni_alumno", nullable = false)
  private Long dniAlumno;

  @Column(nullable = false)
  private LocalDate periodo;

  @Column(name = "estado_sincronizacion", nullable = false)
  private String estadoSincronizacion;

  @Column(name = "fecha_anulacion")
  private LocalDate fechaAnulacion;

  @Column(length = 200)
  private String motivo;

  @Column(name = "dni_admin_anula")
  private Long dniAdminAnula;

  protected Pago() {
  }

  public Pago(LocalDate fecha, BigDecimal monto, String medioPago, Long dniAdmin, Long dniAlumno,
      LocalDate periodo, String estadoSincronizacion) {
    this.fecha = fecha;
    this.monto = monto;
    this.medioPago = medioPago;
    this.dniAdmin = dniAdmin;
    this.dniAlumno = dniAlumno;
    this.periodo = periodo;
    this.estadoSincronizacion = estadoSincronizacion;
  }

  /** Anula el pago conservando el registro y el motivo (RN-30, RN-31). */
  public void anular(LocalDate fechaAnulacion, String motivo, Long dniAdminAnula) {
    this.fechaAnulacion = fechaAnulacion;
    this.motivo = motivo;
    this.dniAdminAnula = dniAdminAnula;
  }

  public Long getIdPago() {
    return idPago;
  }

  public LocalDate getFecha() {
    return fecha;
  }

  public BigDecimal getMonto() {
    return monto;
  }

  public String getMedioPago() {
    return medioPago;
  }

  public LocalDate getFechaAnulacion() {
    return fechaAnulacion;
  }

  public String getMotivo() {
    return motivo;
  }

  public boolean estaAnulado() {
    return fechaAnulacion != null;
  }
}
