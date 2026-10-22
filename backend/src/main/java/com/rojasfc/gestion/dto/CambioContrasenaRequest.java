package com.rojasfc.gestion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Cambio de contrasena del usuario autenticado. El limite de 72 caracteres es
 * el maximo que admite BCrypt.
 */
public record CambioContrasenaRequest(
    @NotBlank String actual,
    @NotBlank @Size(min = 8, max = 72) String nueva
) {
}
