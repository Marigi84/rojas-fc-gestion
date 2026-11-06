package com.rojasfc.gestion.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Dispara la generacion automatica de las cuotas del mes (RF-18, RN-15). Corre
 * todos los dias y al arrancar, no solo el dia 1, porque el servidor puede
 * estar apagado ese dia: si el mes ya tiene cuotas, no hace nada.
 */
@Component
public class GeneradorCuotas {

  private static final Logger LOG = LoggerFactory.getLogger(GeneradorCuotas.class);

  private final CuotaService cuotaService;

  public GeneradorCuotas(CuotaService cuotaService) {
    this.cuotaService = cuotaService;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void alArrancar() {
    generar();
  }

  @Scheduled(cron = "0 0 1 * * *", zone = "${app.zona-horaria}")
  public void todosLosDias() {
    generar();
  }

  /** Un error no debe impedir que el sistema arranque: se reintenta en la proxima corrida. */
  private void generar() {
    try {
      int generadas = cuotaService.generarDelMes();
      if (generadas > 0) {
        LOG.info("Se generaron {} cuotas del mes", generadas);
      }
    } catch (RuntimeException ex) {
      LOG.error("No se pudieron generar las cuotas del mes", ex);
    }
  }
}
