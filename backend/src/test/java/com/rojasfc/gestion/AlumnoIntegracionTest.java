package com.rojasfc.gestion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rojasfc.gestion.security.Rol;
import com.rojasfc.gestion.security.UsuarioAutenticado;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
 * Gestion de alumnos contra MySQL real, para las reglas que controla la base
 * (triggers de RN-03, atomicidad del alta). Usa una base aparte, rojasfc_test,
 * creada desde database/schema.sql. Se activa con TESTS_MYSQL=true y las
 * mismas variables DB_* del backend (ver backend/README.md).
 */
@SpringBootTest
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "TESTS_MYSQL", matches = "true")
class AlumnoIntegracionTest {

  private static final long ADMIN = 20111222L;

  @Autowired
  private MockMvc mvc;

  @Autowired
  private JdbcTemplate jdbc;

  @DynamicPropertySource
  static void baseDePrueba(DynamicPropertyRegistry registro) throws IOException, SQLException {
    String servidor = "jdbc:mysql://" + variable("DB_HOST", "localhost") + ":"
        + variable("DB_PORT", "3306") + "/";
    String opciones = "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    String usuario = System.getenv("DB_USERNAME");
    String clave = variable("DB_PASSWORD", "");
    try (Connection conexion = DriverManager.getConnection(servidor + opciones, usuario, clave);
        Statement sql = conexion.createStatement()) {
      sql.execute("DROP DATABASE IF EXISTS rojasfc_test");
      sql.execute("CREATE DATABASE rojasfc_test CHARACTER SET utf8mb4");
      sql.execute("USE rojasfc_test");
      for (String sentencia : sentencias(Path.of("../database/schema.sql"))) {
        sql.execute(sentencia);
      }
      sql.execute("INSERT INTO persona VALUES (" + ADMIN + ", 'Marta', 'Admin')");
      sql.execute("INSERT INTO administrador VALUES (" + ADMIN + ", 'marta@rojasfc.test', 'x', FALSE)");
    }
    registro.add("spring.datasource.url", () -> servidor + "rojasfc_test" + opciones);
    registro.add("spring.datasource.username", () -> usuario);
    registro.add("spring.datasource.password", () -> clave);
    registro.add("app.jwt.secret", () -> "clave-de-prueba-de-integracion-con-mas-de-32-caracteres");
  }

  @Test
  void elAltaGuardaAlumnoPermanenciaResponsableYVinculo() throws Exception {
    alta(alumno(41000001, "Mateo", responsableNuevo(31000001)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.estado").value("activo"))
        .andExpect(jsonPath("$.responsables[0].dni").value(31000001));

    assertEquals(1, contar("permanencia WHERE dni_alumno = 41000001 AND fecha_baja IS NULL"));
    assertEquals(1, contar("responsable WHERE dni = 31000001 AND contrasena_temporal = 1"));
    assertEquals(1, contar("responsable_alumno WHERE dni_responsable = 31000001 AND dni_alumno = 41000001"));
  }

  @Test
  void unAltaFallidaNoDejaNadaAMedias() throws Exception {
    alta(alumno(41000002, "Tomás", """
        {"dni": 31000002, "nombre": "Ana", "apellido": "Ruiz", "vinculo": "TUTOR",
         "contrasenaProvisoria": "provisoria123"}"""))
        .andExpect(status().isBadRequest());

    assertEquals(0, contar("persona WHERE dni IN (41000002, 31000002)"));
  }

  @Test
  void rn03UnResponsableNoPuedeSerAlumnoActivo() throws Exception {
    alta(alumno(41000003, "Sofía", responsableNuevo(31000003))).andExpect(status().isCreated());

    alta(alumno(31000003, "Laura", responsableNuevo(31000004)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error").value("Regla de negocio"))
        .andExpect(jsonPath("$.message").value("Un responsable no puede ser alumno activo"));

    assertEquals(0, contar("alumno WHERE dni = 31000003"));
    assertEquals(0, contar("persona WHERE dni = 31000004"));
  }

  @Test
  void rn03UnAlumnoActivoNoPuedeSerResponsable() throws Exception {
    alta(alumno(41000005, "Bruno", responsableNuevo(31000005))).andExpect(status().isCreated());

    alta(alumno(41000006, "Emma", responsableNuevo(41000005)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("Un alumno activo no puede ser responsable"));

    assertEquals(0, contar("responsable WHERE dni = 41000005"));
    assertEquals(0, contar("persona WHERE dni = 41000006"));
  }

  @Test
  void unAlumnoExistenteSeInformaAunqueLosResponsablesTenganErrores() throws Exception {
    alta(alumno(41000007, "Lucía", responsableNuevo(31000007))).andExpect(status().isCreated());

    String repetido = "{\"dni\": 31000007, \"vinculo\": \"MADRE\"}";
    alta(alumno(41000007, "Lucía", repetido + ", " + repetido))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error").value("Alumno ya registrado"));
  }

  @Test
  void unResponsableExistenteSeAsociaSoloConDniYVinculo() throws Exception {
    alta(alumno(41000008, "Mía", responsableNuevo(31000008))).andExpect(status().isCreated());

    alta(alumno(41000009, "Luis", "{\"dni\": 31000008, \"vinculo\": \"PADRE\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.responsables[0].nombre").value("Nombre31000008"));
  }

  @Test
  void reactivarElMismoDiaDeLaBajaLaDeshace() throws Exception {
    alta(alumno(41000010, "Tomás", responsableNuevo(31000010))).andExpect(status().isCreated());
    accion(41000010, "baja").andExpect(jsonPath("$.estado").value("inactivo"));

    accion(41000010, "reactivacion")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.estado").value("activo"))
        .andExpect(jsonPath("$.permanencias.length()").value(1));
  }

  @Test
  void reactivarDespuesDeUnaBajaAnteriorConservaElHistorial() throws Exception {
    alta(alumno(41000011, "Ana", responsableNuevo(31000011))).andExpect(status().isCreated());
    accion(41000011, "baja");
    jdbc.update("UPDATE permanencia SET fecha_alta = fecha_alta - INTERVAL 30 DAY, "
        + "fecha_baja = fecha_baja - INTERVAL 1 DAY WHERE dni_alumno = 41000011");

    accion(41000011, "reactivacion")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.estado").value("activo"))
        .andExpect(jsonPath("$.permanencias.length()").value(2));
  }

  @Test
  void deshacerLaBajaRespetaRn03SiSeVolvioResponsable() throws Exception {
    alta(alumno(41000012, "Bruno", responsableNuevo(31000012))).andExpect(status().isCreated());
    accion(41000012, "baja");
    // Ya inactivo, pasa a ser responsable de otro alumno.
    alta(alumno(41000013, "Emma", responsableNuevo(41000012))).andExpect(status().isCreated());

    accion(41000012, "reactivacion")
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error").value("Regla de negocio"));
  }

  private ResultActions alta(String cuerpo) throws Exception {
    return mvc.perform(post("/api/alumnos").with(administrador())
        .contentType(MediaType.APPLICATION_JSON).content(cuerpo));
  }

  private ResultActions accion(long dni, String accion) throws Exception {
    return mvc.perform(post("/api/alumnos/" + dni + "/" + accion).with(administrador()));
  }

  private int contar(String desde) {
    Integer cantidad = jdbc.queryForObject("SELECT COUNT(*) FROM " + desde, Integer.class);
    return cantidad == null ? 0 : cantidad;
  }

  private static String alumno(long dni, String nombre, String responsables) {
    return """
        {"dni": %d, "nombre": "%s", "apellido": "Prueba", "fechaNacimiento": "2015-03-10",
         "direccion": "San Martín 123", "responsables": [%s]}"""
        .formatted(dni, nombre, responsables);
  }

  private static String responsableNuevo(long dni) {
    return """
        {"dni": %d, "nombre": "Nombre%d", "apellido": "Prueba", "telefono": "3511234567",
         "vinculo": "MADRE", "contrasenaProvisoria": "provisoria123"}"""
        .formatted(dni, dni);
  }

  private static RequestPostProcessor administrador() {
    UsuarioAutenticado usuario = new UsuarioAutenticado(ADMIN, Rol.ADMINISTRADOR, false);
    return authentication(new UsernamePasswordAuthenticationToken(
        usuario, null, List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))));
  }

  private static String variable(String nombre, String porDefecto) {
    String valor = System.getenv(nombre);
    return valor == null || valor.isBlank() ? porDefecto : valor;
  }

  /** Separa schema.sql en sentencias, respetando los bloques DELIMITER de los triggers. */
  private static List<String> sentencias(Path script) throws IOException {
    List<String> sentencias = new ArrayList<>();
    String delimitador = ";";
    StringBuilder actual = new StringBuilder();
    for (String linea : Files.readAllLines(script, StandardCharsets.UTF_8)) {
      String recortada = linea.strip();
      if (recortada.startsWith("--")) {
        continue;
      }
      if (recortada.toUpperCase().startsWith("DELIMITER ")) {
        delimitador = recortada.substring("DELIMITER ".length()).strip();
        continue;
      }
      actual.append(linea).append('\n');
      if (recortada.endsWith(delimitador)) {
        String sentencia = actual.toString().strip();
        sentencia = sentencia.substring(0, sentencia.length() - delimitador.length()).strip();
        if (!sentencia.isEmpty()) {
          sentencias.add(sentencia);
        }
        actual.setLength(0);
      }
    }
    return sentencias;
  }
}
