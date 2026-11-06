package com.rojasfc.gestion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Pago de un alumno. periodo esta en formato AAAA-MM, fechaAnulacion y
 * motivo son null mientras el pago este vigente, y numeroRecibo es el recibo
 * emitido para ese pago.
 */
public record PagoDetalle(
    Long idPago,
    LocalDate fecha,
    BigDecimal monto,
    String medioPago,
    String periodo,
    LocalDate fechaAnulacion,
    String motivo,
    Long numeroRecibo
) {
}
