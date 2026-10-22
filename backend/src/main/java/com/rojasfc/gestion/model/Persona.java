package com.rojasfc.gestion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Datos comunes de toda persona del sistema (tabla persona). Alumno,
 * Responsable y Administrador son roles que comparten este DNI.
 */
@Entity
@Table(name = "persona")
public class Persona {

  @Id
  private Long dni;

  @Column(nullable = false, length = 60)
  private String nombre;

  @Column(nullable = false, length = 60)
  private String apellido;

  protected Persona() {
  }

  public Persona(Long dni, String nombre, String apellido) {
    this.dni = dni;
    this.nombre = nombre;
    this.apellido = apellido;
  }

  public Long getDni() {
    return dni;
  }

  public String getNombre() {
    return nombre;
  }

  public String getApellido() {
    return apellido;
  }
}
