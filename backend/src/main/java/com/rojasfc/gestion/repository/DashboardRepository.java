package com.rojasfc.gestion.repository;

import com.rojasfc.gestion.model.Alumno;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

/** Indicadores del Dashboard (Modulo 8). Solo lectura, sobre vistas y pagos. */
public interface DashboardRepository extends Repository<Alumno, Long> {

  @Query(value = "SELECT COUNT(*) FROM vista_alumno WHERE estado = 'activo'", nativeQuery = true)
  long alumnosActivos();

  @Query(value = """
      SELECT categoria, COUNT(*) AS cantidad
      FROM vista_alumno
      WHERE estado = 'activo'
      GROUP BY categoria
      ORDER BY categoria DESC
      """, nativeQuery = true)
  List<CategoriaVista> alumnosPorCategoria();

  /** Mismo criterio que el listado de morosos: tambien cuenta a los dados de baja con deuda. */
  @Query(value = "SELECT COUNT(*) FROM vista_alumno WHERE moroso = 1", nativeQuery = true)
  long alumnosMorosos();

  /** Pagos confirmados y no anulados entre dos fechas (RN-32). */
  @Query(value = """
      SELECT COALESCE(SUM(monto), 0)
      FROM pago
      WHERE fecha BETWEEN :desde AND :hasta
        AND fecha_anulacion IS NULL
        AND estado_sincronizacion = 'confirmado'
      """, nativeQuery = true)
  BigDecimal recaudacion(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

  /** Cantidad de alumnos activos de una categoria. */
  interface CategoriaVista {

    Integer getCategoria();

    Long getCantidad();
  }
}
