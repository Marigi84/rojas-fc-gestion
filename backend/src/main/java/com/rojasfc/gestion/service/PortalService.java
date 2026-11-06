package com.rojasfc.gestion.service;

import com.rojasfc.gestion.dto.PortalAlumno;
import com.rojasfc.gestion.repository.PortalRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Portal del Responsable (Modulo 3: RF-12 a RF-14). El DNI sale siempre de la
 * sesion: no se recibe por parametro, asi que nadie puede pedir los datos de un
 * alumno que no tiene asociado (RNF-03).
 */
@Service
public class PortalService {

  private final PortalRepository portal;
  private final CuotaService cuotaService;

  public PortalService(PortalRepository portal, CuotaService cuotaService) {
    this.portal = portal;
    this.cuotaService = cuotaService;
  }

  @Transactional(readOnly = true)
  public List<PortalAlumno> alumnosDe(Long dniResponsable) {
    return portal.alumnosDe(dniResponsable).stream()
        .map(alumno -> new PortalAlumno(alumno.getDni(), alumno.getNombre(), alumno.getApellido(),
            alumno.getCategoria(), alumno.getEstado(), alumno.getMoroso() == 1,
            cuotaService.deAlumno(alumno.getDni()), pagos(alumno.getDni()),
            recibos(alumno.getDni())))
        .toList();
  }

  private List<PortalAlumno.Pago> pagos(Long dniAlumno) {
    return portal.pagosDe(dniAlumno).stream()
        .map(pago -> new PortalAlumno.Pago(pago.getIdPago(), pago.getFecha(), pago.getMonto(),
            pago.getMedioPago(), mes(pago.getPeriodo()), pago.getAnulado() == 1))
        .toList();
  }

  private List<PortalAlumno.Recibo> recibos(Long dniAlumno) {
    return portal.recibosDe(dniAlumno).stream()
        .map(recibo -> new PortalAlumno.Recibo(recibo.getNumeroRecibo(), recibo.getFechaEmision(),
            recibo.getMonto(), mes(recibo.getPeriodo()), recibo.getAnulado() == 1))
        .toList();
  }

  private static String mes(LocalDate periodo) {
    return periodo == null ? null : YearMonth.from(periodo).toString();
  }
}
