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
│   │   └── ui/          → componentes de interfaz genéricos
│   ├── pages/           → una pantalla por módulo
│   ├── routes/          → definición de rutas y navegación
│   ├── services/        → comunicación con la API REST
│   ├── App.tsx          → componente raíz (monta el router)
│   ├── main.tsx         → punto de entrada de la aplicación
│   └── index.css        → estilos globales
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

Las rutas se definen en `src/routes/` (`paths.ts` y `AppRouter.tsx`).

## Servicio de comunicación con la API

`src/services/apiClient.ts` centraliza todas las llamadas HTTP al backend:
define la URL base, adjunta el token JWT cuando exista y unifica el manejo de
errores. Los servicios de cada módulo deben usar este cliente en lugar de
llamar a `fetch` directamente.

## Calidad de código

- **ESLint** revisa errores y buenas prácticas (incluidas las reglas de Hooks de React).
- **Prettier** se encarga del formato, sin punto y coma y con comillas simples.

Se recomienda ejecutar `npm run lint` y `npm run format:check` antes de cada commit.

## Pendiente para la Etapa 3

- Pantallas con funcionalidad y datos.
- Autenticación real y protección de rutas por perfil.
- Servicios por módulo sobre `apiClient`.
