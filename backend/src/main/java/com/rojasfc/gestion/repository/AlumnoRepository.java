package com.rojasfc.gestion.repository;

import com.rojasfc.gestion.model.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

  /** Morosidad derivada de vista_alumno (RN-23). */
  @Query(value = "SELECT moroso FROM vista_alumno WHERE dni = :dni", nativeQuery = true)
  Long moroso(@Param("dni") Long dni);
}
