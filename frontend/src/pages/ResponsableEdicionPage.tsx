import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState, type FormEvent } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { Aviso } from '../components/ui/Aviso'
import { Boton } from '../components/ui/Boton'
import { Campo } from '../components/ui/Campo'
import { Tarjeta } from '../components/ui/Tarjeta'
import { rutaResponsable } from '../routes/paths'
import {
  claveResponsable,
  responsablesApi,
  type ModificacionResponsable,
  type ResponsableDetalle,
} from '../services/responsables'
import { mensajeError } from '../utils/errores'

// Modificación de los datos del responsable (RF-08). El DNI no se modifica.
export function ResponsableEdicionPage() {
  const { dni: parametro } = useParams()
  const dni = Number(parametro)
  const dniValido = Number.isInteger(dni) && dni > 0

  const consulta = useQuery({
    queryKey: claveResponsable(dni),
    queryFn: () => responsablesApi.consultar(dni),
    enabled: dniValido,
  })

  if (!dniValido) {
    return <Aviso tipo="error">El DNI de la dirección no es válido.</Aviso>
  }
  if (consulta.isPending) {
    return <p className="text-texto-suave">Cargando responsable…</p>
  }
  if (consulta.isError) {
    return <Aviso tipo="error">{mensajeError(consulta.error)}</Aviso>
  }
  return <FormularioEdicion responsable={consulta.data} />
}

function FormularioEdicion({
  responsable,
}: {
  responsable: ResponsableDetalle
}) {
  const navegar = useNavigate()
  const cliente = useQueryClient()
  const [datos, setDatos] = useState<ModificacionResponsable>({
    nombre: responsable.nombre,
    apellido: responsable.apellido,
    telefono: responsable.telefono,
  })

  const modificacion = useMutation({
    mutationFn: (cambios: ModificacionResponsable) =>
      responsablesApi.modificar(responsable.dni, cambios),
    onSuccess: (detalle) => {
      cliente.setQueryData(claveResponsable(responsable.dni), detalle)
      // El nombre y el apellido son de la persona: pueden aparecer tambien en
      // el detalle de un alumno. Se refrescan al volver a esas pantallas.
      cliente.invalidateQueries({ queryKey: ['alumno'] })
      navegar(rutaResponsable(responsable.dni))
    },
  })

  function cambiar(campo: keyof ModificacionResponsable, valor: string) {
    setDatos((actual) => ({ ...actual, [campo]: valor }))
  }

  function enviar(evento: FormEvent) {
    evento.preventDefault()
    modificacion.mutate(datos)
  }

  return (
    <section className="space-y-6">
      <h1 className="text-3xl">Modificar responsable</h1>
      <form onSubmit={enviar} className="space-y-6">
        <Tarjeta>
          <div className="grid gap-4 sm:grid-cols-2">
            <Campo etiqueta="DNI" value={responsable.dni} readOnly disabled />
            <div className="hidden sm:block" />
            <Campo
              etiqueta="Nombre"
              required
              maxLength={60}
              value={datos.nombre}
              onChange={(e) => cambiar('nombre', e.target.value)}
            />
            <Campo
              etiqueta="Apellido"
              required
              maxLength={60}
              value={datos.apellido}
              onChange={(e) => cambiar('apellido', e.target.value)}
            />
            <Campo
              etiqueta="Teléfono"
              type="tel"
              required
              maxLength={20}
              value={datos.telefono}
              onChange={(e) => cambiar('telefono', e.target.value)}
            />
          </div>
          <p className="mt-4 text-sm text-texto-suave">
            El nombre y el apellido son los de la persona: si también es alumno
            o administrador, el cambio se ve en todos sus roles.
          </p>
        </Tarjeta>

        {modificacion.isError && (
          <Aviso tipo="error">{mensajeError(modificacion.error)}</Aviso>
        )}

        <div className="flex flex-wrap gap-3">
          <Boton type="submit" disabled={modificacion.isPending}>
            {modificacion.isPending ? 'Guardando…' : 'Guardar cambios'}
          </Boton>
          <Boton
            variante="secundario"
            onClick={() => navegar(rutaResponsable(responsable.dni))}
          >
            Cancelar
          </Boton>
        </div>
      </form>
    </section>
  )
}
