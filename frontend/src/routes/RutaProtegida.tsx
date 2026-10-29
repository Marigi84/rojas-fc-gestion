import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useSesion, type Rol } from '../stores/sesion'
import { inicioSegunRol, PATHS } from './paths'

interface PropsRutaProtegida {
  // Perfiles que pueden entrar. Sin roles, alcanza con tener sesión.
  roles?: Rol[]
}

// Protección de rutas según el perfil (arquitectura, sección 4.5; RF-52).
// Sin sesión lleva al ingreso; con la contraseña provisoria, solo deja
// cambiarla (RNF-01); con otro perfil, lleva al inicio de ese perfil. Es una
// ayuda para el usuario: los permisos los valida siempre el backend.
export function RutaProtegida({ roles }: PropsRutaProtegida) {
  const sesion = useSesion((estado) => estado.sesion)
  const cerradaPorElUsuario = useSesion((estado) => estado.cerradaPorElUsuario)
  const ubicacion = useLocation()

  if (!sesion) {
    // Si la sesión venció, al volver a ingresar se retoma esta pantalla. Si el
    // usuario la cerró, el próximo que ingrese entra a su inicio.
    // Se guarda la ruta completa, con sus filtros (?...) y su ancla (#...).
    const desde = ubicacion.pathname + ubicacion.search + ubicacion.hash
    const estado = cerradaPorElUsuario ? null : { desde }
    return <Navigate to={PATHS.login} replace state={estado} />
  }
  if (
    sesion.contrasenaTemporal &&
    ubicacion.pathname !== PATHS.cambiarContrasena
  ) {
    return <Navigate to={PATHS.cambiarContrasena} replace />
  }
  if (roles && !roles.includes(sesion.rol)) {
    return <Navigate to={inicioSegunRol(sesion.rol)} replace />
  }
  return <Outlet />
}
