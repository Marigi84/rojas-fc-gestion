package com.rojasfc.gestion.config;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Reloj del sistema en la zona horaria de la escuela. Las fechas de alta y
 * baja se toman de aca y no del servidor, que en la nube suele estar en UTC.
 */
@Configuration
public class RelojConfig {

  @Bean
  public Clock reloj(@Value("${app.zona-horaria}") String zonaHoraria) {
    return Clock.system(ZoneId.of(zonaHoraria));
  }
}
