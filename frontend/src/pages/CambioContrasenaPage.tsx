import { useMutation } from '@tanstack/react-query'
import { useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { Aviso } from '../components/ui/Aviso'
import { Boton } from '../components/ui/Boton'
import { Campo } from '../components/ui/Campo'
import { Tarjeta } from '../components/ui/Tarjeta'
import { inicioSegunRol } from '../routes/paths'
import { authApi } from '../services/auth'
import { useSesion } from '../stores/sesion'
import { mensajeError } from '../utils/errores'

const VACIO = { actual: '', nueva: '', repeticion: '' }

// Cambio de contraseña del usuario con sesión. Con la contraseña provisoria es
// obligatorio antes de usar el sistema (RNF-01); si no, es opcional.
export function CambioContrasenaPage() {
  const navegar = useNavigate()
  const sesion = useSesion((estado) => estado.sesion)
  const iniciar = useSesion((estado) => estado.iniciar)
  const [datos, setDatos] = useState(VACIO)
  const [cambiada, setCambiada] = useState(false)
  // Se toma al abrir la pantalla: después del cambio la sesión ya no es provisoria.
  const [obligatorio] = useState(sesion?.contrasenaTemporal ?? false)

  const cambio = useMutation({
    mutationFn: authApi.cambiarContrasena,
    onSuccess: (nueva) => {
      // El backend devuelve un token nuevo, ya sin la marca de provisoria.
      iniciar(nueva)
      if (obligatorio) {
        navegar(inicioSegunRol(nueva.rol), { replace: true })
      } else {
        setDatos(VACIO)
        setCambiada(true)
      }
    },
  })

  const noCoinciden =
    datos.repeticion !== '' && datos.repeticion !== datos.nueva

  function cambiar(campo: keyof typeof VACIO, valor: string) {
    setCambiada(false)
    setDatos((actual) => ({ ...actual, [campo]: valor }))
  }

  function enviar(evento: FormEvent) {
    evento.preventDefault()
    if (noCoinciden) return
    cambio.mutate({ actual: datos.actual, nueva: datos.nueva })
  }

  return (
    <section className="mx-auto max-w-md space-y-6">
      <h1 className="text-3xl">Cambiar contraseña</h1>
      {obligatorio && (
        <Aviso>
          Tu contraseña es provisoria. Para empezar a usar el sistema, elegí una
          contraseña nueva.
        </Aviso>
      )}
      <Tarjeta>
        <form onSubmit={enviar} className="space-y-4">
          <Campo
            etiqueta={
              obligatorio ? 'Contraseña provisoria' : 'Contraseña actual'
            }
            type="password"
            autoComplete="current-password"
            required
            value={datos.actual}
            onChange={(e) => cambiar('actual', e.target.value)}
          />
          <Campo
            etiqueta="Contraseña nueva"
            type="password"
            autoComplete="new-password"
            required
            minLength={8}
            maxLength={72}
            value={datos.nueva}
            onChange={(e) => cambiar('nueva', e.target.value)}
          />
          <Campo
            etiqueta="Repetir contraseña nueva"
            type="password"
            autoComplete="new-password"
            required
            value={datos.repeticion}
            error={noCoinciden ? 'Las contraseñas no coinciden.' : undefined}
            onChange={(e) => cambiar('repeticion', e.target.value)}
          />
          <p className="text-sm text-texto-suave">
            Al menos 8 caracteres, distinta de la actual.
          </p>
          {cambio.isError && (
            <Aviso tipo="error">{mensajeError(cambio.error)}</Aviso>
          )}
          {cambiada && <Aviso>Listo, tu contraseña se cambió.</Aviso>}
          <Boton type="submit" className="w-full" disabled={cambio.isPending}>
            {cambio.isPending ? 'Guardando…' : 'Cambiar contraseña'}
          </Boton>
        </form>
      </Tarjeta>
    </section>
  )
}
