package com.rojasfc.gestion.repository;

import com.rojasfc.gestion.model.ResponsableAlumno;
import com.rojasfc.gestion.model.ResponsableAlumnoId;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

/** Consultas del Portal del Responsable (Modulo 3). Solo lectura. */
public interface PortalRepository extends Repository<ResponsableAlumno, ResponsableAlumnoId> {

  @Query(value = """
      SELECT vista_alumno.dni, vista_alumno.nombre, vista_alumno.apellido,
             vista_alumno.categoria, vista_alumno.estado, vista_alumno.moroso
      FROM responsable_alumno
      JOIN vista_alumno ON vista_alumno.dni = responsable_alumno.dni_alumno
      WHERE responsable_alumno.dni_responsable = :dniResponsable
      ORDER BY vista_alumno.apellido, vista_alumno.nombre, vista_alumno.dni
      """, nativeQuery = true)
  List<AlumnoVista> alumnosDe(@Param("dniResponsable") Long dniResponsable);

  @Query(value = """
      SELECT id_pago AS idPago, fecha, monto,
             nombre_medio_pago AS medioPago,
             periodo,
             fecha_anulacion IS NOT NULL AS anulado
      FROM pago
      WHERE dni_alumno = :dniAlumno
        AND estado_sincronizacion = 'confirmado'
      ORDER BY fecha DESC, id_pago DESC
      """, nativeQuery = true)
  List<PagoVista> pagosDe(@Param("dniAlumno") Long dniAlumno);

  @Query(value = """
      SELECT vista_recibo.numero_recibo AS numeroRecibo,
             vista_recibo.fecha_emision AS fechaEmision,
             pago.monto, pago.periodo,
             vista_recibo.anulado
      FROM vista_recibo
      JOIN pago ON pago.id_pago = vista_recibo.id_pago
      WHERE pago.dni_alumno = :dniAlumno
      ORDER BY vista_recibo.numero_recibo DESC
      """, nativeQuery = true)
  List<ReciboVista> recibosDe(@Param("dniAlumno") Long dniAlumno);

  /** Alumno a cargo del responsable. */
  interface AlumnoVista {

    Long getDni();

    String getNombre();

    String getApellido();

    Integer getCategoria();

    String getEstado();

    Integer getMoroso();
  }

  /** Pago de un alumno; periodo es null si no corresponde a una cuota. */
  interface PagoVista {

    Long getIdPago();

    LocalDate getFecha();

    BigDecimal getMonto();

    String getMedioPago();

    LocalDate getPeriodo();

    Integer getAnulado();
  }

  /** Recibo de un pago de un alumno; periodo es null si no corresponde a una cuota. */
  interface ReciboVista {

    Long getNumeroRecibo();

    LocalDate getFechaEmision();

    BigDecimal getMonto();

    LocalDate getPeriodo();

    Integer getAnulado();
  }
}
