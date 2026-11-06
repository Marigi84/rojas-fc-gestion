import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState, type FormEvent } from 'react'
import { Link } from 'react-router-dom'
import { Aviso } from '../components/ui/Aviso'
import { Boton } from '../components/ui/Boton'
import { Campo } from '../components/ui/Campo'
import { Tabla } from '../components/ui/Tabla'
import { Tarjeta } from '../components/ui/Tarjeta'
import { rutaAlumno } from '../routes/paths'
import { CLAVE_LISTADOS_ALUMNOS } from '../services/alumnos'
import {
  CLAVE_CUOTAS,
  CLAVE_MOROSOS,
  CLAVE_PERIODOS,
  cuotasApi,
  type Periodo,
} from '../services/cuotas'
import { mensajeError } from '../utils/errores'
import { formatearFecha, formatearPeriodo, hoyIso } from '../utils/fechas'
import { formatearMonto } from '../utils/moneda'

interface DatosPeriodo {
  mes: string
  importe: string
  fechaVencimiento: string
  porcentajeInteres: string
}

function datosDe(periodo: Periodo): DatosPeriodo {
  return {
    mes: periodo.periodo,
    importe: String(periodo.importe),
    fechaVencimiento: periodo.fechaVencimiento,
    porcentajeInteres: String(periodo.porcentajeInteres),
  }
}

// Configuración de los períodos, generación de cuotas y alumnos morosos
// (Módulo 4, RF-17 a RF-21 y RF-23). Las cuotas de cada alumno están en su
// detalle (RF-22).
export function CuotasPage() {
  const cliente = useQueryClient()
  const mesActual = hoyIso().slice(0, 7)
  const [datos, setDatos] = useState<DatosPeriodo>({
    mes: mesActual,
    importe: '',
    fechaVencimiento: '',
    porcentajeInteres: '',
  })
  const [aviso, setAviso] = useState<string | null>(null)

  const periodos = useQuery({
    queryKey: CLAVE_PERIODOS,
    queryFn: cuotasApi.listarPeriodos,
  })
  const morosos = useQuery({
    queryKey: CLAVE_MOROSOS,
    queryFn: cuotasApi.morosos,
  })

  const configuracion = useMutation({
    mutationFn: () =>
      cuotasApi.configurarPeriodo(datos.mes, {
        importe: Number(datos.importe),
        fechaVencimiento: datos.fechaVencimiento,
        porcentajeInteres: Number(datos.porcentajeInteres),
      }),
    onSuccess: (periodo) => {
      cliente.invalidateQueries({ queryKey: CLAVE_PERIODOS })
      setAviso(`Se guardó el período de ${formatearPeriodo(periodo.periodo)}.`)
    },
  })
  const generacion = useMutation({
    mutationFn: cuotasApi.generar,
    onSuccess: ({ periodo, cuotasGeneradas }) => {
      cliente.invalidateQueries({ queryKey: CLAVE_PERIODOS })
      cliente.invalidateQueries({ queryKey: CLAVE_MOROSOS })
      cliente.invalidateQueries({ queryKey: CLAVE_CUOTAS })
      cliente.invalidateQueries({ queryKey: CLAVE_LISTADOS_ALUMNOS })
      cliente.invalidateQueries({ queryKey: ['alumno'] })
      setAviso(
        `Se generaron ${cuotasGeneradas} cuotas de ${formatearPeriodo(periodo)}.`,
      )
    },
  })

  const existente = periodos.data?.find(
    (periodo) => periodo.periodo === datos.mes,
  )
  const bloqueado = (existente?.cuotasGeneradas ?? 0) > 0

  function cambiar(campo: keyof DatosPeriodo, valor: string) {
    setAviso(null)
    configuracion.reset()
    setDatos((actual) => ({ ...actual, [campo]: valor }))
  }

  // Si el mes ya está configurado, se muestran sus condiciones.
  function cambiarMes(mes: string) {
    const periodo = periodos.data?.find((otro) => otro.periodo === mes)
    setAviso(null)
    configuracion.reset()
    setDatos((actual) => (periodo ? datosDe(periodo) : { ...actual, mes }))
  }

  function modificar(periodo: Periodo) {
    setAviso(null)
    configuracion.reset()
    setDatos(datosDe(periodo))
  }

  function enviar(evento: FormEvent) {
    evento.preventDefault()
    configuracion.mutate()
  }

  // Confirmación antes de generar, porque después no se puede cambiar (RNF-13).
  function generar(periodo: Periodo) {
    const mes = formatearPeriodo(periodo.periodo)
    if (
      window.confirm(
        `¿Generar las cuotas de ${mes} para los alumnos activos? Después no se pueden cambiar las condiciones del período.`,
      )
    ) {
      setAviso(null)
      generacion.mutate(periodo.periodo)
    }
  }

  return (
    <section className="space-y-6">
      <h1 className="text-3xl">Cuotas</h1>

      <Tarjeta titulo="Configurar un período">
        <p className="mb-4 text-texto-suave">
          Cada mes tiene su importe, su vencimiento y su interés por mora. Las
          cuotas se generan solas al empezar el mes, con el último valor
          configurado; una vez generadas, las condiciones de ese período ya no
          se pueden cambiar.
        </p>
        <form onSubmit={enviar} className="space-y-4">
          <div className="grid gap-4 sm:grid-cols-2">
            <Campo
              etiqueta="Mes"
              type="month"
              required
              pattern="[0-9]{4}-[0-9]{2}"
              title="Año y mes, por ejemplo 2026-11"
              value={datos.mes}
              onChange={(e) => cambiarMes(e.target.value)}
            />
            <Campo
              etiqueta="Importe de la cuota"
              type="number"
              required
              min="0.01"
              step="0.01"
              value={datos.importe}
              onChange={(e) => cambiar('importe', e.target.value)}
            />
            <Campo
              etiqueta="Fecha de vencimiento"
              type="date"
              required
              min={datos.mes ? `${datos.mes}-01` : undefined}
              value={datos.fechaVencimiento}
              onChange={(e) => cambiar('fechaVencimiento', e.target.value)}
            />
            <Campo
              etiqueta="Interés por mora (%)"
              type="number"
              required
              min="0"
              max="100"
              step="0.01"
              value={datos.porcentajeInteres}
              onChange={(e) => cambiar('porcentajeInteres', e.target.value)}
            />
          </div>

          {bloqueado && (
            <Aviso>
              Este período ya tiene cuotas generadas: sus condiciones no se
              pueden cambiar. Los cambios rigen desde el período siguiente.
            </Aviso>
          )}
          {configuracion.isError && (
            <Aviso tipo="error">{mensajeError(configuracion.error)}</Aviso>
          )}

          <Boton type="submit" disabled={configuracion.isPending || bloqueado}>
            {configuracion.isPending ? 'Guardando…' : 'Guardar período'}
          </Boton>
        </form>
      </Tarjeta>

      {aviso && <Aviso>{aviso}</Aviso>}
      {generacion.isError && (
        <Aviso tipo="error">{mensajeError(generacion.error)}</Aviso>
      )}

      <Tarjeta titulo="Períodos">
        {periodos.isPending && (
          <p className="text-texto-suave">Cargando períodos…</p>
        )}
        {periodos.isError && (
          <Aviso tipo="error">{mensajeError(periodos.error)}</Aviso>
        )}
        {periodos.isSuccess && (
          <Tabla
            columnas={[
              {
                titulo: 'Período',
                valor: (periodo) => formatearPeriodo(periodo.periodo),
              },
              {
                titulo: 'Importe',
                valor: (periodo) => formatearMonto(periodo.importe),
                numerica: true,
              },
              {
                titulo: 'Vencimiento',
                valor: (periodo) => formatearFecha(periodo.fechaVencimiento),
              },
              {
                titulo: 'Interés',
                valor: (periodo) => `${periodo.porcentajeInteres} %`,
                numerica: true,
              },
              {
                titulo: 'Cuotas generadas',
                valor: (periodo) => periodo.cuotasGeneradas,
                numerica: true,
              },
              {
                titulo: 'Acciones',
                valor: (periodo) => {
                  if (periodo.cuotasGeneradas > 0) return '—'
                  const mes = formatearPeriodo(periodo.periodo)
                  return (
                    <div className="flex flex-wrap gap-2">
                      <Boton
                        variante="secundario"
                        className="text-sm whitespace-nowrap"
                        aria-label={`Modificar el período de ${mes}`}
                        onClick={() => modificar(periodo)}
                      >
                        Modificar
                      </Boton>
                      <Boton
                        className="text-sm whitespace-nowrap"
                        aria-label={`Generar las cuotas de ${mes}`}
                        disabled={
                          generacion.isPending || periodo.periodo > mesActual
                        }
                        title={
                          periodo.periodo > mesActual
                            ? 'Se pueden generar cuando empiece el mes'
                            : undefined
                        }
                        onClick={() => generar(periodo)}
                      >
                        Generar cuotas
                      </Boton>
                    </div>
                  )
                },
              },
            ]}
            filas={periodos.data}
            claveFila={(periodo) => periodo.periodo}
            vacia="Todavía no hay períodos configurados."
          />
        )}
      </Tarjeta>

      <Tarjeta titulo="Alumnos morosos">
        {morosos.isPending && (
          <p className="text-texto-suave">Cargando morosos…</p>
        )}
        {morosos.isError && (
          <Aviso tipo="error">{mensajeError(morosos.error)}</Aviso>
        )}
        {morosos.isSuccess && (
          <Tabla
            columnas={[
              {
                titulo: 'Nombre',
                valor: (moroso) => (
                  <Link
                    to={rutaAlumno(moroso.dni)}
                    className="font-semibold underline"
                  >
                    {moroso.apellido}, {moroso.nombre}
                  </Link>
                ),
              },
              {
                titulo: 'DNI',
                valor: (moroso) => moroso.dni,
                numerica: true,
              },
              {
                titulo: 'Estado',
                valor: (moroso) =>
                  moroso.estado === 'activo' ? 'Activo' : 'Inactivo',
              },
              {
                titulo: 'Cuotas vencidas',
                valor: (moroso) => moroso.cuotasVencidas,
                numerica: true,
              },
              {
                titulo: 'Total adeudado',
                valor: (moroso) => formatearMonto(moroso.totalAdeudado),
                numerica: true,
              },
            ]}
            filas={morosos.data}
            claveFila={(moroso) => moroso.dni}
            vacia="No hay alumnos con deuda vencida."
          />
        )}
      </Tarjeta>
    </section>
  )
}
