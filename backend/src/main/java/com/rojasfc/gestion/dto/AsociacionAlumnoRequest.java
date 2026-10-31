package com.rojasfc.gestion.dto;

import com.rojasfc.gestion.model.Vinculo;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Asociacion de un responsable existente con otro alumno (RF-09, RN-11). */
public record AsociacionAlumnoRequest(
    @NotNull @Positive @Max(4294967295L) Long dniAlumno,
    @NotNull Vinculo vinculo
) {
}
