# Pruebas del listado de alumnos

## Sistema de Gestión Rojas FC — issue #41

Este documento registra las pruebas del listado de alumnos con búsqueda, filtros combinables y paginación. Cubre los requisitos RF-38 y RF-39.

Las pruebas automáticas del backend se describen en `backend/README.md`, sección "Tests".

### Cómo se prepararon

1. Base `rojasfc` con dos alumnos: 45111222 (Mateo Gómez, año 2015, activo, no moroso) y 45111223 (Sofía López, año 2016, inactiva, no morosa).
2. Backend levantado con `mvn spring-boot:run` y frontend con `npm run dev`.
3. Ingreso con el administrador DNI 30111222 por la pantalla de inicio de sesión.

**Última ejecución:** 30/10/2026.

Los casos de pantalla se recorrieron con el backend y el frontend levantados en local. La paginación y los errores de validación se probaron contra la API, porque con dos alumnos no se llega a una segunda página.

---

## Casos

| # | Caso | Pasos | Resultado esperado | Resultado |
|---|------|-------|--------------------|-----------|
| 1 | Pantalla Alumnos (RF-39) | Entrar a Alumnos | Se ve el listado con el botón "Nuevo alumno" y el formulario de búsqueda | OK |
| 2 | Solo activos por defecto (RF-38) | Mirar el listado al entrar | Muestra únicamente a Mateo (activo) y el total "1 alumno" | OK |
| 3 | Búsqueda por apellido (RF-38) | Buscar "lopez" con el estado en Activos | No devuelve resultados, porque Sofía está inactiva | OK |
| 4 | Incluir inactivos (RF-38) | Cambiar el estado a Todos y buscar "lopez" | Aparece Sofía López, con estado Inactivo | OK |
| 5 | Búsqueda por DNI (RF-38) | Buscar "45111222" con el estado en Todos | Aparece Mateo Gómez | OK |
| 6 | Filtro por categoría (RF-38) | Poner categoría 2016 con el estado en Todos | Aparece Sofía López (año 2016) | OK |
| 7 | Filtro por morosidad (RF-38) | Elegir Morosidad = Morosos con el estado en Todos | Muestra "No hay alumnos para esos filtros" y "0 alumnos" | OK |
| 8 | Limpiar los filtros | Presionar "Limpiar" | Los campos vuelven vacíos, el estado vuelve a Activos y reaparece Mateo | OK |
| 9 | Acceso al detalle (RF-39) | Hacer clic en el nombre del alumno | Abre `/alumnos/45111222` con sus datos, responsables e historial | OK |
| 10 | Sin sesión | Entrar a `/alumnos` sin sesión | Redirige a la pantalla de ingreso | OK |
| 11 | Paginación (RF-39) | Pedir `?estado=todos&tamano=1&pagina=2` | Devuelve a Sofía como segunda página, con total 2 y 2 páginas | OK |
| 12 | Estado inválido | Pedir `?estado=otro` | Responde 400 con "El estado tiene que ser activo, inactivo o todos." | OK |
| 13 | Página inválida | Pedir `?pagina=0` | Responde 400 con "La página tiene que ser mayor o igual a 1." | OK |
| 14 | Tamaño de página inválido | Pedir `?tamano=200` | Responde 400 con "El tamaño de página debe estar entre 1 y 100." | OK |
| 15 | Caracteres especiales (RF-38) | Buscar `%` y después `_` con el estado en Todos | No devuelve resultados: se buscan como texto y no como comodines | OK |
| 16 | Consola del navegador | Revisar la consola durante el recorrido | Sin errores de JavaScript | OK |

### Verificación automática

- Backend: `mvn test` sin fallas. Incluye cuatro pruebas del listado (estado por defecto, estado "todos", búsqueda recortada con morosidad y estado inválido).
- Frontend: `npm run lint`, `npm run format:check` y `npm run build` sin errores.

---

## Pendiente

- Probar la paginación en pantalla cuando haya más de 20 alumnos, porque con dos no se muestra el paginador.
- Probar el filtro "Morosos" con un alumno que tenga una cuota vencida: en la base local no hay ninguno, así que solo se verificó que no devuelve resultados.
- Repetir el recorrido con la pantalla de ingreso real cuando se integre el #53, en lugar de cargar el token a mano.
