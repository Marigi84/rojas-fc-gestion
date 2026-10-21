import { NavLink, Outlet } from 'react-router-dom'
import { MODULOS, PATHS } from '../../routes/paths'

// Marco común de las pantallas internas: encabezado, menú de módulos y el
// lugar donde se renderiza cada página.
export function Layout() {
  return (
    <div className="app">
      <header className="app-header">
        <span className="app-marca">Rojas FC · Gestión</span>
        <nav className="app-nav">
          {MODULOS.map((modulo) => (
            <NavLink
              key={modulo.ruta}
              to={modulo.ruta}
              end={modulo.ruta === PATHS.dashboard}
              className={({ isActive }) => (isActive ? 'activo' : undefined)}
            >
              {modulo.etiqueta}
            </NavLink>
          ))}
        </nav>
        <NavLink to={PATHS.login} className="app-sesion">
          Iniciar sesión
        </NavLink>
      </header>
      <main className="app-main">
        <Outlet />
      </main>
    </div>
  )
}
