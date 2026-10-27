package com.rojasfc.gestion.exception;

/**
 * La operacion no se puede hacer por el estado actual de los datos (409). El
 * titulo va en el campo error de ApiError, para que el frontend pueda
 * distinguir casos (por ejemplo, "Alumno inactivo" para ofrecer reactivarlo).
 */
public class ConflictoException extends RuntimeException {

  private final String titulo;

  public ConflictoException(String titulo, String mensaje) {
    super(mensaje);
    this.titulo = titulo;
  }

  public String getTitulo() {
    return titulo;
  }
}
