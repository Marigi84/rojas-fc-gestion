package com.rojasfc.gestion.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Alta de otro Administrador (RF-51). Nombre y apellido se piden solo si el
 * DNI no es una persona ya registrada (RN-01). La contrasena la asigna quien
 * lo registra y queda provisoria (RNF-01).
 */
public record AdministradorAltaRequest(
    @NotNull @Positive @Max(4294967295L) Long dni,
    @Size(max = 60) String nombre,
    @Size(max = 60) String apellido,
    @NotBlank @Email @Size(max = 120) String email,
    @NotBlank @Size(min = 8, max = 72) String contrasenaProvisoria
) {
}
