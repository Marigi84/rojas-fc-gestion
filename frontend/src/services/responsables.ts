import { api } from './apiClient'
import type { AlumnoDetalle, ResponsableAlta, Vinculo } from './alumnos'

// Servicio del Módulo 2 (responsables) sobre la API REST del backend.
// Ver backend/README.md, sección "Responsables".

export interface AlumnoAsociado {
  dni: number
  nombre: string
  apellido: string
  vinculo: Vinculo
  activo: boolean
}

export interface ResponsableDetalle {
  dni: number
  nombre: string
  apellido: string
  telefono: string
  alumnos: AlumnoAsociado[]
}

export interface ModificacionResponsable {
  nombre: string
  apellido: string
  telefono: string
}

export interface AsociacionAlumno {
  dniAlumno: number
  vinculo: Vinculo
}

export const claveResponsable = (dni: number) => ['responsable', dni] as const

export const responsablesApi = {
  consultar: (dni: number) =>
    api.get<ResponsableDetalle>(`/responsables/${dni}`),
  modificar: (dni: number, datos: ModificacionResponsable) =>
    api.put<ResponsableDetalle>(`/responsables/${dni}`, datos),
  asociar: (dni: number, datos: AsociacionAlumno) =>
    api.post<ResponsableDetalle>(`/responsables/${dni}/alumnos`, datos),
  desvincular: (dni: number, dniAlumno: number) =>
    api.delete<void>(`/responsables/${dni}/alumnos/${dniAlumno}`),
  restablecerContrasena: (dni: number, contrasenaProvisoria: string) =>
    api.put<void>(`/responsables/${dni}/contrasena`, { contrasenaProvisoria }),
  // El backend acepta una lista para poder cargar mas de un responsable en la
  // misma operacion; la pantalla de este issue carga uno por vez.
  agregarAAlumno: (dniAlumno: number, responsables: ResponsableAlta[]) =>
    api.post<AlumnoDetalle>(`/alumnos/${dniAlumno}/responsables`, {
      responsables,
    }),
}
