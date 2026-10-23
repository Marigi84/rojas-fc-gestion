package com.rojasfc.gestion.repository;

import com.rojasfc.gestion.model.Permanencia;
import com.rojasfc.gestion.model.PermanenciaId;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermanenciaRepository extends JpaRepository<Permanencia, PermanenciaId> {

  List<Permanencia> findByDniAlumnoOrderByFechaAltaDesc(Long dniAlumno);

  Optional<Permanencia> findByDniAlumnoAndFechaBajaIsNull(Long dniAlumno);

  boolean existsByDniAlumnoAndFechaAlta(Long dniAlumno, LocalDate fechaAlta);
}
