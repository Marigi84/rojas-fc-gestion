package com.rojasfc.gestion.service;

import com.rojasfc.gestion.dto.AdministradorAltaRequest;
import com.rojasfc.gestion.dto.AdministradorResumen;
import com.rojasfc.gestion.exception.ConflictoException;
import com.rojasfc.gestion.model.Administrador;
import com.rojasfc.gestion.model.Persona;
import com.rojasfc.gestion.repository.AdministradorRepository;
import com.rojasfc.gestion.repository.PersonaRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Administradores (Modulo 1). El primero se registra en la configuracion
 * inicial; los demas los registra un Administrador desde el sistema (RF-51).
 */
@Service
public class AdministradorService {

  private final AdministradorRepository administradores;
  private final PersonaRepository personas;
  private final PasswordEncoder passwordEncoder;

  public AdministradorService(AdministradorRepository administradores,
      PersonaRepository personas, PasswordEncoder passwordEncoder) {
    this.administradores = administradores;
    this.personas = personas;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional(readOnly = true)
  public List<AdministradorResumen> listar() {
    return administradores.findAll().stream()
        .map(this::resumen)
        .sorted(Comparator.comparing(AdministradorResumen::apellido)
            .thenComparing(AdministradorResumen::nombre))
        .toList();
  }

  /**
   * Registra otro Administrador (RF-51). Si el DNI ya es una persona del
   * sistema, por ejemplo un responsable, reutiliza sus datos (RN-01). La
   * contrasena es provisoria: la cambia en su primer ingreso (RNF-01).
   */
  @Transactional
  public AdministradorResumen registrar(AdministradorAltaRequest pedido) {
    Long dni = pedido.dni();
    String email = pedido.email().trim();
    if (administradores.existsById(dni)) {
      throw new ConflictoException("Administrador ya registrado",
          "Ya hay un administrador con el DNI " + dni + ".");
    }
    if (administradores.existsByEmailIgnoreCase(email)) {
      throw new ConflictoException("Correo en uso",
          "El correo " + email + " ya es de otro administrador.");
    }
    if (!personas.existsById(dni)) {
      Nombres.exigir(pedido.nombre(), pedido.apellido(), "del administrador con DNI " + dni);
      personas.saveAndFlush(new Persona(dni, pedido.nombre().trim(), pedido.apellido().trim()));
    }
    Administrador administrador = administradores.saveAndFlush(new Administrador(dni, email,
        passwordEncoder.encode(pedido.contrasenaProvisoria()), true));
    return resumen(administrador);
  }

  private AdministradorResumen resumen(Administrador administrador) {
    Long dni = administrador.getDni();
    Persona persona = personas.findById(dni)
        .orElseThrow(() -> new IllegalStateException("Falta la persona del DNI " + dni));
    return new AdministradorResumen(dni, persona.getNombre(), persona.getApellido(),
        administrador.getEmail(), administrador.isContrasenaTemporal());
  }
}
