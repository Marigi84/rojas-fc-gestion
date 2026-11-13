import { useQuery } from '@tanstack/react-query'
import { useNavigate, useParams } from 'react-router-dom'
import { Aviso } from '../components/ui/Aviso'
import { Boton } from '../components/ui/Boton'
import { Tarjeta } from '../components/ui/Tarjeta'
import { PATHS } from '../routes/paths'
import { claveRecibo, pagosApi } from '../services/pagos'
import { mensajeError } from '../utils/errores'
import { formatearFecha } from '../utils/fechas'
import { formatearMonto } from '../utils/moneda'
import { conceptoDe, descargarRecibo, nombreMedio } from '../utils/recibos'

// Recibo de un pago, con la descarga para conservarlo o imprimirlo
export function ReciboPage() {
  const { numero: parametro } = useParams()
  const numeroRecibo = Number(parametro)
  const numeroValido = Number.isInteger(numeroRecibo) && numeroRecibo > 0
  const navegar = useNavigate()

  const consulta = useQuery({
    queryKey: claveRecibo(numeroRecibo),
    queryFn: () => pagosApi.recibo(numeroRecibo),
    enabled: numeroValido,
  })

  if (!numeroValido) {
    return <Aviso tipo="error">El número de recibo no es válido.</Aviso>
  }
  if (consulta.isPending) {
    return <p className="text-texto-suave">Cargando recibo…</p>
  }
  if (consulta.isError) {
    return (
      <div className="space-y-4">
        <Aviso tipo="error">{mensajeError(consulta.error)}</Aviso>
        <Boton variante="secundario" onClick={() => navegar(PATHS.pagos)}>
          Volver a Pagos
        </Boton>
      </div>
    )
  }

  const recibo = consulta.data
  const concepto = conceptoDe(recibo)

  const datos: [string, string][] = [
    ['Alumno', `${recibo.alumno} (DNI ${recibo.dniAlumno})`],
    ['Fecha de emisión', formatearFecha(recibo.fechaEmision)],
    ['Concepto', concepto],
    ['Importe', formatearMonto(recibo.importe)],
    ['Medio de pago', nombreMedio(recibo.medioPago)],
    ['Estado', recibo.anulado ? 'Anulado' : 'Vigente'],
  ]

  return (
    <section className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <h1 className="text-3xl">Recibo N° {recibo.numeroRecibo}</h1>
        <div className="flex flex-wrap gap-3">
          <Boton variante="secundario" onClick={() => descargarRecibo(recibo)}>
            Descargar
          </Boton>
          <Boton variante="secundario" onClick={() => navegar(PATHS.pagos)}>
            Volver a Pagos
          </Boton>
        </div>
      </div>

      {recibo.anulado && (
        <Aviso tipo="error">
          El pago de este recibo fue anulado. El recibo se conserva como
          comprobante.
        </Aviso>
      )}

      <Tarjeta titulo="Datos del recibo">
        <dl className="grid gap-4 sm:grid-cols-2">
          {datos.map(([etiqueta, valor]) => (
            <div key={etiqueta}>
              <dt className="text-sm text-texto-suave">{etiqueta}</dt>
              <dd className="font-medium">{valor}</dd>
            </div>
          ))}
        </dl>
      </Tarjeta>
    </section>
  )
}
