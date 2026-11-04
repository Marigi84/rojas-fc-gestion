package com.rojasfc.gestion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Lo que un responsable ve de cada alumno a su cargo (RF-12 a RF-14). En pagos
 * y recibos, periodo esta en formato AAAA-MM, o es null si no es una cuota.
 */
public record PortalAlumno(
    Long dni,
    String nombre,
    String apellido,
    int categoria,
    String estado,
    boolean moroso,
    List<CuotaDetalle> cuotas,
    List<Pago> pagos,
    List<Recibo> recibos
) {

  public record Pago(
      Long idPago,
      LocalDate fecha,
      BigDecimal monto,
      String medioPago,
      String periodo,
      boolean anulado
  ) {
  }

  public record Recibo(
      Long numeroRecibo,
      LocalDate fechaEmision,
      BigDecimal monto,
      String periodo,
      boolean anulado
  ) {
  }
}
