import { useMutation } from '@tanstack/react-query'
import { useState, type FormEvent } from 'react'
import { responsablesApi } from '../../services/responsables'
import { mensajeError } from '../../utils/errores'
import { Aviso } from '../ui/Aviso'
import { Boton } from '../ui/Boton'
import { Campo } from '../ui/Campo'

interface PropsRestablecerContrasena {
  dni: number
  nombre: string
  onCerrar: () => void
}

// El Administrador le asigna a un responsable una contraseña provisoria nueva,
// por ejemplo si se olvidó la suya (RF-15). En su próximo ingreso la tiene que
// cambiar (RNF-01).
export function RestablecerContrasena({
  dni,
  nombre,
  onCerrar,
}: PropsRestablecerContrasena) {
  const [contrasena, setContrasena] = useState('')
  const [repeticion, setRepeticion] = useState('')
  const restablecimiento = useMutation({
    mutationFn: () => responsablesApi.restablecerContrasena(dni, contrasena),
  })

  const noCoinciden = repeticion !== '' && repeticion !== contrasena

  function enviar(evento: FormEvent) {
    evento.preventDefault()
    if (noCoinciden) return
    restablecimiento.mutate()
  }

  if (restablecimiento.isSuccess) {
    return (
      <div className="space-y-3">
        <Aviso>
          Listo: la contraseña de {nombre} quedó provisoria. Pasásela y, en su
          próximo ingreso, el sistema le va a pedir que la cambie.
        </Aviso>
        <Boton variante="secundario" onClick={onCerrar}>
          Cerrar
        </Boton>
      </div>
    )
  }

  return (
    <form onSubmit={enviar} className="space-y-4">
      <p className="text-texto-suave">
        Asigná una contraseña provisoria nueva para {nombre}. La va a tener que
        cambiar en su próximo ingreso al portal.
      </p>
      <div className="grid gap-4 sm:grid-cols-2">
        <Campo
          etiqueta="Contraseña provisoria nueva"
          type="password"
          autoComplete="new-password"
          required
          minLength={8}
          maxLength={72}
          value={contrasena}
          onChange={(e) => setContrasena(e.target.value)}
        />
        <Campo
          etiqueta="Repetir contraseña"
          type="password"
          autoComplete="new-password"
          required
          value={repeticion}
          error={noCoinciden ? 'Las contraseñas no coinciden.' : undefined}
          onChange={(e) => setRepeticion(e.target.value)}
        />
      </div>
      {restablecimiento.isError && (
        <Aviso tipo="error">{mensajeError(restablecimiento.error)}</Aviso>
      )}
      <div className="flex flex-wrap gap-3">
        <Boton type="submit" disabled={restablecimiento.isPending}>
          {restablecimiento.isPending
            ? 'Guardando…'
            : 'Guardar contraseña provisoria'}
        </Boton>
        <Boton variante="secundario" onClick={onCerrar}>
          Cancelar
        </Boton>
      </div>
    </form>
  )
}
