package com.rojasfc.gestion.dto;

/**
 * Fila del listado de alumnos (RF-39): los datos que muestra la tabla y los
 * filtros de la vista_alumno.
 */
public record AlumnoListadoItem(
    Long dni,
    String nombre,
    String apellido,
    int anioNacimiento,
    String estado,
    boolean moroso
) {
}
