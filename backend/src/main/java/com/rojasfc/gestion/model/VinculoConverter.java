package com.rojasfc.gestion.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Locale;

/** Convierte el enum al valor en minusculas de la columna ENUM de MySQL. */
@Converter
public class VinculoConverter implements AttributeConverter<Vinculo, String> {

  @Override
  public String convertToDatabaseColumn(Vinculo vinculo) {
    return vinculo == null ? null : vinculo.name().toLowerCase(Locale.ROOT);
  }

  @Override
  public Vinculo convertToEntityAttribute(String valor) {
    return valor == null ? null : Vinculo.valueOf(valor.toUpperCase(Locale.ROOT));
  }
}
