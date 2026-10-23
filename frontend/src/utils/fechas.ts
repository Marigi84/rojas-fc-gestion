// Fechas de la API en formato ISO (AAAA-MM-DD). Se formatean sin pasar por
// Date para no correrlas un día por la zona horaria del navegador.

export function formatearFecha(iso: string | null): string {
  if (!iso) return '—'
  const [anio, mes, dia] = iso.split('-')
  return `${dia}/${mes}/${anio}`
}

// Hoy en formato ISO, según el reloj local del navegador.
export function hoyIso(): string {
  const ahora = new Date()
  const mes = String(ahora.getMonth() + 1).padStart(2, '0')
  const dia = String(ahora.getDate()).padStart(2, '0')
  return `${ahora.getFullYear()}-${mes}-${dia}`
}
