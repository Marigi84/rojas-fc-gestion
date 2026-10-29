package com.rojasfc.gestion.controller;

import com.rojasfc.gestion.dto.AsociacionAlumnoRequest;
import com.rojasfc.gestion.dto.ModificacionResponsableRequest;
import com.rojasfc.gestion.dto.ResponsableDetalle;
import com.rojasfc.gestion.dto.RestablecimientoContrasenaRequest;
import com.rojasfc.gestion.service.ResponsableService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Responsables (Modulo 2, RF-06 a RF-10) y restablecimiento de la contrasena
 * de un responsable (RF-15). Solo Administrador (SecurityConfig).
 */
@RestController
@RequestMapping("/api/responsables")
public class ResponsableController {

  private final ResponsableService responsableService;

  public ResponsableController(ResponsableService responsableService) {
    this.responsableService = responsableService;
  }

  @GetMapping("/{dni}")
  public ResponsableDetalle consultar(@PathVariable Long dni) {
    return responsableService.consultar(dni);
  }

  @PutMapping("/{dni}")
  public ResponsableDetalle modificar(@PathVariable Long dni,
      @Valid @RequestBody ModificacionResponsableRequest pedido) {
    return responsableService.modificar(dni, pedido);
  }

  @PostMapping("/{dni}/alumnos")
  public ResponsableDetalle asociar(@PathVariable Long dni,
      @Valid @RequestBody AsociacionAlumnoRequest pedido) {
    return responsableService.asociar(dni, pedido);
  }

  @DeleteMapping("/{dni}/alumnos/{dniAlumno}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void desvincular(@PathVariable Long dni, @PathVariable Long dniAlumno) {
    responsableService.desvincular(dniAlumno, dni);
  }

  @PutMapping("/{dni}/contrasena")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void restablecerContrasena(@PathVariable Long dni,
      @Valid @RequestBody RestablecimientoContrasenaRequest pedido) {
    responsableService.restablecerContrasena(dni, pedido);
  }
}
