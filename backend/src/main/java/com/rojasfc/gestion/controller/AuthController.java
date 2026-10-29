package com.rojasfc.gestion.controller;

import com.rojasfc.gestion.dto.CambioContrasenaRequest;
import com.rojasfc.gestion.dto.LoginRequest;
import com.rojasfc.gestion.dto.LoginResponse;
import com.rojasfc.gestion.dto.NuevaContrasenaRequest;
import com.rojasfc.gestion.dto.RecuperacionContrasenaRequest;
import com.rojasfc.gestion.security.UsuarioAutenticado;
import com.rojasfc.gestion.service.AuthService;
import com.rojasfc.gestion.service.RecuperacionContrasenaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  private final RecuperacionContrasenaService recuperacion;

  public AuthController(AuthService authService, RecuperacionContrasenaService recuperacion) {
    this.authService = authService;
    this.recuperacion = recuperacion;
  }

  @PostMapping("/login")
  public LoginResponse login(@Valid @RequestBody LoginRequest pedido) {
    return authService.ingresar(pedido);
  }

  @PutMapping("/contrasena")
  public LoginResponse cambiarContrasena(
      @AuthenticationPrincipal UsuarioAutenticado usuario,
      @Valid @RequestBody CambioContrasenaRequest pedido) {
    return authService.cambiarContrasena(usuario, pedido);
  }

  /** Envia el enlace de recuperacion si el correo es de un Administrador (RF-50). */
  @PostMapping("/recuperacion")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void solicitarRecuperacion(@Valid @RequestBody RecuperacionContrasenaRequest pedido) {
    recuperacion.solicitar(pedido.email());
  }

  /** Guarda la contrasena nueva elegida desde el enlace (RF-50). */
  @PostMapping("/recuperacion/nueva-contrasena")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void restablecer(@Valid @RequestBody NuevaContrasenaRequest pedido) {
    recuperacion.restablecer(pedido);
  }
}
