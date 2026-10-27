package com.rojasfc.gestion.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Envio de correos del sistema por SMTP (ver backend/README.md). Se envian en
 * segundo plano: si el envio falla, queda en el registro del backend y el
 * usuario puede volver a pedirlo.
 */
@Service
public class CorreoService {

  private static final Logger log = LoggerFactory.getLogger(CorreoService.class);

  private final JavaMailSender mailSender;
  private final String remitente;

  public CorreoService(JavaMailSender mailSender,
      @Value("${app.correo.remitente}") String remitente) {
    this.mailSender = mailSender;
    this.remitente = remitente;
  }

  /** Enlace para que el Administrador elija una contrasena nueva (RF-50). */
  @Async
  public void enviarRecuperacion(String destinatario, String nombre, String enlace,
      long minutos) {
    SimpleMailMessage mensaje = new SimpleMailMessage();
    if (!remitente.isBlank()) {
      mensaje.setFrom("Rojas FC <" + remitente + ">");
    }
    mensaje.setTo(destinatario);
    mensaje.setSubject("Rojas FC: recuperar tu contraseña");
    mensaje.setText("""
        Hola, %s:

        Pediste recuperar tu contraseña del Sistema de Gestión Rojas FC. Para \
        elegir una nueva, entrá a este enlace:

        %s

        El enlace vence en %d minutos y sirve una sola vez. Si no lo pediste, \
        ignorá este correo: tu contraseña no cambia.
        """.formatted(nombre, enlace, minutos));
    try {
      mailSender.send(mensaje);
    } catch (MailException e) {
      log.error("No se pudo enviar el correo de recuperacion: {}", e.getMessage());
    }
  }
}
