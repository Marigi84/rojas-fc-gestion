import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState, type FormEvent } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { Aviso } from '../components/ui/Aviso'
import { Boton } from '../components/ui/Boton'
import { Campo } from '../components/ui/Campo'
import { Selector } from '../components/ui/Selector'
import { Tarjeta } from '../components/ui/Tarjeta'
import { rutaResponsable } from '../routes/paths'
import { claveAlumno, VINCULOS, type Vinculo } from '../services/alumnos'
import { claveResponsable, responsablesApi } from '../services/responsables'
import { mensajeError } from '../utils/errores'

// Asociación de un responsable existente con otro alumno (RF-09, RN-11).
export function ResponsableAsociarPage() {
  const { dni: parametro } = useParams()
  const dni = Number(parametro)
  const dniValido = Number.isInteger(dni) && dni > 0
  const navegar = useNavigate()
  const cliente = useQueryClient()

  const consulta = useQuery({
    queryKey: claveResponsable(dni),
    queryFn: () => responsablesApi.consultar(dni),
    enabled: dniValido,
  })
  const [dniAlumno, setDniAlumno] = useState('')
  const [vinculo, setVinculo] = useState<Vinculo>('MADRE')
  const asociacion = useMutation({
    mutationFn: () =>
      responsablesApi.asociar(dni, {
        dniAlumno: Number(dniAlumno),
        vinculo,
      }),
    onSuccess: (detalle) => {
      cliente.setQueryData(claveResponsable(dni), detalle)
      cliente.invalidateQueries({
        queryKey: claveAlumno(Number(dniAlumno)),
      })
      navegar(rutaResponsable(dni))
    },
  })

  if (!dniValido) {
    return <Aviso tipo="error">El DNI de la dirección no es válido.</Aviso>
  }
  if (consulta.isPending) {
    return <p className="text-texto-suave">Cargando responsable…</p>
  }
  if (consulta.isError) {
    return <Aviso tipo="error">{mensajeError(consulta.error)}</Aviso>
  }

  const responsable = consulta.data

  function enviar(evento: FormEvent) {
    evento.preventDefault()
    asociacion.mutate()
  }

  return (
    <section className="space-y-6">
      <h1 className="text-3xl">Asociar a otro alumno</h1>
      <p className="text-texto-suave">
        Responsable:{' '}
        <strong>
          {responsable.apellido}, {responsable.nombre}
        </strong>{' '}
        (DNI {responsable.dni})
      </p>

      <form onSubmit={enviar} className="space-y-6">
        <Tarjeta>
          <div className="grid gap-4 sm:grid-cols-2">
            <Campo
              etiqueta="DNI del alumno"
              inputMode="numeric"
              pattern="[0-9]{1,10}"
              title="Solo números"
              required
              value={dniAlumno}
              onChange={(e) => setDniAlumno(e.target.value.trim())}
            />
            <Selector
              etiqueta="Vínculo con el alumno"
              opciones={VINCULOS}
              value={vinculo}
              onChange={(e) => setVinculo(e.target.value as Vinculo)}
            />
          </div>
          <p className="mt-4 text-sm text-texto-suave">
            El alumno tiene que existir. Si ya está asociado a este responsable,
            la operación se rechaza.
          </p>
        </Tarjeta>

        {asociacion.isError && (
          <Aviso tipo="error">{mensajeError(asociacion.error)}</Aviso>
        )}

        <div className="flex flex-wrap gap-3">
          <Boton type="submit" disabled={asociacion.isPending}>
            {asociacion.isPending ? 'Asociando…' : 'Asociar alumno'}
          </Boton>
          <Boton
            variante="secundario"
            onClick={() => navegar(rutaResponsable(dni))}
          >
            Cancelar
          </Boton>
        </div>
      </form>
    </section>
  )
}
