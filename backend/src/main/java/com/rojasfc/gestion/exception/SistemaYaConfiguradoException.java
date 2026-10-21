package com.rojasfc.gestion.exception;

/** Se intento la configuracion inicial cuando ya existe un Administrador. */
public class SistemaYaConfiguradoException extends RuntimeException {

  public SistemaYaConfiguradoException() {
    super("El sistema ya está configurado. Ingresá con un usuario administrador.");
  }
}
