interface PropsPaginaBase {
  titulo: string
}

// Pantalla de un módulo que todavía no está disponible: muestra solo su nombre.
export function PagePlaceholder({ titulo }: PropsPaginaBase) {
  return (
    <section>
      <h1 className="text-3xl">{titulo}</h1>
    </section>
  )
}
