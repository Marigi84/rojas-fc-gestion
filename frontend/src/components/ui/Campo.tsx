import { useId, type InputHTMLAttributes } from 'react'

interface PropsCampo extends InputHTMLAttributes<HTMLInputElement> {
  etiqueta: string
  error?: string
}

// Campo de formulario con su etiqueta y, si corresponde, el mensaje de error
// debajo. El error se asocia al campo para que lo lean los lectores de pantalla.
export function Campo({
  etiqueta,
  error,
  id,
  className = '',
  ...resto
}: PropsCampo) {
  const idGenerado = useId()
  const idCampo = id ?? idGenerado
  const idError = `${idCampo}-error`

  return (
    <div className={className}>
      <label htmlFor={idCampo} className="mb-1 block text-sm font-medium">
        {etiqueta}
      </label>
      <input
        id={idCampo}
        aria-invalid={error ? true : undefined}
        aria-describedby={error ? idError : undefined}
        className={`w-full rounded-lg border bg-superficie px-3 py-2 outline-none focus:ring-3 ${
          error
            ? 'border-rojo focus:ring-rojo-suave'
            : 'border-borde focus:border-verde focus:ring-lima-suave'
        }`}
        {...resto}
      />
      {error && (
        <p id={idError} className="mt-1 text-sm text-rojo">
          {error}
        </p>
      )}
    </div>
  )
}
