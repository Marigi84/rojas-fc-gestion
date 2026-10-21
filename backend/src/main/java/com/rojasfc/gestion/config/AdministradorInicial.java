package com.rojasfc.gestion.config;

import com.rojasfc.gestion.model.Administrador;
import com.rojasfc.gestion.model.Persona;
import com.rojasfc.gestion.repository.AdministradorRepository;
import com.rojasfc.gestion.repository.PersonaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Crea el primer Administrador al arrancar, si todavia no hay ninguno y estan
 * definidas las variables ADMIN_INICIAL_* (ver backend/README.md). Los demas
 * administradores los registra un Administrador desde el sistema (RF-51).
 *
 * La contrasena queda como provisoria: hay que cambiarla en el primer ingreso
 * (RNF-01).
 */
@Component
public class AdministradorInicial implements ApplicationRunner {

  private static final Logger log = LoggerFactory.getLogger(AdministradorInicial.class);

  private final AdministradorRepository administradores;
  private final PersonaRepository personas;
  private final PasswordEncoder passwordEncoder;
  private final Long dni;
  private final String nombre;
  private final String apellido;
  private final String email;
  private final String contrasena;

  public AdministradorInicial(AdministradorRepository administradores, PersonaRepository personas,
      PasswordEncoder passwordEncoder,
      @Value("${app.admin-inicial.dni:}") Long dni,
      @Value("${app.admin-inicial.nombre:}") String nombre,
      @Value("${app.admin-inicial.apellido:}") String apellido,
      @Value("${app.admin-inicial.email:}") String email,
      @Value("${app.admin-inicial.contrasena:}") String contrasena) {
    this.administradores = administradores;
    this.personas = personas;
    this.passwordEncoder = passwordEncoder;
    this.dni = dni;
    this.nombre = nombre;
    this.apellido = apellido;
    this.email = email;
    this.contrasena = contrasena;
  }

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    if (administradores.count() > 0) {
      return;
    }
    if (dni == null || nombre.isBlank() || apellido.isBlank() || email.isBlank()
        || contrasena.isBlank()) {
      log.warn("No hay ningun administrador. Definir las variables ADMIN_INICIAL_* para crear "
          + "el primero (ver backend/README.md).");
      return;
    }
    // Si la persona ya existe (por ejemplo, es responsable), se reutiliza (RN-01).
    if (!personas.existsById(dni)) {
      personas.save(new Persona(dni, nombre, apellido));
    }
    administradores.save(new Administrador(dni, email, passwordEncoder.encode(contrasena)));
    log.info("Administrador inicial creado con DNI {}", dni);
  }
}
