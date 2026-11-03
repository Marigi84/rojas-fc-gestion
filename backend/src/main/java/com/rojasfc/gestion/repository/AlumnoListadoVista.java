package com.rojasfc.gestion.repository;

/**
 * Proyección de vista_alumno para el listado paginado (RF-38, RF-39).
 * El año de nacimiento es la columna categoría de la vista.
 */
public interface AlumnoListadoVista {

  Long getDni();

  String getNombre();

  String getApellido();

  Integer getAnioNacimiento();

  String getEstado();

  Integer getMoroso();
}
