package com.rojasfc.gestion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Rol Responsable de una persona (tabla responsable). Ingresa al portal con su
 * DNI y la contrasena que le asigna el Administrador (RF-11, RNF-01).
 */
@Entity
@Table(name = "responsable")
public class Responsable implements UsuarioConContrasena {

  @Id
  private Long dni;

  @Column(nullable = false, length = 20)
  private String telefono;

  @Column(name = "contrasena_hash", nullable = false)
  private String contrasenaHash;

  @Column(name = "contrasena_temporal", nullable = false)
  private boolean contrasenaTemporal;

  protected Responsable() {
  }

  public Responsable(Long dni, String telefono, String contrasenaHash,
      boolean contrasenaTemporal) {
    this.dni = dni;
    this.telefono = telefono;
    this.contrasenaHash = contrasenaHash;
    this.contrasenaTemporal = contrasenaTemporal;
  }

  public Long getDni() {
    return dni;
  }

  public String getTelefono() {
    return telefono;
  }

  /** Datos propios del rol Responsable que se pueden modificar (RF-08). */
  public void modificarTelefono(String telefono) {
    this.telefono = telefono;
  }

  @Override
  public String getContrasenaHash() {
    return contrasenaHash;
  }

  @Override
  public boolean isContrasenaTemporal() {
    return contrasenaTemporal;
  }

  @Override
  public void cambiarContrasena(String nuevoHash) {
    this.contrasenaHash = nuevoHash;
    this.contrasenaTemporal = false;
  }

  /**
   * El Administrador le asigna una contrasena provisoria nueva (RF-15). El
   * responsable la tiene que cambiar en su proximo ingreso (RNF-01).
   */
  public void restablecerContrasena(String nuevoHash) {
    this.contrasenaHash = nuevoHash;
    this.contrasenaTemporal = true;
  }
}
