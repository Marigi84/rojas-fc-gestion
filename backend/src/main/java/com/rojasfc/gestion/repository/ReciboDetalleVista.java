package com.rojasfc.gestion.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Datos del recibo con el alumno y el pago al que corresponde. */
public interface ReciboDetalleVista {

  Long getNumeroRecibo();

  Long getIdPago();

  LocalDate getFechaEmision();

  Long getDniAlumno();

  String getNombre();

  String getApellido();

  BigDecimal getMonto();

  String getMedioPago();

  LocalDate getPeriodo();

  Integer getAnulado();
}
