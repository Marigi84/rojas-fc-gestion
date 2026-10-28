import { BrowserRouter, Route, Routes } from 'react-router-dom'
import { Layout } from '../components/layout/Layout'
import {
  AlumnoAltaPage,
  AlumnoDetallePage,
  AlumnoEdicionPage,
  AlumnoResponsableAltaPage,
  AlumnosPage,
  CuotasPage,
  DashboardPage,
  EventosPage,
  LoginPage,
  PagosPage,
  PortalResponsablePage,
  ReportesPage,
  ResponsableAsociarPage,
  ResponsableDetallePage,
  ResponsableEdicionPage,
  ResponsablesPage,
  SinConexionPage,
} from '../pages'
import { PATHS } from './paths'

// Mapa de navegación: una ruta por cada módulo y, debajo, las pantallas de los
// módulos ya implementados. Los segmentos deben coincidir con los valores de
// PATHS y las funciones de ruta de src/routes/paths.ts.
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
          <Route path="alumnos/nuevo" element={<AlumnoAltaPage />} />
          <Route
            path="alumnos/:dni/responsables/nuevo"
            element={<AlumnoResponsableAltaPage />}
          />
          <Route path="alumnos/:dni" element={<AlumnoDetallePage />} />
          <Route path="alumnos/:dni/editar" element={<AlumnoEdicionPage />} />
          <Route path="responsables" element={<ResponsablesPage />} />
          <Route
            path="responsables/:dni"
            element={<ResponsableDetallePage />}
          />
          <Route
            path="responsables/:dni/editar"
            element={<ResponsableEdicionPage />}
          />
          <Route
            path="responsables/:dni/asociar"
            element={<ResponsableAsociarPage />}
          />
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
