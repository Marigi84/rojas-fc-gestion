import { AppRouter } from './routes/AppRouter'

// La aplicación sólo monta el router. El layout y las páginas viven en sus
// propias carpetas (ver src/routes/AppRouter.tsx).
export default function App() {
  return <AppRouter />
}
