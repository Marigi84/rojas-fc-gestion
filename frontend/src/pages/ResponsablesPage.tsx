import { useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { Boton } from '../components/ui/Boton'
import { Campo } from '../components/ui/Campo'
import { Tarjeta } from '../components/ui/Tarjeta'
import { rutaResponsable } from '../routes/paths'

// Entrada del submodulo de responsables. La consulta es por DNI; el alta se
// hace desde el detalle de un alumno, para que quede asociada a alguien.
export function ResponsablesPage() {
  const navegar = useNavigate()
  const [dni, setDni] = useState('')

  function consultar(evento: FormEvent) {
    evento.preventDefault()
    navegar(rutaResponsable(dni))
  }

  return (
    <section className="space-y-6">
      <h1 className="text-3xl">Responsables</h1>

      <Tarjeta titulo="Consultar un responsable">
        <form onSubmit={consultar} className="flex flex-wrap items-end gap-3">
          <Campo
            etiqueta="DNI"
            inputMode="numeric"
            pattern="[0-9]{1,10}"
            title="Solo números"
            required
            value={dni}
            onChange={(e) => setDni(e.target.value.trim())}
            className="w-48"
          />
          <Boton type="submit" variante="secundario">
            Ver responsable
          </Boton>
        </form>
        <p className="mt-4 text-sm text-texto-suave">
          Para dar de alta un responsable, abrí el detalle del alumno y usá
          "Agregar responsable".
        </p>
      </Tarjeta>
    </section>
  )
}
