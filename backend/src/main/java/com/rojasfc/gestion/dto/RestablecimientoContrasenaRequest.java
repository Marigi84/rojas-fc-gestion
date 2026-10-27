package com.rojasfc.gestion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Contrasena provisoria nueva que el Administrador asigna a un responsable
 * (RF-15). El limite de 72 caracteres es el maximo que admite BCrypt.
 */
public record RestablecimientoContrasenaRequest(
    @NotBlank @Size(min = 8, max = 72) String contrasenaProvisoria
) {
}
