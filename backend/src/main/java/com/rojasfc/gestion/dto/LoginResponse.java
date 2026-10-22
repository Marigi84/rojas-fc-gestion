package com.rojasfc.gestion.dto;

import com.rojasfc.gestion.security.Rol;

/**
 * Respuesta de un ingreso correcto. Si contrasenaTemporal es true, el frontend
 * debe pedir el cambio de contrasena antes de mostrar el resto (RNF-01).
 */
public record LoginResponse(
    String token,
    Rol rol,
    Long dni,
    String nombre,
    String apellido,
    boolean contrasenaTemporal
) {
}
