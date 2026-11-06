package com.rojasfc.gestion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Motivo de la anulacion de un pago (RF-28, RN-31). */
public record AnulacionPagoRequest(
    @NotBlank @Size(max = 200) String motivo
) {
}
