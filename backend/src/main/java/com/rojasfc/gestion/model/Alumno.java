package com.rojasfc.gestion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

/**
 * Rol Alumno de una persona (tabla alumno). La categoria, el estado y la
 * morosidad no se guardan: se derivan (vista_alumno).
 */
@Entity
@Table(name = "alumno")
public class Alumno {

  @Id
  private Long dni;

  @Column(name = "fecha_nacimiento", nullable = false)
  private LocalDate fechaNacimiento;

  @Column(nullable = false, length = 120)
  private String direccion;

  protected Alumno() {
  }

  public Alumno(Long dni, LocalDate fechaNacimiento, String direccion) {
    this.dni = dni;
    this.fechaNacimiento = fechaNacimiento;
    this.direccion = direccion;
  }

  public Long getDni() {
    return dni;
  }

  public LocalDate getFechaNacimiento() {
    return fechaNacimiento;
  }

  public String getDireccion() {
    return direccion;
  }

  /** Categoria segun el anio de nacimiento (RF-16, RN-13). */
  public int getCategoria() {
    return fechaNacimiento.getYear();
  }

  public void modificar(LocalDate fechaNacimiento, String direccion) {
    this.fechaNacimiento = fechaNacimiento;
    this.direccion = direccion;
  }
}
