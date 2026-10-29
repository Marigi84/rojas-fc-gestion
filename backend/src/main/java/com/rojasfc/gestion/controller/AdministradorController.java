package com.rojasfc.gestion.controller;

import com.rojasfc.gestion.dto.AdministradorAltaRequest;
import com.rojasfc.gestion.dto.AdministradorResumen;
import com.rojasfc.gestion.service.AdministradorService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Administradores (Modulo 1, RF-51). Solo Administrador (SecurityConfig). */
@RestController
@RequestMapping("/api/administradores")
public class AdministradorController {

  private final AdministradorService administradorService;

  public AdministradorController(AdministradorService administradorService) {
    this.administradorService = administradorService;
  }

  @GetMapping
  public List<AdministradorResumen> listar() {
    return administradorService.listar();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AdministradorResumen registrar(@Valid @RequestBody AdministradorAltaRequest pedido) {
    return administradorService.registrar(pedido);
  }
}
