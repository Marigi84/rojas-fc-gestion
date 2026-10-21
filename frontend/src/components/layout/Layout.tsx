import { NavLink, Outlet } from 'react-router-dom'
import escudo from '../../assets/escudo.png'
import { MODULOS, PATHS } from '../../routes/paths'

// Marco común de las pantallas internas: encabezado con el escudo, menú de
// módulos y el lugar donde se renderiza cada página. La línea lima debajo del
// encabezado retoma el color del borde del escudo.
export function Layout() {
  return (
    <div className="flex min-h-screen flex-col">
      <header className="border-b-3 border-lima bg-superficie">
        <div className="mx-auto flex max-w-6xl items-center gap-3 px-4 py-3 sm:px-6">
          <img src={escudo} alt="Escudo de Rojas FC" className="size-11" />
          <span className="font-display text-xl font-bold">Rojas FC</span>
          <NavLink
            to={PATHS.login}
            className="ml-auto font-medium text-verde-oscuro hover:underline"
          >
            Iniciar sesión
          </NavLink>
        </div>
        <nav className="mx-auto flex max-w-6xl gap-1 overflow-x-auto px-4 whitespace-nowrap sm:px-6 lg:flex-wrap lg:overflow-visible">
          {MODULOS.map((modulo) => (
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
