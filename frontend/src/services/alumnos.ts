import { api } from './apiClient'

// Servicio del Módulo 2 (alumnos) sobre la API REST del backend.
// Ver backend/README.md, sección "Alumnos".

export type Vinculo = 'MADRE' | 'PADRE' | 'TUTOR' | 'OTRO'

export const VINCULOS: { valor: Vinculo; etiqueta: string }[] = [
  { valor: 'MADRE', etiqueta: 'Madre' },
  { valor: 'PADRE', etiqueta: 'Padre' },
  { valor: 'TUTOR', etiqueta: 'Tutor' },
  { valor: 'OTRO', etiqueta: 'Otro' },
]

export function etiquetaVinculo(vinculo: Vinculo): string {
  return VINCULOS.find((v) => v.valor === vinculo)?.etiqueta ?? vinculo
}

// Teléfono y contraseña provisoria solo se envían para responsables nuevos.
export interface ResponsableAlta {
  dni: number
  nombre: string
  apellido: string
  vinculo: Vinculo
  telefono?: string
  contrasenaProvisoria?: string
}

export interface AltaAlumno {
  dni: number
  nombre: string
  apellido: string
  fechaNacimiento: string
  direccion: string
  responsables: ResponsableAlta[]
}

export interface ModificacionAlumno {
  nombre: string
  apellido: string
  fechaNacimiento: string
  direccion: string
}

export interface AlumnoDetalle {
  dni: number
  nombre: string
  apellido: string
  fechaNacimiento: string
  direccion: string
  categoria: number
  estado: 'activo' | 'inactivo'
  moroso: boolean
  permanencias: { fechaAlta: string; fechaBaja: string | null }[]
  responsables: {
    dni: number
    nombre: string
    apellido: string
    telefono: string
    vinculo: Vinculo
  }[]
}

export interface PersonaResumen {
  dni: number
  nombre: string
  apellido: string
  alumno: boolean
  responsable: boolean
  administrador: boolean
}

// Clave de TanStack Query del detalle de un alumno.
export const claveAlumno = (dni: number) => ['alumno', dni] as const

export const alumnosApi = {
  registrar: (datos: AltaAlumno) => api.post<AlumnoDetalle>('/alumnos', datos),
  consultar: (dni: number) => api.get<AlumnoDetalle>(`/alumnos/${dni}`),
  modificar: (dni: number, datos: ModificacionAlumno) =>
    api.put<AlumnoDetalle>(`/alumnos/${dni}`, datos),
  darDeBaja: (dni: number) => api.post<AlumnoDetalle>(`/alumnos/${dni}/baja`),
  reactivar: (dni: number) =>
    api.post<AlumnoDetalle>(`/alumnos/${dni}/reactivacion`),
}

export const personasApi = {
  buscar: (dni: number) => api.get<PersonaResumen>(`/personas/${dni}`),
}
