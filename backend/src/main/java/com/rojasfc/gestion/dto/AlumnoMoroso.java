package com.rojasfc.gestion.dto;

import java.math.BigDecimal;

/** Alumno con deuda vencida y el importe total que adeuda (RF-23). */
public record AlumnoMoroso(
    Long dni,
    String nombre,
    String apellido,
    String estado,
    long cuotasVencidas,
    BigDecimal totalAdeudado
) {
}
