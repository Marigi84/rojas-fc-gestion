package com.rojasfc.gestion.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Lee el JWT del encabezado Authorization y, si es valido, deja autenticado al
 * usuario para esta solicitud. Sin token o con un token invalido la solicitud
 * sigue como anonima y SecurityConfig decide si la rechaza.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  /** Unico permiso de quien todavia no cambio la contrasena provisoria. */
  public static final String CAMBIO_CONTRASENA_PENDIENTE = "CAMBIO_CONTRASENA_PENDIENTE";

  private static final String PREFIJO = "Bearer ";

  private final JwtService jwtService;

  public JwtAuthenticationFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    String encabezado = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (encabezado != null && encabezado.startsWith(PREFIJO)) {
      try {
        UsuarioAutenticado usuario = jwtService.validar(encabezado.substring(PREFIJO.length()));
        // Con contrasena provisoria solo se habilita el cambio de contrasena (RNF-01).
        String permiso = usuario.contrasenaTemporal()
            ? CAMBIO_CONTRASENA_PENDIENTE
            : "ROLE_" + usuario.rol().name();
        var autenticacion = new UsernamePasswordAuthenticationToken(
            usuario, null, List.of(new SimpleGrantedAuthority(permiso)));
        SecurityContextHolder.getContext().setAuthentication(autenticacion);
      } catch (JwtException | IllegalArgumentException e) {
        SecurityContextHolder.clearContext();
      }
    }
    chain.doFilter(request, response);
  }
}
