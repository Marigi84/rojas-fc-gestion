package com.rojasfc.gestion.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/** Clave de permanencia: el alumno y la fecha de alta. */
public class PermanenciaId implements Serializable {

  private Long dniAlumno;
  private LocalDate fechaAlta;

  protected PermanenciaId() {
  }

  public PermanenciaId(Long dniAlumno, LocalDate fechaAlta) {
    this.dniAlumno = dniAlumno;
    this.fechaAlta = fechaAlta;
  }

  @Override
  public boolean equals(Object otro) {
    return otro instanceof PermanenciaId id
        && Objects.equals(dniAlumno, id.dniAlumno)
        && Objects.equals(fechaAlta, id.fechaAlta);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dniAlumno, fechaAlta);
  }
}
