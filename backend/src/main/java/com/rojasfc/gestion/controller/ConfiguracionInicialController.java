package com.rojasfc.gestion.controller;

import com.rojasfc.gestion.dto.ConfiguracionInicialRequest;
import com.rojasfc.gestion.dto.EstadoConfiguracion;
import com.rojasfc.gestion.dto.LoginResponse;
import com.rojasfc.gestion.service.ConfiguracionInicialService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/configuracion-inicial")
public class ConfiguracionInicialController {

  private final ConfiguracionInicialService configuracionInicialService;

  public ConfiguracionInicialController(ConfiguracionInicialService configuracionInicialService) {
    this.configuracionInicialService = configuracionInicialService;
  }

  @GetMapping
  public EstadoConfiguracion estado() {
    return configuracionInicialService.estado();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public LoginResponse configurar(@Valid @RequestBody ConfiguracionInicialRequest pedido) {
    return configuracionInicialService.configurar(pedido);
  }
}
