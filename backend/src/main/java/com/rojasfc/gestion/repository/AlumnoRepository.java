package com.rojasfc.gestion.repository;

import com.rojasfc.gestion.model.Alumno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

  /** Morosidad derivada de vista_alumno (RN-23). */
  @Query(value = "SELECT moroso FROM vista_alumno WHERE dni = :dni", nativeQuery = true)
  Long moroso(@Param("dni") Long dni);

  /**
   * Listado paginado de alumnos (RF-38, RF-39). Los filtros son opcionales:
   * el servicio decide el estado por defecto y traduce moroso a 1/0.
   *
   * La busqueda usa LOCATE y no LIKE para que % y _ se busquen como texto y no
   * como comodines.
   */
  @Query(value = """
      SELECT dni, nombre, apellido, categoria AS anioNacimiento, estado, moroso
      FROM vista_alumno
      WHERE (:busqueda IS NULL
             OR LOCATE(LOWER(:busqueda), LOWER(nombre)) > 0
             OR LOCATE(LOWER(:busqueda), LOWER(apellido)) > 0
             OR LOCATE(:busqueda, CAST(dni AS CHAR)) > 0)
        AND (:anioNacimiento IS NULL OR categoria = :anioNacimiento)
        AND (:estado IS NULL OR estado = :estado)
        AND (:moroso IS NULL OR moroso = :moroso)
      ORDER BY apellido, nombre, dni
      """, countQuery = """
      SELECT COUNT(*)
      FROM vista_alumno
      WHERE (:busqueda IS NULL
             OR LOCATE(LOWER(:busqueda), LOWER(nombre)) > 0
             OR LOCATE(LOWER(:busqueda), LOWER(apellido)) > 0
             OR LOCATE(:busqueda, CAST(dni AS CHAR)) > 0)
        AND (:anioNacimiento IS NULL OR categoria = :anioNacimiento)
        AND (:estado IS NULL OR estado = :estado)
        AND (:moroso IS NULL OR moroso = :moroso)
      """, nativeQuery = true)
  Page<AlumnoListadoVista> listar(
      @Param("busqueda") String busqueda,
      @Param("anioNacimiento") Integer anioNacimiento,
      @Param("estado") String estado,
      @Param("moroso") Integer moroso,
      Pageable pageable);
}
