package com.rojasfc.gestion.service;

import com.rojasfc.gestion.dto.NuevaContrasenaRequest;
import com.rojasfc.gestion.model.Administrador;
import com.rojasfc.gestion.model.Persona;
import com.rojasfc.gestion.repository.AdministradorRepository;
import com.rojasfc.gestion.repository.PersonaRepository;
import com.rojasfc.gestion.security.TokenRecuperacion;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Recuperacion de la contrasena del Administrador por correo (RF-50). El
 * Responsable no la usa: si se olvida la suya, el Administrador se la
 * restablece (RF-15).
 */
@Service
public class RecuperacionContrasenaService {

  static final String ENLACE_INVALIDO =
      "El enlace no es válido, ya venció o ya se usó. Pedí uno nuevo.";

  private final AdministradorRepository administradores;
  private final PersonaRepository personas;
  private final PasswordEncoder passwordEncoder;
  private final TokenRecuperacion tokens;
  private final CorreoService correo;
  private final String frontendUrl;
  private final long minutos;

  public RecuperacionContrasenaService(AdministradorRepository administradores,
      PersonaRepository personas, PasswordEncoder passwordEncoder, TokenRecuperacion tokens,
      CorreoService correo, @Value("${app.frontend-url}") String frontendUrl,
      @Value("${app.recuperacion.duracion-minutos}") long minutos) {
    this.administradores = administradores;
    this.personas = personas;
    this.passwordEncoder = passwordEncoder;
    this.tokens = tokens;
    this.correo = correo;
    this.frontendUrl = frontendUrl.replaceAll("/+$", "");
    this.minutos = minutos;
  }

  /**
   * Si el correo es de un Administrador, le envia el enlace. Si no, no hace
   * nada: la respuesta es la misma en los dos casos, para no revelar que
   * correos estan registrados.
   */
  @Transactional(readOnly = true)
  public void solicitar(String email) {
    administradores.findByEmailIgnoreCase(email.trim()).ifPresent(administrador -> {
      String nombre = personas.findById(administrador.getDni())
          .map(Persona::getNombre)
          .orElse("administrador");
      // El token va despues de # para que no quede en los registros de ningun servidor.
      String enlace = frontendUrl + "/restablecer-contrasena#"
          + tokens.generar(administrador.getDni(), administrador.getContrasenaHash());
      correo.enviarRecuperacion(administrador.getEmail(), nombre, enlace, minutos);
    });
  }

  /** Guarda la contrasena nueva si el enlace es valido. Despues ya no sirve. */
  @Transactional
  public void restablecer(NuevaContrasenaRequest pedido) {
    TokenRecuperacion.Datos datos;
    try {
      datos = tokens.validar(pedido.token());
    } catch (JwtException | IllegalArgumentException e) {
      throw new IllegalArgumentException(ENLACE_INVALIDO);
    }
    Administrador administrador = administradores.findById(datos.dni())
        .filter(a -> tokens.coincide(datos, a.getContrasenaHash()))
        .orElseThrow(() -> new IllegalArgumentException(ENLACE_INVALIDO));
    administrador.cambiarContrasena(passwordEncoder.encode(pedido.nueva()));
  }
}
