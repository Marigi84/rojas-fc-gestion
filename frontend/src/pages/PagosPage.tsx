import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState, type FormEvent } from 'react'
import { Link } from 'react-router-dom'
import { Aviso } from '../components/ui/Aviso'
import { Boton } from '../components/ui/Boton'
import { Campo } from '../components/ui/Campo'
import { Selector } from '../components/ui/Selector'
import { Tabla } from '../components/ui/Tabla'
import { Tarjeta } from '../components/ui/Tarjeta'
import { rutaRecibo } from '../routes/paths'
import {
  claveCuotasAlumno,
  CLAVE_MOROSOS,
  cuotasApi,
  type Cuota,
} from '../services/cuotas'
import { CLAVE_DASHBOARD } from '../services/dashboard'
import {
  clavePagosAlumno,
  pagosApi,
  type MedioPago,
  type Pago,
} from '../services/pagos'
import { CLAVE_PORTAL } from '../services/portal'
import { mensajeError } from '../utils/errores'
import { formatearFecha, formatearPeriodo } from '../utils/fechas'
import { formatearMonto } from '../utils/moneda'

const OPCIONES_MEDIO = [
  { valor: 'efectivo', etiqueta: 'Efectivo' },
  { valor: 'transferencia', etiqueta: 'Transferencia' },
]

function nombreEstadoCuota(cuota: Cuota): string {
  if (cuota.estado === 'pagada') return 'Pagada'
  return cuota.estado === 'vencida' ? 'Vencida' : 'Pendiente'
}

function nombreMedio(medioPago: string): string {
  return medioPago === 'efectivo' ? 'Efectivo' : 'Transferencia'
}

// Registro y anulación de pagos de cuotas, con su historial y los recibos
// (Módulo 5, RF-24 a RF-31 y RF-33).
export function PagosPage() {
  const cliente = useQueryClient()
  const [dni, setDni] = useState('')
  const [dniConsultado, setDniConsultado] = useState<number | null>(null)
  const [cuotaSeleccionada, setCuotaSeleccionada] = useState<Cuota | null>(null)
  const [medioPago, setMedioPago] = useState<MedioPago>('efectivo')
  const [pagoAAnular, setPagoAAnular] = useState<Pago | null>(null)
  const [motivoAnulacion, setMotivoAnulacion] = useState('')
  const [aviso, setAviso] = useState<string | null>(null)

  const cuotas = useQuery({
    queryKey: claveCuotasAlumno(dniConsultado ?? 0),
    queryFn: () => cuotasApi.deAlumno(dniConsultado ?? 0),
    enabled: dniConsultado !== null,
  })
  const pagos = useQuery({
    queryKey: clavePagosAlumno(dniConsultado ?? 0),
    queryFn: () => pagosApi.deAlumno(dniConsultado ?? 0),
    enabled: dniConsultado !== null,
  })

  function refrescar() {
    if (dniConsultado === null) return
    cliente.invalidateQueries({ queryKey: claveCuotasAlumno(dniConsultado) })
    cliente.invalidateQueries({ queryKey: clavePagosAlumno(dniConsultado) })
    cliente.invalidateQueries({ queryKey: CLAVE_MOROSOS })
    cliente.invalidateQueries({ queryKey: CLAVE_DASHBOARD })
    cliente.invalidateQueries({ queryKey: CLAVE_PORTAL })
  }

  const registro = useMutation({
    mutationFn: () =>
      pagosApi.registrar({
        dniAlumno: dniConsultado ?? 0,
        periodo: cuotaSeleccionada?.periodo ?? '',
        medioPago,
      }),
    onSuccess: (pago) => {
      refrescar()
      setAviso(`Se registró el pago. Recibo N° ${pago.numeroRecibo ?? '—'}.`)
      setCuotaSeleccionada(null)
    },
  })
  const anulacion = useMutation({
    mutationFn: () =>
      pagosApi.anular(pagoAAnular?.idPago ?? 0, motivoAnulacion),
    onSuccess: () => {
      refrescar()
      setAviso('Se anuló el pago. El recibo queda conservado como anulado.')
      setPagoAAnular(null)
      setMotivoAnulacion('')
    },
  })

  function consultar(evento: FormEvent) {
    evento.preventDefault()
    setDniConsultado(Number(dni))
    setCuotaSeleccionada(null)
    setPagoAAnular(null)
    setAviso(null)
  }

  function elegirCuota(cuota: Cuota) {
    setCuotaSeleccionada(cuota)
    setMedioPago('efectivo')
    registro.reset()
  }

  function elegirPagoAAnular(pago: Pago) {
    setPagoAAnular(pago)
    setMotivoAnulacion('')
    anulacion.reset()
  }

  const cargando = cuotas.isPending || pagos.isPending

  return (
    <section className="space-y-6">
      <h1 className="text-3xl">Pagos y Recibos</h1>

      <Tarjeta titulo="Buscar alumno">
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
            Ver pagos
          </Boton>
        </form>
      </Tarjeta>

      {aviso && <Aviso tipo="info">{aviso}</Aviso>}

      {dniConsultado !== null && cargando && (
        <p className="text-texto-suave">Cargando pagos…</p>
      )}

      {dniConsultado !== null && cuotas.isError && (
        <Aviso tipo="error">{mensajeError(cuotas.error)}</Aviso>
      )}

      {dniConsultado !== null && pagos.isError && (
        <Aviso tipo="error">{mensajeError(pagos.error)}</Aviso>
      )}

      {dniConsultado !== null && !cargando && !cuotas.isError && (
        <Tarjeta titulo="Cuotas del alumno">
          <Tabla
            columnas={[
              {
                titulo: 'Período',
                valor: (cuota) => formatearPeriodo(cuota.periodo),
              },
              {
                titulo: 'Importe',
                valor: (cuota) => formatearMonto(cuota.montoAPagar),
                numerica: true,
              },
              { titulo: 'Estado', valor: nombreEstadoCuota },
              {
                titulo: 'Acción',
                valor: (cuota) =>
                  cuota.estado === 'pagada' ? (
                    '—'
                  ) : (
                    <Boton
                      variante="secundario"
                      className="text-sm whitespace-nowrap"
                      aria-label={`Registrar el pago de ${formatearPeriodo(cuota.periodo)}`}
                      onClick={() => elegirCuota(cuota)}
                    >
                      Registrar pago
                    </Boton>
                  ),
              },
            ]}
            filas={cuotas.data ?? []}
            claveFila={(cuota) => cuota.periodo}
            vacia="El alumno no tiene cuotas generadas."
          />
        </Tarjeta>
      )}

      {cuotaSeleccionada && (
        <Tarjeta titulo="Registrar pago">
          <form
            onSubmit={(evento) => {
              evento.preventDefault()
              registro.mutate()
            }}
            className="space-y-4"
          >
            <p className="text-sm text-texto-suave">
              Cuota de {formatearPeriodo(cuotaSeleccionada.periodo)}. Se abona
              el total: {formatearMonto(cuotaSeleccionada.montoAPagar)}.
            </p>
            <Selector
              etiqueta="Medio de pago"
              opciones={OPCIONES_MEDIO}
              value={medioPago}
              onChange={(e) => setMedioPago(e.target.value as MedioPago)}
            />
            {registro.isError && (
              <Aviso tipo="error">{mensajeError(registro.error)}</Aviso>
            )}
            <div className="flex flex-wrap gap-3">
              <Boton type="submit" disabled={registro.isPending}>
                Registrar pago
              </Boton>
              <Boton
                variante="secundario"
                onClick={() => setCuotaSeleccionada(null)}
              >
                Cancelar
              </Boton>
            </div>
          </form>
        </Tarjeta>
      )}

      {dniConsultado !== null && !cargando && !pagos.isError && (
        <Tarjeta titulo="Historial de pagos">
          <Tabla
            columnas={[
              { titulo: 'Fecha', valor: (pago) => formatearFecha(pago.fecha) },
              {
                titulo: 'Período',
                valor: (pago) =>
                  pago.periodo ? formatearPeriodo(pago.periodo) : '—',
              },
              {
                titulo: 'Monto',
                valor: (pago) => formatearMonto(pago.monto),
                numerica: true,
              },
              {
                titulo: 'Medio',
                valor: (pago) => nombreMedio(pago.medioPago),
              },
              {
                titulo: 'Estado',
                valor: (pago) =>
                  pago.fechaAnulacion ? `Anulado: ${pago.motivo}` : 'Vigente',
              },
              {
                titulo: 'Recibo',
                valor: (pago) =>
                  pago.numeroRecibo ? (
                    <Link
                      to={rutaRecibo(pago.numeroRecibo)}
                      className="font-semibold underline"
                    >
                      N° {pago.numeroRecibo}
                    </Link>
                  ) : (
                    '—'
                  ),
              },
              {
                titulo: 'Acción',
                valor: (pago) =>
                  pago.fechaAnulacion ? (
                    '—'
                  ) : (
                    <Boton
                      variante="peligro"
                      className="text-sm whitespace-nowrap"
                      aria-label={`Anular el pago del ${formatearFecha(pago.fecha)}`}
                      onClick={() => elegirPagoAAnular(pago)}
                    >
                      Anular
                    </Boton>
                  ),
              },
            ]}
            filas={pagos.data ?? []}
            claveFila={(pago) => pago.idPago}
            vacia="El alumno no tiene pagos registrados."
          />
        </Tarjeta>
      )}

      {pagoAAnular && (
        <Tarjeta titulo="Anular pago">
          <form
            onSubmit={(evento) => {
              evento.preventDefault()
              anulacion.mutate()
            }}
            className="space-y-4"
          >
            <p className="text-sm text-texto-suave">
              Pago del {formatearFecha(pagoAAnular.fecha)} por{' '}
              {formatearMonto(pagoAAnular.monto)}. El pago no se borra: queda
              anulado con el motivo.
            </p>
            <Campo
              etiqueta="Motivo de la anulación"
              required
              maxLength={200}
              value={motivoAnulacion}
              onChange={(e) => setMotivoAnulacion(e.target.value)}
            />
            {anulacion.isError && (
              <Aviso tipo="error">{mensajeError(anulacion.error)}</Aviso>
            )}
            <div className="flex flex-wrap gap-3">
              <Boton
                variante="peligro"
                type="submit"
                disabled={anulacion.isPending}
              >
                Anular pago
              </Boton>
              <Boton variante="secundario" onClick={() => setPagoAAnular(null)}>
                Cancelar
              </Boton>
            </div>
          </form>
        </Tarjeta>
      )}
    </section>
  )
}
