import { api, ErrorApi } from './apiClient'

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

// Nombre y apellido solo se envían para una persona nueva; teléfono y
// contraseña provisoria, solo para quien todavía no es responsable.
export interface ResponsableAlta {
  dni: number
  nombre?: string
  apellido?: string
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

export interface FiltrosAlumnos {
  busqueda: string
  anioNacimiento: string
  estado: string
  moroso: string
  pagina: number
  tamano: number
}

export interface AlumnoListadoItem {
  dni: number
  nombre: string
  apellido: string
  anioNacimiento: number
  estado: 'activo' | 'inactivo'
  moroso: boolean
}

export interface PaginaAlumnos {
  contenido: AlumnoListadoItem[]
  pagina: number
  tamano: number
  total: number
  totalPaginas: number
}

// Clave de TanStack Query del detalle de un alumno.
export const claveAlumno = (dni: number) => ['alumno', dni] as const

// Prefijo de las claves de los listados de alumnos (#41). Las altas, bajas,
// reactivaciones y modificaciones lo invalidan para que el listado se actualice.
export const CLAVE_LISTADOS_ALUMNOS = ['alumnos'] as const

export const claveListadoAlumnos = (filtros: FiltrosAlumnos) =>
  [...CLAVE_LISTADOS_ALUMNOS, filtros] as const

export const alumnosApi = {
  listar: (filtros: FiltrosAlumnos) => {
    const parametros = new URLSearchParams()
    if (filtros.busqueda.trim()) {
      parametros.set('busqueda', filtros.busqueda.trim())
    }
    if (filtros.anioNacimiento.trim()) {
      parametros.set('anioNacimiento', filtros.anioNacimiento.trim())
    }
    parametros.set('estado', filtros.estado)
    if (filtros.moroso) {
      parametros.set('moroso', filtros.moroso)
    }
    parametros.set('pagina', String(filtros.pagina))
    parametros.set('tamano', String(filtros.tamano))
    return api.get<PaginaAlumnos>(`/alumnos?${parametros.toString()}`)
  },
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

// Persona registrada con ese DNI, o null si no existe o el DNI no es válido.
export async function buscarPersonaSiExiste(
  dni: string,
): Promise<PersonaResumen | null> {
  if (!/^[0-9]{1,10}$/.test(dni)) return null
  try {
    return await personasApi.buscar(Number(dni))
  } catch (error) {
    if (error instanceof ErrorApi && error.estado === 404) return null
    throw error
  }
}
