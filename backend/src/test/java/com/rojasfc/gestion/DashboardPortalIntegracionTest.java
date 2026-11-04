package com.rojasfc.gestion;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rojasfc.gestion.security.Rol;
import com.rojasfc.gestion.security.UsuarioAutenticado;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/**
 * Dashboard y Portal del Responsable contra MySQL, porque leen de las vistas y
 * de pago. Hay dos familias: la de Laura (dos alumnos) y la de Jorge (uno). Se
 * activa con TESTS_MYSQL=true (ver backend/README.md).
 */
@SpringBootTest
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "TESTS_MYSQL", matches = "true")
class DashboardPortalIntegracionTest {

  private static final long ADMIN = 20111222L;
  private static final long LAURA = 30200001L;
  private static final long JORGE = 30200002L;
  private static final long MATEO = 43000001L;
  private static final long SOFIA = 43000002L;
  private static final long LUCIA = 43000003L;

  @Autowired
  private MockMvc mvc;

  @Autowired
  private JdbcTemplate jdbc;

  @DynamicPropertySource
  static void baseDePrueba(DynamicPropertyRegistry registro) throws IOException, SQLException {
    AlumnoIntegracionTest.baseDePrueba(registro);
  }

  @BeforeEach
  void datos() {
    if (jdbc.queryForObject("SELECT COUNT(*) FROM alumno WHERE dni = ?", Integer.class, MATEO) > 0) {
      return;
    }
    responsable(LAURA, "Laura");
    responsable(JORGE, "Jorge");
    alumno(MATEO, "Mateo", "2015-03-10", LAURA);
    alumno(SOFIA, "Sofia", "2016-07-01", LAURA);
    alumno(LUCIA, "Lucia", "2016-02-02", JORGE);

    // Enero 2026: vencida para todos. Mateo la paga; a Lucia se le anula el pago.
    jdbc.update("INSERT INTO periodo VALUES ('2026-01-01', 1000, '2026-01-13', 10, ?, '2026-01-01')", ADMIN);
    for (long dni : List.of(MATEO, SOFIA, LUCIA)) {
      jdbc.update("INSERT INTO cuota VALUES (?, '2026-01-01', '2026-01-01')", dni);
    }
    pagoDeHoy(MATEO, 1100);
    pagoDeHoy(LUCIA, 1100);
    jdbc.update("UPDATE pago SET fecha_anulacion = CURDATE(), motivo = 'error', dni_admin_anula = ? "
        + "WHERE dni_alumno = ?", ADMIN, LUCIA);
    jdbc.update("INSERT INTO recibo (id_pago, fecha_emision) "
        + "SELECT id_pago, fecha FROM pago WHERE dni_alumno = ?", MATEO);
  }

  @Test
  void rf45a48ElDashboardMuestraLosIndicadores() throws Exception {
    mvc.perform(get("/api/dashboard").with(sesion(ADMIN, Rol.ADMINISTRADOR)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.alumnosActivos").value(3))
        .andExpect(jsonPath("$.alumnosPorCategoria[0].categoria").value(2016))
        .andExpect(jsonPath("$.alumnosPorCategoria[0].cantidad").value(2))
        .andExpect(jsonPath("$.alumnosPorCategoria[1].categoria").value(2015))
        .andExpect(jsonPath("$.alumnosPorCategoria[1].cantidad").value(1))
        .andExpect(jsonPath("$.alumnosMorosos").value(2))
        .andExpect(jsonPath("$.mes").value(LocalDate.now(ZoneId.of("America/Argentina/Cordoba"))
            .toString().substring(0, 7)));
  }

  @Test
  void rn32LaRecaudacionNoCuentaLosPagosAnulados() throws Exception {
    mvc.perform(get("/api/dashboard").with(sesion(ADMIN, Rol.ADMINISTRADOR)))
        .andExpect(jsonPath("$.recaudacionDelMes").value(1100));
  }

  @Test
  void rf52UnResponsableNoVeElDashboard() throws Exception {
    mvc.perform(get("/api/dashboard").with(sesion(LAURA, Rol.RESPONSABLE)))
        .andExpect(status().isForbidden());
  }

  @Test
  void rf12ElResponsableVeSoloSusAlumnos() throws Exception {
    mvc.perform(get("/api/portal/alumnos").with(sesion(LAURA, Rol.RESPONSABLE)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[*].nombre", contains("Mateo", "Sofia")));
    mvc.perform(get("/api/portal/alumnos").with(sesion(JORGE, Rol.RESPONSABLE)))
        .andExpect(jsonPath("$[*].nombre", contains("Lucia")));
  }

  @Test
  void rf13ElResponsableVeCuotasEHistorialDePagos() throws Exception {
    mvc.perform(get("/api/portal/alumnos").with(sesion(LAURA, Rol.RESPONSABLE)))
        .andExpect(jsonPath("$[0].moroso").value(false))
        .andExpect(jsonPath("$[0].cuotas[0].periodo").value("2026-01"))
        .andExpect(jsonPath("$[0].cuotas[0].estado").value("pagada"))
        .andExpect(jsonPath("$[0].pagos[0].monto").value(1100))
        .andExpect(jsonPath("$[0].pagos[0].medioPago").value("efectivo"))
        .andExpect(jsonPath("$[0].pagos[0].periodo").value("2026-01"))
        .andExpect(jsonPath("$[0].pagos[0].anulado").value(false))
        .andExpect(jsonPath("$[1].moroso").value(true))
        .andExpect(jsonPath("$[1].cuotas[0].estado").value("vencida"))
        .andExpect(jsonPath("$[1].cuotas[0].montoAPagar").value(1100))
        .andExpect(jsonPath("$[1].pagos", empty()));
  }

  @Test
  void rf28UnPagoAnuladoFiguraComoAnuladoEnElHistorial() throws Exception {
    mvc.perform(get("/api/portal/alumnos").with(sesion(JORGE, Rol.RESPONSABLE)))
        .andExpect(jsonPath("$[0].pagos[0].anulado").value(true))
        .andExpect(jsonPath("$[0].cuotas[0].estado").value("vencida"));
  }

  @Test
  void rf14ElResponsableVeLosRecibosYSiEstanAnulados() throws Exception {
    mvc.perform(get("/api/portal/alumnos").with(sesion(LAURA, Rol.RESPONSABLE)))
        .andExpect(jsonPath("$[0].recibos", hasSize(1)))
        .andExpect(jsonPath("$[0].recibos[0].monto").value(1100))
        .andExpect(jsonPath("$[0].recibos[0].periodo").value("2026-01"))
        .andExpect(jsonPath("$[0].recibos[0].anulado").value(false))
        .andExpect(jsonPath("$[1].recibos", empty()));
  }

  @Test
  void rnf03QuienNoTieneAlumnosNoVeNada() throws Exception {
    mvc.perform(get("/api/portal/alumnos").with(sesion(ADMIN, Rol.ADMINISTRADOR)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", empty()));
  }

  private void responsable(long dni, String nombre) {
    jdbc.update("INSERT INTO persona VALUES (?, ?, 'Prueba')", dni, nombre);
    jdbc.update("INSERT INTO responsable (dni, telefono, contrasena_hash, contrasena_temporal) "
        + "VALUES (?, '351', 'x', FALSE)", dni);
  }

  private void alumno(long dni, String nombre, String nacimiento, long responsable) {
    jdbc.update("INSERT INTO persona VALUES (?, ?, 'Prueba')", dni, nombre);
    jdbc.update("INSERT INTO alumno (dni, fecha_nacimiento, direccion) VALUES (?, ?, 'Calle 1')",
        dni, nacimiento);
    jdbc.update("INSERT INTO permanencia (dni_alumno, fecha_alta, dni_admin_alta) "
        + "VALUES (?, '2025-01-01', ?)", dni, ADMIN);
    jdbc.update("INSERT INTO responsable_alumno (dni_responsable, dni_alumno, vinculo) "
        + "VALUES (?, ?, 'madre')", responsable, dni);
  }

  private void pagoDeHoy(long dni, int monto) {
    jdbc.update("INSERT INTO pago (fecha, monto, nombre_medio_pago, dni_admin, dni_alumno, periodo) "
        + "VALUES (CURDATE(), ?, 'efectivo', ?, ?, '2026-01-01')", monto, ADMIN, dni);
  }

  private static RequestPostProcessor sesion(long dni, Rol rol) {
    UsuarioAutenticado usuario = new UsuarioAutenticado(dni, rol, false);
    return authentication(new UsernamePasswordAuthenticationToken(usuario, null,
        List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()))));
  }
}
