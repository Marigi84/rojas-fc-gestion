package com.rojasfc.gestion.dto;

import com.rojasfc.gestion.model.Vinculo;
import java.time.LocalDate;
import java.util.List;

/**
 * Datos y historial de un alumno (RF-02): categoria (RF-16), estado activo o
 * inactivo, morosidad, permanencias de la mas reciente a la mas antigua y
 * responsables a cargo.
 */
public record AlumnoDetalle(
    Long dni,
    String nombre,
    String apellido,
    LocalDate fechaNacimiento,
    String direccion,
    int categoria,
    String estado,
    boolean moroso,
    List<PermanenciaDetalle> permanencias,
    List<ResponsableDetalle> responsables
) {

  public record PermanenciaDetalle(LocalDate fechaAlta, LocalDate fechaBaja) {
  }

  public record ResponsableDetalle(
      Long dni, String nombre, String apellido, String telefono, Vinculo vinculo) {
  }
}
