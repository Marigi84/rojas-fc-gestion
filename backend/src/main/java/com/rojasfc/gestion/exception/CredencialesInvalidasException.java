package com.rojasfc.gestion.exception;

/**
 * DNI o contrasena incorrectos. El mensaje no aclara cual de los dos fallo,
 * para no revelar que DNI estan registrados.
 */
public class CredencialesInvalidasException extends RuntimeException {

  public CredencialesInvalidasException(String mensaje) {
    super(mensaje);
  }
}
