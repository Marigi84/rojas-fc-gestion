package com.rojasfc.gestion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.LocalDate;

/**
 * Cuota de un alumno en un periodo (tabla cuota). La clave impide dos cuotas
 * del mismo alumno y periodo (RN-16). El importe, el interes y el estado no se
 * guardan aca: los calcula vista_cuota a partir del periodo y del pago (RN-19,
 * RN-20).
 */
@Entity
@Table(name = "cuota")
@IdClass(CuotaId.class)
public class Cuota {

  @Id
  @Column(name = "dni_alumno")
  private Long dniAlumno;

  @Id
  @Column(name = "periodo")
  private LocalDate periodo;

  @Column(name = "fecha_generacion", nullable = false)
  private LocalDate fechaGeneracion;

  protected Cuota() {
  }

  public Long getDniAlumno() {
    return dniAlumno;
  }

  public LocalDate getPeriodo() {
    return periodo;
  }

  public LocalDate getFechaGeneracion() {
    return fechaGeneracion;
  }
}
