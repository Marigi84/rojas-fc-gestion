import type { Cuota, EstadoCuota } from '../../services/cuotas'
import { formatearFecha, formatearPeriodo } from '../../utils/fechas'
import { formatearMonto } from '../../utils/moneda'
import { Tabla } from '../ui/Tabla'

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

// Cuotas con su estado y su pago asociado (RF-22). "A pagar" incluye el interés
// por mora de las cuotas vencidas (RF-21).
export function TablaCuotas({ cuotas }: { cuotas: Cuota[] }) {
  return (
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
      filas={cuotas}
      claveFila={(cuota) => cuota.periodo}
      vacia="Todavía no hay cuotas."
    />
  )
}
