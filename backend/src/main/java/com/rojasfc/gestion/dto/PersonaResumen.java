package com.rojasfc.gestion.dto;

/**
 * Persona registrada y sus roles. Permite al formulario reutilizar los datos
 * de un DNI ya cargado en lugar de pedirlos de nuevo (RF-01, RF-06, RN-01).
 */
public record PersonaResumen(
    Long dni,
    String nombre,
    String apellido,
    boolean alumno,
    boolean responsable,
    boolean administrador
) {
}
