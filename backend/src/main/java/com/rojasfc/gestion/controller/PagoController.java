package com.rojasfc.gestion.controller;

import com.rojasfc.gestion.dto.AnulacionPagoRequest;
import com.rojasfc.gestion.dto.PagoDetalle;
import com.rojasfc.gestion.dto.ReciboDetalle;
import com.rojasfc.gestion.dto.RegistroPagoRequest;
import com.rojasfc.gestion.security.UsuarioAutenticado;
import com.rojasfc.gestion.service.PagoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Pagos y recibos (Modulo 5). Solo Administrador (SecurityConfig). */
@RestController
@RequestMapping("/api")
public class PagoController {

  private final PagoService pagoService;

  public PagoController(PagoService pagoService) {
    this.pagoService = pagoService;
  }

  @PostMapping("/pagos")
  @ResponseStatus(HttpStatus.CREATED)
  public PagoDetalle registrar(@Valid @RequestBody RegistroPagoRequest pedido,
      @AuthenticationPrincipal UsuarioAutenticado usuario) {
    return pagoService.registrar(pedido, usuario.dni());
  }

  @PostMapping("/pagos/{idPago}/anulacion")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void anular(@PathVariable Long idPago,
      @Valid @RequestBody AnulacionPagoRequest pedido,
      @AuthenticationPrincipal UsuarioAutenticado usuario) {
    pagoService.anular(idPago, pedido.motivo(), usuario.dni());
  }

  @GetMapping("/alumnos/{dni}/pagos")
  public List<PagoDetalle> historial(@PathVariable Long dni) {
    return pagoService.historial(dni);
  }

  @GetMapping("/recibos/{numeroRecibo}")
  public ReciboDetalle recibo(@PathVariable Long numeroRecibo) {
    return pagoService.recibo(numeroRecibo);
  }
}
