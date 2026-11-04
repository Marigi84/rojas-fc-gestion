package com.rojasfc.gestion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Periodo configurado, con el mes en formato AAAA-MM y las cuotas ya generadas. */
public record PeriodoDetalle(
    String periodo,
    BigDecimal importe,
    LocalDate fechaVencimiento,
    BigDecimal porcentajeInteres,
    LocalDate fechaConfiguracion,
    long cuotasGeneradas
) {
}
