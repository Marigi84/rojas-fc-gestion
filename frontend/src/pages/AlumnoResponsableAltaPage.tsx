import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useRef, useState, type FormEvent } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { CamposResponsable } from '../components/responsables/CamposResponsable'
import {
  aResponsableAlta,
  conDniNuevo,
  datosDePersona,
  filaResponsableVacia,
  type FilaResponsable,
} from '../components/responsables/filaResponsable'
import { Aviso } from '../components/ui/Aviso'
import { Boton } from '../components/ui/Boton'
import { Tarjeta } from '../components/ui/Tarjeta'
import { rutaAlumno } from '../routes/paths'
import {
  alumnosApi,
  buscarPersonaSiExiste,
  claveAlumno,
  type ResponsableAlta,
} from '../services/alumnos'
import { responsablesApi } from '../services/responsables'
import { mensajeError } from '../utils/errores'

// Alta de un responsable para un alumno que ya existe (RF-06). Reutiliza la
// persona y el rol si ya están registrados (RN-01) y pide la contraseña
// provisoria solo cuando el responsable es nuevo (RF-11, RNF-01).
export function AlumnoResponsableAltaPage() {
  const { dni: parametro } = useParams()
  const dniAlumno = Number(parametro)
  const dniAlumnoValido = Number.isInteger(dniAlumno) && dniAlumno > 0
  const navegar = useNavigate()
  const cliente = useQueryClient()
  const dniActual = useRef('')

  const consulta = useQuery({
    queryKey: claveAlumno(dniAlumno),
    queryFn: () => alumnosApi.consultar(dniAlumno),
    enabled: dniAlumnoValido,
  })

  const [fila, setFila] = useState<FilaResponsable>(() =>
    filaResponsableVacia(0),
  )
  const [errorBusqueda, setErrorBusqueda] = useState<string | null>(null)

  const alta = useMutation({
    mutationFn: (datos: ResponsableAlta) =>
      responsablesApi.agregarAAlumno(dniAlumno, [datos]),
    onSuccess: (detalle) => {
      cliente.setQueryData(claveAlumno(dniAlumno), detalle)
      navegar(rutaAlumno(dniAlumno))
    },
  })

  if (!dniAlumnoValido) {
    return <Aviso tipo="error">El DNI del alumno no es válido.</Aviso>
  }
  if (consulta.isPending) {
    return <p className="text-texto-suave">Cargando alumno…</p>
  }
  if (consulta.isError) {
    return <Aviso tipo="error">{mensajeError(consulta.error)}</Aviso>
  }

  const alumno = consulta.data

  function cambiarDni(valor: string) {
    dniActual.current = valor
    setFila((actual) => conDniNuevo(actual, valor))
    setErrorBusqueda(null)
  }

  async function revisarDni(valor: string) {
    setErrorBusqueda(null)
    try {
      const persona = await buscarPersonaSiExiste(valor)
      if (dniActual.current !== valor) return
      setFila((actual) => ({ ...actual, ...datosDePersona(persona) }))
    } catch (error) {
      setErrorBusqueda(mensajeError(error))
    }
  }

  function enviar(evento: FormEvent) {
    evento.preventDefault()
    alta.mutate(aResponsableAlta(fila))
  }

  return (
    <section className="space-y-6">
      <h1 className="text-3xl">Agregar responsable</h1>
      <p className="text-texto-suave">
        Alumno:{' '}
        <strong>
          {alumno.apellido}, {alumno.nombre}
        </strong>{' '}
        (DNI {alumno.dni})
      </p>

      <form onSubmit={enviar} className="space-y-6">
        <Tarjeta titulo="Datos del responsable">
          <CamposResponsable
            numero={1}
            fila={fila}
            onCambiar={(cambios) =>
              setFila((actual) => ({ ...actual, ...cambios }))
            }
            onCambiarDni={cambiarDni}
            onRevisarDni={revisarDni}
          />
        </Tarjeta>

        {errorBusqueda && <Aviso tipo="error">{errorBusqueda}</Aviso>}
        {alta.isError && <Aviso tipo="error">{mensajeError(alta.error)}</Aviso>}

        <div className="flex flex-wrap gap-3">
          <Boton type="submit" disabled={alta.isPending}>
            {alta.isPending ? 'Agregando…' : 'Agregar responsable'}
          </Boton>
          <Boton
            variante="secundario"
            onClick={() => navegar(rutaAlumno(dniAlumno))}
          >
            Cancelar
          </Boton>
        </div>
      </form>
    </section>
  )
}
