import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useNavigate, useParams } from 'react-router-dom'
import { Aviso } from '../components/ui/Aviso'
import { Boton } from '../components/ui/Boton'
import { Tabla } from '../components/ui/Tabla'
import { Tarjeta } from '../components/ui/Tarjeta'
import { PATHS, rutaEditarAlumno } from '../routes/paths'
import {
  alumnosApi,
  CLAVE_LISTADOS_ALUMNOS,
  claveAlumno,
  etiquetaVinculo,
  type AlumnoDetalle,
} from '../services/alumnos'
import { mensajeError } from '../utils/errores'
import { formatearFecha } from '../utils/fechas'

// Datos e historial de un alumno, con su baja y reactivación (RF-02, RF-04,
// RF-05, RF-16).
export function AlumnoDetallePage() {
  const { dni: parametro } = useParams()
  const dni = Number(parametro)
  const dniValido = Number.isInteger(dni) && dni > 0
  const navegar = useNavigate()
  const cliente = useQueryClient()

  const consulta = useQuery({
    queryKey: claveAlumno(dni),
    queryFn: () => alumnosApi.consultar(dni),
    enabled: dniValido,
  })
  const actualizar = (detalle: AlumnoDetalle) => {
    cliente.setQueryData(claveAlumno(dni), detalle)
    cliente.invalidateQueries({ queryKey: CLAVE_LISTADOS_ALUMNOS })
  }
  const baja = useMutation({
    mutationFn: () => alumnosApi.darDeBaja(dni),
    onSuccess: actualizar,
  })
  const reactivacion = useMutation({
    mutationFn: () => alumnosApi.reactivar(dni),
    onSuccess: actualizar,
  })

  if (!dniValido) {
    return <Aviso tipo="error">El DNI de la dirección no es válido.</Aviso>
  }
  if (consulta.isPending) {
    return <p className="text-texto-suave">Cargando alumno…</p>
  }
  if (consulta.isError) {
    return (
      <div className="space-y-4">
        <Aviso tipo="error">{mensajeError(consulta.error)}</Aviso>
        <Boton variante="secundario" onClick={() => navegar(PATHS.alumnos)}>
          Volver a Alumnos
        </Boton>
      </div>
    )
  }

  const alumno = consulta.data
  const activo = alumno.estado === 'activo'
  const nombreCompleto = `${alumno.nombre} ${alumno.apellido}`
  const enCurso = baja.isPending || reactivacion.isPending

  // Confirmación antes de cambiar el estado del alumno (RNF-13).
  function darDeBaja() {
    if (
      window.confirm(
        `¿Dar de baja a ${nombreCompleto}? Sus datos e historial se conservan y se lo puede reactivar más adelante.`,
      )
    ) {
      baja.mutate()
    }
  }
  function reactivar() {
    if (window.confirm(`¿Reactivar a ${nombreCompleto}?`)) {
      reactivacion.mutate()
    }
  }

  const datos: [string, string][] = [
    ['DNI', String(alumno.dni)],
    ['Fecha de nacimiento', formatearFecha(alumno.fechaNacimiento)],
    ['Categoría', String(alumno.categoria)],
    ['Dirección', alumno.direccion],
  ]

  return (
    <section className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-3xl">{nombreCompleto}</h1>
          <div className="mt-2 flex flex-wrap gap-2">
            <span
              className={`rounded-full border px-3 py-0.5 text-sm font-semibold ${
                activo
                  ? 'border-verde bg-lima-suave text-verde-oscuro'
                  : 'border-borde bg-fondo text-texto-suave'
              }`}
            >
              {activo ? 'Activo' : 'Inactivo'}
            </span>
            {alumno.moroso && (
              <span className="rounded-full border border-rojo bg-rojo-suave px-3 py-0.5 text-sm font-semibold text-rojo">
                Moroso
              </span>
            )}
          </div>
        </div>
        <div className="flex flex-wrap gap-3">
          <Boton
            variante="secundario"
            onClick={() => navegar(rutaEditarAlumno(alumno.dni))}
          >
            Modificar datos
          </Boton>
          {/* Claves distintas: al cambiar de estado se monta un botón nuevo
              en lugar de animar el color del anterior bajo el cursor. */}
          {activo ? (
            <Boton
              key="baja"
              variante="peligro"
              disabled={enCurso}
              onClick={darDeBaja}
            >
              Dar de baja
            </Boton>
          ) : (
            <Boton key="reactivar" disabled={enCurso} onClick={reactivar}>
              Reactivar
            </Boton>
          )}
        </div>
      </div>

      {baja.isError && <Aviso tipo="error">{mensajeError(baja.error)}</Aviso>}
      {reactivacion.isError && (
        <Aviso tipo="error">{mensajeError(reactivacion.error)}</Aviso>
      )}

      <Tarjeta titulo="Datos">
        <dl className="grid gap-4 sm:grid-cols-2">
          {datos.map(([etiqueta, valor]) => (
            <div key={etiqueta}>
              <dt className="text-sm text-texto-suave">{etiqueta}</dt>
              <dd className="font-medium">{valor}</dd>
            </div>
          ))}
        </dl>
      </Tarjeta>

      <Tarjeta titulo="Responsables">
        <Tabla
          columnas={[
            { titulo: 'Nombre', valor: (r) => `${r.apellido}, ${r.nombre}` },
            { titulo: 'DNI', valor: (r) => r.dni, numerica: true },
            { titulo: 'Vínculo', valor: (r) => etiquetaVinculo(r.vinculo) },
            { titulo: 'Teléfono', valor: (r) => r.telefono },
          ]}
          filas={alumno.responsables}
          claveFila={(r) => r.dni}
          vacia="El alumno no tiene responsables asociados."
        />
      </Tarjeta>

      <Tarjeta titulo="Historial en la escuela">
        <Tabla
          columnas={[
            { titulo: 'Alta', valor: (p) => formatearFecha(p.fechaAlta) },
            {
              titulo: 'Baja',
              valor: (p) =>
                p.fechaBaja ? formatearFecha(p.fechaBaja) : 'Vigente',
            },
          ]}
          filas={alumno.permanencias}
          claveFila={(p) => p.fechaAlta}
          vacia="Sin registros de permanencia."
        />
      </Tarjeta>
    </section>
  )
}
