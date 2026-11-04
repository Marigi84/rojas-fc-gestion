package com.rojasfc.gestion.service;

import com.rojasfc.gestion.dto.DashboardResumen;
import com.rojasfc.gestion.dto.DashboardResumen.AlumnosPorCategoria;
import com.rojasfc.gestion.repository.DashboardRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Indicadores de la pantalla de inicio del Administrador (Modulo 8: RF-45 a RF-48). */
@Service
public class DashboardService {

  private final DashboardRepository indicadores;
  private final Clock reloj;

  public DashboardService(DashboardRepository indicadores, Clock reloj) {
    this.indicadores = indicadores;
    this.reloj = reloj;
  }

  @Transactional(readOnly = true)
  public DashboardResumen resumen() {
    LocalDate hoy = LocalDate.now(reloj);
    List<AlumnosPorCategoria> categorias = indicadores.alumnosPorCategoria().stream()
        .map(fila -> new AlumnosPorCategoria(fila.getCategoria(), fila.getCantidad()))
        .toList();
    return new DashboardResumen(indicadores.alumnosActivos(), categorias,
        indicadores.alumnosMorosos(), YearMonth.from(hoy).toString(),
        indicadores.recaudacion(hoy.withDayOfMonth(1), hoy));
  }
}
