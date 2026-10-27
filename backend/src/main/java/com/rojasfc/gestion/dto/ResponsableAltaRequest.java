package com.rojasfc.gestion.dto;

import com.rojasfc.gestion.model.Vinculo;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Responsable cargado en el alta de un alumno (RF-01, RF-06). Si el DNI ya es
 * responsable, solo se asocia: alcanzan el DNI y el vinculo. Nombre y apellido
 * se piden solo si la persona es nueva (RN-01). Telefono y contrasenaProvisoria
 * se piden solo si todavia no es responsable: la contrasena la asigna el
 * Administrador y queda provisoria (RF-11, RNF-01).
 */
public record ResponsableAltaRequest(
    @NotNull @Positive @Max(4294967295L) Long dni,
    @Size(max = 60) String nombre,
    @Size(max = 60) String apellido,
    @Size(max = 20) String telefono,
    @NotNull Vinculo vinculo,
    @Size(min = 8, max = 72) String contrasenaProvisoria
) {
}
