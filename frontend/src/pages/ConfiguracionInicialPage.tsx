import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState, type FormEvent } from 'react'
import { Navigate } from 'react-router-dom'
import { PantallaAcceso } from '../components/layout/PantallaAcceso'
import { Aviso } from '../components/ui/Aviso'
import { Boton } from '../components/ui/Boton'
import { Campo } from '../components/ui/Campo'
import { inicioSegunRol, PATHS } from '../routes/paths'
import {
  authApi,
  CLAVE_CONFIGURACION,
  type ConfiguracionInicial,
} from '../services/auth'
import { useSesion } from '../stores/sesion'
import { mensajeError } from '../utils/errores'

type DatosFormulario = Omit<ConfiguracionInicial, 'dni'> & {
  dni: string
  repeticion: string
}

// Alta del primer Administrador. Solo se puede usar mientras el sistema no
// tiene ninguno; después, el backend la rechaza y esta pantalla lleva al
// ingreso. Al terminar, la sesión queda iniciada.
export function ConfiguracionInicialPage() {
  const sesion = useSesion((estado) => estado.sesion)
  const iniciar = useSesion((estado) => estado.iniciar)
  const cliente = useQueryClient()
  const [datos, setDatos] = useState<DatosFormulario>({
    dni: '',
    nombre: '',
    apellido: '',
    email: '',
    contrasena: '',
    repeticion: '',
  })

  const configuracion = useQuery({
    queryKey: CLAVE_CONFIGURACION,
    queryFn: authApi.estadoConfiguracion,
    enabled: !sesion,
  })
  const alta = useMutation({
    mutationFn: authApi.configurar,
    onSuccess: (nueva) => {
      cliente.setQueryData(CLAVE_CONFIGURACION, { configurado: true })
      iniciar(nueva)
    },
  })

  if (sesion) {
    return <Navigate to={inicioSegunRol(sesion.rol)} replace />
  }
  if (configuracion.data?.configurado) {
    return <Navigate to={PATHS.login} replace />
  }

  const noCoinciden =
    datos.repeticion !== '' && datos.repeticion !== datos.contrasena

  function cambiar(campo: keyof DatosFormulario, valor: string) {
    setDatos((actual) => ({ ...actual, [campo]: valor }))
  }

  function enviar(evento: FormEvent) {
    evento.preventDefault()
    if (noCoinciden) return
    alta.mutate({
      dni: Number(datos.dni),
      nombre: datos.nombre.trim(),
      apellido: datos.apellido.trim(),
      email: datos.email.trim(),
      contrasena: datos.contrasena,
    })
  }

  return (
    <PantallaAcceso
      titulo="Configuración inicial"
      descripcion="Es la primera vez que se usa el sistema. Cargá los datos del primer Administrador."
    >
      {configuracion.isPending ? (
        <p className="text-texto-suave">Revisando el sistema…</p>
      ) : (
        <form onSubmit={enviar} className="space-y-4">
          <Campo
            etiqueta="DNI"
            inputMode="numeric"
            pattern="[0-9]{1,10}"
            title="Solo números"
            autoComplete="username"
            required
            value={datos.dni}
            onChange={(e) => cambiar('dni', e.target.value.trim())}
          />
          <div className="grid gap-4 sm:grid-cols-2">
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
          </div>
          <Campo
            etiqueta="Correo electrónico"
            type="email"
            autoComplete="email"
            required
            maxLength={120}
            value={datos.email}
            onChange={(e) => cambiar('email', e.target.value)}
          />
          <Campo
            etiqueta="Contraseña"
            type="password"
            autoComplete="new-password"
            required
            minLength={8}
            maxLength={72}
            value={datos.contrasena}
            onChange={(e) => cambiar('contrasena', e.target.value)}
          />
          <Campo
            etiqueta="Repetir contraseña"
            type="password"
            autoComplete="new-password"
            required
            value={datos.repeticion}
            error={noCoinciden ? 'Las contraseñas no coinciden.' : undefined}
            onChange={(e) => cambiar('repeticion', e.target.value)}
          />
          <p className="text-sm text-texto-suave">
            La contraseña tiene que tener al menos 8 caracteres. El correo se
            usa para recuperar la contraseña.
          </p>
          {configuracion.isError && (
            <Aviso tipo="error">{mensajeError(configuracion.error)}</Aviso>
          )}
          {alta.isError && (
            <Aviso tipo="error">{mensajeError(alta.error)}</Aviso>
          )}
          <Boton type="submit" className="w-full" disabled={alta.isPending}>
            {alta.isPending ? 'Guardando…' : 'Crear Administrador'}
          </Boton>
        </form>
      )}
    </PantallaAcceso>
  )
}
