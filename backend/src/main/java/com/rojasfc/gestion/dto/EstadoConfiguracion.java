package com.rojasfc.gestion.dto;

/**
 * Indica si el sistema ya tiene su primer Administrador. Si configurado es
 * false, el frontend muestra la pantalla de configuracion inicial en lugar del
 * ingreso.
 */
public record EstadoConfiguracion(boolean configurado) {
}
