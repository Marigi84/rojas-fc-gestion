package com.rojasfc.gestion.controller;

import com.rojasfc.gestion.dto.CambioContrasenaRequest;
import com.rojasfc.gestion.dto.LoginRequest;
import com.rojasfc.gestion.dto.LoginResponse;
import com.rojasfc.gestion.security.UsuarioAutenticado;
import com.rojasfc.gestion.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
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
}
