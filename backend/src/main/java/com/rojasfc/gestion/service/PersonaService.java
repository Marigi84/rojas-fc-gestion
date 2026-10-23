package com.rojasfc.gestion.service;

import com.rojasfc.gestion.dto.PersonaResumen;
import com.rojasfc.gestion.exception.RecursoNoEncontradoException;
import com.rojasfc.gestion.model.Persona;
import com.rojasfc.gestion.repository.AdministradorRepository;
import com.rojasfc.gestion.repository.AlumnoRepository;
import com.rojasfc.gestion.repository.PersonaRepository;
import com.rojasfc.gestion.repository.ResponsableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Consulta de una persona ya registrada y sus roles (RN-01). */
@Service
public class PersonaService {

  private final PersonaRepository personas;
  private final AlumnoRepository alumnos;
  private final ResponsableRepository responsables;
  private final AdministradorRepository administradores;

  public PersonaService(PersonaRepository personas, AlumnoRepository alumnos,
      ResponsableRepository responsables, AdministradorRepository administradores) {
    this.personas = personas;
    this.alumnos = alumnos;
    this.responsables = responsables;
    this.administradores = administradores;
  }

  @Transactional(readOnly = true)
  public PersonaResumen buscar(Long dni) {
    Persona persona = personas.findById(dni).orElseThrow(() ->
        new RecursoNoEncontradoException("No hay ninguna persona registrada con el DNI " + dni + "."));
    return new PersonaResumen(dni, persona.getNombre(), persona.getApellido(),
        alumnos.existsById(dni), responsables.existsById(dni), administradores.existsById(dni));
  }
}
