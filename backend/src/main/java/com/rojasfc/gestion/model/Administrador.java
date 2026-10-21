package com.rojasfc.gestion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Rol Administrador de una persona (tabla administrador). La contrasena se
 * guarda cifrada y es provisoria hasta que el usuario la cambia (RNF-01).
 */
@Entity
@Table(name = "administrador")
public class Administrador implements UsuarioConContrasena {

  @Id
  private Long dni;

  @Column(nullable = false, length = 120)
  private String email;

  @Column(name = "contrasena_hash", nullable = false)
  private String contrasenaHash;

  @Column(name = "contrasena_temporal", nullable = false)
  private boolean contrasenaTemporal;

  protected Administrador() {
  }

  public Administrador(Long dni, String email, String contrasenaHash) {
    this.dni = dni;
    this.email = email;
    this.contrasenaHash = contrasenaHash;
    this.contrasenaTemporal = true;
  }

  public Long getDni() {
    return dni;
  }

  public String getEmail() {
    return email;
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
}
