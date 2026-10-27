package com.rojasfc.gestion.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Centraliza el manejo de errores de la API para devolver siempre la misma
 * forma de respuesta (ApiError), segun lo definido en arquitectura-inicial.md, seccion 6.
 *
 * Extiende ResponseEntityExceptionHandler para que los errores propios de
 * Spring MVC (404, 405, JSON invalido, etc.) conserven su codigo de estado
 * real en vez de caer en el handler generico de 500.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  /** SQLSTATE con el que los triggers de database/schema.sql rechazan una operacion. */
  private static final String REGLA_DE_NEGOCIO = "45000";

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      Exception ex, @Nullable Object body, HttpHeaders headers,
      HttpStatusCode statusCode, WebRequest request) {
    HttpStatus status = HttpStatus.valueOf(statusCode.value());
    String message = (ex instanceof MethodArgumentNotValidException manv)
        ? validationMessage(manv)
        : status.getReasonPhrase();
    ApiError apiError = ApiError.of(status.value(), status.getReasonPhrase(), message, path(request));
    return super.handleExceptionInternal(ex, apiError, headers, statusCode, request);
  }

  @ExceptionHandler(CredencialesInvalidasException.class)
  public ResponseEntity<ApiError> handleCredencialesInvalidas(
      CredencialesInvalidasException ex, HttpServletRequest request) {
    return build(HttpStatus.UNAUTHORIZED, "Credenciales invalidas", ex.getMessage(),
        request.getRequestURI());
  }

  @ExceptionHandler(SistemaYaConfiguradoException.class)
  public ResponseEntity<ApiError> handleSistemaYaConfigurado(
      SistemaYaConfiguradoException ex, HttpServletRequest request) {
    return build(HttpStatus.CONFLICT, "Sistema ya configurado", ex.getMessage(),
        request.getRequestURI());
  }

  @ExceptionHandler(ConflictoException.class)
  public ResponseEntity<ApiError> handleConflicto(
      ConflictoException ex, HttpServletRequest request) {
    return build(HttpStatus.CONFLICT, ex.getTitulo(), ex.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(RecursoNoEncontradoException.class)
  public ResponseEntity<ApiError> handleNoEncontrado(
      RecursoNoEncontradoException ex, HttpServletRequest request) {
    return build(HttpStatus.NOT_FOUND, "No encontrado", ex.getMessage(), request.getRequestURI());
  }

  /**
   * Rechazos de la base. Los triggers de las reglas de negocio usan
   * SQLSTATE 45000: se responde 409 con su mensaje, que ya esta escrito para
   * el usuario. Una clave o UNIQUE repetida tambien es un 409.
   */
  @ExceptionHandler({DataAccessException.class, TransactionSystemException.class})
  public ResponseEntity<ApiError> handleBaseDeDatos(Exception ex, HttpServletRequest request) {
    SQLException sql = causaSql(ex);
    if (sql != null && REGLA_DE_NEGOCIO.equals(sql.getSQLState())) {
      return build(HttpStatus.CONFLICT, "Regla de negocio", sql.getMessage(),
          request.getRequestURI());
    }
    if (ex instanceof DataIntegrityViolationException) {
      return build(HttpStatus.CONFLICT, "Conflicto",
          "La operación entra en conflicto con datos ya registrados.", request.getRequestURI());
    }
    return handleGeneric(ex, request);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> handleIllegalArgument(
      IllegalArgumentException ex, HttpServletRequest request) {
    return build(HttpStatus.BAD_REQUEST, "Solicitud invalida", ex.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno",
        "Ocurrio un error inesperado al procesar la solicitud", request.getRequestURI());
  }

  private static SQLException causaSql(Throwable ex) {
    for (Throwable causa = ex; causa != null; causa = causa.getCause()) {
      if (causa instanceof SQLException sql) {
        return sql;
      }
    }
    return null;
  }

  private String validationMessage(MethodArgumentNotValidException ex) {
    return ex.getBindingResult().getFieldErrors().stream()
        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
        .reduce((a, b) -> a + "; " + b)
        .orElse("Datos invalidos");
  }

  private String path(WebRequest request) {
    if (request instanceof ServletWebRequest servletWebRequest) {
      return servletWebRequest.getRequest().getRequestURI();
    }
    return request.getDescription(false);
  }

  private ResponseEntity<ApiError> build(
      HttpStatus status, String error, String message, String path) {
    ApiError apiError = ApiError.of(status.value(), error, message, path);
    return ResponseEntity.status(status).body(apiError);
  }
}
