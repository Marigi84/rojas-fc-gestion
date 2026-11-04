import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { Aviso } from '../components/ui/Aviso'
import { Tabla } from '../components/ui/Tabla'
import { Tarjeta } from '../components/ui/Tarjeta'
import { PATHS } from '../routes/paths'
import { CLAVE_DASHBOARD, dashboardApi } from '../services/dashboard'
import { mensajeError } from '../utils/errores'
import { formatearPeriodo } from '../utils/fechas'
import { formatearMonto } from '../utils/moneda'

// Indicadores de la pantalla de inicio del Administrador (Módulo 8, RF-45 a RF-48).
export function DashboardPage() {
  const consulta = useQuery({
    queryKey: CLAVE_DASHBOARD,
    queryFn: dashboardApi.resumen,
  })

  return (
    <section className="space-y-6">
      <h1 className="text-3xl">Dashboard</h1>

      {consulta.isPending && (
        <p className="text-texto-suave">Cargando indicadores…</p>
      )}
      {consulta.isError && (
        <Aviso tipo="error">{mensajeError(consulta.error)}</Aviso>
      )}
      {consulta.isSuccess && (
        <>
          <div className="grid gap-4 sm:grid-cols-3">
            <Tarjeta titulo="Alumnos activos">
              <p className="text-4xl font-semibold tabular-nums">
                {consulta.data.alumnosActivos}
              </p>
            </Tarjeta>
            <Tarjeta titulo="Alumnos morosos">
              <p
                className={`text-4xl font-semibold tabular-nums ${
                  consulta.data.alumnosMorosos > 0 ? 'text-rojo' : ''
                }`}
              >
                {consulta.data.alumnosMorosos}
              </p>
              <Link
                to={PATHS.cuotas}
                className="mt-2 inline-block font-semibold underline"
              >
                Ver el listado
              </Link>
            </Tarjeta>
            <Tarjeta titulo="Recaudación del mes">
              <p className="text-4xl font-semibold tabular-nums">
                {formatearMonto(consulta.data.recaudacionDelMes)}
              </p>
              <p className="mt-2 text-sm text-texto-suave">
                {formatearPeriodo(consulta.data.mes)}, hasta hoy
              </p>
            </Tarjeta>
          </div>

          <Tarjeta titulo="Alumnos activos por categoría">
            <Tabla
              columnas={[
                { titulo: 'Categoría', valor: (fila) => fila.categoria },
                {
                  titulo: 'Alumnos',
                  valor: (fila) => fila.cantidad,
                  numerica: true,
                },
              ]}
              filas={consulta.data.alumnosPorCategoria}
              claveFila={(fila) => fila.categoria}
              vacia="Todavía no hay alumnos activos."
            />
          </Tarjeta>
        </>
      )}
    </section>
  )
}
