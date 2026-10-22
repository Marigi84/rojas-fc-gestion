import type { ReactNode } from 'react'

export interface Columna<T> {
  titulo: string
  // Contenido de la celda para cada fila.
  valor: (fila: T) => ReactNode
  // Los números (DNI, montos) se alinean a la derecha y con cifras parejas.
  numerica?: boolean
}

interface PropsTabla<T> {
  columnas: Columna<T>[]
  filas: T[]
  claveFila: (fila: T) => string | number
  // Mensaje cuando no hay filas, por ejemplo "No hay alumnos con esos filtros".
  vacia: string
}

// Tabla común para los listados. En pantallas angostas se desliza de costado
// en lugar de desbordar la página.
export function Tabla<T>({ columnas, filas, claveFila, vacia }: PropsTabla<T>) {
  return (
    <div className="overflow-x-auto rounded-xl border border-borde bg-superficie">
      <table className="w-full border-collapse text-left">
        <thead>
          <tr>
            {columnas.map((columna) => (
              <th
                key={columna.titulo}
                scope="col"
                className={`border-b border-borde px-4 py-2.5 text-sm font-semibold whitespace-nowrap text-texto-suave ${
                  columna.numerica ? 'text-right' : ''
                }`}
              >
                {columna.titulo}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {filas.length === 0 ? (
            <tr>
              <td
                colSpan={columnas.length}
                className="px-4 py-6 text-center text-texto-suave"
              >
                {vacia}
              </td>
            </tr>
          ) : (
            filas.map((fila) => (
              <tr
                key={claveFila(fila)}
                className="border-b border-borde last:border-0"
              >
                {columnas.map((columna) => (
                  <td
                    key={columna.titulo}
                    className={`px-4 py-3 ${
                      columna.numerica ? 'text-right tabular-nums' : ''
                    }`}
                  >
                    {columna.valor(fila)}
                  </td>
                ))}
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  )
}
