import { api } from './apiClient'

// Servicio de administradores (Módulo 1, RF-51) sobre la API REST del backend.
// Ver backend/README.md, sección "Autenticación".

export interface AdministradorResumen {
  dni: number
  nombre: string
  apellido: string
  email: string
  contrasenaTemporal: boolean
}

// Nombre y apellido solo se envían para una persona nueva (RN-01).
export interface AltaAdministrador {
  dni: number
  nombre?: string
  apellido?: string
  email: string
  contrasenaProvisoria: string
}

// Clave de TanStack Query del listado de administradores.
export const CLAVE_ADMINISTRADORES = ['administradores'] as const

export const administradoresApi = {
  listar: () => api.get<AdministradorResumen[]>('/administradores'),
  registrar: (datos: AltaAdministrador) =>
    api.post<AdministradorResumen>('/administradores', datos),
}
