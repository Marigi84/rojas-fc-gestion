package com.rojasfc.gestion.dto;

/** Administrador registrado, para el listado de administradores (RF-51). */
public record AdministradorResumen(
    Long dni,
    String nombre,
    String apellido,
    String email,
    boolean contrasenaTemporal
) {
}
