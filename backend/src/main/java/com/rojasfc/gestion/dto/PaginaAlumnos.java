package com.rojasfc.gestion.dto;

import java.util.List;

/** Página del listado de alumnos (RF-38, RF-39). */
public record PaginaAlumnos(
    List<AlumnoListadoItem> contenido,
    int pagina,
    int tamano,
    long total,
    int totalPaginas
) {
}
