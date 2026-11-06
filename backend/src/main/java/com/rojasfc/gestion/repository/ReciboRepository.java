package com.rojasfc.gestion.repository;

import com.rojasfc.gestion.model.Recibo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReciboRepository extends JpaRepository<Recibo, Long> {

  /** Recibo con el alumno y el pago al que corresponde. */
  @Query(value = """
      SELECT recibo.numero_recibo AS numeroRecibo, recibo.id_pago AS idPago,
             recibo.fecha_emision AS fechaEmision,
             pago.dni_alumno AS dniAlumno,
             persona.nombre, persona.apellido,
             pago.monto, pago.nombre_medio_pago AS medioPago, pago.periodo,
             pago.fecha_anulacion IS NOT NULL AS anulado
      FROM recibo
      JOIN pago ON pago.id_pago = recibo.id_pago
      JOIN persona ON persona.dni = pago.dni_alumno
      WHERE recibo.numero_recibo = :numero
      """, nativeQuery = true)
  Optional<ReciboDetalleVista> detalle(@Param("numero") Long numero);
}
