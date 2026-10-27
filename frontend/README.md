# Frontend — Sistema de Gestión Rojas FC

Aplicación web del Sistema de Gestión Rojas FC, desarrollada con **React + TypeScript + Vite**.

> **Estado:** Etapa 2 — estructura base. Incluye el proyecto, la organización de
> carpetas, la navegación entre los 9 módulos y el servicio base de API. Todavía
> **no hay pantallas con funcionalidad ni datos**: eso corresponde a otra etapa,
> una vez aprobado el esquema de base de datos.

## Requisitos previos

- **Node.js** 20.19 o superior (probado con Node 26). Vite requiere `^20.19.0 || >=22.12.0`.
- **npm** (viene incluido con Node).

Podés verificar las versiones instaladas con:

```bash
node -v
npm -v
```

## Instalación

Desde la raíz del repositorio:

```bash
cd frontend
npm install
```

## Variables de entorno (opcional)

El frontend lee la URL del backend desde la variable `VITE_API_URL`. Si querés
cambiarla, copiá el archivo de ejemplo y editalo:

```bash
cp .env.example .env
```

Si no creás el `.env`, se usa el valor por defecto `http://localhost:8080/api`.

## Levantar el proyecto en desarrollo

```bash
npm run dev
```

Vite levanta un servidor de desarrollo. La consola muestra la dirección, por
defecto:

```
http://localhost:5173/
```

Abrí esa URL en el navegador. Los cambios que hagas en el código se reflejan al
instante (recarga en caliente / HMR).

## Scripts disponibles

| Script                 | Qué hace                                              |
| ---------------------- | ----------------------------------------------------- |
| `npm run dev`          | Levanta el servidor de desarrollo.                    |
| `npm run build`        | Compila TypeScript y genera la versión de producción. |
| `npm run preview`      | Sirve localmente la versión ya compilada (`dist/`).   |
| `npm run lint`         | Revisa el código con ESLint.                          |
| `npm run lint:fix`     | Igual que `lint`, pero corrige lo que pueda.          |
| `npm run format`       | Formatea el código con Prettier.                      |
| `npm run format:check` | Verifica el formato sin modificar archivos.           |

## Estructura de carpetas

```text
frontend/
├── public/              → archivos estáticos servidos tal cual (favicon)
├── src/
│   ├── components/      → componentes reutilizables
│   │   ├── layout/      → marco común de las pantallas (Layout)
│   │   └── ui/          → componentes de interfaz genéricos (Boton, Campo, Selector, Aviso, Tarjeta, Tabla)
│   ├── pages/           → una pantalla por módulo
│   ├── routes/          → definición de rutas y navegación
│   ├── services/        → comunicación con la API REST y cliente de TanStack Query
│   ├── utils/           → funciones auxiliares (fechas, mensajes de error)
│   ├── assets/          → imágenes propias (escudo del club)
│   ├── App.tsx          → componente raíz (TanStack Query y router)
│   ├── main.tsx         → punto de entrada de la aplicación
│   └── index.css        → Tailwind, paleta y tipografía
├── .env.example         → variables de entorno de ejemplo
├── eslint.config.js     → configuración de ESLint
├── .prettierrc.json     → configuración de Prettier
├── index.html           → documento HTML base
└── vite.config.ts       → configuración de Vite
```

## Rutas

La navegación base incluye las secciones de los 9 módulos del sistema
(definidos en `docs/modulos/propuesta-modulos.md`), más el login.

| Ruta            | Módulo                                          |
| --------------- | ----------------------------------------------- |
| `/`             | 8. Dashboard                                    |
| `/alumnos`      | 2. Alumnos, Responsables y Categorías           |
| `/portal`       | 3. Portal del Responsable                       |
| `/cuotas`       | 4. Cuotas                                       |
| `/pagos`        | 5. Pagos y Recibos                              |
| `/eventos`      | 6. Eventos e Indumentaria                       |
| `/reportes`     | 7. Búsquedas, Listados, Exportación y Reportes  |
| `/sin-conexion` | 9. Funcionamiento sin Conexión y Sincronización |
| `/login`        | 1. Autenticación y Autorización                 |

Del Módulo 1 están además estas pantallas:

| Ruta                     | Pantalla                                               |
| ------------------------ | ------------------------------------------------------ |
| `/configuracion-inicial` | Alta del primer Administrador (solo si todavía no hay) |
| `/cambiar-contrasena`    | Cambio de contraseña (obligatorio con la provisoria)   |

Dentro del Módulo 2 están además las pantallas de alumnos:

| Ruta                   | Pantalla                                |
| ---------------------- | --------------------------------------- |
| `/alumnos/nuevo`       | Alta de un alumno con sus responsables  |
| `/alumnos/:dni`        | Detalle, historial, baja y reactivación |
| `/alumnos/:dni/editar` | Modificación de los datos del alumno    |

Las rutas se definen en `src/routes/` (`paths.ts` y `AppRouter.tsx`).

## Sesión y rutas protegidas

La sesión (token JWT, perfil y nombre del usuario) se guarda con **Zustand** en
`src/stores/sesion.ts` y se conserva en `localStorage`, así que recargar la
página no la cierra. `apiClient` toma el token de ahí.

`src/routes/RutaProtegida.tsx` decide quién entra a cada ruta (RF-52):

- **Sin sesión:** lleva a `/login`. Si el sistema todavía no tiene
  Administrador, el ingreso lleva a `/configuracion-inicial`.
- **Con la contraseña provisoria:** solo deja entrar a `/cambiar-contrasena`
  (RNF-01).
- **Responsable:** solo ve el Portal del Responsable; el resto de las rutas lo
  lleva a `/portal`.
- **Administrador:** entra a todas las rutas.

Si el backend responde 401 (token vencido o inválido), la sesión se cierra y se
vuelve al ingreso; al ingresar de nuevo se retoma la pantalla en la que estaba.
Esto es una ayuda para el usuario: los permisos los valida siempre el backend.

## Servicio de comunicación con la API

`src/services/apiClient.ts` centraliza todas las llamadas HTTP al backend:
define la URL base, adjunta el token JWT cuando exista y unifica el manejo de
errores. Los servicios de cada módulo deben usar este cliente en lugar de
llamar a `fetch` directamente.

Los datos del servidor se manejan con **TanStack Query**
(`src/services/queryClient.ts`): `useQuery` para consultar y `useMutation` para
registrar o modificar. Los errores 4xx no se reintentan.

## Estilos

Los estilos se escriben con **Tailwind CSS**, con clases directamente en cada
componente. La paleta y la tipografía están definidas en `src/index.css`
(bloque `@theme`) y generan sus propias clases:

| Token                                                  | Uso                                            | Ejemplo de clase    |
| ------------------------------------------------------ | ---------------------------------------------- | ------------------- |
| `verde`                                                | acción principal, sección activa               | `bg-verde`          |
| `verde-oscuro`                                         | texto verde sobre fondo claro                  | `text-verde-oscuro` |
| `lima`                                                 | detalles (línea del encabezado, foco)          | `border-lima`       |
| `rojo`                                                 | errores, deudas y acciones de baja o anulación | `text-rojo`         |
| `fondo`, `superficie`, `borde`, `texto`, `texto-suave` | base de la interfaz                            | `bg-superficie`     |

La letra es **Barlow** (textos) y **Barlow Semi Condensed** (títulos), instaladas
con `@fontsource` para que funcionen sin conexión.

Antes de crear un estilo nuevo, conviene reutilizar los componentes de
`src/components/ui/`: `Boton` (variantes principal, secundario y peligro),
`Campo` (etiqueta, campo y error; gris si es de solo lectura), `Selector`
(lista desplegable con el mismo aspecto que `Campo`), `Aviso` (errores de la API
y avisos), `Tarjeta` y `Tabla` (con mensaje cuando no hay filas). No se usan colores sueltos: si hace falta uno nuevo, se agrega a la
paleta.

## Calidad de código

- **ESLint** revisa errores y buenas prácticas (incluidas las reglas de Hooks de React).
- **Prettier** se encarga del formato, sin punto y coma y con comillas simples.

Se recomienda ejecutar `npm run lint` y `npm run format:check` antes de cada commit.

## Pendiente para la Etapa 3

- Pantallas con funcionalidad y datos.
- Autenticación real y protección de rutas por perfil.
- Servicios por módulo sobre `apiClient`.
