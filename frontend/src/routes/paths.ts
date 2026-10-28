// Rutas de la aplicación. Cada ruta corresponde a uno de los 9 módulos
// definidos en docs/modulos/propuesta-modulos.md.
export const PATHS = {
  login: '/login',
  dashboard: '/',
  alumnos: '/alumnos',
  alumnoNuevo: '/alumnos/nuevo',
  responsables: '/responsables',
  portal: '/portal',
  cuotas: '/cuotas',
  pagos: '/pagos',
  eventos: '/eventos',
  reportes: '/reportes',
  sinConexion: '/sin-conexion',
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

// Entradas del menú de navegación principal: los módulos y los submodulos ya
// implementados, en el orden en que se muestran.
export const MODULOS = [
  { ruta: PATHS.dashboard, etiqueta: 'Dashboard' },
  { ruta: PATHS.alumnos, etiqueta: 'Alumnos' },
  { ruta: PATHS.responsables, etiqueta: 'Responsables' },
  { ruta: PATHS.portal, etiqueta: 'Portal del Responsable' },
  { ruta: PATHS.cuotas, etiqueta: 'Cuotas' },
  { ruta: PATHS.pagos, etiqueta: 'Pagos y Recibos' },
  { ruta: PATHS.eventos, etiqueta: 'Eventos e Indumentaria' },
  { ruta: PATHS.reportes, etiqueta: 'Reportes' },
  { ruta: PATHS.sinConexion, etiqueta: 'Sin Conexión' },
]
