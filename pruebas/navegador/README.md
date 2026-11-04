# Recorridos en el navegador

Pruebas automatizadas con Playwright que recorren las pantallas como lo haría un usuario. Son los recorridos que se describen en `docs/pruebas/`.

**Borran la base de desarrollo** cada vez que corren: no hay que usarlos con datos que se quieran conservar.

## Requisitos

- Backend y frontend levantados en local (ver el README principal).
- MySQL y su cliente de línea de comandos, `mysql`.
- Un navegador basado en Chromium. Por defecto se usa Microsoft Edge en Windows.

## Cómo correrlos

```powershell
cd pruebas/navegador
npm install
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "tu_password"
npm test
```

También se puede correr uno solo con `node alumnos.js`, con la base en el estado que necesita (ver `correr.js`).

## Variables

| Variable | Por defecto | Descripción |
|---|---|---|
| `DB_USERNAME` | obligatoria | Usuario de MySQL |
| `DB_PASSWORD` | vacía | Contraseña de MySQL |
| `DB_NAME` | `rojasfc` | Base de desarrollo |
| `MYSQL_CLI` | `mysql` | Ruta del cliente de MySQL, si no está en el PATH |
| `NAVEGADOR` | Edge en Windows | Ruta del navegador |
| `WEB` | `http://localhost:5173` | Dirección del frontend |
| `API` | `http://localhost:8080/api` | Dirección del backend |

## Qué cubre cada uno

| Archivo | Datos | Pruebas |
|---|---|---|
| `alumnos.js` | Base vacía | Ingreso, alta, baja, reactivación y modificación de alumnos (`docs/pruebas/gestion-alumnos.md`) |
| `responsables-listado.js` | Los de `alumnos.js` | Responsables y listado de alumnos |
| `cuotas.js` | Base vacía | Períodos, generación, interés y morosos (`docs/pruebas/cuotas.md`) |
| `dashboard-portal.js` | `database/datos-demo.sql` | Dashboard y Portal del Responsable (`docs/pruebas/dashboard-portal.md`) |

Las capturas quedan en `capturas/`, que no se sube al repositorio.
