package com.rojasfc.gestion.controller;

import java.sql.Connection;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<Map<String, Object>> health() {
    boolean databaseOk = checkDatabase();
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("status", databaseOk ? "ok" : "error");
    body.put("timestamp", Instant.now().toString());
    body.put("database", databaseOk ? "ok" : "error");

    return databaseOk
        ? ResponseEntity.ok(body)
        : ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
  }

  private boolean checkDatabase() {
    try (Connection connection = dataSource.getConnection()) {
      return connection.isValid(2);
    } catch (Exception e) {
      return false;
    }
  }
}
