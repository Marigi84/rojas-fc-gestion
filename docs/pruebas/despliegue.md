# Pruebas del despliegue

## Sistema de Gestión Rojas FC — issue #60

Este documento registra las pruebas del despliegue en línea: la base en Aiven, el backend en Render y el frontend en Vercel, todos en su plan gratuito.

### Cómo se prepararon

1. Servicio MySQL 8.4 en Aiven, plan Free, con conexión cifrada obligatoria (`ssl-mode=REQUIRED`).
2. Base `rojasfc` creada desde el panel de Aiven.
3. Carga desde Git Bash con el cliente `mysql` de MySQL 8.4, usando `source`, porque `schema.sql` usa `DELIMITER` y necesita el cliente `mysql`.

**Última ejecución:** 05/11/2026.

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

Pendientes: se completan cuando estén publicados el backend en Render y el frontend en Vercel.

## Limitaciones

- **Recuperación de contraseña por correo (RF-50):** el plan gratuito de Render bloquea los puertos SMTP (25, 465 y 587) desde septiembre de 2025, así que en línea no se envía el correo. En local funciona.
- **Arranque lento:** Render apaga el backend tras 15 minutos sin uso; el primer pedido después tarda alrededor de un minuto.
- **Base inactiva:** Aiven puede apagar la base gratuita si pasa tiempo sin uso; se vuelve a encender desde su panel.
- **Datos de demo públicos:** la contraseña de la demo está en el repositorio. No cargar datos reales del club en este despliegue.
