import { useMutation } from '@tanstack/react-query'
import { useState, type FormEvent } from 'react'
import { Link } from 'react-router-dom'
import { PantallaAcceso } from '../components/layout/PantallaAcceso'
import { Aviso } from '../components/ui/Aviso'
import { Boton } from '../components/ui/Boton'
import { Campo } from '../components/ui/Campo'
import { PATHS } from '../routes/paths'
import { authApi } from '../services/auth'
import { mensajeError } from '../utils/errores'

// El Administrador pide un enlace a su correo para elegir una contraseña nueva
// (RF-50). El Responsable no la recupera así: se la restablece el
// Administrador (RF-15).
export function RecuperarContrasenaPage() {
  const [email, setEmail] = useState('')
  const pedido = useMutation({ mutationFn: authApi.solicitarRecuperacion })

  function enviar(evento: FormEvent) {
    evento.preventDefault()
    pedido.mutate(email.trim())
  }

  return (
    <PantallaAcceso
      titulo="Recuperar contraseña"
      descripcion="Escribí el correo con el que estás registrado como administrador y te enviamos un enlace para elegir una contraseña nueva."
    >
      {pedido.isSuccess ? (
        <div className="space-y-4">
          <Aviso>
            Si {email.trim()} es el correo de un administrador, te enviamos un
            enlace. Revisá tu bandeja de entrada y el correo no deseado. El
            enlace vence en unos minutos: en el correo figura cuántos.
          </Aviso>
          <VolverAlIngreso />
        </div>
      ) : (
        <form onSubmit={enviar} className="space-y-4">
          <Campo
            etiqueta="Correo electrónico"
            type="email"
            autoComplete="email"
            required
            autoFocus
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          {pedido.isError && (
            <Aviso tipo="error">{mensajeError(pedido.error)}</Aviso>
          )}
          <Boton type="submit" className="w-full" disabled={pedido.isPending}>
            {pedido.isPending ? 'Enviando…' : 'Enviar enlace'}
          </Boton>
          <p className="text-sm text-texto-suave">
            Si sos responsable de un alumno, pedile al administrador de la
            escuela que te asigne una contraseña nueva.
          </p>
          <VolverAlIngreso />
        </form>
      )}
    </PantallaAcceso>
  )
}

function VolverAlIngreso() {
  return (
    <p className="text-center text-sm">
      <Link
        to={PATHS.login}
        className="text-verde-oscuro underline hover:text-verde"
      >
        Volver al ingreso
      </Link>
    </p>
  )
}
