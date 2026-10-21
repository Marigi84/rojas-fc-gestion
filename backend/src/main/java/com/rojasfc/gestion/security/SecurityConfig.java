package com.rojasfc.gestion.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Reglas de acceso de la API (RF-52, RNF-02). La validacion se hace siempre en
 * el backend, aunque el frontend oculte opciones.
 *
 * Convencion de rutas:
 * - /api/health, /api/auth/login y /api/configuracion-inicial: publicas (la
 *   configuracion inicial se rechaza sola cuando ya hay un Administrador).
 * - /api/auth/contrasena: cualquier usuario autenticado, incluso con
 *   contrasena provisoria.
 * - /api/portal/**: Responsable y Administrador (el Administrador ya incluye
 *   lo que consulta un Responsable, arquitectura-inicial.md, seccion 8).
 * - Todo lo demas: solo Administrador.
 */
@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http, JwtAuthenticationFilter jwtFilter, ApiErrorWriter errorWriter)
      throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .httpBasic(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers("/error").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/health").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
            .requestMatchers("/api/configuracion-inicial").permitAll()
            .requestMatchers("/api/auth/contrasena").authenticated()
            .requestMatchers("/api/portal/**").hasAnyRole("RESPONSABLE", "ADMINISTRADOR")
            .anyRequest().hasRole("ADMINISTRADOR"))
        .exceptionHandling(e -> e
            .authenticationEntryPoint((request, response, ex) -> errorWriter.escribir(
                request, response, HttpStatus.UNAUTHORIZED,
                "Hay que iniciar sesión para acceder a este recurso"))
            .accessDeniedHandler((request, response, ex) -> errorWriter.escribir(
                request, response, HttpStatus.FORBIDDEN,
                tieneCambioPendiente()
                    ? "Antes de continuar hay que cambiar la contraseña provisoria"
                    : "Tu usuario no tiene permiso para esta operación")))
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  private static boolean tieneCambioPendiente() {
    Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
    return autenticacion != null && autenticacion.getAuthorities().stream()
        .anyMatch(a -> JwtAuthenticationFilter.CAMBIO_CONTRASENA_PENDIENTE.equals(a.getAuthority()));
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
