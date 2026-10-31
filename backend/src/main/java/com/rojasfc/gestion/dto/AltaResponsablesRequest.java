package com.rojasfc.gestion.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** Alta de uno o mas responsables para un alumno existente (RF-06). */
public record AltaResponsablesRequest(
    @NotEmpty @Valid List<ResponsableAltaRequest> responsables
) {
}
