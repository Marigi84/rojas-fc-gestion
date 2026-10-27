import { useMutation } from '@tanstack/react-query'
import { useEffect, useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { PantallaAcceso } from '../components/layout/PantallaAcceso'
import { Aviso } from '../components/ui/Aviso'
import { Boton } from '../components/ui/Boton'
import { Campo } from '../components/ui/Campo'
import { PATHS } from '../routes/paths'
import { authApi } from '../services/auth'
import { mensajeError } from '../utils/errores'

const ESTILO_ENLACE = 'text-verde-oscuro underline hover:text-verde'

// Pantalla a la que lleva el enlace del correo de recuperación (RF-50). El
// token viene después de # en la dirección: así no viaja a ningún servidor y
// se saca de la barra apenas se lee.
export function NuevaContrasenaPage() {
  const navegar = useNavigate()
  const [token] = useState(() => globalThis.location.hash.slice(1))
  const [nueva, setNueva] = useState('')
  const [repeticion, setRepeticion] = useState('')
  const guardado = useMutation({ mutationFn: authApi.guardarNuevaContrasena })

  useEffect(() => {
    if (globalThis.location.hash) {
      globalThis.history.replaceState(
        globalThis.history.state,
        '',
        globalThis.location.pathname,
      )
    }
  }, [])

  const noCoinciden = repeticion !== '' && repeticion !== nueva

  function enviar(evento: FormEvent) {
    evento.preventDefault()
    if (noCoinciden) return
    guardado.mutate({ token, nueva })
  }

  if (!token) {
    return (
      <PantallaAcceso
        titulo="Enlace incompleto"
        descripcion="Este enlace no tiene los datos para cambiar la contraseña."
      >
        <p className="text-center text-sm">
          <Link to={PATHS.recuperarContrasena} className={ESTILO_ENLACE}>
            Pedir un enlace nuevo
          </Link>
        </p>
      </PantallaAcceso>
    )
  }

  if (guardado.isSuccess) {
    return (
      <PantallaAcceso
        titulo="Contraseña cambiada"
        descripcion="Ya podés ingresar con tu contraseña nueva."
      >
        <Boton
          className="w-full"
          onClick={() => navegar(PATHS.login, { replace: true })}
        >
          Ir al ingreso
        </Boton>
      </PantallaAcceso>
    )
  }

  return (
    <PantallaAcceso
      titulo="Elegir contraseña nueva"
      descripcion="Escribí la contraseña nueva para ingresar como administrador."
    >
      <form onSubmit={enviar} className="space-y-4">
        <Campo
          etiqueta="Contraseña nueva"
          type="password"
          autoComplete="new-password"
          required
          autoFocus
          minLength={8}
          maxLength={72}
          value={nueva}
          onChange={(e) => setNueva(e.target.value)}
        />
        <Campo
          etiqueta="Repetir contraseña nueva"
          type="password"
          autoComplete="new-password"
          required
          value={repeticion}
          error={noCoinciden ? 'Las contraseñas no coinciden.' : undefined}
          onChange={(e) => setRepeticion(e.target.value)}
        />
        <p className="text-sm text-texto-suave">Al menos 8 caracteres.</p>
        {guardado.isError && (
          <div className="space-y-2">
            <Aviso tipo="error">{mensajeError(guardado.error)}</Aviso>
            <p className="text-sm">
              <Link to={PATHS.recuperarContrasena} className={ESTILO_ENLACE}>
                Pedir un enlace nuevo
              </Link>
            </p>
          </div>
        )}
        <Boton type="submit" className="w-full" disabled={guardado.isPending}>
          {guardado.isPending ? 'Guardando…' : 'Guardar contraseña'}
        </Boton>
      </form>
    </PantallaAcceso>
  )
}
