package com.rojasfc.gestion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/** Responsable a cargo de un alumno, con su vinculo (tabla responsable_alumno). */
@Entity
@Table(name = "responsable_alumno")
@IdClass(ResponsableAlumnoId.class)
public class ResponsableAlumno {

  @Id
  @Column(name = "dni_responsable")
  private Long dniResponsable;

  @Id
  @Column(name = "dni_alumno")
  private Long dniAlumno;

  @Convert(converter = VinculoConverter.class)
  @Column(nullable = false)
  private Vinculo vinculo;

  protected ResponsableAlumno() {
  }

  public ResponsableAlumno(Long dniResponsable, Long dniAlumno, Vinculo vinculo) {
    this.dniResponsable = dniResponsable;
    this.dniAlumno = dniAlumno;
    this.vinculo = vinculo;
  }

  public Long getDniResponsable() {
    return dniResponsable;
  }

  public Long getDniAlumno() {
    return dniAlumno;
  }

  public Vinculo getVinculo() {
    return vinculo;
  }
}
