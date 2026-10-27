import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState, type FormEvent } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { Aviso } from '../components/ui/Aviso'
import { Boton } from '../components/ui/Boton'
import { Campo } from '../components/ui/Campo'
import { Tarjeta } from '../components/ui/Tarjeta'
import { rutaAlumno } from '../routes/paths'
import {
  alumnosApi,
  CLAVE_LISTADOS_ALUMNOS,
  claveAlumno,
  type AlumnoDetalle,
  type ModificacionAlumno,
} from '../services/alumnos'
import { mensajeError } from '../utils/errores'
import { hoyIso } from '../utils/fechas'

// Modificación de los datos de un alumno (RF-03). El DNI no se modifica.
export function AlumnoEdicionPage() {
  const { dni: parametro } = useParams()
  const dni = Number(parametro)
  const dniValido = Number.isInteger(dni) && dni > 0

  const consulta = useQuery({
    queryKey: claveAlumno(dni),
    queryFn: () => alumnosApi.consultar(dni),
    enabled: dniValido,
  })

  if (!dniValido) {
    return <Aviso tipo="error">El DNI de la dirección no es válido.</Aviso>
  }
  if (consulta.isPending) {
    return <p className="text-texto-suave">Cargando alumno…</p>
  }
  if (consulta.isError) {
    return <Aviso tipo="error">{mensajeError(consulta.error)}</Aviso>
  }
  return <FormularioEdicion alumno={consulta.data} />
}

function FormularioEdicion({ alumno }: { alumno: AlumnoDetalle }) {
  const navegar = useNavigate()
  const cliente = useQueryClient()
  const [datos, setDatos] = useState<ModificacionAlumno>({
    nombre: alumno.nombre,
    apellido: alumno.apellido,
    fechaNacimiento: alumno.fechaNacimiento,
    direccion: alumno.direccion,
  })

  const modificacion = useMutation({
    mutationFn: (cambios: ModificacionAlumno) =>
      alumnosApi.modificar(alumno.dni, cambios),
    onSuccess: (detalle) => {
      cliente.setQueryData(claveAlumno(alumno.dni), detalle)
      cliente.invalidateQueries({ queryKey: CLAVE_LISTADOS_ALUMNOS })
      navegar(rutaAlumno(alumno.dni))
    },
  })

  function cambiar(campo: keyof ModificacionAlumno, valor: string) {
    setDatos((actual) => ({ ...actual, [campo]: valor }))
  }

  function enviar(evento: FormEvent) {
    evento.preventDefault()
    modificacion.mutate(datos)
  }

  return (
    <section className="space-y-6">
      <h1 className="text-3xl">Modificar alumno</h1>
      <form onSubmit={enviar} className="space-y-6">
        <Tarjeta>
          <div className="grid gap-4 sm:grid-cols-2">
            <Campo etiqueta="DNI" value={alumno.dni} readOnly disabled />
            <div className="hidden sm:block" />
            <Campo
              etiqueta="Nombre"
              required
              maxLength={60}
              value={datos.nombre}
              onChange={(e) => cambiar('nombre', e.target.value)}
            />
            <Campo
              etiqueta="Apellido"
              required
              maxLength={60}
              value={datos.apellido}
              onChange={(e) => cambiar('apellido', e.target.value)}
            />
            <Campo
              etiqueta="Fecha de nacimiento"
              type="date"
              required
              max={hoyIso()}
              value={datos.fechaNacimiento}
              onChange={(e) => cambiar('fechaNacimiento', e.target.value)}
            />
            <Campo
              etiqueta="Dirección"
              required
              maxLength={120}
              value={datos.direccion}
              onChange={(e) => cambiar('direccion', e.target.value)}
            />
          </div>
          <p className="mt-4 text-sm text-texto-suave">
            El nombre y el apellido son los de la persona: si también es
            responsable o administrador, el cambio se ve en todos sus roles.
          </p>
        </Tarjeta>

        {modificacion.isError && (
          <Aviso tipo="error">{mensajeError(modificacion.error)}</Aviso>
        )}

        <div className="flex flex-wrap gap-3">
          <Boton type="submit" disabled={modificacion.isPending}>
            {modificacion.isPending ? 'Guardando…' : 'Guardar cambios'}
          </Boton>
          <Boton
            variante="secundario"
            onClick={() => navegar(rutaAlumno(alumno.dni))}
          >
            Cancelar
          </Boton>
        </div>
      </form>
    </section>
  )
}
