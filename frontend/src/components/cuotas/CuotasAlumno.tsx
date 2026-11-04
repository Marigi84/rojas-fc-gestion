import { useQuery } from '@tanstack/react-query'
import {
  claveCuotasAlumno,
  cuotasApi,
  type EstadoCuota,
} from '../../services/cuotas'
import { mensajeError } from '../../utils/errores'
import { formatearFecha, formatearPeriodo } from '../../utils/fechas'
import { formatearMonto } from '../../utils/moneda'
import { Aviso } from '../ui/Aviso'
import { Tabla } from '../ui/Tabla'
import { Tarjeta } from '../ui/Tarjeta'

const ESTADOS: Record<EstadoCuota, { etiqueta: string; clases: string }> = {
  pagada: {
    etiqueta: 'Pagada',
    clases: 'border-verde bg-lima-suave text-verde-oscuro',
  },
  pendiente: {
    etiqueta: 'Pendiente',
    clases: 'border-borde bg-fondo text-texto-suave',
  },
  vencida: {
    etiqueta: 'Vencida',
    clases: 'border-rojo bg-rojo-suave text-rojo',
  },
}

// Cuotas de un alumno con su estado y su pago asociado (RF-22). "A pagar"
// incluye el interés por mora de las cuotas vencidas (RF-21).
export function CuotasAlumno({ dni }: { dni: number }) {
  const consulta = useQuery({
    queryKey: claveCuotasAlumno(dni),
    queryFn: () => cuotasApi.deAlumno(dni),
  })

  return (
    <Tarjeta titulo="Cuotas">
      {consulta.isPending && (
        <p className="text-texto-suave">Cargando cuotas…</p>
      )}
      {consulta.isError && (
        <Aviso tipo="error">{mensajeError(consulta.error)}</Aviso>
      )}
      {consulta.isSuccess && (
        <Tabla
          columnas={[
            {
              titulo: 'Período',
              valor: (cuota) => formatearPeriodo(cuota.periodo),
            },
            {
              titulo: 'Importe',
              valor: (cuota) => formatearMonto(cuota.importe),
              numerica: true,
            },
            {
              titulo: 'Vencimiento',
              valor: (cuota) => formatearFecha(cuota.fechaVencimiento),
            },
            {
              titulo: 'A pagar',
              valor: (cuota) => formatearMonto(cuota.montoAPagar),
              numerica: true,
            },
            {
              titulo: 'Estado',
              valor: (cuota) => (
                <span
                  className={`rounded-full border px-3 py-0.5 text-sm font-semibold ${ESTADOS[cuota.estado].clases}`}
                >
                  {ESTADOS[cuota.estado].etiqueta}
                </span>
              ),
            },
            {
              titulo: 'Pago',
              valor: (cuota) =>
                cuota.idPago
                  ? `N.º ${cuota.idPago}, ${formatearFecha(cuota.fechaPago)}`
                  : '—',
            },
          ]}
          filas={consulta.data}
          claveFila={(cuota) => cuota.periodo}
          vacia="El alumno todavía no tiene cuotas."
        />
      )}
    </Tarjeta>
  )
}
