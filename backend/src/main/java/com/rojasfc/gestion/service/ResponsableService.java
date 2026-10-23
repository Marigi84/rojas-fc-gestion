package com.rojasfc.gestion.service;

import com.rojasfc.gestion.dto.ResponsableAltaRequest;
import com.rojasfc.gestion.model.Persona;
import com.rojasfc.gestion.model.Responsable;
import com.rojasfc.gestion.model.ResponsableAlumno;
import com.rojasfc.gestion.repository.PersonaRepository;
import com.rojasfc.gestion.repository.ResponsableAlumnoRepository;
import com.rojasfc.gestion.repository.ResponsableRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Responsables (Modulo 2). Por ahora cubre lo que necesita el alta de un
 * alumno; el resto de la gestion de responsables (RF-06 a RF-10) se suma en
 * el issue #40.
 */
@Service
public class ResponsableService {

  private final PersonaRepository personas;
  private final ResponsableRepository responsables;
  private final ResponsableAlumnoRepository vinculos;
  private final PasswordEncoder passwordEncoder;

  public ResponsableService(PersonaRepository personas, ResponsableRepository responsables,
      ResponsableAlumnoRepository vinculos, PasswordEncoder passwordEncoder) {
    this.personas = personas;
    this.responsables = responsables;
    this.vinculos = vinculos;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Asocia un responsable al alumno, reutilizando la persona y el rol si ya
   * existen (RF-06, RN-01) y registrando el vinculo (RN-12). Corre dentro de
   * la transaccion del alta, para que alumno y responsables se guarden juntos
   * (RN-05).
   */
  @Transactional(propagation = Propagation.MANDATORY)
  public void asociarEnAlta(Long dniAlumno, ResponsableAltaRequest pedido) {
    Long dni = pedido.dni();
    boolean esResponsable = responsables.existsById(dni);
    if (!esResponsable) {
      validarResponsableNuevo(pedido);
    }
    if (!personas.existsById(dni)) {
      personas.saveAndFlush(new Persona(dni, pedido.nombre().trim(), pedido.apellido().trim()));
    }
    if (!esResponsable) {
      // La contrasena la asigna el Administrador y es provisoria (RF-11, RNF-01).
      responsables.saveAndFlush(new Responsable(dni, pedido.telefono().trim(),
          passwordEncoder.encode(pedido.contrasenaProvisoria()), true));
    }
    vinculos.saveAndFlush(new ResponsableAlumno(dni, dniAlumno, pedido.vinculo()));
  }

  private void validarResponsableNuevo(ResponsableAltaRequest pedido) {
    if (pedido.telefono() == null || pedido.telefono().isBlank()) {
      throw new IllegalArgumentException("Falta el teléfono del responsable con DNI "
          + pedido.dni() + ", que se registra por primera vez.");
    }
    if (pedido.contrasenaProvisoria() == null) {
      throw new IllegalArgumentException("Falta la contraseña provisoria del responsable con DNI "
          + pedido.dni() + ", que se registra por primera vez.");
    }
  }
}
