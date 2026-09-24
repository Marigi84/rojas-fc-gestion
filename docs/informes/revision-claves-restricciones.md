# Informe de revisión de claves y restricciones

## Sistema de Gestión Rojas FC

**Objetivo:** Revisar y definir las claves y restricciones necesarias para garantizar la integridad del modelo relacional.  
**Alcance:** Claves candidatas, claves primarias, claves naturales y artificiales, unicidades, claves foráneas y restricciones directamente relacionadas con el modelo.

---

## 1. Fuentes y criterio de revisión

Se utilizan como referencia:

- `docs/2da Entrega - Diseño y Modulos.md`: DER actualizado y decisiones vigentes.
- `docs/entidades-relaciones-cardinalidad/entidades-relaciones-cardinalidad.md`: entidades, relaciones y cardinalidades del dominio.
- `database/schema.sql`: esquema físico actual, tomado como propuesta a validar.
- Requisitos funcionales y reglas de negocio: solo como respaldo de restricciones del dominio.

---

Las claves y restricciones que ya aparecen en `schema.sql` se validan caso por caso contra el DER.

## 2. Decisiones del DER que afectan las claves y restricciones

- `CATEGORIA` se elimina como entidad independiente; la categoría se deriva del año de nacimiento.
- `CONFIGURACION_CUOTA` se relaciona con `CUOTA` de uno a muchos. Por eso, `cuota` debe tener una FK hacia `configuracion_cuota`.
- `ALUMNO_MOVIMIENTO` debe distinguir `ALTA`, `BAJA` y `REACTIVACION`.
- `alumno.dni` y `responsable.dni` deben ser `NOT NULL UNIQUE`.

### 2.1 Elementos que conviene distinguir

| Elemento | Se encuentra en el DER | Se encuentra en el `schema.sql` | Lectura correcta |
|---|---|---|---|
| `cuota.id` | Sí | Sí | Clave artificial actual, utilizada por `pago.cuota_id` |
| `(alumno_id, periodo)` | Identifica la cuota del dominio | `UNIQUE` | Clave natural alternativa de `cuota` |
| `cuota.configuracion_cuota_id` | **Sí** | **No** | La relación está definida en el DER, pero falta en el esquema |
| `cuota_id_activo` | **No** | **Sí**, como columna generada con `UNIQUE` | Restricción técnica del esquema para limitar pagos no anulados por cuota |
| `recibo.numero` | Sí | Sí, con `UNIQUE` | Clave natural si los números emitidos son inmutables y no se reutilizan |
| `recibo.pago_id` | Sí | Sí, con `NOT NULL UNIQUE` | Clave alternativa y relación de como máximo un recibo por pago |
| `REACTIVACION` | **Sí** | **No**, el `ENUM` actual solo tiene `ALTA` y `BAJA` | Decisión pendiente de aplicar en el esquema |
| `categoria` y `alumno.categoria_id` | **No**, la entidad fue eliminada | **Sí** | Tabla y FK que todavía existen en el esquema |
| `constancia_offline` y `auditoria` | No forman parte del modelo relacional funcional vigente | Sí, actualmente están en `schema.sql` | Deben eliminarse al consolidar el esquema |

---

## 3. Análisis de claves por entidad

| Entidad o estructura | Claves candidatas o identificadores condicionales | PK actual | Decisión y justificación |
|---|---|---|---|
| `responsable` | `dni` | `id` | Mantener `id` como PK artificial y `dni` como clave natural única |
| `usuario` | `email` o `responsable_id`, según el rol | `id` | Mantener `id`; `email` y `responsable_id` son identificadores condicionales, no claves globales |
| `alumno` | `dni` | `id` | Mantener `id` artificial y `dni` natural único |
| `alumno_responsable` | `(alumno_id, responsable_id)` | PK compuesta | No agregar `id`; la combinación identifica la relación |
| `alumno_movimiento` | No hay una clave natural robusta | `id` | Mantener `id` porque cada fila representa un evento del historial |
| `preinscripcion` | No existe número de solicitud funcional | `id` | Mantener `id`; `alumno_id` controla la relación opcional con un alumno |
| `configuracion_cuota` | No hay clave natural declarada y demostrada | `id` | Mantener `id` porque la configuración tiene versiones históricas |
| `cuota` | `(alumno_id, periodo)` | `id` | Mantener `id` para `pago.cuota_id`; conservar la clave natural como alternativa única |
| `cobro_extraordinario` | `concepto` no es una clave única | `id` | Mantener `id` artificial porque el concepto puede repetirse |
| `pago` | No existe identificador externo de transacción | `id` | Mantener `id`; cada pago es una operación individual |
| `recibo` | `pago_id`; `numero` si es inmutable | `id` | Mantener `id` técnico y declarar las otras claves alternativas |

### 3.1 Claves naturales

Se utilizan como clave natural cuando representan un identificador del negocio y son suficientemente estables:

- `alumno.dni`;
- `responsable.dni`;
- `(alumno_id, periodo)` para una cuota;
- `(alumno_id, responsable_id)` para una asociación;
- `recibo.numero`, si no se modifica ni se reutiliza.

### 3.2 Claves artificiales

Se utiliza una clave artificial cuando la entidad representa un evento, una transacción o una operación sin identificador natural confiable, o cuando se requiere una referencia estable para otras tablas.

Por ejemplo:

- `alumno_movimiento.id`;
- `preinscripcion.id`;
- `pago.id`;
- `cobro_extraordinario.id`;
- `configuracion_cuota.id`;
- `cuota.id`, para simplificar `pago.cuota_id`;
- `recibo.id`, como clave técnica de acceso interno.

`alumno_responsable` es la excepción que demuestra que no todos los `id` son necesarios: su PK compuesta identifica directamente la relación.

---

## 4. Restricciones de unicidad

### 4.1 Unicidades que se mantienen

| Restricción | Entidad | Justificación |
|---|---|---|
| `responsable.dni UNIQUE` | `responsable` | Evita duplicar una persona responsable |
| `usuario.email UNIQUE` | `usuario` | Evita duplicar cuentas de Administradores o Coordinadores |
| `usuario.responsable_id UNIQUE` | `usuario` | Garantiza como máximo un usuario no nulo por responsable |
| `alumno.dni UNIQUE` | `alumno` | Evita duplicar alumnos |
| `(alumno_id, responsable_id)` PK | `alumno_responsable` | Evita duplicar la misma asociación |
| `alumno_id UNIQUE` | `preinscripcion` | Garantiza como máximo una preinscripción vinculada por alumno |
| `(alumno_id, periodo) UNIQUE` | `cuota` | Garantiza una cuota por período y alumno |
| `cuota_id_activo UNIQUE` | `pago` | **[SOLO ESTA EN EL SCHEMA]** Garantiza como máximo un pago no anulado por cuota y permite varios anulados |
| `pago_id UNIQUE` | `recibo` | Garantiza como máximo un recibo por pago |
| `numero UNIQUE` | `recibo` | Evita números de recibo repetidos |

No se agregan restricciones `UNIQUE` a nombres, apellidos, teléfonos, vínculos, barrios, localidades, colegios, conceptos o tipos sin una regla de negocio que lo justifique.

### 4.2 Unicidad del DNI

Se mantienen:

- `alumno.dni NOT NULL UNIQUE`;
- `responsable.dni NOT NULL UNIQUE`.

La preinscripción utiliza el DNI para identificar al alumno durante la solicitud, pero la aprobación no debe generar un nuevo alumno si ya existe un registro con ese DNI. En caso de reingreso de un alumno inactivo, se reutiliza y reactiva el registro existente sin generar una nueva preinscripción.

La restricción `preinscripcion.alumno_id UNIQUE` garantiza que un alumno quede vinculado como máximo a una preinscripción.

---

## 5. Claves foráneas

Las cardinalidades se encuentran en `docs/entidades-relaciones-cardinalidad/entidades-relaciones-cardinalidad.md`. Aquí solo se identifican las FKs necesarias para controlar las claves y la integridad.

| FK | Estado |
|---|---|
| `usuario.responsable_id → responsable.id` | Implementada |
| `alumno.categoria_id → categoria.id` | **[SOLO ESTA EN EL SCHEMA]**; debe desaparecer para aplicar la decisión del DER |
| `alumno_movimiento.alumno_id → alumno.id` | Implementada |
| `alumno_responsable.alumno_id → alumno.id` | Implementada |
| `alumno_responsable.responsable_id → responsable.id` | Implementada |
| `preinscripcion.usuario_revisor_id → usuario.id` | Implementada |
| `preinscripcion.alumno_id → alumno.id` | Implementada |
| `cuota.alumno_id → alumno.id` | Implementada |
| `cuota.configuracion_cuota_id → configuracion_cuota.id` | **Falta en `schema.sql`; definida en el DER** |
| `cobro_extraordinario.alumno_id → alumno.id` | Implementada |
| `pago.cuota_id → cuota.id` | Implementada |
| `pago.cobro_extraordinario_id → cobro_extraordinario.id` | Implementada |
| `pago.usuario_id → usuario.id` | Implementada |
| `pago.usuario_anulador_id → usuario.id` | Implementada |
| `recibo.pago_id → pago.id` | Implementada |

Las FKs no especifican `ON DELETE` ni `ON UPDATE`, por lo que se conserva el comportamiento restrictivo por defecto. Esto es coherente con la conservación del historial y con la baja lógica de los alumnos.

---

## 6. Restricciones de integridad directamente relacionadas

| Regla | Estado | Evaluación |
|---|---|---|
| `alumno.dni` único | Implementada | Correcta |
| `responsable.dni` único | Implementada | Correcta |
| Un responsable puede tener como máximo un usuario | Implementada con `responsable_id UNIQUE` | Correcta |
| No repetir la misma asociación alumno–responsable | Implementada con PK compuesta | Correcta |
| Máximo dos responsables por alumno | Implementada con triggers | Coherente con el DER |
| Cada alumno tiene al menos un responsable | No garantizada por una FK o `CHECK` | Debe controlarse en el proceso de alta |
| Un pago corresponde a exactamente una obligación | Implementada con `CHECK` | Correcta |
| Un pago anulado tiene motivo | Implementada con `CHECK` | Correcta |
| Solo un pago no anulado por cuota | Implementada con `cuota_id_activo` | **[SOLO ESTA EN EL SCHEMA]**; correcta como restricción técnica |
| Número de recibo único | Implementada con `UNIQUE` | Correcta |
| Un recibo como máximo por pago | Implementada con `pago_id UNIQUE` | La obligatoriedad de un recibo para un pago confirmado pertenece al proceso |
| Datos de prenda y talle para indumentaria | Implementada con `CHECK` | Correcta |

---

## 7. Discrepancias que afectan el modelo de claves

| Discrepancia | Decisión vigente | Situación de `schema.sql` |
|---|---|---|
| `CATEGORIA` | Se elimina como entidad | La tabla y `alumno.categoria_id` todavía existen |
| `CONFIGURACION_CUOTA`–`CUOTA` | La relación 1:N y `configuracion_cuota_id` están definidas | La columna y la FK no están implementadas |
| `ALUMNO_MOVIMIENTO.tipo` | Debe incluir `ALTA`, `BAJA` y `REACTIVACION` | El `ENUM` actual solo contiene `ALTA` y `BAJA` |
| `PAGO`–`RECIBO` | Un pago confirmado debe tener recibo | La base garantiza como máximo un recibo; la obligatoriedad es de proceso |
| `CONSTANCIA_OFFLINE` | No se modela como entidad ni tabla del modelo relacional vigente | Existe actualmente en `schema.sql`, pero debe eliminarse al consolidar el esquema |
| `AUDITORIA` | No se modela como entidad ni tabla necesaria en esta etapa | Existe actualmente en `schema.sql`, pero debe eliminarse al consolidar el esquema |

---

## 8. Conclusión

La revisión de claves y restricciones queda completa. Las decisiones sobre claves naturales, claves artificiales, unicidades y claves foráneas están justificadas y alineadas con el DER.
Las discrepancias detectadas en schema.sql quedan pendientes de corrección.

