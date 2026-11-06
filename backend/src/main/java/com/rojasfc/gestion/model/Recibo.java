package com.rojasfc.gestion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

/**
 * Recibo de un pago (tabla recibo). Se emite solo, uno por cada pago confirmado
 * (RN-33), y se conserva aunque el pago se anule (RF-33).
 */
@Entity
@Table(name = "recibo")
public class Recibo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "numero_recibo")
  private Long numeroRecibo;

  @Column(name = "id_pago", nullable = false)
  private Long idPago;

  @Column(name = "fecha_emision", nullable = false)
  private LocalDate fechaEmision;

  protected Recibo() {
  }

  public Recibo(Long idPago, LocalDate fechaEmision) {
    this.idPago = idPago;
    this.fechaEmision = fechaEmision;
  }

  public Long getNumeroRecibo() {
    return numeroRecibo;
  }
}
