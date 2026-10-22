package com.rojasfc.gestion.repository;

import com.rojasfc.gestion.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaRepository extends JpaRepository<Persona, Long> {
}
