import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState, type FormEvent } from 'react'
import { Link, Navigate, useLocation } from 'react-router-dom'
import { PantallaAcceso } from '../components/layout/PantallaAcceso'
import { Aviso } from '../components/ui/Aviso'
import { Boton } from '../components/ui/Boton'
import { Campo } from '../components/ui/Campo'
import { inicioSegunRol, PATHS } from '../routes/paths'
import { authApi, CLAVE_CONFIGURACION } from '../services/auth'
import { useSesion } from '../stores/sesion'
import { mensajeError } from '../utils/errores'

// Ingreso del Administrador y del Responsable con DNI y contraseña (RF-49,
// RF-11). Si el sistema todavía no tiene Administrador, lleva a la
// configuración inicial.
export function LoginPage() {
  const sesion = useSesion((estado) => estado.sesion)
  const iniciar = useSesion((estado) => estado.iniciar)
  const cliente = useQueryClient()
  // Pantalla que se quiso abrir sin sesión, para volver a ella al ingresar.
  const desde = (useLocation().state as { desde?: string } | null)?.desde
  const [dni, setDni] = useState('')
  const [contrasena, setContrasena] = useState('')

  // Mientras se averigua si el sistema ya tiene Administrador, "Ingresar" queda
  // desactivado: sin Administrador, este formulario lleva a la configuración inicial.
  const configuracion = useQuery({
    queryKey: CLAVE_CONFIGURACION,
    queryFn: authApi.estadoConfiguracion,
    enabled: !sesion,
  })
  const ingreso = useMutation({
    mutationFn: authApi.ingresar,
    onSuccess: (nueva) => {
      // Si la sesión anterior venció, sus datos consultados no los ve este usuario.
      cliente.clear()
      iniciar(nueva)
    },
  })

  if (sesion) {
    return <Navigate to={desde ?? inicioSegunRol(sesion.rol)} replace />
  }
  if (configuracion.data && !configuracion.data.configurado) {
    return <Navigate to={PATHS.configuracionInicial} replace />
  }

  function enviar(evento: FormEvent) {
    evento.preventDefault()
    ingreso.mutate({ dni: Number(dni), contrasena })
  }

  return (
    <PantallaAcceso
      titulo="Iniciar sesión"
      descripcion="Ingresá con tu DNI y tu contraseña."
    >
      <form onSubmit={enviar} className="space-y-4">
        <Campo
          etiqueta="DNI"
          inputMode="numeric"
          pattern="[0-9]{1,10}"
          title="Solo números"
          autoComplete="username"
          required
          autoFocus
          value={dni}
          onChange={(e) => setDni(e.target.value.trim())}
        />
        <Campo
          etiqueta="Contraseña"
          type="password"
          autoComplete="current-password"
          required
          value={contrasena}
          onChange={(e) => setContrasena(e.target.value)}
        />
        {configuracion.isError && (
          <Aviso tipo="error">{mensajeError(configuracion.error)}</Aviso>
        )}
        {ingreso.isError && (
          <Aviso tipo="error">{mensajeError(ingreso.error)}</Aviso>
        )}
        <Boton
          type="submit"
          className="w-full"
          disabled={ingreso.isPending || configuracion.isPending}
        >
          {ingreso.isPending ? 'Ingresando…' : 'Ingresar'}
        </Boton>
        <p className="text-center text-sm">
          <Link
            to={PATHS.recuperarContrasena}
            className="text-verde-oscuro underline hover:text-verde"
          >
            ¿Olvidaste tu contraseña?
          </Link>
        </p>
      </form>
    </PantallaAcceso>
  )
}
