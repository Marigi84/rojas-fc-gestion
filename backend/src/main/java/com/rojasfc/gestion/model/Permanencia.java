package com.rojasfc.gestion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.LocalDate;

/**
 * Periodo en que un alumno estuvo activo (tabla permanencia). La baja cierra
 * la permanencia y la reactivacion abre una nueva, conservando el historial
 * (RN-06 a RN-09). La columna generada vigente no se mapea: la vigencia es
 * fechaBaja == null.
 */
@Entity
@Table(name = "permanencia")
@IdClass(PermanenciaId.class)
public class Permanencia {

  @Id
  @Column(name = "dni_alumno")
  private Long dniAlumno;

  @Id
  @Column(name = "fecha_alta")
  private LocalDate fechaAlta;

  @Column(name = "fecha_baja")
  private LocalDate fechaBaja;

  @Column(name = "dni_admin_alta", nullable = false)
  private Long dniAdminAlta;

  @Column(name = "dni_admin_baja")
  private Long dniAdminBaja;

  protected Permanencia() {
  }

  public Permanencia(Long dniAlumno, LocalDate fechaAlta, Long dniAdminAlta) {
    this.dniAlumno = dniAlumno;
    this.fechaAlta = fechaAlta;
    this.dniAdminAlta = dniAdminAlta;
  }

  public LocalDate getFechaAlta() {
    return fechaAlta;
  }

  public LocalDate getFechaBaja() {
    return fechaBaja;
  }

  public boolean isVigente() {
    return fechaBaja == null;
  }

  public void darDeBaja(LocalDate fecha, Long dniAdmin) {
    this.fechaBaja = fecha;
    this.dniAdminBaja = dniAdmin;
  }

  /** Deshace una baja (reactivacion el mismo dia de la baja). */
  public void anularBaja() {
    this.fechaBaja = null;
    this.dniAdminBaja = null;
  }
}
