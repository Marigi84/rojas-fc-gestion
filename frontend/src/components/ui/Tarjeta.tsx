import type { ReactNode } from 'react'

interface PropsTarjeta {
  titulo?: string
  children: ReactNode
  className?: string
}

// Contenedor blanco con borde para agrupar un formulario o un bloque de datos.
export function Tarjeta({ titulo, children, className = '' }: PropsTarjeta) {
  return (
    <section
      className={`rounded-xl border border-borde bg-superficie p-5 ${className}`}
    >
      {titulo && <h2 className="mb-3 text-2xl">{titulo}</h2>}
      {children}
    </section>
  )
}
