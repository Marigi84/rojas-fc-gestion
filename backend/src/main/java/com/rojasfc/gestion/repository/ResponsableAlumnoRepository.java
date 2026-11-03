package com.rojasfc.gestion.repository;

import com.rojasfc.gestion.model.ResponsableAlumno;
import com.rojasfc.gestion.model.ResponsableAlumnoId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponsableAlumnoRepository
    extends JpaRepository<ResponsableAlumno, ResponsableAlumnoId> {

  List<ResponsableAlumno> findByDniAlumno(Long dniAlumno);

  boolean existsByDniResponsable(Long dniResponsable);

  List<ResponsableAlumno> findByDniResponsable(Long dniResponsable);

  Optional<ResponsableAlumno> findByDniResponsableAndDniAlumno(
      Long dniResponsable, Long dniAlumno);

  boolean existsByDniResponsableAndDniAlumno(Long dniResponsable, Long dniAlumno);

  long countByDniAlumno(Long dniAlumno);

  long countByDniResponsable(Long dniResponsable);
}
