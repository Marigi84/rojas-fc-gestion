package com.rojasfc.gestion.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Datos del primer Administrador, cargados en la puesta en marcha del sistema.
 * Los limites siguen las columnas de persona y administrador
 * (database/schema.sql).
 */
public record ConfiguracionInicialRequest(
    @NotNull @Positive @Max(4294967295L) Long dni,
    @NotBlank @Size(max = 60) String nombre,
    @NotBlank @Size(max = 60) String apellido,
    @NotBlank @Email @Size(max = 120) String email,
    @NotBlank @Size(min = 8, max = 72) String contrasena
) {
}
