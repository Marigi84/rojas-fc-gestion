package com.rojasfc.gestion.model;

import java.io.Serializable;
import java.util.Objects;

/** Clave del vinculo: el responsable y el alumno. */
public class ResponsableAlumnoId implements Serializable {

  private Long dniResponsable;
  private Long dniAlumno;

  protected ResponsableAlumnoId() {
  }

  public ResponsableAlumnoId(Long dniResponsable, Long dniAlumno) {
    this.dniResponsable = dniResponsable;
    this.dniAlumno = dniAlumno;
  }

  @Override
  public boolean equals(Object otro) {
    return otro instanceof ResponsableAlumnoId id
        && Objects.equals(dniResponsable, id.dniResponsable)
        && Objects.equals(dniAlumno, id.dniAlumno);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dniResponsable, dniAlumno);
  }
}
