package com.rojasfc.gestion.repository;

import com.rojasfc.gestion.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministradorRepository extends JpaRepository<Administrador, Long> {

  boolean existsByEmailIgnoreCase(String email);
}
