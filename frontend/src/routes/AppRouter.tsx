import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { Layout } from '../components/layout/Layout'
import {
  AdministradoresPage,
  AlumnoAltaPage,
  AlumnoDetallePage,
  AlumnoEdicionPage,
  AlumnosPage,
  CambioContrasenaPage,
  ConfiguracionInicialPage,
  CuotasPage,
  DashboardPage,
  EventosPage,
  LoginPage,
  NuevaContrasenaPage,
  PagosPage,
  PortalResponsablePage,
  RecuperarContrasenaPage,
  ReportesPage,
  SinConexionPage,
} from '../pages'
import { PATHS } from './paths'
import { RutaProtegida } from './RutaProtegida'

// Mapa de navegación: una ruta por cada módulo y, debajo, las pantallas de los
// módulos ya implementados. Los segmentos deben coincidir con los valores de
// PATHS y las funciones de ruta de src/routes/paths.ts.
// Protección por perfil (arquitectura, sección 4.5; RF-52): el ingreso, la
// configuración inicial y la recuperación de la contraseña son públicos; el cambio de contraseña y el portal
// piden sesión; el resto, sesión de Administrador.
export function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path={PATHS.login} element={<LoginPage />} />
        <Route
          path={PATHS.configuracionInicial}
          element={<ConfiguracionInicialPage />}
        />
        <Route
          path={PATHS.recuperarContrasena}
          element={<RecuperarContrasenaPage />}
        />
        <Route path={PATHS.nuevaContrasena} element={<NuevaContrasenaPage />} />
        <Route element={<RutaProtegida />}>
          <Route element={<Layout />}>
            <Route
              path="cambiar-contrasena"
              element={<CambioContrasenaPage />}
            />
            <Route path="portal" element={<PortalResponsablePage />} />
            <Route element={<RutaProtegida roles={['ADMINISTRADOR']} />}>
              <Route index element={<DashboardPage />} />
              <Route path="alumnos" element={<AlumnosPage />} />
              <Route path="alumnos/nuevo" element={<AlumnoAltaPage />} />
              <Route path="alumnos/:dni" element={<AlumnoDetallePage />} />
              <Route
                path="alumnos/:dni/editar"
                element={<AlumnoEdicionPage />}
              />
              <Route path="cuotas" element={<CuotasPage />} />
              <Route path="pagos" element={<PagosPage />} />
              <Route path="eventos" element={<EventosPage />} />
              <Route path="reportes" element={<ReportesPage />} />
              <Route path="sin-conexion" element={<SinConexionPage />} />
              <Route path="administradores" element={<AdministradoresPage />} />
            </Route>
          </Route>
        </Route>
        <Route path="*" element={<Navigate to={PATHS.dashboard} replace />} />
      </Routes>
    </BrowserRouter>
  )
}
