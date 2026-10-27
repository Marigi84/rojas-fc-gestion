import type {
  PersonaResumen,
  ResponsableAlta,
  Vinculo,
} from '../../services/alumnos'

// Estado y armado de una fila de responsable en un formulario. Se usa desde
// CamposResponsable y desde las pantallas que cargan responsables.

// Situación del DNI cargado: persona nueva, persona ya registrada con otro rol
// (se reutilizan sus datos, RN-01) o responsable existente (solo se asocia).
export type EstadoDni = 'nueva' | 'registrada' | 'responsable'

export interface FilaResponsable {
  clave: number
  dni: string
  nombre: string
  apellido: string
  vinculo: Vinculo
  telefono: string
  contrasena: string
  estado: EstadoDni
}

export function filaResponsableVacia(clave: number): FilaResponsable {
  return {
    clave,
    dni: '',
    nombre: '',
    apellido: '',
    vinculo: 'MADRE',
    telefono: '',
    contrasena: '',
    estado: 'nueva',
  }
}

// Fila con un DNI nuevo. Si los datos venían de otra persona registrada, se
// borran para que no se envíen con el DNI nuevo.
export function conDniNuevo(
  fila: FilaResponsable,
  dni: string,
): FilaResponsable {
  const veniaDeOtraPersona = fila.estado !== 'nueva'
  return {
    ...fila,
    dni,
    estado: 'nueva',
    ...(veniaDeOtraPersona
      ? { nombre: '', apellido: '', telefono: '', contrasena: '' }
      : {}),
  }
}

// Cambios en la fila según la persona encontrada para su DNI.
export function datosDePersona(
  persona: PersonaResumen | null,
): Partial<FilaResponsable> {
  if (!persona) return { estado: 'nueva' }
  return {
    nombre: persona.nombre,
    apellido: persona.apellido,
    estado: persona.responsable ? 'responsable' : 'registrada',
  }
}

// Lo que se envía a la API. Teléfono y contraseña solo para quien todavía no
// es responsable; nombre y apellido solo para una persona nueva.
export function aResponsableAlta(fila: FilaResponsable): ResponsableAlta {
  const base = { dni: Number(fila.dni), vinculo: fila.vinculo }
  const nombres =
    fila.estado === 'nueva'
      ? { nombre: fila.nombre, apellido: fila.apellido }
      : {}
  const acceso =
    fila.estado === 'responsable'
      ? {}
      : { telefono: fila.telefono, contrasenaProvisoria: fila.contrasena }
  return { ...base, ...nombres, ...acceso }
}
