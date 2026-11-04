package com.rojasfc.gestion.dto;

/** Resultado de generar las cuotas de un periodo (RF-18). */
public record GeneracionCuotasResultado(
    String periodo,
    int cuotasGeneradas
) {
}
