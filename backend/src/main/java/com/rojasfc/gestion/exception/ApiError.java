package com.rojasfc.gestion.exception;

import java.time.Instant;

/**
 * Forma uniforme de respuesta para cualquier error de la API (arquitectura-inicial.md, seccion 6).
 */
public record ApiError(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path
) {

  public static ApiError of(int status, String error, String message, String path) {
    return new ApiError(Instant.now(), status, error, message, path);
  }
}
