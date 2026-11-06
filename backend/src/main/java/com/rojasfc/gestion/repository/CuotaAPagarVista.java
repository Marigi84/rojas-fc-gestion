package com.rojasfc.gestion.repository;

import java.math.BigDecimal;

/**
 * Estado de la cuota y su monto a pagar. El monto es el de vista_cuota, que
 * incluye el interes cuando la cuota esta vencida.
 */
public interface CuotaAPagarVista {

  String getEstado();

  BigDecimal getMonto();
}
