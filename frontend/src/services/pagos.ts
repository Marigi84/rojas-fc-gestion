import { api } from './apiClient'

// Servicio del Módulo 5 (pagos y recibos) sobre la API REST del backend.

export type MedioPago = 'efectivo' | 'transferencia'

export interface RegistroPago {
  dniAlumno: number
  // Mes de la cuota, en formato AAAA-MM.
  periodo: string
  medioPago: MedioPago
}

export interface Pago {
  idPago: number
  fecha: string
  monto: number
  medioPago: string
  periodo: string | null
  fechaAnulacion: string | null
  motivo: string | null
  numeroRecibo: number | null
}

export interface Recibo {
  numeroRecibo: number
  idPago: number
  fechaEmision: string
  dniAlumno: number
  alumno: string
  periodo: string | null
  importe: number
  medioPago: string
  anulado: boolean
}

// Prefijo de las claves del historial de pagos de cada alumno.
export const CLAVE_PAGOS = ['pagos'] as const
export const clavePagosAlumno = (dni: number) => [...CLAVE_PAGOS, dni] as const
export const claveRecibo = (numeroRecibo: number) =>
  ['recibo', numeroRecibo] as const

export const pagosApi = {
  registrar: (datos: RegistroPago) => api.post<Pago>('/pagos', datos),
  anular: (idPago: number, motivo: string) =>
    api.post<void>(`/pagos/${idPago}/anulacion`, { motivo }),
  deAlumno: (dni: number) => api.get<Pago[]>(`/alumnos/${dni}/pagos`),
  recibo: (numeroRecibo: number) => api.get<Recibo>(`/recibos/${numeroRecibo}`),
}
