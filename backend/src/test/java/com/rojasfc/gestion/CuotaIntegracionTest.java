package com.rojasfc.gestion;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rojasfc.gestion.security.Rol;
import com.rojasfc.gestion.security.UsuarioAutenticado;
import com.rojasfc.gestion.service.CuotaService;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/**
 * Cuotas contra MySQL real, para lo que resuelve la base: el interes y el estado
 * de vista_cuota y los triggers de RN-14 y RN-22. Cada prueba usa sus propios
 * alumnos y periodos. Se activa con TESTS_MYSQL=true (ver backend/README.md).
 */
@SpringBootTest
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "TESTS_MYSQL", matches = "true")
class CuotaIntegracionTest {

  private static final long ADMIN = 20111222L;

  @Autowired
  private MockMvc mvc;

  @Autowired
  private JdbcTemplate jdbc;

  @Autowired
  private CuotaService cuotaService;

  @DynamicPropertySource
  static void baseDePrueba(DynamicPropertyRegistry registro) throws IOException, SQLException {
    AlumnoIntegracionTest.baseDePrueba(registro);
  }

  /** Cierra las permanencias de las pruebas anteriores: cada generacion ve solo a sus alumnos. */
  @BeforeEach
  void soloLosAlumnosDeCadaPrueba() {
    jdbc.update("UPDATE permanencia SET fecha_baja = CURDATE(), dni_admin_baja = ? "
        + "WHERE fecha_baja IS NULL", ADMIN);
  }

  @Test
  void rn14SeGeneraSoloParaLosAlumnosActivos() throws Exception {
    alumno(42000001, "Activo");
    alumno(42000002, "Inactivo");
    darDeBaja(42000002);
    configurar("2020-01", "2099-12-31", "1000", "10").andExpect(status().isOk());

    generar("2020-01")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.cuotasGeneradas").value(1));

    assertEquals(1, contar("cuota WHERE dni_alumno = 42000001 AND periodo = '2020-01-01'"));
    assertEquals(0, contar("cuota WHERE dni_alumno = 42000002"));
    pedir("/api/periodos")
        .andExpect(jsonPath("$[?(@.periodo == '2020-01')].cuotasGeneradas", contains(1)));
  }

  @Test
  void rn14UnMesPasadoNoSeLeCobraAQuienTodaviaNoSeHabiaInscripto() throws Exception {
    alumno(42000011, "Antiguo");
    alumno(42000012, "Nuevo");
    jdbc.update("UPDATE permanencia SET fecha_alta = '2021-06-15' WHERE dni_alumno = 42000012");
    configurar("2021-05", "2099-12-31", "1000", "10");
    configurar("2021-06", "2099-12-31", "1000", "10");

    generar("2021-05").andExpect(jsonPath("$.cuotasGeneradas").value(1));
    generar("2021-06").andExpect(jsonPath("$.cuotasGeneradas").value(2));

    assertEquals(0, contar("cuota WHERE dni_alumno = 42000012 AND periodo = '2021-05-01'"));
    assertEquals(1, contar("cuota WHERE dni_alumno = 42000012 AND periodo = '2021-06-01'"));
  }

  @Test
  void rf52UnResponsableNoAccedeALasCuotas() throws Exception {
    UsuarioAutenticado usuario = new UsuarioAutenticado(30000001L, Rol.RESPONSABLE, false);
    RequestPostProcessor responsable = authentication(new UsernamePasswordAuthenticationToken(
        usuario, null, List.of(new SimpleGrantedAuthority("ROLE_RESPONSABLE"))));

    mvc.perform(get("/api/periodos").with(responsable)).andExpect(status().isForbidden());
    mvc.perform(get("/api/cuotas/morosos").with(responsable)).andExpect(status().isForbidden());
    mvc.perform(get("/api/alumnos/42000001/cuotas").with(responsable))
        .andExpect(status().isForbidden());
    mvc.perform(post("/api/periodos/2020-01/cuotas").with(responsable))
        .andExpect(status().isForbidden());
  }

  @Test
  void rn16NoSeGeneraDosVeces() throws Exception {
    alumno(42000003, "Unico");
    configurar("2020-02", "2099-12-31", "1000", "10");
    generar("2020-02").andExpect(status().isOk());

    generar("2020-02")
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("El período ya tiene cuotas generadas"));

    assertEquals(1, contar("cuota WHERE periodo = '2020-02-01'"));
  }

  @Test
  void rn22ElPeriodoSeModificaSoloAntesDeGenerar() throws Exception {
    alumno(42000004, "Fijo");
    configurar("2020-03", "2099-12-31", "1000", "10");
    configurar("2020-03", "2099-11-30", "1500", "5")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.importe").value(1500));
    generar("2020-03").andExpect(status().isOk());

    configurar("2020-03", "2099-11-30", "2000", "5")
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("El período ya tiene cuotas generadas"));

    // El trigger de la base lo rechaza aunque no se pase por el servicio.
    assertThrows(DataAccessException.class, () -> jdbc.update(
        "UPDATE periodo SET importe = 2000 WHERE periodo = '2020-03-01'"));
    assertEquals(1500, jdbc.queryForObject(
        "SELECT importe FROM periodo WHERE periodo = '2020-03-01'", Integer.class));
  }

  @Test
  void rf21UnaCuotaVencidaLlevaElInteresUnaSolaVez() throws Exception {
    alumno(42000005, "Vencida");
    configurar("2020-04", "2020-04-13", "1000", "10");
    generar("2020-04");

    pedir("/api/alumnos/42000005/cuotas")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].periodo").value("2020-04"))
        .andExpect(jsonPath("$[0].importe").value(1000))
        .andExpect(jsonPath("$[0].fechaVencimiento").value("2020-04-13"))
        .andExpect(jsonPath("$[0].montoAPagar").value(1100))
        .andExpect(jsonPath("$[0].estado").value("vencida"))
        .andExpect(jsonPath("$[0].idPago").isEmpty());
  }

  @Test
  void rn24LaMorosidadNoDaDeBajaAlAlumno() throws Exception {
    alumno(42000006, "Moroso");
    configurar("2020-05", "2020-05-13", "1000", "10");
    generar("2020-05");

    pedir("/api/alumnos/42000006")
        .andExpect(jsonPath("$.moroso").value(true))
        .andExpect(jsonPath("$.estado").value("activo"));
  }

  @Test
  void unaCuotaSinVencerNoLlevaInteresNiEsMorosa() throws Exception {
    alumno(42000007, "AlDia");
    configurar("2020-06", "2099-12-31", "1000", "10");
    generar("2020-06");

    pedir("/api/alumnos/42000007/cuotas")
        .andExpect(jsonPath("$[0].montoAPagar").value(1000))
        .andExpect(jsonPath("$[0].estado").value("pendiente"));
    pedir("/api/cuotas/morosos")
        .andExpect(jsonPath("$[?(@.dni == 42000007)]", empty()));
  }

  @Test
  void rf23ElMorosoMuestraElTotalAdeudadoConInteres() throws Exception {
    alumno(42000008, "Deudor");
    configurar("2020-07", "2020-07-13", "1000", "10");
    configurar("2020-08", "2020-08-13", "2000", "5");
    generar("2020-07");
    generar("2020-08");

    pedir("/api/cuotas/morosos")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[?(@.dni == 42000008)].nombre", contains("Deudor")))
        .andExpect(jsonPath("$[?(@.dni == 42000008)].cuotasVencidas", contains(2)))
        .andExpect(jsonPath("$[?(@.dni == 42000008)].totalAdeudado", contains(3200.0)));
  }

  @Test
  void rf22LaCuotaPagadaMuestraSuPagoYUnaAnulacionLaDevuelveALaDeuda() throws Exception {
    alumno(42000009, "Pagador");
    configurar("2020-09", "2020-09-13", "1000", "10");
    generar("2020-09");
    jdbc.update("INSERT INTO pago (fecha, monto, nombre_medio_pago, dni_admin, dni_alumno, periodo) "
        + "VALUES ('2020-09-05', 1000, 'efectivo', ?, 42000009, '2020-09-01')", ADMIN);

    pedir("/api/alumnos/42000009/cuotas")
        .andExpect(jsonPath("$[0].estado").value("pagada"))
        .andExpect(jsonPath("$[0].montoAPagar").value(1000))
        .andExpect(jsonPath("$[0].fechaPago").value("2020-09-05"))
        .andExpect(jsonPath("$[0].idPago").isNumber());
    pedir("/api/cuotas/morosos")
        .andExpect(jsonPath("$[?(@.dni == 42000009)]", empty()));

    // RN-32: un pago anulado no cancela la cuota.
    jdbc.update("UPDATE pago SET fecha_anulacion = '2020-09-06', motivo = 'error', "
        + "dni_admin_anula = ? WHERE dni_alumno = 42000009", ADMIN);

    pedir("/api/alumnos/42000009/cuotas")
        .andExpect(jsonPath("$[0].estado").value("vencida"))
        .andExpect(jsonPath("$[0].montoAPagar").value(1100))
        .andExpect(jsonPath("$[0].idPago").isEmpty());
  }

  @Test
  void rf18LaGeneracionAutomaticaTomaElValorVigenteYNoSeRepite() throws Exception {
    YearMonth mes = YearMonth.now(ZoneId.of("America/Argentina/Cordoba"));
    YearMonth anterior = mes.minusMonths(1);
    alumno(42000010, "Automatico");
    configurar(anterior.toString(), anterior.atDay(13).toString(), "1800", "10")
        .andExpect(status().isOk());

    assertEquals(1, cuotaService.generarDelMes());
    assertEquals(0, cuotaService.generarDelMes());

    String periodo = mes.atDay(1).toString();
    assertEquals(1, contar("cuota WHERE dni_alumno = 42000010 AND periodo = '" + periodo + "'"));
    assertEquals(1800, jdbc.queryForObject(
        "SELECT importe FROM periodo WHERE periodo = ?", Integer.class, periodo));
    assertEquals(mes.atDay(13).toString(), jdbc.queryForObject(
        "SELECT fecha_vencimiento FROM periodo WHERE periodo = ?", LocalDate.class, periodo)
        .toString());
  }

  @Test
  void losDatosInvalidosSeRechazan() throws Exception {
    configurar("2020-10", "2020-09-30", "1000", "10").andExpect(status().isBadRequest());
    configurar("2020-10", "2020-10-13", "0", "10").andExpect(status().isBadRequest());
    configurar("2020-10", "2020-10-13", "1000", "101").andExpect(status().isBadRequest());
    configurar("octubre", "2020-10-13", "1000", "10").andExpect(status().isBadRequest());

    generar("2020-11").andExpect(status().isNotFound());
    generar(YearMonth.now().plusMonths(2).toString()).andExpect(status().isNotFound());
    pedir("/api/alumnos/99999999/cuotas").andExpect(status().isNotFound());
  }

  @Test
  void unMesFuturoSeConfiguraPeroNoSeGenera() throws Exception {
    String futuro = YearMonth.now().plusMonths(3).toString();
    configurar(futuro, futuro + "-13", "1000", "10").andExpect(status().isOk());

    generar(futuro).andExpect(status().isBadRequest());

    assertEquals(0, contar("cuota WHERE periodo = '" + futuro + "-01'"));
  }

  private void alumno(long dni, String nombre) {
    jdbc.update("INSERT INTO persona VALUES (?, ?, 'Prueba')", dni, nombre);
    jdbc.update("INSERT INTO alumno (dni, fecha_nacimiento, direccion) "
        + "VALUES (?, '2015-01-01', 'Calle 1')", dni);
    jdbc.update("INSERT INTO permanencia (dni_alumno, fecha_alta, dni_admin_alta) "
        + "VALUES (?, '2019-01-01', ?)", dni, ADMIN);
  }

  private void darDeBaja(long dni) {
    jdbc.update("UPDATE permanencia SET fecha_baja = CURDATE(), dni_admin_baja = ? "
        + "WHERE dni_alumno = ?", ADMIN, dni);
  }

  private ResultActions configurar(String periodo, String vencimiento, String importe,
      String interes) throws Exception {
    return mvc.perform(put("/api/periodos/" + periodo)
        .with(administrador())
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {"importe": %s, "fechaVencimiento": "%s", "porcentajeInteres": %s}"""
            .formatted(importe, vencimiento, interes)));
  }

  private ResultActions generar(String periodo) throws Exception {
    return mvc.perform(post("/api/periodos/" + periodo + "/cuotas").with(administrador()));
  }

  private ResultActions pedir(String ruta) throws Exception {
    return mvc.perform(get(ruta).with(administrador()));
  }

  private int contar(String desde) {
    return jdbc.queryForObject("SELECT COUNT(*) FROM " + desde, Integer.class);
  }

  private static RequestPostProcessor administrador() {
    UsuarioAutenticado usuario = new UsuarioAutenticado(ADMIN, Rol.ADMINISTRADOR, false);
    return authentication(new UsernamePasswordAuthenticationToken(usuario, null,
        List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))));
  }
}
