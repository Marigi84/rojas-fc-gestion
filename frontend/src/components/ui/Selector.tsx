import { useId, type SelectHTMLAttributes } from 'react'

interface PropsSelector extends SelectHTMLAttributes<HTMLSelectElement> {
  etiqueta: string
  opciones: { valor: string; etiqueta: string }[]
}

// Lista desplegable con su etiqueta, con el mismo aspecto que Campo.
export function Selector({
  etiqueta,
  opciones,
  id,
  className = '',
  ...resto
}: PropsSelector) {
  const idGenerado = useId()
  const idSelector = id ?? idGenerado

  return (
    <div className={className}>
      <label htmlFor={idSelector} className="mb-1 block text-sm font-medium">
        {etiqueta}
      </label>
      <select
        id={idSelector}
        className="w-full rounded-lg border border-borde bg-superficie px-3 py-2 outline-none focus:border-verde focus:ring-3 focus:ring-lima-suave"
        {...resto}
      >
        {opciones.map((opcion) => (
          <option key={opcion.valor} value={opcion.valor}>
            {opcion.etiqueta}
          </option>
        ))}
      </select>
    </div>
  )
}
