package com.rojasfc.gestion.controller;

import com.rojasfc.gestion.dto.PortalAlumno;
import com.rojasfc.gestion.dto.ReciboDetalle;
import com.rojasfc.gestion.security.UsuarioAutenticado;
import com.rojasfc.gestion.service.PortalService;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/** Portal del Responsable (Modulo 3). Responsable y Administrador (SecurityConfig). */
@RestController
public class PortalController {

  private final PortalService portalService;

  public PortalController(PortalService portalService) {
    this.portalService = portalService;
  }

  @GetMapping("/api/portal/alumnos")
  public List<PortalAlumno> alumnos(@AuthenticationPrincipal UsuarioAutenticado usuario) {
    return portalService.alumnosDe(usuario.dni());
  }

  @GetMapping("/api/portal/recibos/{numeroRecibo}")
  public ReciboDetalle recibo(@PathVariable Long numeroRecibo,
      @AuthenticationPrincipal UsuarioAutenticado usuario) {
    return portalService.recibo(usuario.dni(), numeroRecibo);
  }
}
