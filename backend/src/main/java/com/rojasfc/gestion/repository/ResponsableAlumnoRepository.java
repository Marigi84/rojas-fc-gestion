package com.rojasfc.gestion.repository;

import com.rojasfc.gestion.model.ResponsableAlumno;
import com.rojasfc.gestion.model.ResponsableAlumnoId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponsableAlumnoRepository
    extends JpaRepository<ResponsableAlumno, ResponsableAlumnoId> {

  List<ResponsableAlumno> findByDniAlumno(Long dniAlumno);
}
