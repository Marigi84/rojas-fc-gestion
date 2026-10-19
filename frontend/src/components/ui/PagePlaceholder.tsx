interface PropsPaginaBase {
  titulo: string
}

// Componente compartido por las pantallas de la Etapa 2: cada una muestra
// únicamente el nombre de su sección, sin funcionalidad ni datos. El contenido
// real llega en la Etapa 3, una vez aprobado el esquema de base de datos.
export function PagePlaceholder({ titulo }: PropsPaginaBase) {
  return (
    <section>
      <h1>{titulo}</h1>
    </section>
  )
}
