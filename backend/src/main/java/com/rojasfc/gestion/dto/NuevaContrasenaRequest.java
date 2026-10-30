package com.rojasfc.gestion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Contrasena nueva elegida desde el enlace de recuperacion (RF-50). El limite
 * de 72 caracteres es el maximo que admite BCrypt.
 */
public record NuevaContrasenaRequest(
    @NotBlank String token,
    @NotBlank @Size(min = 8, max = 72) String nueva
) {
}
