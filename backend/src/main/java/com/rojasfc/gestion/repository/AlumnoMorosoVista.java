package com.rojasfc.gestion.repository;

import java.math.BigDecimal;

/** Alumno con deuda vencida y el total que adeuda (RF-23). */
public interface AlumnoMorosoVista {

  Long getDni();

  String getNombre();

  String getApellido();

  String getEstado();

  Long getCuotasVencidas();

  BigDecimal getTotalAdeudado();
}
