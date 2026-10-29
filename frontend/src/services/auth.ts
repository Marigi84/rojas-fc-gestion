import type { Sesion } from '../stores/sesion'
import { api } from './apiClient'

// Servicio del Módulo 1 (autenticación) sobre la API REST del backend.
// Ver backend/README.md, sección "Autenticación".

export interface Ingreso {
  dni: number
  contrasena: string
}

export interface CambioContrasena {
  actual: string
  nueva: string
}

export interface ConfiguracionInicial {
  dni: number
  nombre: string
  apellido: string
  email: string
  contrasena: string
}

// Clave de TanStack Query del estado de la configuración inicial.
export const CLAVE_CONFIGURACION = ['configuracion-inicial'] as const

export const authApi = {
  ingresar: (datos: Ingreso) => api.post<Sesion>('/auth/login', datos),
  cambiarContrasena: (datos: CambioContrasena) =>
    api.put<Sesion>('/auth/contrasena', datos),
  estadoConfiguracion: () =>
    api.get<{ configurado: boolean }>('/configuracion-inicial'),
  configurar: (datos: ConfiguracionInicial) =>
    api.post<Sesion>('/configuracion-inicial', datos),
}
