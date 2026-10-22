# Índices del esquema relacional

## Sistema de Gestión Rojas FC

> En este documento se definen los índices del esquema relacional y la justificación de cada uno, a partir de las búsquedas, filtros y reportes de los requisitos funcionales y de los requisitos de rendimiento (RNF-06 y RNF-07). Los índices se crean en la sección 10 de `database/schema.sql` y aparecen en el [diagrama relacional](diagrama-relacional.md).

---

## 1. Criterio

MySQL (InnoDB) crea automáticamente un índice por cada clave primaria, cada restricción `UNIQUE` y cada clave foránea del esquema. Por eso se parte de esos índices y se agregan **solo los que cubren consultas que ellos no resuelven**, evitando índices redundantes.

Con el volumen actual de la escuela (alrededor de 140 alumnos activos) cualquier consulta responde de inmediato. Los índices se justifican por RNF-07, que exige mantener el tiempo de respuesta *"aun con el crecimiento del historial de alumnos, cuotas, pagos y recibos"*, y por los criterios de búsqueda que piden expresamente RF-38 y RF-39.

---

## 2. Consultas a cubrir

| Requisito | Consulta | Cobertura |
|---|---|---|
| RF-38 | Buscar alumnos por DNI | Ya cubierta: clave primaria de `persona` y `alumno` |
| RF-38 | Buscar alumnos por apellido | **Índice nuevo** `idx_persona_apellido_nombre` |
| RF-38 | Buscar alumnos por nombre | **Índice nuevo** `idx_persona_nombre` |
| RF-38, RF-46 | Filtrar y agrupar por categoría (año de nacimiento) | **Índice nuevo** `idx_alumno_categoria` |
| RF-38, RF-45 | Filtrar por estado (activo / inactivo) | Ya cubierta: `UNIQUE (dni_alumno, vigente)` de `permanencia` |
| RF-38, RF-23, RF-47 | Filtrar por morosidad | Ya cubierta: claves de `cuota` y `periodo`, y `UNIQUE (dni_alumno, periodo, vigente)` de `pago` |
| RF-39 | Listado paginado de alumnos, ordenado | **Índice nuevo** `idx_persona_apellido_nombre` |
| RF-22, RF-27, RF-37 | Cuotas, pagos, participaciones y encargos de un alumno | Ya cubiertas: claves primarias y foráneas que empiezan por `dni_alumno` |
| RF-12 a RF-14 | Alumnos, cuotas y recibos de un responsable (portal) | Ya cubiertas: clave primaria de `responsable_alumno` y claves de `cuota`, `pago` y `recibo` |
| RF-42, RF-48 | Recaudación entre fechas y del mes en curso | **Índice nuevo** `idx_pago_fecha` |
| RF-44 | Altas entre fechas | **Índice nuevo** `idx_permanencia_fecha_alta` |
| RF-44 | Bajas entre fechas | **Índice nuevo** `idx_permanencia_fecha_baja` |

---

## 3. Índices agregados

| Índice | Tabla y columnas | Requisitos | Justificación |
|---|---|---|---|
| `idx_persona_apellido_nombre` | `persona (apellido, nombre)` | RF-38, RF-39 | Resuelve la búsqueda por apellido y devuelve el listado ya ordenado por apellido y nombre, sin ordenar en memoria. Sirve también para buscar por apellido y nombre juntos. |
| `idx_persona_nombre` | `persona (nombre)` | RF-38 | RF-38 permite buscar solo por nombre; el índice anterior no lo resuelve porque empieza por apellido. |
| `idx_alumno_categoria` | `alumno ((YEAR(fecha_nacimiento)))` | RF-38, RF-46 | La categoría se deriva del año de nacimiento (RN-13). Es un índice funcional sobre la misma expresión que usa `vista_alumno.categoria`, por lo que el filtro sobre la vista también lo aprovecha. |
| `idx_pago_fecha` | `pago (fecha)` | RF-42, RF-48 | La recaudación se calcula por la fecha de cada pago (RF-42). `pago` es la tabla que más crece y ninguna de sus claves empieza por `fecha`. |
| `idx_permanencia_fecha_alta` | `permanencia (fecha_alta)` | RF-44 | La clave primaria `(dni_alumno, fecha_alta)` empieza por el alumno, así que no sirve para buscar altas por rango de fechas. |
| `idx_permanencia_fecha_baja` | `permanencia (fecha_baja)` | RF-44 | Ninguna clave incluye `fecha_baja`. |

---

## 4. Paginación (RNF-06)

El listado de alumnos (RF-39) se ordena por `apellido, nombre, dni`. El `dni` actúa como desempate para que el orden sea estable entre páginas cuando dos alumnos tienen el mismo apellido y nombre.

`idx_persona_apellido_nombre` resuelve ese orden completo: en InnoDB todo índice secundario incluye al final la clave primaria (`dni`). Con `LIMIT` y `OFFSET`, MySQL lee del índice solo las filas de las páginas pedidas.

---

## 5. Índices descartados

| Posible índice | Motivo para no agregarlo |
|---|---|
| `pago (fecha_anulacion)` o `pago (estado_sincronizacion)` | Casi todos los pagos están vigentes y confirmados: son columnas poco selectivas. En la recaudación se filtran después del rango de fechas, que ya resuelve `idx_pago_fecha`. |
| `permanencia (vigente)` | El estado de cada alumno se consulta por `UNIQUE (dni_alumno, vigente)`. El total de activos (RF-45) recorre ese mismo índice, que crece solo con las altas. |
| Índices para la deuda pendiente (RF-43) | La deuda total recorre todas las cuotas por definición; las uniones con `periodo` y `pago` ya usan sus claves. |
| Índices en `periodo` | Tiene una fila por mes (12 por año); leerla completa es inmediato. |
| Índice `FULLTEXT` para buscar texto en cualquier parte del nombre | Innecesario para el volumen de la escuela. La búsqueda de RF-38 se resuelve por prefijo (ver sección 6). |

---

## 6. Criterios para las consultas del backend

Para que las consultas aprovechen los índices:

- **Búsqueda por apellido o nombre:** por prefijo, `LIKE 'texto%'`. Una búsqueda `LIKE '%texto%'` no puede usar el índice.
- **Búsqueda por DNI:** por igualdad (`dni = :dni`), que usa la clave primaria. El DNI es numérico, así que una búsqueda parcial con `LIKE` no usaría el índice.
- **Filtro por categoría:** sobre `vista_alumno.categoria = <año>` o `YEAR(fecha_nacimiento) = <año>`, con la misma expresión del índice.
- **Rangos de fechas:** comparar la columna directamente (`fecha BETWEEN :desde AND :hasta`), sin aplicarle funciones.
- **Listado paginado:** `ORDER BY apellido, nombre, dni` con `LIMIT` y `OFFSET`.

---

## 7. Verificación

Se aplicó `database/schema.sql` en MySQL 8.4 con datos de prueba (2.000 alumnos, 12 períodos, 24.000 cuotas y unos 16.000 pagos, cargados respetando todos los triggers) y se revisó con `EXPLAIN` cada consulta de la sección 2:

- Las consultas por apellido, nombre, categoría, recaudación entre fechas, altas y bajas usan su índice nuevo.
- El filtro por categoría usa `idx_alumno_categoria` también cuando se consulta sobre `vista_alumno`.
- El listado paginado lee del índice solo las filas de la página, sin ordenamiento adicional, incluido el desempate por `dni`.
- Las consultas marcadas como "ya cubiertas" usan las claves primarias, `UNIQUE` y foráneas existentes.
