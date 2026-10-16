package com.rojasfc.gestion.controller;

import java.sql.Connection;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

  private final DataSource dataSource;

  public HealthController(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @GetMapping
  public Map<String, Object> health() {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("status", "ok");
    body.put("timestamp", Instant.now().toString());
    body.put("database", checkDatabase());
    return body;
  }

  private String checkDatabase() {
    try (Connection connection = dataSource.getConnection()) {
      return connection.isValid(2) ? "ok" : "sin respuesta";
    } catch (Exception e) {
      return "error: " + e.getMessage();
    }
  }
}
