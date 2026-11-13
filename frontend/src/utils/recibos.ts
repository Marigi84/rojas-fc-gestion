import type { Recibo } from '../services/pagos'
import { formatearFecha, formatearPeriodo } from './fechas'
import { formatearMonto } from './moneda'

export function nombreMedio(medioPago: string): string {
  return medioPago === 'efectivo' ? 'Efectivo' : 'Transferencia'
}

export function conceptoDe(recibo: Recibo): string {
  return recibo.periodo
    ? `Cuota de ${formatearPeriodo(recibo.periodo)}`
    : 'Pago'
}

// El recibo se descarga como texto plano, que se puede guardar o imprimir.
export function descargarRecibo(recibo: Recibo) {
  const lineas = [
    `Recibo N° ${recibo.numeroRecibo}`,
    'Escuela de Fútbol Rojas FC',
    '',
    `Alumno: ${recibo.alumno} (DNI ${recibo.dniAlumno})`,
    `Fecha de emisión: ${formatearFecha(recibo.fechaEmision)}`,
    `Concepto: ${conceptoDe(recibo)}`,
    `Importe: ${formatearMonto(recibo.importe)}`,
    `Medio de pago: ${nombreMedio(recibo.medioPago)}`,
    ...(recibo.anulado ? ['Estado: ANULADO'] : []),
    '',
  ]
  const archivo = new Blob([lineas.join('\n')], {
    type: 'text/plain;charset=utf-8',
  })
  const enlace = document.createElement('a')
  enlace.href = URL.createObjectURL(archivo)
  enlace.download = `recibo-${recibo.numeroRecibo}.txt`
  enlace.click()
  URL.revokeObjectURL(enlace.href)
}
