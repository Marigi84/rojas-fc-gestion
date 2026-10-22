import type { ButtonHTMLAttributes } from 'react'

type Variante = 'principal' | 'secundario' | 'peligro'

interface PropsBoton extends ButtonHTMLAttributes<HTMLButtonElement> {
  variante?: Variante
}

const ESTILOS: Record<Variante, string> = {
  principal: 'border-verde bg-verde text-white hover:bg-verde-oscuro',
  secundario:
    'border-verde bg-superficie text-verde-oscuro hover:bg-lima-suave',
  peligro: 'border-rojo bg-superficie text-rojo hover:bg-rojo-suave',
}

// Botón común del sistema. "principal" es la acción más importante de cada
// pantalla; "secundario" las demás; "peligro" las que dan de baja o anulan.
export function Boton({
  variante = 'principal',
  type = 'button',
  className = '',
  ...resto
}: PropsBoton) {
  return (
    <button
      type={type}
      className={`rounded-lg border px-4 py-2 font-semibold transition-colors focus-visible:outline-3 focus-visible:outline-offset-2 focus-visible:outline-lima disabled:cursor-not-allowed disabled:opacity-50 ${ESTILOS[variante]} ${className}`}
      {...resto}
    />
  )
}
