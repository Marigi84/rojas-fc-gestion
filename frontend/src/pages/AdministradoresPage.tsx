import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useRef, useState, type FormEvent } from 'react'
import { Aviso } from '../components/ui/Aviso'
import { Boton } from '../components/ui/Boton'
import { Campo } from '../components/ui/Campo'
import { Tabla } from '../components/ui/Tabla'
import { Tarjeta } from '../components/ui/Tarjeta'
import {
  administradoresApi,
  CLAVE_ADMINISTRADORES,
} from '../services/administradores'
import { buscarPersonaSiExiste, type PersonaResumen } from '../services/alumnos'
import { mensajeError } from '../utils/errores'

const VACIO = {
  dni: '',
  nombre: '',
  apellido: '',
  email: '',
  contrasena: '',
  repeticion: '',
}

// Administradores del sistema y alta de uno nuevo (Módulo 1, RF-51). La
// contraseña la asigna quien lo registra y queda provisoria (RNF-01).
export function AdministradoresPage() {
  const cliente = useQueryClient()
  const [datos, setDatos] = useState(VACIO)
  // Persona ya registrada con el DNI escrito: se usan su nombre y apellido (RN-01).
  const [persona, setPersona] = useState<PersonaResumen | null>(null)
  const [errorBusqueda, setErrorBusqueda] = useState<string | null>(null)
  const [registrado, setRegistrado] = useState<string | null>(null)
  // DNI tal como está escrito ahora, para descartar búsquedas viejas.
  const dniActual = useRef('')

  const listado = useQuery({
    queryKey: CLAVE_ADMINISTRADORES,
    queryFn: administradoresApi.listar,
  })
  const alta = useMutation({
    mutationFn: administradoresApi.registrar,
    onSuccess: (nuevo) => {
      cliente.invalidateQueries({ queryKey: CLAVE_ADMINISTRADORES })
      setRegistrado(`${nuevo.nombre} ${nuevo.apellido}`)
      setDatos(VACIO)
      setPersona(null)
      dniActual.current = ''
    },
  })

  const noCoinciden =
    datos.repeticion !== '' && datos.repeticion !== datos.contrasena
  const yaEsAdministrador = persona?.administrador ?? false

  function cambiar(campo: keyof typeof VACIO, valor: string) {
    setRegistrado(null)
    setDatos((actual) => ({ ...actual, [campo]: valor }))
  }

  function cambiarDni(dni: string) {
    dniActual.current = dni
    setRegistrado(null)
    setErrorBusqueda(null)
    alta.reset()
    // Si el nombre venía de otra persona registrada, no se arrastra al DNI nuevo.
    setDatos((actual) => ({
      ...actual,
      dni,
      ...(persona ? { nombre: '', apellido: '' } : {}),
    }))
    setPersona(null)
  }

  async function revisarDni() {
    const dni = datos.dni
    try {
      const encontrada = await buscarPersonaSiExiste(dni)
      // Si el DNI cambió mientras se buscaba, la respuesta ya no corresponde.
      if (dniActual.current !== dni) return
      setPersona(encontrada)
      if (encontrada) {
        setDatos((actual) => ({
          ...actual,
          nombre: encontrada.nombre,
          apellido: encontrada.apellido,
        }))
      }
    } catch (error) {
      setErrorBusqueda(mensajeError(error))
    }
  }

  function enviar(evento: FormEvent) {
    evento.preventDefault()
    if (noCoinciden || yaEsAdministrador) return
    alta.mutate({
      dni: Number(datos.dni),
      ...(persona
        ? {}
        : { nombre: datos.nombre.trim(), apellido: datos.apellido.trim() }),
      email: datos.email.trim(),
      contrasenaProvisoria: datos.contrasena,
    })
  }

  let contenidoListado
  if (listado.isPending) {
    contenidoListado = (
      <p className="text-texto-suave">Cargando administradores…</p>
    )
  } else if (listado.isError) {
    contenidoListado = <Aviso tipo="error">{mensajeError(listado.error)}</Aviso>
  } else {
    contenidoListado = (
      <Tabla
        columnas={[
          { titulo: 'Nombre', valor: (a) => `${a.apellido}, ${a.nombre}` },
          { titulo: 'DNI', valor: (a) => a.dni, numerica: true },
          { titulo: 'Correo', valor: (a) => a.email },
          {
            titulo: 'Contraseña',
            valor: (a) =>
              a.contrasenaTemporal ? 'Provisoria, sin cambiar' : 'Propia',
          },
        ]}
        filas={listado.data}
        claveFila={(a) => a.dni}
        vacia="No hay administradores."
      />
    )
  }

  return (
    <section className="space-y-6">
      <h1 className="text-3xl">Administradores</h1>

      <Tarjeta titulo="Administradores del sistema">{contenidoListado}</Tarjeta>

      <Tarjeta titulo="Registrar administrador">
        <form onSubmit={enviar} className="space-y-4">
          <div className="grid gap-4 sm:grid-cols-2">
            <Campo
              etiqueta="DNI"
              inputMode="numeric"
              pattern="[0-9]{1,10}"
              title="Solo números"
              required
              value={datos.dni}
              onChange={(e) => cambiarDni(e.target.value.trim())}
              onBlur={revisarDni}
            />
            <Campo
              etiqueta="Correo electrónico"
              type="email"
              required
              maxLength={120}
              value={datos.email}
              onChange={(e) => cambiar('email', e.target.value)}
            />
            <Campo
              etiqueta="Nombre"
              required
              maxLength={60}
              readOnly={persona !== null}
              value={datos.nombre}
              onChange={(e) => cambiar('nombre', e.target.value)}
            />
            <Campo
              etiqueta="Apellido"
              required
              maxLength={60}
              readOnly={persona !== null}
              value={datos.apellido}
              onChange={(e) => cambiar('apellido', e.target.value)}
            />
            <Campo
              etiqueta="Contraseña provisoria"
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
          </div>
          <p className="text-sm text-texto-suave">
            La contraseña es provisoria: el nuevo administrador la cambia en su
            primer ingreso. El correo sirve para recuperar la contraseña.
          </p>
          {yaEsAdministrador && (
            <Aviso tipo="error">
              {persona?.nombre} {persona?.apellido} ya es administrador.
            </Aviso>
          )}
          {persona && !yaEsAdministrador && (
            <Aviso>
              Esta persona ya está registrada en el sistema: se usan su nombre y
              apellido.
            </Aviso>
          )}
          {errorBusqueda && <Aviso tipo="error">{errorBusqueda}</Aviso>}
          {alta.isError && (
            <Aviso tipo="error">{mensajeError(alta.error)}</Aviso>
          )}
          {registrado && (
            <Aviso>
              Listo: {registrado} ya es administrador. Pasale su DNI y la
              contraseña provisoria para que ingrese.
            </Aviso>
          )}
          <Boton type="submit" disabled={alta.isPending || yaEsAdministrador}>
            {alta.isPending ? 'Registrando…' : 'Registrar administrador'}
          </Boton>
        </form>
      </Tarjeta>
    </section>
  )
}
