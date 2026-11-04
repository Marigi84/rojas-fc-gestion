package com.rojasfc.gestion.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Proyeccion de vista_cuota: la cuota con su interes, estado y pago (RF-22). */
public interface CuotaVista {

  LocalDate getPeriodo();

  BigDecimal getImporte();

  LocalDate getFechaVencimiento();

  BigDecimal getMontoAPagar();

  String getEstado();

  Long getIdPago();

  LocalDate getFechaPago();
}
