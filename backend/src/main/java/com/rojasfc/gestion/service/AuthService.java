package com.rojasfc.gestion.service;

import com.rojasfc.gestion.dto.CambioContrasenaRequest;
import com.rojasfc.gestion.dto.LoginRequest;
import com.rojasfc.gestion.dto.LoginResponse;
import com.rojasfc.gestion.exception.CredencialesInvalidasException;
import com.rojasfc.gestion.model.Persona;
import com.rojasfc.gestion.model.UsuarioConContrasena;
import com.rojasfc.gestion.repository.AdministradorRepository;
import com.rojasfc.gestion.repository.PersonaRepository;
import com.rojasfc.gestion.repository.ResponsableRepository;
import com.rojasfc.gestion.security.JwtService;
import com.rojasfc.gestion.security.Rol;
import com.rojasfc.gestion.security.UsuarioAutenticado;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ingreso al sistema y cambio de contrasena (Modulo 1: RF-11, RF-49, RF-52,
 * RNF-01).
 *
 * Una persona que es Administrador y tambien Responsable ingresa con una unica
 * sesion de Administrador (arquitectura-inicial.md, seccion 8), por eso se
 * busca primero en administrador y se usa esa contrasena.
 */
@Service
public class AuthService {

  private static final String CREDENCIALES_INVALIDAS = "El DNI o la contraseña no coinciden";

  private final AdministradorRepository administradores;
  private final ResponsableRepository responsables;
  private final PersonaRepository personas;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  // Hash contra el que se compara cuando el DNI no existe, para que el login
  // tarde lo mismo y el tiempo de respuesta no revele que DNI estan registrados.
  private final String hashFicticio;

  public AuthService(AdministradorRepository administradores, ResponsableRepository responsables,
      PersonaRepository personas, PasswordEncoder passwordEncoder, JwtService jwtService) {
    this.administradores = administradores;
    this.responsables = responsables;
    this.personas = personas;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.hashFicticio = passwordEncoder.encode("contrasena-ficticia");
  }

  @Transactional(readOnly = true)
  public LoginResponse ingresar(LoginRequest pedido) {
    UsuarioConRol usuario = buscar(pedido.dni()).orElse(null);
    String hash = usuario != null ? usuario.datos().getContrasenaHash() : hashFicticio;
    if (!passwordEncoder.matches(pedido.contrasena(), hash) || usuario == null) {
      throw new CredencialesInvalidasException(CREDENCIALES_INVALIDAS);
    }
    return respuesta(pedido.dni(), usuario);
  }

  /**
   * Cambia la contrasena del usuario autenticado y devuelve un token nuevo, ya
   * sin la marca de contrasena provisoria.
   */
  @Transactional
  public LoginResponse cambiarContrasena(UsuarioAutenticado autenticado,
      CambioContrasenaRequest pedido) {
    UsuarioConRol usuario = buscar(autenticado.dni())
        .orElseThrow(() -> new CredencialesInvalidasException(CREDENCIALES_INVALIDAS));
    if (!passwordEncoder.matches(pedido.actual(), usuario.datos().getContrasenaHash())) {
      throw new CredencialesInvalidasException("La contraseña actual no coincide");
    }
    if (pedido.actual().equals(pedido.nueva())) {
      throw new IllegalArgumentException("La contraseña nueva tiene que ser distinta de la actual");
    }
    usuario.datos().cambiarContrasena(passwordEncoder.encode(pedido.nueva()));
    return respuesta(autenticado.dni(), usuario);
  }

  private Optional<UsuarioConRol> buscar(Long dni) {
    return administradores.findById(dni)
        .<UsuarioConRol>map(a -> new UsuarioConRol(a, Rol.ADMINISTRADOR))
        .or(() -> responsables.findById(dni).map(r -> new UsuarioConRol(r, Rol.RESPONSABLE)));
  }

  private LoginResponse respuesta(Long dni, UsuarioConRol usuario) {
    Persona persona = personas.findById(dni)
        .orElseThrow(() -> new IllegalStateException("Falta la persona del DNI " + dni));
    boolean temporal = usuario.datos().isContrasenaTemporal();
    String token = jwtService.generar(new UsuarioAutenticado(dni, usuario.rol(), temporal));
    return new LoginResponse(token, usuario.rol(), dni, persona.getNombre(),
        persona.getApellido(), temporal);
  }

  private record UsuarioConRol(UsuarioConContrasena datos, Rol rol) {
  }
}
