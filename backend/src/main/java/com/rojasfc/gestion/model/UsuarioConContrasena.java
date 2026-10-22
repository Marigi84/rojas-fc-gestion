package com.rojasfc.gestion.model;

/**
 * Lo que comparten los roles que ingresan al sistema (Administrador y
 * Responsable): una contrasena cifrada que puede ser provisoria.
 */
public interface UsuarioConContrasena {

  String getContrasenaHash();

  boolean isContrasenaTemporal();

  /** Guarda la nueva contrasena ya cifrada y la marca como definitiva. */
  void cambiarContrasena(String nuevoHash);
}
