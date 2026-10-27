import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useRef, useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
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
import { Campo } from '../components/ui/Campo'
import { Tarjeta } from '../components/ui/Tarjeta'
import { PATHS, rutaAlumno } from '../routes/paths'
import {
  alumnosApi,
  buscarPersonaSiExiste,
  CLAVE_LISTADOS_ALUMNOS,
  claveAlumno,
  type AlumnoDetalle,
  type PersonaResumen,
} from '../services/alumnos'
import { mensajeError, tituloError } from '../utils/errores'
import { hoyIso } from '../utils/fechas'

interface DatosAlumno {
  dni: string
  nombre: string
  apellido: string
  fechaNacimiento: string
  direccion: string
}

// Alta de un alumno con al menos un responsable (RF-01, RF-06, RN-05).
export function AlumnoAltaPage() {
  const navegar = useNavigate()
  const cliente = useQueryClient()
  // Clave de cada fila de responsable; la primera fila usa 0.
  const siguienteClave = useRef(1)
  // DNI del alumno tal como está escrito ahora, para descartar búsquedas viejas.
  const dniAlumnoActual = useRef('')

  const [alumno, setAlumno] = useState<DatosAlumno>({
    dni: '',
    nombre: '',
    apellido: '',
    fechaNacimiento: '',
    direccion: '',
  })
  const [personaAlumno, setPersonaAlumno] = useState<PersonaResumen | null>(
    null,
  )
  const [alumnoExistente, setAlumnoExistente] = useState(false)
  const [responsables, setResponsables] = useState<FilaResponsable[]>(() => [
    filaResponsableVacia(0),
  ])
  const [errorBusqueda, setErrorBusqueda] = useState<string | null>(null)

  const alAbrirDetalle = (detalle: AlumnoDetalle) => {
    cliente.setQueryData(claveAlumno(detalle.dni), detalle)
    cliente.invalidateQueries({ queryKey: CLAVE_LISTADOS_ALUMNOS })
    navegar(rutaAlumno(detalle.dni))
  }
  const alta = useMutation({
    mutationFn: alumnosApi.registrar,
    onSuccess: alAbrirDetalle,
  })
  const reactivacion = useMutation({
    mutationFn: alumnosApi.reactivar,
    onSuccess: alAbrirDetalle,
  })

  function cambiarAlumno(campo: keyof DatosAlumno, valor: string) {
    if (campo !== 'dni') {
      setAlumno((actual) => ({ ...actual, [campo]: valor }))
      return
    }
    dniAlumnoActual.current = valor
    // Si el nombre venía de otra persona registrada, no se arrastra al DNI nuevo.
    const veniaDeOtraPersona = personaAlumno !== null
    setAlumno((actual) => ({
      ...actual,
      dni: valor,
      ...(veniaDeOtraPersona ? { nombre: '', apellido: '' } : {}),
    }))
    setPersonaAlumno(null)
    setAlumnoExistente(false)
    setErrorBusqueda(null)
    // El error anterior (y su botón "Reactivar") correspondía al DNI viejo.
    alta.reset()
    reactivacion.reset()
  }

  async function revisarDniAlumno() {
    setErrorBusqueda(null)
    const dni = alumno.dni
    try {
      const persona = await buscarPersonaSiExiste(dni)
      // Si el DNI cambió mientras se buscaba, la respuesta ya no corresponde.
      if (dniAlumnoActual.current !== dni) return
      if (persona?.alumno) {
        setAlumnoExistente(true)
        return
      }
      setPersonaAlumno(persona)
      if (persona) {
        setAlumno((actual) => ({
          ...actual,
          nombre: persona.nombre,
          apellido: persona.apellido,
        }))
      }
    } catch (error) {
      setErrorBusqueda(mensajeError(error))
    }
  }

  function cambiarFila(clave: number, cambios: Partial<FilaResponsable>) {
    setResponsables((filas) =>
      filas.map((fila) =>
        fila.clave === clave ? { ...fila, ...cambios } : fila,
      ),
    )
  }

  function cambiarDniResponsable(clave: number, dni: string) {
    setErrorBusqueda(null)
    setResponsables((filas) =>
      filas.map((fila) =>
        fila.clave === clave ? conDniNuevo(fila, dni) : fila,
      ),
    )
  }

  async function revisarDniResponsable(clave: number, dni: string) {
    setErrorBusqueda(null)
    try {
      const cambios = datosDePersona(await buscarPersonaSiExiste(dni))
      // Solo se aplica si el DNI de la fila no cambió mientras se buscaba.
      setResponsables((filas) =>
        filas.map((fila) =>
          fila.clave === clave && fila.dni === dni
            ? { ...fila, ...cambios }
            : fila,
        ),
      )
    } catch (error) {
      setErrorBusqueda(mensajeError(error))
    }
  }

  function agregarResponsable() {
    const clave = siguienteClave.current++
    setResponsables((filas) => [...filas, filaResponsableVacia(clave)])
  }

  function quitarResponsable(clave: number) {
    setResponsables((filas) => filas.filter((fila) => fila.clave !== clave))
  }

  function enviar(evento: FormEvent) {
    evento.preventDefault()
    alta.mutate({
      dni: Number(alumno.dni),
      nombre: alumno.nombre,
      apellido: alumno.apellido,
      fechaNacimiento: alumno.fechaNacimiento,
      direccion: alumno.direccion,
      responsables: responsables.map(aResponsableAlta),
    })
  }

  const enCurso = alta.isPending || reactivacion.isPending
  const alumnoInactivo = tituloError(alta.error) === 'Alumno inactivo'

  return (
    <section className="space-y-6">
      <h1 className="text-3xl">Nuevo alumno</h1>

      <form onSubmit={enviar} className="space-y-6">
        <Tarjeta titulo="Datos del alumno">
          <div className="grid gap-4 sm:grid-cols-2">
            <Campo
              etiqueta="DNI"
              inputMode="numeric"
              pattern="[0-9]{1,10}"
              title="Solo números"
              required
              value={alumno.dni}
              onChange={(e) => cambiarAlumno('dni', e.target.value.trim())}
              onBlur={revisarDniAlumno}
            />
            <div className="hidden sm:block" />
            <Campo
              etiqueta="Nombre"
              required
              maxLength={60}
              readOnly={personaAlumno !== null}
              value={alumno.nombre}
              onChange={(e) => cambiarAlumno('nombre', e.target.value)}
            />
            <Campo
              etiqueta="Apellido"
              required
              maxLength={60}
              readOnly={personaAlumno !== null}
              value={alumno.apellido}
              onChange={(e) => cambiarAlumno('apellido', e.target.value)}
            />
            <Campo
              etiqueta="Fecha de nacimiento"
              type="date"
              required
              max={hoyIso()}
              value={alumno.fechaNacimiento}
              onChange={(e) => cambiarAlumno('fechaNacimiento', e.target.value)}
            />
            <Campo
              etiqueta="Dirección"
              required
              maxLength={120}
              value={alumno.direccion}
              onChange={(e) => cambiarAlumno('direccion', e.target.value)}
            />
          </div>

          {personaAlumno && (
            <div className="mt-4">
              <Aviso>
                Esta persona ya está registrada en el sistema: se usan su nombre
                y apellido.
              </Aviso>
            </div>
          )}
          {alumnoExistente && (
            <div className="mt-4">
              <Aviso>
                Ya hay un alumno con el DNI {alumno.dni}.{' '}
                <Link
                  to={rutaAlumno(alumno.dni)}
                  className="font-semibold underline"
                >
                  Ver alumno
                </Link>
              </Aviso>
            </div>
          )}
        </Tarjeta>

        <Tarjeta titulo="Responsables">
          <p className="mb-4 text-texto-suave">
            Todo alumno tiene al menos un responsable. Si el DNI ya está
            registrado, se usan sus datos.
          </p>
          <div className="space-y-4">
            {responsables.map((fila, indice) => (
              <CamposResponsable
                key={fila.clave}
                numero={indice + 1}
                fila={fila}
                onCambiar={(cambios) => cambiarFila(fila.clave, cambios)}
                onCambiarDni={(dni) => cambiarDniResponsable(fila.clave, dni)}
                onRevisarDni={(dni) => revisarDniResponsable(fila.clave, dni)}
                onQuitar={
                  responsables.length > 1
                    ? () => quitarResponsable(fila.clave)
                    : undefined
                }
              />
            ))}
          </div>
          <Boton
            variante="secundario"
            className="mt-4"
            onClick={agregarResponsable}
          >
            Agregar otro responsable
          </Boton>
        </Tarjeta>

        {errorBusqueda && <Aviso tipo="error">{errorBusqueda}</Aviso>}
        {alta.isError &&
          (alumnoInactivo ? (
            <Aviso tipo="error">
              <p>{mensajeError(alta.error)}</p>
              <Boton
                className="mt-3"
                disabled={enCurso}
                onClick={() => reactivacion.mutate(Number(alumno.dni))}
              >
                Reactivar alumno
              </Boton>
            </Aviso>
          ) : (
            <Aviso tipo="error">{mensajeError(alta.error)}</Aviso>
          ))}
        {reactivacion.isError && (
          <Aviso tipo="error">{mensajeError(reactivacion.error)}</Aviso>
        )}

        <div className="flex flex-wrap gap-3">
          <Boton type="submit" disabled={enCurso || alumnoExistente}>
            {alta.isPending ? 'Registrando…' : 'Registrar alumno'}
          </Boton>
          <Boton variante="secundario" onClick={() => navegar(PATHS.alumnos)}>
            Cancelar
          </Boton>
        </div>
      </form>
    </section>
  )
}
