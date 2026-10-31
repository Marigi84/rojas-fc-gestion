package com.rojasfc.gestion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos modificables de un responsable (RF-08): los de la persona y el
 * telefono propio del rol. La contrasena se gestiona por separado (RF-15).
 */
public record ModificacionResponsableRequest(
    @NotBlank @Size(max = 60) String nombre,
    @NotBlank @Size(max = 60) String apellido,
    @NotBlank @Size(max = 20) String telefono
) {
}
