import { BrowserRouter, Route, Routes } from 'react-router-dom'
import { Layout } from '../components/layout/Layout'
import {
  AlumnosPage,
  CuotasPage,
  DashboardPage,
  EventosPage,
  LoginPage,
  PagosPage,
  PortalResponsablePage,
  ReportesPage,
  SinConexionPage,
} from '../pages'
import { PATHS } from './paths'

// Mapa de navegación de la Etapa 2: una ruta por cada módulo, sin funcionalidad.
// Los segmentos deben coincidir con los valores de PATHS (src/routes/paths.ts).
// La protección por perfil (arquitectura, sección 4.5) se agrega en la Etapa 3,
// junto con la autenticación real.
export function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path={PATHS.login} element={<LoginPage />} />
        <Route element={<Layout />}>
          <Route index element={<DashboardPage />} />
          <Route path="alumnos" element={<AlumnosPage />} />
          <Route path="portal" element={<PortalResponsablePage />} />
          <Route path="cuotas" element={<CuotasPage />} />
          <Route path="pagos" element={<PagosPage />} />
          <Route path="eventos" element={<EventosPage />} />
          <Route path="reportes" element={<ReportesPage />} />
          <Route path="sin-conexion" element={<SinConexionPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}
