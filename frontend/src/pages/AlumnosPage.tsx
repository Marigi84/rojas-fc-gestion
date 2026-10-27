import { useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { Boton } from '../components/ui/Boton'
import { Campo } from '../components/ui/Campo'
import { Tarjeta } from '../components/ui/Tarjeta'
import { PATHS, rutaAlumno } from '../routes/paths'

// Entrada del Módulo 2. El listado con búsqueda y filtros (RF-38, RF-39) se
// suma en el issue #41; mientras tanto, se accede a un alumno por su DNI.
export function AlumnosPage() {
  const navegar = useNavigate()
  const [dni, setDni] = useState('')

  function consultar(evento: FormEvent) {
    evento.preventDefault()
    navegar(rutaAlumno(dni))
  }

  return (
    <section className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <h1 className="text-3xl">Alumnos, Responsables y Categorías</h1>
        <Boton onClick={() => navegar(PATHS.alumnoNuevo)}>Nuevo alumno</Boton>
      </div>

      <Tarjeta titulo="Consultar un alumno">
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
            Ver alumno
          </Boton>
        </form>
      </Tarjeta>
    </section>
  )
}
