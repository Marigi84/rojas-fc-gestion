package com.rojasfc.gestion.controller;

import com.rojasfc.gestion.dto.RestablecimientoContrasenaRequest;
import com.rojasfc.gestion.service.ResponsableService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Responsables. Por ahora, el restablecimiento de la contrasena (RF-15); la
 * gestion de responsables (RF-06 a RF-10) se suma en el issue #40. Solo
 * Administrador (SecurityConfig).
 */
@RestController
@RequestMapping("/api/responsables")
public class ResponsableController {

  private final ResponsableService responsableService;

  public ResponsableController(ResponsableService responsableService) {
    this.responsableService = responsableService;
  }

  @PutMapping("/{dni}/contrasena")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void restablecerContrasena(@PathVariable Long dni,
      @Valid @RequestBody RestablecimientoContrasenaRequest pedido) {
    responsableService.restablecerContrasena(dni, pedido);
  }
}
