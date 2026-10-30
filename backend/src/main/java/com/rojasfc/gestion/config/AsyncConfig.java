package com.rojasfc.gestion.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Habilita las tareas en segundo plano. Se usa para enviar correos sin que el
 * tiempo de respuesta revele si el correo pedido esta registrado (RF-50).
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
