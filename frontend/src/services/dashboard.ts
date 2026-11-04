import { api } from './apiClient'

// Servicio del Módulo 8 (Dashboard). Ver backend/README.md, sección "Dashboard".

export interface ResumenDashboard {
  alumnosActivos: number
  alumnosPorCategoria: { categoria: number; cantidad: number }[]
  alumnosMorosos: number
  // Mes en curso, en formato AAAA-MM.
  mes: string
  recaudacionDelMes: number
}

export const CLAVE_DASHBOARD = ['dashboard'] as const

export const dashboardApi = {
  resumen: () => api.get<ResumenDashboard>('/dashboard'),
}
