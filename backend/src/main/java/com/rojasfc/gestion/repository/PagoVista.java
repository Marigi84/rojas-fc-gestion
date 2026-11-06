package com.rojasfc.gestion.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Pago de un alumno con su recibo y su anulacion, para el historial. */
public interface PagoVista {

  Long getIdPago();

  LocalDate getFecha();

  BigDecimal getMonto();

  String getMedioPago();

  LocalDate getPeriodo();

  LocalDate getFechaAnulacion();

  String getMotivo();

  Long getNumeroRecibo();
}
