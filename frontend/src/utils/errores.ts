import { ErrorApi } from '../services/apiClient'

// Texto para mostrar al usuario. Los errores de la API ya traen su mensaje en
// castellano; si el pedido ni siquiera llegó al backend, se avisa eso.
export function mensajeError(error: unknown): string {
  if (error instanceof ErrorApi) return error.message
  return 'No se pudo conectar con el servidor. Revisá la conexión e intentá de nuevo.'
}

// Campo "error" de ApiError (por ejemplo, "Alumno inactivo"), para distinguir
// casos que la pantalla resuelve de forma distinta.
export function tituloError(error: unknown): string | undefined {
  if (!(error instanceof ErrorApi)) return undefined
  const detalles = error.detalles
  if (
    typeof detalles === 'object' &&
    detalles !== null &&
    'error' in detalles
  ) {
    const titulo = (detalles as { error: unknown }).error
    return typeof titulo === 'string' ? titulo : undefined
  }
  return undefined
}
