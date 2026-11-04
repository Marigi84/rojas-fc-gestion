package com.rojasfc.gestion.controller;

import com.rojasfc.gestion.dto.DashboardResumen;
import com.rojasfc.gestion.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Dashboard (Modulo 8). Solo Administrador (SecurityConfig). */
@RestController
public class DashboardController {

  private final DashboardService dashboardService;

  public DashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  @GetMapping("/api/dashboard")
  public DashboardResumen resumen() {
    return dashboardService.resumen();
  }
}
