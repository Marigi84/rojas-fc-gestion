package com.rojasfc.gestion.dto;

import com.rojasfc.gestion.model.Vinculo;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Responsable cargado en el alta de un alumno (RF-01, RF-06). Si el DNI ya es
 * responsable, solo se asocia y se ignoran telefono y contrasena. Si es nuevo,
 * telefono y contrasenaProvisoria son obligatorios: la contrasena la asigna el
 * Administrador y queda provisoria (RF-11, RNF-01).
 */
public record ResponsableAltaRequest(
    @NotNull @Positive @Max(4294967295L) Long dni,
    @NotBlank @Size(max = 60) String nombre,
    @NotBlank @Size(max = 60) String apellido,
    @Size(max = 20) String telefono,
    @NotNull Vinculo vinculo,
    @Size(min = 8, max = 72) String contrasenaProvisoria
) {
}
