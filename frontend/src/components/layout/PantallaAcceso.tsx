import type { ReactNode } from 'react'
import escudo from '../../assets/escudo.png'

interface PropsPantallaAcceso {
  titulo: string
  descripcion: string
  children: ReactNode
}

// Marco de las pantallas sin sesión (ingreso y configuración inicial): el
// escudo y una tarjeta centrada, sin el menú de módulos.
export function PantallaAcceso({
  titulo,
  descripcion,
  children,
}: PropsPantallaAcceso) {
  return (
    <main className="flex min-h-screen items-center justify-center px-4 py-10">
      <div className="w-full max-w-md">
        <div className="mb-6 flex flex-col items-center gap-2 text-center">
          <img src={escudo} alt="Escudo de Rojas FC" className="size-20" />
          <span className="font-display text-2xl font-bold">Rojas FC</span>
        </div>
        <section className="rounded-xl border border-borde border-t-4 border-t-verde bg-superficie p-6">
          <h1 className="text-3xl">{titulo}</h1>
          <p className="mt-1 mb-5 text-texto-suave">{descripcion}</p>
          {children}
        </section>
      </div>
    </main>
  )
}
