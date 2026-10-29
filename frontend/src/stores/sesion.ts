import { create } from 'zustand'
import { persist } from 'zustand/middleware'

export type Rol = 'ADMINISTRADOR' | 'RESPONSABLE'

// Lo que devuelve el backend al ingresar, al configurar el primer
// Administrador y al cambiar la contraseña (LoginResponse).
export interface Sesion {
  token: string
  rol: Rol
  dni: number
  nombre: string
  apellido: string
  contrasenaTemporal: boolean
}

interface EstadoSesion {
  sesion: Sesion | null
  // true si el usuario cerró la sesión con el botón; false si venció.
  cerradaPorElUsuario: boolean
  iniciar: (sesion: Sesion) => void
  cerrar: () => void
  salir: () => void
}

// Sesión del usuario (arquitectura, sección 4.2). Es el estado global de la
// aplicación, por eso vive en Zustand y no en TanStack Query. Se guarda en
// localStorage para que recargar la página no cierre la sesión.
export const useSesion = create<EstadoSesion>()(
  persist(
    (set) => ({
      sesion: null,
      cerradaPorElUsuario: false,
      iniciar: (sesion) => set({ sesion, cerradaPorElUsuario: false }),
      cerrar: () => set({ sesion: null }),
      salir: () => set({ sesion: null, cerradaPorElUsuario: true }),
    }),
    {
      name: 'rojas_fc_sesion',
      partialize: (estado) => ({ sesion: estado.sesion }),
      // Al abrir la aplicación se descarta una sesión con el token vencido.
      onRehydrateStorage: () => (estado) => {
        if (estado?.sesion && tokenVencido(estado.sesion.token)) {
          estado.cerrar()
        }
      },
    },
  ),
)

// El vencimiento (exp) viaja dentro del JWT. Leerlo acá solo sirve para no
// mostrar como iniciada una sesión vencida: el backend es el que valida.
function tokenVencido(token: string): boolean {
  try {
    const carga = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')
    const { exp } = JSON.parse(atob(carga)) as { exp?: unknown }
    return typeof exp !== 'number' || exp * 1000 <= Date.now()
  } catch {
    return true
  }
}
