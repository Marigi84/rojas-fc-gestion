package com.rojasfc.gestion.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rojasfc.gestion.exception.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/**
 * Escribe los rechazos de seguridad (401 y 403) con la misma forma ApiError
 * que el resto de los errores de la API (arquitectura-inicial.md, seccion 6).
 * Hace falta porque estos rechazos ocurren antes de llegar a los controladores.
 */
@Component
public class ApiErrorWriter {

  private final ObjectMapper objectMapper;

  public ApiErrorWriter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public void escribir(
      HttpServletRequest request, HttpServletResponse response, HttpStatus status, String mensaje)
      throws IOException {
    response.setStatus(status.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    ApiError error = ApiError.of(status.value(), status.getReasonPhrase(), mensaje,
        request.getRequestURI());
    objectMapper.writeValue(response.getOutputStream(), error);
  }
}
