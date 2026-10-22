package com.rojasfc.gestion.security;

/**
 * Datos del usuario que viajan dentro del JWT. contrasenaTemporal indica que
 * todavia no cambio la contrasena provisoria (RNF-01).
 */
public record UsuarioAutenticado(Long dni, Rol rol, boolean contrasenaTemporal) {
}
