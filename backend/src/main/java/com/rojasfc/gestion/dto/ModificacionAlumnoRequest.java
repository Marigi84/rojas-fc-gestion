package com.rojasfc.gestion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** Datos modificables de un alumno (RF-03). El DNI no se modifica: es su clave. */
public record ModificacionAlumnoRequest(
    @NotBlank @Size(max = 60) String nombre,
    @NotBlank @Size(max = 60) String apellido,
    @NotNull @Past LocalDate fechaNacimiento,
    @NotBlank @Size(max = 120) String direccion
) {
}
