package com.rojasfc.gestion.repository;

import com.rojasfc.gestion.model.Cuota;
import com.rojasfc.gestion.model.CuotaId;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CuotaRepository extends JpaRepository<Cuota, CuotaId> {

  boolean existsByPeriodo(LocalDate periodo);

  @Query("SELECT cuota.periodo AS periodo, COUNT(cuota) AS cantidad FROM Cuota cuota GROUP BY cuota.periodo")
  List<CuotasPorPeriodo> contarPorPeriodo();

  /** Una cuota por cada alumno con permanencia vigente al generar (RN-14, RN-16). */
  @Modifying
  @Query(value = """
      INSERT INTO cuota (dni_alumno, periodo, fecha_generacion)
      SELECT dni_alumno, :periodo, :hoy
      FROM permanencia
      WHERE vigente = 1
      """, nativeQuery = true)
  int generar(@Param("periodo") LocalDate periodo, @Param("hoy") LocalDate hoy);

  @Query(value = """
      SELECT periodo, importe,
             fecha_vencimiento AS fechaVencimiento,
             monto_a_pagar AS montoAPagar,
             estado,
             id_pago AS idPago,
             fecha_pago AS fechaPago
      FROM vista_cuota
      WHERE dni_alumno = :dni
      ORDER BY periodo DESC
      """, nativeQuery = true)
  List<CuotaVista> deAlumno(@Param("dni") Long dni);

  /** Morosos: al menos una cuota vencida e impaga, con el interes incluido (RN-23). */
  @Query(value = """
      SELECT vista_alumno.dni, vista_alumno.nombre, vista_alumno.apellido, vista_alumno.estado,
             COUNT(*) AS cuotasVencidas,
             SUM(vista_cuota.monto_a_pagar) AS totalAdeudado
      FROM vista_cuota
      JOIN vista_alumno ON vista_alumno.dni = vista_cuota.dni_alumno
      WHERE vista_cuota.estado = 'vencida'
      GROUP BY vista_alumno.dni, vista_alumno.nombre, vista_alumno.apellido, vista_alumno.estado
      ORDER BY vista_alumno.apellido, vista_alumno.nombre, vista_alumno.dni
      """, nativeQuery = true)
  List<AlumnoMorosoVista> morosos();
}
