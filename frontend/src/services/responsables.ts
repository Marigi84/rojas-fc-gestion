import { api } from './apiClient'

// Servicio de responsables sobre la API REST del backend. Por ahora, el
// restablecimiento de la contraseña (RF-15); la gestión de responsables
// (RF-06 a RF-10) se suma en el issue #40.

export const responsablesApi = {
  restablecerContrasena: (dni: number, contrasenaProvisoria: string) =>
    api.put<void>(`/responsables/${dni}/contrasena`, { contrasenaProvisoria }),
}
