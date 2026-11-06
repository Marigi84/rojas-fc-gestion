package com.rojasfc.gestion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Pago de una cuota. La fecha es la del registro y el importe
 * lo calcula el servicio: el pago tiene que ser por el total adeudado.
 */
public record RegistroPagoRequest(
    @NotNull @Positive Long dniAlumno,
    @NotBlank String periodo,
    @NotBlank String medioPago
) {
}
