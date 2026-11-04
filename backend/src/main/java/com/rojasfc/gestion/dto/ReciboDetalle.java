package com.rojasfc.gestion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Recibo emitido para un pago. periodo esta en formato AAAA-MM
 * y anulado indica que el pago se anulo, aunque el recibo se conserva.
 */
public record ReciboDetalle(
    Long numeroRecibo,
    Long idPago,
    LocalDate fechaEmision,
    Long dniAlumno,
    String alumno,
    String periodo,
    BigDecimal importe,
    String medioPago,
    boolean anulado
) {
}
