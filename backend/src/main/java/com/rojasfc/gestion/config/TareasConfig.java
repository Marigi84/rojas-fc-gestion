package com.rojasfc.gestion.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Habilita las tareas programadas: la generacion de las cuotas del mes (RF-18). */
@Configuration
@EnableScheduling
public class TareasConfig {
}
