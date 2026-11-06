import { api } from './apiClient'
import type { Cuota } from './cuotas'
import type { Recibo } from './pagos'

// Servicio del Módulo 3 (Portal del Responsable). Ver backend/README.md, sección
// "Portal del Responsable". Los datos son siempre los del usuario de la sesión.

export interface PagoPortal {
  idPago: number
  fecha: string
  monto: number
  medioPago: string
  // Mes en formato AAAA-MM, o null si el pago no es de una cuota.
  periodo: string | null
  anulado: boolean
}

export interface ReciboPortal {
  numeroRecibo: number
  fechaEmision: string
  monto: number
  periodo: string | null
  anulado: boolean
}

export interface AlumnoPortal {
  dni: number
  nombre: string
  apellido: string
  categoria: number
  estado: 'activo' | 'inactivo'
  moroso: boolean
  cuotas: Cuota[]
  pagos: PagoPortal[]
  recibos: ReciboPortal[]
}

export const CLAVE_PORTAL = ['portal'] as const

export const portalApi = {
  alumnos: () => api.get<AlumnoPortal[]>('/portal/alumnos'),
  recibo: (numeroRecibo: number) =>
    api.get<Recibo>(`/portal/recibos/${numeroRecibo}`),
}
