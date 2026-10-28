import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { Aviso } from '../components/ui/Aviso'
import { Boton } from '../components/ui/Boton'
import { Tabla } from '../components/ui/Tabla'
import { Tarjeta } from '../components/ui/Tarjeta'
import {
  PATHS,
  rutaAlumno,
  rutaAsociarResponsable,
  rutaEditarResponsable,
} from '../routes/paths'
import { claveAlumno, etiquetaVinculo } from '../services/alumnos'
import { claveResponsable, responsablesApi } from '../services/responsables'
import { mensajeError } from '../utils/errores'

// Datos de un responsable y los alumnos a su cargo (RF-07), con acceso a la
// edición (RF-08), la asociación con otro alumno (RF-09) y la desvinculación
// (RF-10).
export function ResponsableDetallePage() {
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
  const desvinculacion = useMutation({
    mutationFn: (dniAlumno: number) =>
      responsablesApi.desvincular(dni, dniAlumno),
    onSuccess: (_, dniAlumno) => {
      cliente.invalidateQueries({ queryKey: claveAlumno(dniAlumno) })
      // Si era el ultimo alumno del responsable, el backend elimina su rol.
      // En ese caso no tiene sentido recargar un detalle que va a dar 404.
      if ((consulta.data?.alumnos.length ?? 0) <= 1) {
        navegar(PATHS.responsables)
      } else {
        cliente.invalidateQueries({ queryKey: claveResponsable(dni) })
      }
    },
  })

  if (!dniValido) {
    return <Aviso tipo="error">El DNI de la dirección no es válido.</Aviso>
  }
  if (consulta.isPending) {
    return <p className="text-texto-suave">Cargando responsable…</p>
  }
  if (consulta.isError) {
    return (
      <div className="space-y-4">
        <Aviso tipo="error">{mensajeError(consulta.error)}</Aviso>
        <Boton
          variante="secundario"
          onClick={() => navegar(PATHS.responsables)}
        >
          Volver a Responsables
        </Boton>
      </div>
    )
  }

  const responsable = consulta.data
  const nombreCompleto = `${responsable.nombre} ${responsable.apellido}`

  return (
    <section className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <h1 className="text-3xl">{nombreCompleto}</h1>
        <div className="flex flex-wrap gap-3">
          <Boton
            variante="secundario"
            onClick={() => navegar(rutaEditarResponsable(responsable.dni))}
          >
            Modificar datos
          </Boton>
          <Boton
            onClick={() => navegar(rutaAsociarResponsable(responsable.dni))}
          >
            Asociar a otro alumno
          </Boton>
        </div>
      </div>

      {desvinculacion.isError && (
        <Aviso tipo="error">{mensajeError(desvinculacion.error)}</Aviso>
      )}

      <Tarjeta titulo="Datos">
        <dl className="grid gap-4 sm:grid-cols-2">
          <div>
            <dt className="text-sm text-texto-suave">DNI</dt>
            <dd className="font-medium">{responsable.dni}</dd>
          </div>
          <div>
            <dt className="text-sm text-texto-suave">Teléfono</dt>
            <dd className="font-medium">{responsable.telefono}</dd>
          </div>
        </dl>
      </Tarjeta>

      <Tarjeta titulo="Alumnos a cargo">
        <Tabla
          columnas={[
            {
              titulo: 'Nombre',
              valor: (a) => (
                <Link
                  to={rutaAlumno(a.dni)}
                  className="font-semibold underline"
                >
                  {a.apellido}, {a.nombre}
                </Link>
              ),
            },
            { titulo: 'DNI', valor: (a) => a.dni, numerica: true },
            { titulo: 'Vínculo', valor: (a) => etiquetaVinculo(a.vinculo) },
            {
              titulo: 'Estado',
              valor: (a) => (a.activo ? 'Activo' : 'Inactivo'),
            },
            {
              titulo: 'Acciones',
              valor: (a) => (
                <Boton
                  variante="peligro"
                  disabled={desvinculacion.isPending}
                  onClick={() => {
                    if (
                      window.confirm(
                        `¿Desvincular a ${responsable.nombre} ${responsable.apellido} de ${a.nombre} ${a.apellido}?`,
                      )
                    ) {
                      desvinculacion.mutate(a.dni)
                    }
                  }}
                >
                  Desvincular
                </Boton>
              ),
            },
          ]}
          filas={responsable.alumnos}
          claveFila={(a) => a.dni}
          vacia="El responsable no tiene alumnos asociados."
        />
      </Tarjeta>
    </section>
  )
}
