// Forma uniforme de error que devuelve el backend (arquitectura, sección 6).
// Coincide con el record ApiError de Spring Boot: ver
// backend/src/main/java/com/rojasfc/gestion/exception/ApiError.java.
interface ApiErrorCuerpo {
  timestamp: string
  status: number
  error: string
  message: string
  path: string
}

// URL base de la API REST. Se toma de la variable VITE_API_URL (ver .env.example)
// y por defecto apunta al backend local de Spring Boot.
const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api'

// Clave bajo la que se guarda el JWT para las operaciones protegidas
// (arquitectura, sección 4.2). La sesión real se implementa en la Etapa 3.
const CLAVE_TOKEN = import.meta.env.VITE_API_TOKEN || 'rojas_fc_token'

// Error propio para que los llamadores puedan distinguir un fallo de la API
// de un error de programación, y leer el código HTTP.
export class ErrorApi extends Error {
  readonly estado: number
  readonly detalles?: unknown

  constructor(mensaje: string, estado: number, detalles?: unknown) {
    super(mensaje)
    this.name = 'ErrorApi'
    this.estado = estado
    this.detalles = detalles
  }
}

interface OpcionesPeticion extends Omit<RequestInit, 'body'> {
  cuerpo?: unknown
}

function obtenerToken(): string | null {
  return localStorage.getItem(CLAVE_TOKEN)
}

// Punto único de salida HTTP: centraliza la URL base, el envío del JWT y el
// manejo uniforme de errores (arquitectura, secciones 4.3 y 6). Los servicios
// de cada módulo se apoyan en esta función en lugar de llamar a fetch sueltos.
export async function peticion<T>(
  ruta: string,
  opciones: OpcionesPeticion = {},
): Promise<T> {
  const { cuerpo, headers, ...resto } = opciones

  const token = obtenerToken()
  const respuesta = await fetch(`${API_URL}${ruta}`, {
    ...resto,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...headers,
    },
    body: cuerpo !== undefined ? JSON.stringify(cuerpo) : undefined,
  })

  if (!respuesta.ok) {
    throw await construirError(respuesta)
  }

  // 204 (sin contenido) es la respuesta típica de un DELETE exitoso.
  if (respuesta.status === 204) {
    return undefined as T
  }

  return (await respuesta.json()) as T
}

async function construirError(respuesta: Response): Promise<ErrorApi> {
  let mensaje = `Error ${respuesta.status}: ${respuesta.statusText}`
  let detalles: unknown

  try {
    const cuerpo = (await respuesta.json()) as ApiErrorCuerpo
    if (cuerpo?.message) {
      mensaje = cuerpo.message
    }
    detalles = cuerpo
  } catch {
    // La respuesta de error no traía JSON: usamos el mensaje HTTP por defecto.
  }

  return new ErrorApi(mensaje, respuesta.status, detalles)
}

// Atajos para no repetir el método HTTP en cada servicio de módulo.
export const api = {
  get: <T>(ruta: string) => peticion<T>(ruta),
  post: <T>(ruta: string, cuerpo?: unknown) =>
    peticion<T>(ruta, { method: 'POST', cuerpo }),
  put: <T>(ruta: string, cuerpo?: unknown) =>
    peticion<T>(ruta, { method: 'PUT', cuerpo }),
  patch: <T>(ruta: string, cuerpo?: unknown) =>
    peticion<T>(ruta, { method: 'PATCH', cuerpo }),
  delete: <T>(ruta: string) => peticion<T>(ruta, { method: 'DELETE' }),
}
