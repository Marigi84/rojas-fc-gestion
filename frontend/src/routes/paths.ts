import type { Rol } from '../stores/sesion'

// Rutas de la aplicación. Corresponden a los módulos de
// docs/modulos/propuesta-modulos.md que entran en la entrega final (ver
// docs/alcance/revision-del-alcance.md, sección 3).
export const PATHS = {
  login: '/login',
  configuracionInicial: '/configuracion-inicial',
  cambiarContrasena: '/cambiar-contrasena',
  recuperarContrasena: '/recuperar-contrasena',
  nuevaContrasena: '/restablecer-contrasena',
  dashboard: '/',
  alumnos: '/alumnos',
  alumnoNuevo: '/alumnos/nuevo',
  responsables: '/responsables',
  portal: '/portal',
  cuotas: '/cuotas',
  pagos: '/pagos',
  administradores: '/administradores',
} as const

// Rutas con parámetros del Módulo 2.
export const rutaAlumno = (dni: number | string) => `/alumnos/${dni}`
export const rutaEditarAlumno = (dni: number | string) =>
  `/alumnos/${dni}/editar`
export const rutaNuevoResponsable = (dniAlumno: number | string) =>
  `/alumnos/${dniAlumno}/responsables/nuevo`
export const rutaResponsable = (dni: number | string) => `/responsables/${dni}`
export const rutaEditarResponsable = (dni: number | string) =>
  `/responsables/${dni}/editar`
export const rutaAsociarResponsable = (dni: number | string) =>
  `/responsables/${dni}/asociar`

// Ruta del recibo de un pago.
export const rutaRecibo = (numeroRecibo: number | string) =>
  `/recibos/${numeroRecibo}`

const SOLO_ADMINISTRADOR: Rol[] = ['ADMINISTRADOR']
const AMBOS_PERFILES: Rol[] = ['ADMINISTRADOR', 'RESPONSABLE']

// Entradas del menú de navegación principal, en el mismo orden que los módulos,
// con los perfiles que las ven (RF-52). El Responsable solo ve su portal.
export const MODULOS = [
  { ruta: PATHS.dashboard, etiqueta: 'Dashboard', roles: SOLO_ADMINISTRADOR },
  { ruta: PATHS.alumnos, etiqueta: 'Alumnos', roles: SOLO_ADMINISTRADOR },
  {
    ruta: PATHS.responsables,
    etiqueta: 'Responsables',
    roles: SOLO_ADMINISTRADOR,
  },
  {
    ruta: PATHS.portal,
    etiqueta: 'Portal del Responsable',
    roles: AMBOS_PERFILES,
  },
  { ruta: PATHS.cuotas, etiqueta: 'Cuotas', roles: SOLO_ADMINISTRADOR },
  { ruta: PATHS.pagos, etiqueta: 'Pagos y Recibos', roles: SOLO_ADMINISTRADOR },
  // Parte del Módulo 1 (RF-51): va al final para no mezclarse con los módulos.
  {
    ruta: PATHS.administradores,
    etiqueta: 'Administradores',
    roles: SOLO_ADMINISTRADOR,
  },
]

// Pantalla a la que entra cada perfil después de iniciar sesión.
export function inicioSegunRol(rol: Rol): string {
  return rol === 'RESPONSABLE' ? PATHS.portal : PATHS.dashboard
}
