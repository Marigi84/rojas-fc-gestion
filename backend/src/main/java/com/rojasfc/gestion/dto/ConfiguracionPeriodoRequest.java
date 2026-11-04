package com.rojasfc.gestion.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Condiciones de un periodo: importe, vencimiento e interes (RF-17, RF-19, RF-20). */
public record ConfiguracionPeriodoRequest(
    @NotNull @DecimalMin("0.01") @Digits(integer = 8, fraction = 2) BigDecimal importe,
    @NotNull LocalDate fechaVencimiento,
    @NotNull @DecimalMin("0") @DecimalMax("100") @Digits(integer = 3, fraction = 2)
    BigDecimal porcentajeInteres
) {
}
