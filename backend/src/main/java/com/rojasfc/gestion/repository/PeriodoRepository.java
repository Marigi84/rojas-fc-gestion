package com.rojasfc.gestion.repository;

import com.rojasfc.gestion.model.Periodo;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeriodoRepository extends JpaRepository<Periodo, LocalDate> {

  List<Periodo> findAllByOrderByPeriodoDesc();

  /** Ultimo periodo configurado antes del indicado: el valor vigente (RN-17). */
  Optional<Periodo> findFirstByPeriodoLessThanOrderByPeriodoDesc(LocalDate periodo);
}
