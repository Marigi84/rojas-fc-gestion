# Pruebas de la gestión de responsables

## Sistema de Gestión Rojas FC — issue #40

Este documento registra las pruebas de las pantallas de responsables (alta, consulta, modificación, asociación y desvinculación) hechas con el sistema completo levantado en local: backend, frontend y base MySQL creada desde `database/schema.sql`.

Cubre los requisitos RF-06 a RF-10 y las reglas RN-01, RN-02, RN-05, RN-11 y RN-12. Las pruebas automáticas del backend se describen en `backend/README.md`, sección "Tests".

### Cómo se prepararon

1. Base `rojasfc_pruebas` creada desde `database/schema.sql`, para no tocar la base de trabajo.
2. Backend levantado con `mvn spring-boot:run` apuntando a esa base y frontend con `npm run dev`.
3. Configuración inicial con el administrador DNI 20111222.
4. Los alumnos y sus responsables base se cargaron con el alta de alumnos (#39): Mateo 45111222 con Laura 30111223 (madre) y Sofía 45111223 con Ana 30111225 (madre).

**Última ejecución:** 30/10/2026.

---

## Casos

| # | Caso | Pasos | Resultado esperado | Resultado |
|---|------|-------|--------------------|-----------|
| 1 | Consultar un responsable (RF-07) | En Responsables, escribir 30111223 y presionar "Ver responsable" | Abre el detalle de Laura Gómez | OK |
| 2 | Datos y alumnos a cargo (RF-07) | Mirar el detalle | Muestra DNI, teléfono y la tabla con Mateo, vínculo Madre y estado Activo | OK |
| 3 | Formulario precargado (RF-08) | Presionar "Modificar datos" | Trae DNI, nombre, apellido y teléfono actuales | OK |
| 4 | Guardar la modificación (RF-08) | Cambiar el teléfono a 3511112222 y presionar "Guardar cambios" | Vuelve al detalle con el teléfono nuevo | OK |
| 5 | Asociar a otro alumno (RF-09, RN-11) | Presionar "Asociar a otro alumno", cargar 45111223 con vínculo Madre y confirmar | El detalle muestra a Mateo y a Sofía | OK |
| 6 | Alta desde el alumno (RF-06) | En el detalle de Mateo, presionar "Agregar responsable" y cargar 30111224, Padre, Pedro Ruiz, 3515555555 y contraseña provisoria | El responsable aparece en la tabla del alumno | OK |
| 7 | Persona ya registrada (RN-01) | En "Agregar responsable", escribir 30111223 y salir del campo | Completa nombre y apellido y los deja fijos, oculta teléfono y contraseña, y avisa que se asocia sin cambiar sus datos | OK |
| 8 | Confirmación antes de desvincular (RF-10) | Presionar "Desvincular" en la fila de Pedro y cancelar | Pregunta "¿Desvincular a Pedro Ruiz del alumno?" y no cambia nada | OK |
| 9 | Desvincular (RF-10) | Aceptar esa confirmación | Pedro desaparece de la tabla | OK |
| 10 | Responsable sin alumnos (RN-02) | Consultar el 30111224 después de desvincularlo | Responde 404: se eliminó su rol y también la persona | OK |
| 11 | Último responsable en pantalla (RN-05) | Abrir el detalle de Mateo, que quedó con un solo responsable | "Desvincular" aparece deshabilitado, con el aviso "El alumno debe conservar al menos un responsable" | OK |
| 12 | Último responsable en el backend (RN-05) | Pedir `DELETE /api/responsables/30111223/alumnos/45111222` | Responde 409 con "El alumno debe conservar al menos un responsable." | OK |
| 13 | Desvincular desde el responsable (RF-10) | En el detalle de Laura, presionar "Desvincular" en la fila de Sofía y aceptar | Sofía desaparece; queda solo Mateo | OK |
| 14 | Responsable inexistente | Pedir `GET /api/responsables/99999999` | Responde 404 con "No hay ningún responsable con el DNI 99999999." | OK |
| 15 | Sin sesión | Entrar a `/responsables` sin sesión | Redirige a la pantalla de ingreso | OK |
| 16 | Consola del navegador | Revisar la consola durante todo el recorrido | Sin errores de JavaScript | OK |

### Verificación automática

- Backend: `mvn test` sin fallas. Incluye las pruebas de alta, consulta, modificación, asociación y desvinculación del servicio de responsables.
- Frontend: `npm run lint`, `npm run format:check` y `npm run build` sin errores.

---

## Pendiente

- Probar el recorrido en celular (390 px de ancho), que en esta ejecución no se hizo.
- Repetir los casos con los datos reales de la escuela, en lugar de los alumnos de prueba.
