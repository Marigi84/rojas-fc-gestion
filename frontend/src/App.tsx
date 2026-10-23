import { QueryClientProvider } from '@tanstack/react-query'
import { AppRouter } from './routes/AppRouter'
import { queryClient } from './services/queryClient'

// La aplicación monta el cliente de TanStack Query (datos del servidor) y el
// router. El layout y las páginas viven en sus propias carpetas.
export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AppRouter />
    </QueryClientProvider>
  )
}
