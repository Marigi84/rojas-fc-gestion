import { api } from './apiClient'

// Servicio del Módulo 4 (cuotas) sobre la API REST del backend.
// Ver backend/README.md, sección "Cuotas".

export interface ConfiguracionPeriodo {
  importe: number
  fechaVencimiento: string
  porcentajeInteres: number
}

export interface Periodo {
  // Mes en formato AAAA-MM.
  periodo: string
  importe: number
  fechaVencimiento: string
  porcentajeInteres: number
  fechaConfiguracion: string
  cuotasGeneradas: number
}

export interface GeneracionCuotas {
  periodo: string
  cuotasGeneradas: number
}

export type EstadoCuota = 'pagada' | 'pendiente' | 'vencida'

export interface Cuota {
  periodo: string
  importe: number
  fechaVencimiento: string
  // Incluye el interés por mora si corresponde (RF-21).
  montoAPagar: number
  estado: EstadoCuota
  idPago: number | null
  fechaPago: string | null
}

export interface AlumnoMoroso {
  dni: number
  nombre: string
  apellido: string
  estado: 'activo' | 'inactivo'
  cuotasVencidas: number
  totalAdeudado: number
}

export const CLAVE_PERIODOS = ['periodos'] as const
export const CLAVE_MOROSOS = ['morosos'] as const

// Prefijo de las claves de las cuotas de cada alumno.
export const CLAVE_CUOTAS = ['cuotas'] as const
export const claveCuotasAlumno = (dni: number) =>
  [...CLAVE_CUOTAS, dni] as const

export const cuotasApi = {
  listarPeriodos: () => api.get<Periodo[]>('/periodos'),
  configurarPeriodo: (mes: string, datos: ConfiguracionPeriodo) =>
    api.put<Periodo>(`/periodos/${mes}`, datos),
  generar: (mes: string) =>
    api.post<GeneracionCuotas>(`/periodos/${mes}/cuotas`),
  deAlumno: (dni: number) => api.get<Cuota[]>(`/alumnos/${dni}/cuotas`),
  morosos: () => api.get<AlumnoMoroso[]>('/cuotas/morosos'),
}
