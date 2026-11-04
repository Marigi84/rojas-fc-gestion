package com.rojasfc.gestion.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Indicadores del Dashboard (RF-45 a RF-48). recaudacionDelMes es lo cobrado
 * desde el dia 1 del mes hasta hoy; mes esta en formato AAAA-MM.
 */
public record DashboardResumen(
    long alumnosActivos,
    List<AlumnosPorCategoria> alumnosPorCategoria,
    long alumnosMorosos,
    String mes,
    BigDecimal recaudacionDelMes
) {

  public record AlumnosPorCategoria(int categoria, long cantidad) {
  }
}
