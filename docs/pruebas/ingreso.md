# Pruebas del ingreso al sistema

## Sistema de Gestión Rojas FC — issue #38

Este documento registra las pruebas de las pantallas de ingreso (configuración inicial, inicio de sesión, cambio de contraseña y protección de rutas) hechas con el sistema completo levantado en local: backend, frontend y base MySQL creada desde `database/schema.sql`.

Cubre los requisitos RF-11, RF-49 y RF-52, y RNF-01 y RNF-02.

### Cómo se prepararon

1. Base `rojasfc` vacía, creada desde `database/schema.sql`.
2. Backend levantado con `mvn spring-boot:run` y frontend con `npm run dev`.
3. Todo el resto se hace desde la interfaz, empezando por la configuración inicial.

Los casos se recorrieron en Chromium, en escritorio (1280 px) y en celular (390 px). El recorrido se automatizó con Playwright para poder repetirlo después de cada cambio.

Los casos se ejecutan en orden y sobre la misma base, porque unos preparan los datos de los siguientes: por ejemplo, el responsable que se da de alta en el caso 9 es el que se usa desde el caso 10.

**Última ejecución:** 29/10/2026 15:36, rama `frontend/pantallas-ingreso`, commit `015f574`: 25 de 25 casos correctos. El caso 24 se ejecuta antes del caso 1, con la base todavía vacía.

---

## Casos

| # | Caso | Pasos | Resultado esperado | Resultado |
| - | ---- | ----- | ------------------ | --------- |
| 1 | Sistema sin Administrador | Entrar a `/alumnos` | Lleva a la configuración inicial | OK |
| 2 | Contraseñas distintas | En la configuración inicial, repetir otra contraseña | Avisa "Las contraseñas no coinciden." | OK |
| 3 | Primer Administrador | Completar la configuración inicial | Crea el Administrador, inicia la sesión y abre el Dashboard con su nombre | OK |
| 4 | Recargar la página | Recargar con la sesión iniciada | La sesión sigue | OK |
| 5 | Cerrar sesión | Presionar "Cerrar sesión" | Lleva al ingreso | OK |
| 6 | Configuración ya hecha | Entrar a `/configuracion-inicial` | Lleva al ingreso | OK |
| 7 | Contraseña incorrecta (RF-49) | Ingresar con una contraseña equivocada | Avisa "El DNI o la contraseña no coinciden" | OK |
| 8 | Volver a la pantalla pedida | Sin sesión, entrar a `/alumnos/nuevo` e ingresar | Después del ingreso abre `/alumnos/nuevo` | OK |
| 9 | Alta con responsable | El Administrador da de alta un alumno con un responsable nuevo y contraseña provisoria | Registra el alumno | OK |
| 10 | Contraseña provisoria (RNF-01) | Ingresar como ese responsable | Abre el cambio de contraseña, sin menú, con el aviso de contraseña provisoria | OK |
| 11 | Provisoria y otra ruta | Con la provisoria, entrar a `/alumnos` | Vuelve al cambio de contraseña | OK |
| 12 | Provisoria incorrecta | Escribir mal la contraseña provisoria | Avisa "La contraseña actual no coincide" y la sesión sigue | OK |
| 13 | Cambio de la provisoria (RF-11) | Cambiarla | Abre el Portal del Responsable | OK |
| 14 | Menú del Responsable (RF-52) | Mirar el menú | Solo aparece "Portal del Responsable" | OK |
| 15 | Rutas del Administrador (RF-52) | Como Responsable, entrar a `/alumnos` | Lleva al portal | OK |
| 16 | Cambio voluntario | Entrar a "Cambiar contraseña" y cambiarla | Confirma "Listo, tu contraseña se cambió." | OK |
| 17 | Contraseña nueva | Cerrar sesión e ingresar con la nueva | Abre el portal | OK |
| 18 | Token inválido (RNF-02) | Alterar el token guardado y abrir un alumno | El backend responde 401, se cierra la sesión y lleva al ingreso | OK |
| 19 | Token vencido | Guardar en el navegador (clave `rojas_fc_sesion` de localStorage) una sesión cuyo token tiene una fecha de vencimiento ya pasada, y abrir la aplicación | La sesión se descarta y lleva al ingreso | OK |
| 20 | Celular | Ingresar con 390 px de ancho | No hay desplazamiento horizontal; el usuario aparece debajo del escudo | OK |
| 21 | Token inválido al cambiar la contraseña | Alterar el token guardado e intentar cambiar la contraseña | El backend responde 401, se cierra la sesión y lleva al ingreso | OK |
| 22 | Ingreso después del rechazo | Volver a ingresar | Retoma el cambio de contraseña, que era la pantalla abierta | OK |
| 23 | Consola del navegador | Revisar la consola durante todo el recorrido | Sin errores inesperados de JavaScript (los 401 de los casos 18 y 21 se provocan a propósito) | OK |
| 24 | Ingreso mientras se consulta la configuración | Con la base vacía, demorar 2,5 s la respuesta de `/api/configuracion-inicial` y abrir `/login` | "Ingresar" está desactivado hasta que llega la respuesta, y después lleva a la configuración inicial | OK |
| 25 | Volver a la ruta completa | Sin sesión, entrar a `/alumnos?buscar=lopez#resultados` e ingresar | Después del ingreso abre `/alumnos?buscar=lopez#resultados` | OK |

---

## Pendiente

- Restablecer la contraseña de un Responsable (RF-15), registrar otro Administrador (RF-51) y recuperar la contraseña del Administrador por correo (RF-50) se prueban cuando estén hechos.
