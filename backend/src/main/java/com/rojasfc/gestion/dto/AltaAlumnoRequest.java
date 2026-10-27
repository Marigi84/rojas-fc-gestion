package com.rojasfc.gestion.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

/**
 * Alta de un alumno con al menos un responsable (RF-01, RN-05). Los limites
 * siguen las columnas de persona y alumno (database/schema.sql). Nombre y
 * apellido se piden solo si el DNI no es una persona ya registrada (RN-01).
 */
public record AltaAlumnoRequest(
    @NotNull @Positive @Max(4294967295L) Long dni,
    @Size(max = 60) String nombre,
    @Size(max = 60) String apellido,
    @NotNull @Past LocalDate fechaNacimiento,
    @NotBlank @Size(max = 120) String direccion,
    @NotEmpty @Valid List<ResponsableAltaRequest> responsables
) {
}
