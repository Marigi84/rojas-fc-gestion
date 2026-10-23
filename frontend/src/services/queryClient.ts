import { QueryClient } from '@tanstack/react-query'
import { ErrorApi } from './apiClient'

// Cliente de TanStack Query compartido por todas las pantallas. Los errores
// 4xx (no encontrado, sin permiso, datos inválidos) no se reintentan: repetir
// el pedido daría el mismo resultado y demoraría el mensaje al usuario.
export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: (intentos, error) =>
        !(error instanceof ErrorApi && error.estado < 500) && intentos < 2,
      refetchOnWindowFocus: false,
    },
  },
})
