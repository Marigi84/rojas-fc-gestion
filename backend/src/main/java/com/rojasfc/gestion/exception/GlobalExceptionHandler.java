package com.rojasfc.gestion.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Centraliza el manejo de errores de la API para devolver siempre la misma
 * forma de respuesta (ApiError), segun lo definido en arquitectura-inicial.md, seccion 6.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    String message = ex.getBindingResult().getFieldErrors().stream()
        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
        .reduce((a, b) -> a + "; " + b)
        .orElse("Datos invalidos");
    return build(HttpStatus.BAD_REQUEST, "Validacion", message, request);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> handleIllegalArgument(
      IllegalArgumentException ex, HttpServletRequest request) {
    return build(HttpStatus.BAD_REQUEST, "Solicitud invalida", ex.getMessage(), request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno",
        "Ocurrio un error inesperado al procesar la solicitud", request);
  }

  private ResponseEntity<ApiError> build(
      HttpStatus status, String error, String message, HttpServletRequest request) {
    ApiError body = ApiError.of(status.value(), error, message, request.getRequestURI());
    return ResponseEntity.status(status).body(body);
  }
}
