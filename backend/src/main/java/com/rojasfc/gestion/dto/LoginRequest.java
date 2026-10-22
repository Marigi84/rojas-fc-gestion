package com.rojasfc.gestion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Credenciales de ingreso: DNI y contrasena (RF-11, RF-49). */
public record LoginRequest(
    @NotNull @Positive Long dni,
    @NotBlank String contrasena
) {
}
