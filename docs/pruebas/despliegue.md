# Pruebas del despliegue

## Sistema de Gestión Rojas FC — issue #60

Este documento registra las pruebas del despliegue en línea: la base en Aiven, el backend en Render y el frontend en Vercel, todos en su plan gratuito.

### Cómo se prepararon

1. Servicio MySQL 8.4 en Aiven, plan Free, con conexión cifrada obligatoria (`ssl-mode=REQUIRED`).
2. Base `rojasfc` creada desde el panel de Aiven.
3. Carga desde Git Bash con el cliente `mysql` de MySQL 8.4, usando `source`, porque `schema.sql` usa `DELIMITER` y necesita el cliente `mysql`.

**Última ejecución:** 05/11/2026 (base, compilación y recorrido en línea).

---

## Base de datos (Aiven)

| # | Caso | Pasos | Resultado esperado | Resultado |
|---|------|-------|--------------------|-----------|
| 1 | Conexión cifrada | Conectarse con `--ssl-mode=REQUIRED` y el usuario `avnadmin` | Conecta a MySQL 8.4 | OK |
| 2 | Crear el esquema | `source database/schema.sql` | Termina sin errores, incluidos los triggers, aunque el usuario del plan gratuito no tiene privilegio SUPER | OK |
| 3 | Contar objetos | Consultar `information_schema` | 14 tablas, 15 triggers y 5 vistas | OK |
| 4 | Datos de demo | `source database/datos-demo.sql` | Termina sin errores | OK |
| 5 | Configuración inicial cerrada | Mirar si existe un administrador | La demo trae la administradora DNI 20111222, así que la pantalla de configuración inicial no queda abierta | OK |
| 5b | Tildes | Cargar los scripts desde la consola de Windows y mirar "Nicolás Sosa" en el listado | Se ve "Nicolás". La primera carga, sin `SET NAMES utf8mb4`, guardó las tildes mal; se agregó esa línea a los dos scripts y se recargó la base | Corregido |

## Compilación

| # | Caso | Pasos | Resultado esperado | Resultado |
|---|------|-------|--------------------|-----------|
| 6 | Backend | `mvn -DskipTests package` | Genera `gestion-backend-0.1.0.jar` | OK |
| 7 | Frontend | `npm ci` y `npm run build` | Genera `dist/` sin errores | OK |

## Pruebas en línea

Frontend en https://rojasfc-gestion.vercel.app (Vercel, rama `main`) y backend en https://rojasfc-backend.onrender.com (Render). Recorrido en Google Chrome, en escritorio.

| # | Caso | Pasos | Resultado esperado | Resultado |
|---|------|-------|--------------------|-----------|
| 8 | Backend publicado | Deploy en Render con `backend/Dockerfile` | El log muestra `Started GestionApplication` y el servicio queda Live | OK (arranque de unos 2 minutos) |
| 9 | Backend conectado a la base | Abrir `/api/health` | Responde `"status":"ok"` y `"database":"ok"` | OK |
| 10 | Frontend publicado | Deploy en Vercel con raíz `frontend/` y `VITE_API_URL` | Muestra la pantalla de inicio de sesión | OK |
| 11 | Origen no permitido | Ingresar desde la dirección propia de un deploy (`rojasfc-gestion-...-marigi84s-projects.vercel.app`) | El navegador bloquea el pedido por CORS: "No se pudo conectar con el servidor" | OK: solo se acepta `rojasfc-gestion.vercel.app` |
| 12 | Ingreso del administrador | Ingresar con DNI 20111222 desde `rojasfc-gestion.vercel.app` | Entra al Dashboard | OK |
| 13 | Dashboard | Mirar las tarjetas y la tabla por categoría | 7 alumnos activos, 3 morosos, recaudación del mes y alumnos por categoría | OK |
| 14 | Alumnos | Abrir el listado y el detalle | Lista los alumnos de la demo con sus tildes | OK |
| 15 | Pantallas pendientes | Abrir Pagos y Recibos, Eventos e Indumentaria, Reportes y Sin Conexión | Se ven vacías, igual que en `main` (Pagos depende del PR #81; el resto, del PR #72) | Esperado |
| 16 | Portal del Responsable | Ingresar con DNI 30111222 | Muestra sus alumnos con cuotas, pagos y recibos (por ejemplo, Mateo Gómez: octubre vencida con interés, septiembre pagada con recibo N.º 2) | OK. Observaciones: la lista no se pagina ni se pliega, y todavía no permite descargar el recibo (RF-14) |

## Limitaciones

- **Recuperación de contraseña por correo (RF-50):** el plan gratuito de Render bloquea los puertos SMTP (25, 465 y 587) desde septiembre de 2025, así que en línea no se envía el correo. En local funciona.
- **Arranque lento:** Render apaga el backend tras 15 minutos sin uso; el primer pedido después tarda alrededor de un minuto.
- **Base inactiva:** Aiven puede apagar la base gratuita si pasa tiempo sin uso; se vuelve a encender desde su panel.
- **Recargar una página interna:** hasta que `frontend/vercel.json` llegue a `main`, recargar con F5 una ruta como `/alumnos` da 404 en Vercel.
- **Datos de demo públicos:** la contraseña de la demo está en el repositorio. No cargar datos reales del club en este despliegue.
