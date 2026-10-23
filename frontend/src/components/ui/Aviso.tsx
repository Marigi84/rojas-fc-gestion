import type { ReactNode } from 'react'

type Tipo = 'error' | 'info'

const ESTILOS: Record<Tipo, string> = {
  error: 'border-rojo bg-rojo-suave text-rojo',
  info: 'border-verde bg-lima-suave text-verde-oscuro',
}

interface PropsAviso {
  tipo?: Tipo
  children: ReactNode
}

// Mensaje destacado: errores que devuelve la API o avisos para el usuario.
// Los errores se anuncian a los lectores de pantalla al aparecer.
export function Aviso({ tipo = 'info', children }: PropsAviso) {
  return (
    <div
      role={tipo === 'error' ? 'alert' : 'status'}
      className={`rounded-lg border px-4 py-3 ${ESTILOS[tipo]}`}
    >
      {children}
    </div>
  )
}
