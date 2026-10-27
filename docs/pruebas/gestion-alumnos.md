# Pruebas de la gestión de alumnos

## Sistema de Gestión Rojas FC — issue #39

Este documento registra las pruebas de las pantallas de alumnos (alta, consulta, modificación, baja y reactivación) hechas con el sistema completo levantado en local: backend, frontend y base MySQL creada desde `database/schema.sql`.

Cubre los requisitos RF-01 a RF-06 y RF-16, y las reglas RN-01, RN-03, RN-05 y RN-12. Las pruebas automáticas del backend (unitarias y de integración contra MySQL) se describen en `backend/README.md`, sección "Tests".

### Cómo se prepararon

1. Base `rojasfc` vacía, creada desde `database/schema.sql`.
2. Backend levantado con `java -jar` y frontend con `npm run dev`.
3. Configuración inicial con el administrador DNI 20111222 (`POST /api/configuracion-inicial`).
4. Como todavía no está la pantalla de ingreso (#38), el token del administrador se carga a mano en el navegador.

Los casos se recorrieron en Microsoft Edge, en escritorio (1280 px) y en celular (390 px). El recorrido se automatizó con Playwright para poder repetirlo después de cada cambio.

**Última ejecución:** 27/10/2026, 20 de 20 casos correctos.

---

## Casos

| # | Caso | Pasos | Resultado esperado | Resultado |
|---|------|-------|--------------------|-----------|
| 1 | Pantalla Alumnos | Entrar a Alumnos | Se ve el botón "Nuevo alumno" | OK |
| 2 | Alta con responsable nuevo (RF-01, RF-06, RN-05) | Completar el alumno 45111222 y la responsable 30111222 con teléfono y contraseña, y registrar | Abre el detalle del alumno, Activo, con la responsable en la tabla | OK |
| 3 | Datos del detalle | Mirar el detalle del alumno recién creado | Muestra la categoría (año de nacimiento) y la fecha en formato dd/mm/aaaa | OK |
| 4 | Baja (RF-04, RNF-13) | Presionar "Dar de baja" y aceptar | Pide confirmación con el nombre del alumno y pasa a Inactivo | OK |
| 5 | Historial después de la baja (RF-16) | Mirar "Historial en la escuela" | La permanencia ya no figura como Vigente | OK |
| 6 | Reactivación el mismo día (RF-05) | Presionar "Reactivar" el mismo día de la baja | Vuelve a Activo y la baja se anula: queda una sola permanencia, Vigente | OK |
| 7 | Edición precargada (RF-03) | Presionar "Modificar datos" | El formulario trae los datos actuales | OK |
| 8 | Guardar la modificación | Cambiar la dirección y guardar | Vuelve al detalle con la dirección nueva | OK |
| 9 | Responsable ya registrado (RN-01) | En un alta nueva, poner el DNI 30111222 en el responsable y salir del campo | Completa y bloquea nombre y apellido, y oculta teléfono y contraseña | OK |
| 10 | Cambiar el DNI del responsable | Reemplazar ese DNI por uno que no existe | Nombre y apellido quedan vacíos y editables; vuelven teléfono y contraseña | OK |
| 11 | No arrastrar teléfono ni contraseña | Cargar teléfono y contraseña, pasar a un DNI de responsable existente y después a otro DNI nuevo | Teléfono y contraseña aparecen vacíos | OK |
| 12 | Alta con responsable existente | Registrar el alumno 45333444 con la responsable 30111222 | El detalle muestra a la misma responsable | OK |
| 13 | Alumno ya registrado | En un alta, poner el DNI 45333444 y salir del campo | Avisa que ya existe, ofrece "Ver alumno" y desactiva "Registrar alumno" | OK |
| 14 | Alta de un alumno dado de baja | Dar de baja al 45333444; en un alta, completar todo, poner ese DNI y presionar Enter | Muestra que está dado de baja y ofrece "Reactivar alumno" | OK |
| 15 | Cambiar el DNI después de ese aviso | Cambiar el DNI del alumno | El aviso y el botón "Reactivar alumno" desaparecen | OK |
| 16 | Consultar por DNI (RF-02) | En Alumnos, escribir 45111222 y presionar "Ver alumno" | Abre el detalle | OK |
| 17 | Alumno inexistente | Entrar a `/alumnos/99999999` | Muestra "No hay ningún alumno…" | OK |
| 18 | Sin sesión | Borrar el token y entrar a un detalle | Muestra el mensaje del backend pidiendo iniciar sesión | OK |
| 19 | Celular | Abrir el alta con 390 px de ancho | No hay desplazamiento horizontal | OK |
| 20 | Consola del navegador | Revisar la consola durante todo el recorrido | Sin errores de JavaScript; solo las respuestas 401, 404 y 409 que los casos provocan a propósito | OK |

---

## Pendiente

- Repetir los casos con la pantalla de ingreso real cuando esté el #38, en lugar de cargar el token a mano.
- El listado de alumnos (#41) se prueba en su propio issue. Las pantallas de este documento ya invalidan su consulta para que se actualice después de cada alta, baja, reactivación o modificación.
