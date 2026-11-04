import { useQuery } from '@tanstack/react-query'
import { TablaCuotas } from '../components/cuotas/TablaCuotas'
import { Aviso } from '../components/ui/Aviso'
import { Tabla } from '../components/ui/Tabla'
import { Tarjeta } from '../components/ui/Tarjeta'
import { CLAVE_PORTAL, portalApi, type AlumnoPortal } from '../services/portal'
import { mensajeError } from '../utils/errores'
import { formatearFecha, formatearPeriodo } from '../utils/fechas'
import { formatearMonto } from '../utils/moneda'

function concepto(periodo: string | null): string {
  return periodo ? `Cuota de ${formatearPeriodo(periodo)}` : 'Otro concepto'
}

function estadoAnulado(anulado: boolean): string {
  return anulado ? 'Anulado' : 'Vigente'
}

function AlumnoACargo({ alumno }: { alumno: AlumnoPortal }) {
  return (
    <Tarjeta titulo={`${alumno.nombre} ${alumno.apellido}`}>
      <div className="mb-4 flex flex-wrap gap-2 text-sm font-semibold">
        <span className="rounded-full border border-borde bg-fondo px-3 py-0.5 text-texto-suave">
          Categoría {alumno.categoria}
        </span>
        <span className="rounded-full border border-borde bg-fondo px-3 py-0.5 text-texto-suave">
          {alumno.estado === 'activo' ? 'Activo' : 'Inactivo'}
        </span>
        {alumno.moroso && (
          <span className="rounded-full border border-rojo bg-rojo-suave px-3 py-0.5 text-rojo">
            Con deuda vencida
          </span>
        )}
      </div>

      <h3 className="mb-2 text-xl">Cuotas</h3>
      <TablaCuotas cuotas={alumno.cuotas} />

      <h3 className="mt-5 mb-2 text-xl">Pagos</h3>
      <Tabla
        columnas={[
          { titulo: 'Fecha', valor: (pago) => formatearFecha(pago.fecha) },
          { titulo: 'Concepto', valor: (pago) => concepto(pago.periodo) },
          {
            titulo: 'Importe',
            valor: (pago) => formatearMonto(pago.monto),
            numerica: true,
          },
          {
            titulo: 'Medio de pago',
            valor: (pago) =>
              pago.medioPago === 'efectivo' ? 'Efectivo' : 'Transferencia',
          },
          { titulo: 'Estado', valor: (pago) => estadoAnulado(pago.anulado) },
        ]}
        filas={alumno.pagos}
        claveFila={(pago) => pago.idPago}
        vacia="Todavía no hay pagos registrados."
      />

      <h3 className="mt-5 mb-2 text-xl">Recibos</h3>
      <Tabla
        columnas={[
          { titulo: 'Número', valor: (recibo) => recibo.numeroRecibo },
          {
            titulo: 'Fecha',
            valor: (recibo) => formatearFecha(recibo.fechaEmision),
          },
          { titulo: 'Concepto', valor: (recibo) => concepto(recibo.periodo) },
          {
            titulo: 'Importe',
            valor: (recibo) => formatearMonto(recibo.monto),
            numerica: true,
          },
          {
            titulo: 'Estado',
            valor: (recibo) => estadoAnulado(recibo.anulado),
          },
        ]}
        filas={alumno.recibos}
        claveFila={(recibo) => recibo.numeroRecibo}
        vacia="Todavía no hay recibos."
      />
    </Tarjeta>
  )
}

// Consulta de los alumnos a cargo, sus cuotas, pagos y recibos (Módulo 3, RF-12
// a RF-14). Solo lectura.
export function PortalResponsablePage() {
  const consulta = useQuery({
    queryKey: CLAVE_PORTAL,
    queryFn: portalApi.alumnos,
  })

  return (
    <section className="space-y-6">
      <h1 className="text-3xl">Portal del Responsable</h1>

      {consulta.isPending && (
        <p className="text-texto-suave">Cargando alumnos…</p>
      )}
      {consulta.isError && (
        <Aviso tipo="error">{mensajeError(consulta.error)}</Aviso>
      )}
      {consulta.isSuccess && consulta.data.length === 0 && (
        <Aviso>No tenés alumnos asociados.</Aviso>
      )}
      {consulta.data?.map((alumno) => (
        <AlumnoACargo key={alumno.dni} alumno={alumno} />
      ))}
    </section>
  )
}
