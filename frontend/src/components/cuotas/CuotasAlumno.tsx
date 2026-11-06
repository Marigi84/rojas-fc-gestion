import { useQuery } from '@tanstack/react-query'
import { claveCuotasAlumno, cuotasApi } from '../../services/cuotas'
import { mensajeError } from '../../utils/errores'
import { Aviso } from '../ui/Aviso'
import { Tarjeta } from '../ui/Tarjeta'
import { TablaCuotas } from './TablaCuotas'

// Sección de cuotas en el detalle de un alumno (RF-22).
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
      {consulta.isSuccess && <TablaCuotas cuotas={consulta.data} />}
    </Tarjeta>
  )
}
