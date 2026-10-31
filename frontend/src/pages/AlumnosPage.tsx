import { useQuery } from '@tanstack/react-query'
import { useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Aviso } from '../components/ui/Aviso'
import { Boton } from '../components/ui/Boton'
import { Campo } from '../components/ui/Campo'
import { Selector } from '../components/ui/Selector'
import { Tabla } from '../components/ui/Tabla'
import { Tarjeta } from '../components/ui/Tarjeta'
import { PATHS, rutaAlumno } from '../routes/paths'
import {
  alumnosApi,
  claveListadoAlumnos,
  type FiltrosAlumnos,
} from '../services/alumnos'
import { mensajeError } from '../utils/errores'

const TAMANO_PAGINA = 20

interface FiltrosFormulario {
  busqueda: string
  anioNacimiento: string
  estado: string
  moroso: string
}

const FORMULARIO_INICIAL: FiltrosFormulario = {
  busqueda: '',
  anioNacimiento: '',
  estado: 'activo',
  moroso: '',
}

const FILTROS_INICIALES: FiltrosAlumnos = {
  ...FORMULARIO_INICIAL,
  pagina: 1,
  tamano: TAMANO_PAGINA,
}

const OPCIONES_ESTADO = [
  { valor: 'activo', etiqueta: 'Activos' },
  { valor: 'inactivo', etiqueta: 'Inactivos' },
  { valor: 'todos', etiqueta: 'Todos' },
]

const OPCIONES_MOROSIDAD = [
  { valor: '', etiqueta: 'Todos' },
  { valor: 'true', etiqueta: 'Morosos' },
  { valor: 'false', etiqueta: 'No morosos' },
]

// Listado de alumnos con búsqueda, filtros combinables y paginación
// (RF-38, RF-39). Por defecto muestra solo los activos.
export function AlumnosPage() {
  const navegar = useNavigate()
  const [formulario, setFormulario] = useState(FORMULARIO_INICIAL)
  const [filtros, setFiltros] = useState<FiltrosAlumnos>(FILTROS_INICIALES)
  const consulta = useQuery({
    queryKey: claveListadoAlumnos(filtros),
    queryFn: () => alumnosApi.listar(filtros),
  })

  function cambiar(campo: keyof FiltrosFormulario, valor: string) {
    setFormulario((actual) => ({ ...actual, [campo]: valor }))
  }

  function aplicar(evento: FormEvent) {
    evento.preventDefault()
    setFiltros({ ...formulario, pagina: 1, tamano: TAMANO_PAGINA })
  }

  function limpiar() {
    setFormulario(FORMULARIO_INICIAL)
    setFiltros(FILTROS_INICIALES)
  }

  function irAPagina(pagina: number) {
    setFiltros((actual) => ({ ...actual, pagina }))
  }

  const pagina = consulta.data

  return (
    <section className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <h1 className="text-3xl">Alumnos, Responsables y Categorías</h1>
        <Boton onClick={() => navegar(PATHS.alumnoNuevo)}>Nuevo alumno</Boton>
      </div>

      <Tarjeta titulo="Buscar alumnos">
        <form onSubmit={aplicar} className="space-y-4">
          <div className="grid gap-4 sm:grid-cols-2">
            <Campo
              etiqueta="Buscar"
              placeholder="Nombre, apellido o DNI"
              value={formulario.busqueda}
              onChange={(e) => cambiar('busqueda', e.target.value)}
            />
            <Campo
              etiqueta="Categoría (año de nacimiento)"
              type="number"
              min={1900}
              max={2100}
              placeholder="Ej. 2015"
              value={formulario.anioNacimiento}
              onChange={(e) => cambiar('anioNacimiento', e.target.value)}
            />
            <Selector
              etiqueta="Estado"
              opciones={OPCIONES_ESTADO}
              value={formulario.estado}
              onChange={(e) => cambiar('estado', e.target.value)}
            />
            <Selector
              etiqueta="Morosidad"
              opciones={OPCIONES_MOROSIDAD}
              value={formulario.moroso}
              onChange={(e) => cambiar('moroso', e.target.value)}
            />
          </div>
          <div className="flex flex-wrap gap-3">
            <Boton type="submit">Buscar</Boton>
            <Boton variante="secundario" onClick={limpiar}>
              Limpiar
            </Boton>
          </div>
        </form>
      </Tarjeta>

      <Tarjeta titulo="Alumnos">
        {consulta.isPending ? (
          <p className="text-texto-suave">Cargando alumnos…</p>
        ) : consulta.isError ? (
          <Aviso tipo="error">{mensajeError(consulta.error)}</Aviso>
        ) : (
          <>
            <Tabla
              columnas={[
                {
                  titulo: 'Nombre',
                  valor: (a) => (
                    <Link
                      to={rutaAlumno(a.dni)}
                      className="font-semibold underline"
                    >
                      {a.apellido}, {a.nombre}
                    </Link>
                  ),
                },
                { titulo: 'DNI', valor: (a) => a.dni, numerica: true },
                {
                  titulo: 'Año',
                  valor: (a) => a.anioNacimiento,
                  numerica: true,
                },
                {
                  titulo: 'Estado',
                  valor: (a) => (a.estado === 'activo' ? 'Activo' : 'Inactivo'),
                },
                {
                  titulo: 'Moroso',
                  valor: (a) => (a.moroso ? 'Sí' : 'No'),
                },
              ]}
              filas={pagina?.contenido ?? []}
              claveFila={(a) => a.dni}
              vacia="No hay alumnos para esos filtros."
            />
            {pagina && (
              <div className="mt-4 flex flex-wrap items-center justify-between gap-3">
                <p className="text-sm text-texto-suave">
                  {pagina.total} {pagina.total === 1 ? 'alumno' : 'alumnos'}
                  {pagina.totalPaginas > 1 &&
                    ` · Página ${pagina.pagina} de ${pagina.totalPaginas}`}
                </p>
                {pagina.totalPaginas > 1 && (
                  <div className="flex gap-2">
                    <Boton
                      variante="secundario"
                      disabled={pagina.pagina <= 1}
                      onClick={() => irAPagina(pagina.pagina - 1)}
                    >
                      Anterior
                    </Boton>
                    <Boton
                      variante="secundario"
                      disabled={pagina.pagina >= pagina.totalPaginas}
                      onClick={() => irAPagina(pagina.pagina + 1)}
                    >
                      Siguiente
                    </Boton>
                  </div>
                )}
              </div>
            )}
          </>
        )}
      </Tarjeta>
    </section>
  )
}
