package com.rojasfc.gestion.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Pedido del enlace para recuperar la contrasena del Administrador (RF-50). */
public record RecuperacionContrasenaRequest(
    @NotBlank @Email String email
) {
}
