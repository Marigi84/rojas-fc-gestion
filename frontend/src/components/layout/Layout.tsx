import { useQueryClient } from '@tanstack/react-query'
import { Link, NavLink, Outlet } from 'react-router-dom'
import escudo from '../../assets/escudo.png'
import { MODULOS, PATHS } from '../../routes/paths'
import { useSesion } from '../../stores/sesion'
import { Boton } from '../ui/Boton'

const NOMBRE_ROL = {
  ADMINISTRADOR: 'Administrador',
  RESPONSABLE: 'Responsable',
} as const

// Marco común de las pantallas internas: encabezado con el escudo, el usuario
// con sesión, menú de módulos y el lugar donde se renderiza cada página. La
// línea lima debajo del encabezado retoma el verde de la ropa del club.
// Solo se usa dentro de RutaProtegida, así que siempre hay sesión.
export function Layout() {
  const sesion = useSesion((estado) => estado.sesion)
  const salir = useSesion((estado) => estado.salir)
  const cliente = useQueryClient()
  if (!sesion) return null

  // El menú muestra solo los módulos del perfil (RF-52). Con la contraseña
  // provisoria no hay menú: primero hay que cambiarla (RNF-01).
  const modulos = sesion.contrasenaTemporal
    ? []
    : MODULOS.filter((modulo) => modulo.roles.includes(sesion.rol))

  function cerrarSesion() {
    // Se borran también los datos consultados, para que no los vea el próximo usuario.
    cliente.clear()
    salir()
  }

  return (
    <div className="flex min-h-screen flex-col">
      <header className="border-b-3 border-lima bg-superficie">
        <div className="mx-auto flex max-w-6xl flex-wrap items-center gap-x-3 gap-y-2 px-4 py-3 sm:px-6">
          <img
            src={escudo}
            alt="Escudo de Rojas FC"
            className="size-14 sm:size-16"
          />
          <span className="font-display text-2xl font-bold">Rojas FC</span>
          {/* En celular el usuario pasa a una línea propia debajo del escudo. */}
          <div className="order-last w-full text-sm sm:order-none sm:ml-auto sm:w-auto sm:text-right">
            <p className="font-semibold sm:text-base sm:leading-tight">
              {sesion.nombre} {sesion.apellido}
            </p>
            <p className="text-texto-suave">
              {NOMBRE_ROL[sesion.rol]} ·{' '}
              <Link
                to={PATHS.cambiarContrasena}
                className="text-verde-oscuro underline hover:text-verde"
              >
                Cambiar contraseña
              </Link>
            </p>
          </div>
          <Boton
            variante="secundario"
            className="ml-auto sm:ml-0"
            onClick={cerrarSesion}
          >
            Cerrar sesión
          </Boton>
        </div>
        <nav className="mx-auto flex max-w-6xl gap-1 overflow-x-auto px-4 whitespace-nowrap sm:px-6 lg:flex-wrap lg:overflow-visible">
          {modulos.map((modulo) => (
            <NavLink
              key={modulo.ruta}
              to={modulo.ruta}
              end={modulo.ruta === PATHS.dashboard}
              className={({ isActive }) =>
                `border-b-3 lg:-mb-[3px] px-3 py-2.5 font-medium ${
                  isActive
                    ? 'border-verde font-semibold text-verde-oscuro'
                    : 'border-transparent text-texto-suave hover:text-texto'
                }`
              }
            >
              {modulo.etiqueta}
            </NavLink>
          ))}
        </nav>
      </header>
      <main className="mx-auto w-full max-w-6xl flex-1 px-4 py-7 sm:px-6">
        <Outlet />
      </main>
    </div>
  )
}
