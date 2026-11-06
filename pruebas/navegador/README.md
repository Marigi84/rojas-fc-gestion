# Recorridos en el navegador

Pruebas automatizadas con Playwright que recorren las pantallas como lo haría un usuario. Son los recorridos que se describen en `docs/pruebas/`.

Usan una base propia, `rojasfc_pruebas`, que se borra y se vuelve a crear en cada corrida. La base de desarrollo, `rojasfc`, no se toca.

## Requisitos

- Node.js, y MySQL con su cliente de línea de comandos, `mysql`.
- Microsoft Edge en Windows, o Google Chrome en macOS y Linux. Para usar otro navegador basado en Chromium, se indica su ruta en `NAVEGADOR`.

Se ejecutaron en Windows con Edge. En macOS y Linux todavía no se probaron.

## Cómo correrlos

Hacen falta tres terminales, las tres con las variables de MySQL cargadas.

**En Windows (PowerShell):**

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "tu_password"
```

**En macOS o Linux:**

```bash
export DB_USERNAME=root
export DB_PASSWORD=tu_password
```

**1. Crear la base de las pruebas.** Solo hace falta antes de levantar el backend:

```bash
cd pruebas/navegador
npm install
npm run preparar
```

**2. Levantar el backend apuntando a esa base.** Además de las variables de `backend/README.md`, en Windows va `$env:DB_NAME = "rojasfc_pruebas"` y en macOS o Linux `export DB_NAME=rojasfc_pruebas`. Después:

```bash
cd backend
mvn spring-boot:run
```

**3. Levantar el frontend:**

```bash
cd frontend
npm run dev
```

**4. Correr los recorridos,** en la terminal del paso 1:

```bash
npm test
```

También se puede correr uno solo con `node alumnos.js`, con la base en el estado que necesita (ver `correr.js`).

Si el backend quedó apuntando a la base de desarrollo, `alumnos.js` y `cuotas.js` fallan al empezar, porque esa base ya tiene un administrador, y no se cambia nada en ella.

## Variables

| Variable | Por defecto | Descripción |
|---|---|---|
| `DB_USERNAME` | obligatoria | Usuario de MySQL |
| `DB_PASSWORD` | vacía | Contraseña de MySQL |
| `PRUEBAS_DB_NAME` | `rojasfc_pruebas` | Base que usan las pruebas. Se borra en cada corrida |
| `MYSQL_CLI` | `mysql` | Ruta del cliente de MySQL, si no está en el PATH |
| `NAVEGADOR` | Edge en Windows, Chrome en el resto | Ruta de otro navegador basado en Chromium |
| `WEB` | `http://localhost:5173` | Dirección del frontend |
| `API` | `http://localhost:8080/api` | Dirección del backend |

## Qué cubre cada uno

| Archivo | Datos | Pruebas |
|---|---|---|
| `alumnos.js` | Base vacía | Ingreso, alta, baja, reactivación y modificación de alumnos (`docs/pruebas/gestion-alumnos.md`) |
| `responsables-listado.js` | Los de `alumnos.js` | Responsables y listado de alumnos |
| `cuotas.js` | Base vacía | Períodos, generación, interés y morosos (`docs/pruebas/cuotas.md`) |
| `dashboard-portal.js` | `database/datos-demo.sql` | Dashboard y Portal del Responsable (`docs/pruebas/dashboard-portal.md`) |
| `pagos.js` | `database/datos-demo.sql` | Circuito de pago completo: registro, recibo, anulación y nuevo pago (`docs/pruebas/pagos.md`) |

Las capturas quedan en `capturas/`, que no se sube al repositorio.
