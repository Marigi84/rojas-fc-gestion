package com.rojasfc.gestion.controller;

import com.rojasfc.gestion.dto.AltaAlumnoRequest;
import com.rojasfc.gestion.dto.AlumnoDetalle;
import com.rojasfc.gestion.dto.ModificacionAlumnoRequest;
import com.rojasfc.gestion.dto.PaginaAlumnos;
import com.rojasfc.gestion.security.UsuarioAutenticado;
import com.rojasfc.gestion.service.AlumnoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Gestion de alumnos (Modulo 2). Solo Administrador (SecurityConfig). */
@RestController
@RequestMapping("/api/alumnos")
public class AlumnoController {

  private final AlumnoService alumnoService;

  public AlumnoController(AlumnoService alumnoService) {
    this.alumnoService = alumnoService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AlumnoDetalle registrar(@Valid @RequestBody AltaAlumnoRequest pedido,
      @AuthenticationPrincipal UsuarioAutenticado usuario) {
    return alumnoService.registrar(pedido, usuario.dni());
  }

  @GetMapping
  public PaginaAlumnos listar(
      @RequestParam(required = false) String busqueda,
      @RequestParam(required = false) Integer anioNacimiento,
      @RequestParam(required = false) String estado,
      @RequestParam(required = false) Boolean moroso,
      @RequestParam(defaultValue = "1") int pagina,
      @RequestParam(defaultValue = "20") int tamano) {
    return alumnoService.listar(busqueda, anioNacimiento, estado, moroso, pagina, tamano);
  }

  @GetMapping("/{dni}")
  public AlumnoDetalle consultar(@PathVariable Long dni) {
    return alumnoService.consultar(dni);
  }

  @PutMapping("/{dni}")
  public AlumnoDetalle modificar(@PathVariable Long dni,
      @Valid @RequestBody ModificacionAlumnoRequest pedido) {
    return alumnoService.modificar(dni, pedido);
  }

  @PostMapping("/{dni}/baja")
  public AlumnoDetalle darDeBaja(@PathVariable Long dni,
      @AuthenticationPrincipal UsuarioAutenticado usuario) {
    return alumnoService.darDeBaja(dni, usuario.dni());
  }

  @PostMapping("/{dni}/reactivacion")
  public AlumnoDetalle reactivar(@PathVariable Long dni,
      @AuthenticationPrincipal UsuarioAutenticado usuario) {
    return alumnoService.reactivar(dni, usuario.dni());
  }
}
