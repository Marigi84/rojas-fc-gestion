# Propuesta de Módulos

## Sistema de Gestión Rojas FC

Este documento propone los módulos funcionales del Sistema de Gestión Rojas FC, construidos exclusivamente a partir de:

- `docs/requisitos/requisitos-funcionales.md` (RF-01 a RF-57)
- `docs/requisitos/requisitos-no-funcionales.md` (RNF-01 a RNF-16)
- `docs/reglas-negocio/reglas-de-negocio.md` (RN-01 a RN-43)
- `docs/alcance/revision-del-alcance.md`
- la Primera Entrega aprobada

En este documento, **Administrador** designa al actor "Administrador / Coordinador" definido en la Primera Entrega.

### Alcance de esta propuesta

Esta etapa define únicamente la estructura funcional del sistema: qué módulos existen, de qué se encarga cada uno y qué requisitos funcionales cubre. **No se definen tablas, claves, entidades ni atributos** — esas decisiones corresponden al modelado de entidades y relaciones y al diseño del esquema relacional, que siguen a esta propuesta.

### Antecedente

Existió una propuesta de módulos anterior (issue #7, validada por el equipo), construida sobre una versión previa de los requisitos (64 RF). Se utiliza aquí únicamente como antecedente. Varios requisitos cambiaron sustancialmente desde entonces —se retiró la preinscripción, se reformularon eventos e indumentaria, se unificó Administrador/Coordinador— por lo que cada módulo fue revisado desde cero contra el documento de requisitos vigente, sin asumir que una decisión anterior sigue siendo válida solo por haber sido incluida antes.

---

## 1. Autenticación y Autorización

**Responsabilidad principal:** gestionar el acceso al sistema para los dos tipos de usuario —Administrador y Responsable— y restringir qué funcionalidades ve cada uno una vez autenticado. Se agrupa en un mismo módulo el login y la recuperación/restablecimiento de contraseña de ambos actores, porque es el mismo mecanismo de acceso aplicado a dos roles distintos, no dos mecanismos separados.

**Requisitos funcionales cubiertos:** RF-11, RF-15, RF-49, RF-50, RF-51, RF-52.

---

## 2. Alumnos, Responsables y Categorías

**Responsabilidad principal:** alta, consulta, modificación, baja y reactivación de alumnos; alta, consulta y modificación de responsables; asociación y desvinculación entre ambos; y determinación automática de la categoría del alumno según su año de nacimiento.

Se agrupan los tres porque comparten el mismo actor (Administrador) y porque un alumno no tiene sentido administrativo sin al menos un responsable asociado ni sin su categoría asignada. El alta de un alumno —directa, ya que la preinscripción quedó fuera del alcance actual (ver sección "Fuera de alcance")— exige asociar al menos un responsable (RF-01, RN-05), y ese mínimo se preserva también al desvincular (RF-10). Si el DNI ya corresponde a una persona registrada con otro rol, el sistema reutiliza sus datos (RF-01, RF-06, RN-01).

Las categorías quedan fijas, determinadas únicamente por el año de nacimiento (RF-16, RN-13); esta entrega no incluye un ABM de categorías, por no formar parte del alcance comprometido en la Primera Entrega.

**Requisitos funcionales cubiertos:** RF-01, RF-02, RF-03, RF-04, RF-05, RF-06, RF-07, RF-08, RF-09, RF-10, RF-16.

---

## 3. Portal del Responsable

**Responsabilidad principal:** una vez autenticado, permitir a un responsable consultar —solo lectura— los alumnos que tiene a su cargo, su situación administrativa (cuotas e historial de pagos) y sus recibos.

**Requisitos funcionales cubiertos:** RF-12, RF-13, RF-14.

> El login y el restablecimiento de contraseña del responsable (RF-11, RF-15) quedan en el módulo de Autenticación y Autorización, no en este, porque son el mismo mecanismo de acceso que usa el Administrador — separar "cómo entro" de "qué puedo ver una vez adentro" evita mezclar responsabilidades.

---

## 4. Cuotas

**Responsabilidad principal:** configuración del valor general, vencimiento único e interés por mora de cada período; generación automática de las cuotas mensuales para alumnos activos; aplicación del interés a cuotas vencidas e impagas; y consulta de cuotas por alumno y de alumnos morosos.

**Requisitos funcionales cubiertos:** RF-17, RF-18, RF-19, RF-20, RF-21, RF-22, RF-23.

---

## 5. Pagos y Recibos

**Responsabilidad principal:** registrar pagos —de cuotas, de participaciones en eventos o de encargos de indumentaria—, admitiendo pagos parciales donde corresponda; anularlos cuando se registren incorrectamente; y generar, consultar, descargar y compartir el recibo asociado a cada pago confirmado.

Se agrupan Pagos y Recibos en un mismo módulo porque todo pago confirmado genera un recibo (RN-33): son dos caras de la misma operación, no dos funcionalidades independientes.

**Requisitos funcionales cubiertos:** RF-24, RF-25, RF-26, RF-27, RF-28, RF-29, RF-30, RF-31, RF-32, RF-33.

---

## 6. Eventos e Indumentaria

**Responsabilidad principal:** registrar eventos con su importe único; asignar alumnos como participantes de un evento; registrar encargos de indumentaria asociados a un alumno; y consultar participaciones y encargos, con su importe total, pagos realizados y saldo pendiente.

**Requisitos funcionales cubiertos:** RF-34, RF-35, RF-36, RF-37.

---

## 7. Búsquedas, Listados, Exportación y Reportes

**Responsabilidad principal:** búsqueda y filtrado de alumnos; listado paginado; exportación a Excel de listados de alumnos y de participaciones/encargos; y consultas por rango de fechas de recaudación, deuda pendiente y altas/bajas.

**Requisitos funcionales cubiertos:** RF-38, RF-39, RF-40, RF-41, RF-42, RF-43, RF-44.

---

## 8. Dashboard

**Responsabilidad principal:** mostrar los indicadores agregados de uso frecuente —cantidad de alumnos activos, alumnos por categoría, existencia de alumnos morosos y recaudación del mes en curso— como vista inicial del sistema.

Se mantiene separado del módulo de Búsquedas, Listados, Exportación y Reportes porque responde a una necesidad distinta: el Dashboard es una vista fija de indicadores al ingresar al sistema, mientras que el módulo anterior responde a consultas específicas que el Administrador define con filtros y rangos de fechas.

**Requisitos funcionales cubiertos:** RF-45, RF-46, RF-47, RF-48.

---

## 9. Funcionamiento sin Conexión y Sincronización

**Responsabilidad principal:** permitir la consulta de información básica y el registro provisional de pagos durante una pérdida de conectividad, y sincronizar esas operaciones con el sistema central al restablecerse la conexión, informando el resultado y emitiendo el recibo correspondiente una vez validada la sincronización.

**Requisitos funcionales cubiertos:** RF-53, RF-54, RF-55, RF-56, RF-57.

---

## Cobertura

| Módulo | RF cubiertos | Cantidad |
|---|---|---|
| 1. Autenticación y Autorización | RF-11, RF-15, RF-49 a RF-52 | 6 |
| 2. Alumnos, Responsables y Categorías | RF-01 a RF-10, RF-16 | 11 |
| 3. Portal del Responsable | RF-12 a RF-14 | 3 |
| 4. Cuotas | RF-17 a RF-23 | 7 |
| 5. Pagos y Recibos | RF-24 a RF-33 | 10 |
| 6. Eventos e Indumentaria | RF-34 a RF-37 | 4 |
| 7. Búsquedas, Listados, Exportación y Reportes | RF-38 a RF-44 | 7 |
| 8. Dashboard | RF-45 a RF-48 | 4 |
| 9. Funcionamiento sin Conexión y Sincronización | RF-53 a RF-57 | 5 |
| **Total** | **RF-01 a RF-57** | **57** |

Los 9 módulos cubren la totalidad de los 57 requisitos funcionales (RF-01 a RF-57), verificado uno por uno. Cada RF tiene un único módulo responsable: no hay requisitos sin cubrir ni requisitos asignados a más de un módulo.

Esto no impide que los módulos interactúen entre sí en tiempo de ejecución (por ejemplo, Pagos y Recibos con Cuotas y con Eventos e Indumentaria, o Búsquedas/Reportes y Dashboard leyendo datos de casi todos los demás): la agrupación de arriba define quién es **dueño** de cada funcionalidad, no quién puede consumir sus datos.

---

## Fuera de alcance

- **Preinscripción en línea:** reprogramada como evolución posterior del sistema. El detalle y la justificación están en `docs/alcance/revision-del-alcance.md`, sección 1. Ningún módulo de esta propuesta la incluye.
- **Matrícula de ingreso:** no forma parte de los requisitos vigentes. RN-25 enumera taxativamente las obligaciones económicas del sistema —cuota, participación en evento o encargo de indumentaria— y la matrícula no es una de ellas.

## Requisitos no funcionales

Los requisitos no funcionales (`docs/requisitos/requisitos-no-funcionales.md`) no generan módulos propios: son condiciones de calidad —seguridad, integridad, rendimiento, usabilidad, disponibilidad— que atraviesan a todos los módulos definidos arriba, no funcionalidades independientes con pantalla propia.

Esto incluye específicamente **RNF-11 (Trazabilidad de operaciones)**: no se propone un módulo de "Auditoría" separado, porque ningún requisito funcional pide una pantalla dedicada para consultarla. Es una capacidad transversal —registro interno de qué administrador hizo cada operación y cuándo— que da soporte a los módulos de Alumnos, Cuotas, Pagos y Eventos e Indumentaria, no una funcionalidad propia con la que interactúe un usuario.

---

**Estado:** Propuesta inicial, construida contra los requisitos, reglas de negocio y revisión de alcance vigentes. Pendiente de revisión por Silvia y Marina.
