package com.rojasfc.gestion.controller;

import com.rojasfc.gestion.dto.PersonaResumen;
import com.rojasfc.gestion.service.PersonaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Consulta de una persona por DNI, para que los formularios reutilicen sus
 * datos (RN-01). Solo Administrador (SecurityConfig).
 */
@RestController
@RequestMapping("/api/personas")
public class PersonaController {

  private final PersonaService personaService;

  public PersonaController(PersonaService personaService) {
    this.personaService = personaService;
  }

  @GetMapping("/{dni}")
  public PersonaResumen buscar(@PathVariable Long dni) {
    return personaService.buscar(dni);
  }
}
