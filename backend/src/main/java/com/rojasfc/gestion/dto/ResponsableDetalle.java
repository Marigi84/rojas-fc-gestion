package com.rojasfc.gestion.dto;

import com.rojasfc.gestion.model.Vinculo;
import java.util.List;

/**
 * Datos de un responsable y los alumnos a su cargo (RF-07). El vinculo es
 * propio de cada par responsable-alumno (RN-12).
 */
public record ResponsableDetalle(
    Long dni,
    String nombre,
    String apellido,
    String telefono,
    List<AlumnoAsociado> alumnos
) {

  public record AlumnoAsociado(
      Long dni,
      String nombre,
      String apellido,
      Vinculo vinculo,
      boolean activo
  ) {
  }
}
