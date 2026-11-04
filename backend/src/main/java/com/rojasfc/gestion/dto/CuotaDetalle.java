package com.rojasfc.gestion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Cuota de un alumno (RF-22). montoAPagar incluye el interes si corresponde
 * (RF-21) y estado es pagada, pendiente o vencida. idPago y fechaPago son los
 * del pago vigente asociado, o null si no tiene.
 */
public record CuotaDetalle(
    String periodo,
    BigDecimal importe,
    LocalDate fechaVencimiento,
    BigDecimal montoAPagar,
    String estado,
    Long idPago,
    LocalDate fechaPago
) {
}
