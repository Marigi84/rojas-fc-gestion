import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useRef, useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Aviso } from '../components/ui/Aviso'
import { Boton } from '../components/ui/Boton'
import { Campo } from '../components/ui/Campo'
import { Selector } from '../components/ui/Selector'
import { Tarjeta } from '../components/ui/Tarjeta'
import { PATHS, rutaAlumno } from '../routes/paths'
import { ErrorApi } from '../services/apiClient'
import {
  alumnosApi,
  claveAlumno,
  personasApi,
  VINCULOS,
  type AlumnoDetalle,
  type PersonaResumen,
  type ResponsableAlta,
  type Vinculo,
} from '../services/alumnos'
import { mensajeError, tituloError } from '../utils/errores'
import { hoyIso } from '../utils/fechas'

// Situación del DNI cargado: persona nueva, persona ya registrada con otro rol
// (se reutilizan sus datos, RN-01) o responsable existente (solo se asocia).
type EstadoDni = 'nueva' | 'registrada' | 'responsable'

interface FilaResponsable {
  clave: number
  dni: string
  nombre: string
  apellido: string
  vinculo: Vinculo
  telefono: string
  contrasena: string
  estado: EstadoDni
}

interface DatosAlumno {
  dni: string
  nombre: string
  apellido: string
  fechaNacimiento: string
  direccion: string
}

const DNI_VALIDO = /^[0-9]{1,10}$/

function filaVacia(clave: number): FilaResponsable {
  return {
    clave,
    dni: '',
    nombre: '',
    apellido: '',
    vinculo: 'MADRE',
    telefono: '',
    contrasena: '',
    estado: 'nueva',
  }
}

async function buscarPersona(dni: string): Promise<PersonaResumen | null> {
  if (!DNI_VALIDO.test(dni)) return null
  try {
    return await personasApi.buscar(Number(dni))
  } catch (error) {
    if (error instanceof ErrorApi && error.estado === 404) return null
    throw error
  }
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
    filaVacia(0),
  ])
  const [errorBusqueda, setErrorBusqueda] = useState<string | null>(null)

  const alAbrirDetalle = (detalle: AlumnoDetalle) => {
    cliente.setQueryData(claveAlumno(detalle.dni), detalle)
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
  }

  async function revisarDniAlumno() {
    setErrorBusqueda(null)
    const dni = alumno.dni
    try {
      const persona = await buscarPersona(dni)
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
    // Si el nombre venía de otra persona registrada, no se arrastra al DNI nuevo.
    setResponsables((filas) =>
      filas.map((fila) =>
        fila.clave !== clave
          ? fila
          : {
              ...fila,
              dni,
              estado: 'nueva',
              ...(fila.estado !== 'nueva' ? { nombre: '', apellido: '' } : {}),
            },
      ),
    )
  }

  async function revisarDniResponsable(clave: number, dni: string) {
    setErrorBusqueda(null)
    try {
      const persona = await buscarPersona(dni)
      const cambios: Partial<FilaResponsable> = persona
        ? {
            nombre: persona.nombre,
            apellido: persona.apellido,
            estado: persona.responsable ? 'responsable' : 'registrada',
          }
        : { estado: 'nueva' }
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

  function enviar(evento: FormEvent) {
    evento.preventDefault()
    alta.mutate({
      dni: Number(alumno.dni),
      nombre: alumno.nombre,
      apellido: alumno.apellido,
      fechaNacimiento: alumno.fechaNacimiento,
      direccion: alumno.direccion,
      responsables: responsables.map((fila): ResponsableAlta => {
        const base = {
          dni: Number(fila.dni),
          nombre: fila.nombre,
          apellido: fila.apellido,
          vinculo: fila.vinculo,
        }
        // Teléfono y contraseña solo para quien todavía no es responsable.
        return fila.estado === 'responsable'
          ? base
          : {
              ...base,
              telefono: fila.telefono,
              contrasenaProvisoria: fila.contrasena,
            }
      }),
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
              <fieldset
                key={fila.clave}
                className="rounded-lg border border-borde p-4"
              >
                <legend className="px-1 font-semibold">
                  Responsable {indice + 1}
                </legend>
                <div className="grid gap-4 sm:grid-cols-2">
                  <Campo
                    etiqueta="DNI"
                    inputMode="numeric"
                    pattern="[0-9]{1,10}"
                    title="Solo números"
                    required
                    value={fila.dni}
                    onChange={(e) =>
                      cambiarDniResponsable(fila.clave, e.target.value.trim())
                    }
                    onBlur={(e) =>
                      revisarDniResponsable(fila.clave, e.target.value.trim())
                    }
                  />
                  <Selector
                    etiqueta="Vínculo con el alumno"
                    opciones={VINCULOS}
                    value={fila.vinculo}
                    onChange={(e) =>
                      cambiarFila(fila.clave, {
                        vinculo: e.target.value as Vinculo,
                      })
                    }
                  />
                  <Campo
                    etiqueta="Nombre"
                    required
                    maxLength={60}
                    readOnly={fila.estado !== 'nueva'}
                    value={fila.nombre}
                    onChange={(e) =>
                      cambiarFila(fila.clave, { nombre: e.target.value })
                    }
                  />
                  <Campo
                    etiqueta="Apellido"
                    required
                    maxLength={60}
                    readOnly={fila.estado !== 'nueva'}
                    value={fila.apellido}
                    onChange={(e) =>
                      cambiarFila(fila.clave, { apellido: e.target.value })
                    }
                  />
                  {fila.estado !== 'responsable' && (
                    <>
                      <Campo
                        etiqueta="Teléfono"
                        type="tel"
                        required
                        maxLength={20}
                        value={fila.telefono}
                        onChange={(e) =>
                          cambiarFila(fila.clave, { telefono: e.target.value })
                        }
                      />
                      <Campo
                        etiqueta="Contraseña provisoria"
                        type="password"
                        autoComplete="new-password"
                        required
                        minLength={8}
                        maxLength={72}
                        value={fila.contrasena}
                        onChange={(e) =>
                          cambiarFila(fila.clave, {
                            contrasena: e.target.value,
                          })
                        }
                      />
                    </>
                  )}
                </div>
                <p className="mt-3 text-sm text-texto-suave">
                  {fila.estado === 'responsable'
                    ? 'Ya es responsable de otro alumno: se asocia sin cambiar sus datos ni su contraseña.'
                    : 'La contraseña es provisoria: el responsable la cambia en su primer ingreso al portal.'}
                </p>
                {responsables.length > 1 && (
                  <Boton
                    variante="secundario"
                    className="mt-3"
                    onClick={() =>
                      setResponsables((filas) =>
                        filas.filter((f) => f.clave !== fila.clave),
                      )
                    }
                  >
                    Quitar responsable
                  </Boton>
                )}
              </fieldset>
            ))}
          </div>
          <Boton
            variante="secundario"
            className="mt-4"
            onClick={() => {
              const clave = siguienteClave.current++
              setResponsables((filas) => [...filas, filaVacia(clave)])
            }}
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
