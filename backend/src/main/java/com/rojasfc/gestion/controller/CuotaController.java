package com.rojasfc.gestion.controller;

import com.rojasfc.gestion.dto.AlumnoMoroso;
import com.rojasfc.gestion.dto.ConfiguracionPeriodoRequest;
import com.rojasfc.gestion.dto.CuotaDetalle;
import com.rojasfc.gestion.dto.GeneracionCuotasResultado;
import com.rojasfc.gestion.dto.PeriodoDetalle;
import com.rojasfc.gestion.security.UsuarioAutenticado;
import com.rojasfc.gestion.service.CuotaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Periodos y cuotas (Modulo 4). Solo Administrador (SecurityConfig). */
@RestController
@RequestMapping("/api")
public class CuotaController {

  private final CuotaService cuotaService;

  public CuotaController(CuotaService cuotaService) {
    this.cuotaService = cuotaService;
  }

  @GetMapping("/periodos")
  public List<PeriodoDetalle> listarPeriodos() {
    return cuotaService.listarPeriodos();
  }

  @PutMapping("/periodos/{periodo}")
  public PeriodoDetalle configurar(@PathVariable String periodo,
      @Valid @RequestBody ConfiguracionPeriodoRequest pedido,
      @AuthenticationPrincipal UsuarioAutenticado usuario) {
    return cuotaService.configurar(periodo, pedido, usuario.dni());
  }

  @PostMapping("/periodos/{periodo}/cuotas")
  public GeneracionCuotasResultado generar(@PathVariable String periodo) {
    return cuotaService.generar(periodo);
  }

  @GetMapping("/alumnos/{dni}/cuotas")
  public List<CuotaDetalle> deAlumno(@PathVariable Long dni) {
    return cuotaService.deAlumno(dni);
  }

  @GetMapping("/cuotas/morosos")
  public List<AlumnoMoroso> morosos() {
    return cuotaService.morosos();
  }
}
