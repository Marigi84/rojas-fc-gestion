package com.rojasfc.gestion.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/** Clave de cuota: el alumno y el periodo. */
public class CuotaId implements Serializable {

  private Long dniAlumno;
  private LocalDate periodo;

  protected CuotaId() {
  }

  public CuotaId(Long dniAlumno, LocalDate periodo) {
    this.dniAlumno = dniAlumno;
    this.periodo = periodo;
  }

  @Override
  public boolean equals(Object otro) {
    if (this == otro) {
      return true;
    }
    if (!(otro instanceof CuotaId id)) {
      return false;
    }
    return Objects.equals(dniAlumno, id.dniAlumno) && Objects.equals(periodo, id.periodo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dniAlumno, periodo);
  }
}
