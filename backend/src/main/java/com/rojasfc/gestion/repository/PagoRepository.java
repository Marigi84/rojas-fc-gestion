package com.rojasfc.gestion.repository;

import com.rojasfc.gestion.model.Pago;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PagoRepository extends JpaRepository<Pago, Long> {

  /**
   * Estado de la cuota y su monto a pagar. El monto sale de vista_cuota, que ya
   * incluye el interes cuando la cuota esta vencida.
   */
  @Query(value = """
      SELECT estado, monto_a_pagar AS monto
      FROM vista_cuota
      WHERE dni_alumno = :dni
        AND periodo = :periodo
      """, nativeQuery = true)
  Optional<CuotaAPagarVista> cuotaAPagar(@Param("dni") Long dni,
      @Param("periodo") LocalDate periodo);

  /** Pagos del alumno, de los mas recientes a los mas antiguos. */
  @Query(value = """
      SELECT pago.id_pago AS idPago, pago.fecha, pago.monto,
             pago.nombre_medio_pago AS medioPago, pago.periodo,
             pago.fecha_anulacion AS fechaAnulacion, pago.motivo,
             recibo.numero_recibo AS numeroRecibo
      FROM pago
      LEFT JOIN recibo ON recibo.id_pago = pago.id_pago
      WHERE pago.dni_alumno = :dni
      ORDER BY pago.fecha DESC, pago.id_pago DESC
      """, nativeQuery = true)
  List<PagoVista> deAlumno(@Param("dni") Long dni);
}
