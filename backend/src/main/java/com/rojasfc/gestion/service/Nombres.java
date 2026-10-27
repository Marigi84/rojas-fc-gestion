package com.rojasfc.gestion.service;

/**
 * Nombre y apellido solo se piden para registrar una persona nueva: si el DNI
 * ya existe, se reutilizan sus datos (RN-01).
 */
final class Nombres {

  private Nombres() {
  }

  static void exigir(String nombre, String apellido, String quien) {
    if (nombre == null || nombre.isBlank() || apellido == null || apellido.isBlank()) {
      throw new IllegalArgumentException(
          "Faltan el nombre y el apellido " + quien + ", que se registra por primera vez.");
    }
  }
}
