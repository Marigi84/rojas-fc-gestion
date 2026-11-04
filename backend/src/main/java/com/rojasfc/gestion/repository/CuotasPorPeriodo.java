package com.rojasfc.gestion.repository;

import java.time.LocalDate;

/** Cantidad de cuotas generadas de cada periodo. */
public interface CuotasPorPeriodo {

  LocalDate getPeriodo();

  Long getCantidad();
}
