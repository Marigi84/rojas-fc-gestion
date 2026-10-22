package com.rojasfc.gestion.service;

import com.rojasfc.gestion.dto.ConfiguracionInicialRequest;
import com.rojasfc.gestion.dto.EstadoConfiguracion;
import com.rojasfc.gestion.dto.LoginRequest;
import com.rojasfc.gestion.dto.LoginResponse;
import com.rojasfc.gestion.exception.SistemaYaConfiguradoException;
import com.rojasfc.gestion.model.Administrador;
import com.rojasfc.gestion.model.Persona;
import com.rojasfc.gestion.repository.AdministradorRepository;
import com.rojasfc.gestion.repository.PersonaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Puesta en marcha del sistema: registro del primer Administrador la primera
 * vez que se abre la aplicacion. Los demas administradores se registran desde
 * el sistema (RF-51).
 *
 * Solo funciona mientras no haya ningun Administrador. Despues responde 409.
 */
@Service
public class ConfiguracionInicialService {

  private final AdministradorRepository administradores;
  private final PersonaRepository personas;
  private final PasswordEncoder passwordEncoder;
  private final TransactionTemplate transaccion;
  private final AuthService authService;

  public ConfiguracionInicialService(AdministradorRepository administradores,
      PersonaRepository personas, PasswordEncoder passwordEncoder,
      TransactionTemplate transaccion, AuthService authService) {
    this.administradores = administradores;
    this.personas = personas;
    this.passwordEncoder = passwordEncoder;
    this.transaccion = transaccion;
    this.authService = authService;
  }

  public EstadoConfiguracion estado() {
    return new EstadoConfiguracion(administradores.count() > 0);
  }

  /**
   * Crea el primer Administrador y devuelve su sesion ya iniciada. Es
   * synchronized y la transaccion termina adentro, para que dos envios
   * simultaneos no creen dos administradores: el segundo encuentra la tabla
   * con datos y recibe 409.
   */
  public synchronized LoginResponse configurar(ConfiguracionInicialRequest pedido) {
    transaccion.executeWithoutResult(estado -> {
      if (administradores.count() > 0) {
        throw new SistemaYaConfiguradoException();
      }
      // Si la persona ya existe (por ejemplo, es responsable), se reutiliza (RN-01).
      if (!personas.existsById(pedido.dni())) {
        personas.save(new Persona(pedido.dni(), pedido.nombre(), pedido.apellido()));
      }
      // La contrasena la elige el propio administrador, por eso no es provisoria.
      administradores.save(new Administrador(pedido.dni(), pedido.email(),
          passwordEncoder.encode(pedido.contrasena()), false));
    });
    return authService.ingresar(new LoginRequest(pedido.dni(), pedido.contrasena()));
  }
}
