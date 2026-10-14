# Claves y restricciones del esquema relacional

## Sistema de Gestión Rojas FC

> **Propuesta inicial para la resolución del Issue #23:** Definir las claves primarias, foráneas y restricciones de integridad del modelo relacional para su revisión y validación por parte del equipo.

---

## 1. Criterio de elección de claves primarias

Se prioriza el uso de claves naturales siempre que los atributos del dominio garanticen unicidad y estabilidad.

Como resultado, 12 de las 14 tablas emplean claves naturales o compuestas, reservando identificadores artificiales exclusivamente para las 2 tablas donde una regla de negocio lo exige de forma explícita (RN-26 y RN-34).

---

## 2. Clave primaria de cada tabla

| Tabla | Clave primaria | Tipo | Justificación |
|---|---|---|---|
| `persona` | `dni` | Natural | Identificador único. Garantiza que una persona física exista una sola vez en el sistema (RN-01, RN-04). |
| `alumno` | `dni` | Natural (heredada) | Especialización de Persona: el alumno adopta la identidad natural de la persona a la que representa. |
| `responsable` | `dni` | Natural (heredada) | Especialización de Persona: hereda el DNI como clave primaria. |
| `administrador` | `dni` | Natural (heredada) | Especialización de Persona: hereda el DNI como clave primaria (RF-51). |
| `permanencia` | `(dni_alumno, fecha_alta)` | Natural (compuesta) | Entidad dependiente del alumno: un período de actividad cobra identidad al combinar al alumno con su fecha de ingreso. |
| `periodo` | `periodo` | Natural | Corresponde al atributo "Mes y año" del DER; identifica de forma unívoca a cada período mensual. |
| `cuota` | `(dni_alumno, periodo)` | Natural (compuesta) | Obligación mensual: no existe aislada, su identidad está dada por el alumno y el período al que corresponde (RN-16). |
| `evento` | `(nombre, anio)` | Natural (compuesta) | Identificador compuesto del dominio: el nombre del evento dentro de un año determinado. |
| `participacion` | `(dni_alumno, nombre_evento, anio_evento)` | Natural (compuesta) | Inscripción a un evento: depende de la existencia simultánea del alumno y del evento específico (RN-40). |
| `encargo` | `(dni_alumno, fecha_hora)` | Natural (compuesta) | Pedido de indumentaria: depende del alumno que lo encarga y del momento exacto del pedido. |
| `pago` | `id_pago` | **Artificial** | Exigencia explícita de **RN-26**: cada pago debe contar con un número identificatorio propio asignado por el sistema. |
| `medio_pago` | `nombre` | Natural | Dominio cerrado y tipificado con valores fijos: efectivo y transferencia (RN-27). |
| `recibo` | `numero_recibo` | **Artificial** | Exigencia explícita de **RN-34**: cada comprobante emitido requiere un número identificatorio propio. |
| `responsable_alumno` | `(dni_responsable, dni_alumno)` | Natural (compuesta) | Tabla intermedia de la relación M:N; la clave es la combinación del responsable y el alumno a su cargo. |

Las entidades dependientes conservan sus claves compuestas naturales, evitando agregar identificadores artificiales innecesarios.

---

## 3. Claves foráneas

Toda clave foránea es obligatoria (NOT NULL) cuando el DER muestra participación total —línea doble— y opcional —admite NULL— cuando es parcial —línea simple—. En tablas intermedias (M:N), ambas columnas son obligatorias porque vinculan los dos extremos.

| Tabla | Clave foránea | Tabla referenciada | Justificación y obligatoriedad |
|---|---|---|---|
| `alumno` | `dni` | `persona.dni` | Subtipo obligatorio: un alumno debe existir previamente como persona. |
| `responsable` | `dni` | `persona.dni` | Subtipo obligatorio: un responsable debe existir previamente como persona. |
| `administrador` | `dni` | `persona.dni` | Subtipo obligatorio: un administrador debe existir previamente como persona. |
| `responsable_alumno` | `dni_responsable` | `responsable.dni` | Vínculo obligatorio: referencia al adulto a cargo. |
| `responsable_alumno` | `dni_alumno` | `alumno.dni` | Vínculo obligatorio: referencia al menor. |
| `permanencia` | `dni_alumno` | `alumno.dni` | Obligatoria: toda permanencia pertenece a un alumno. |
| `permanencia` | `dni_admin_alta` | `administrador.dni` | Obligatoria (trazabilidad RNF-11): registra qué administrador dio el alta. |
| `permanencia` | `dni_admin_baja` | `administrador.dni` | Opcional (trazabilidad RNF-11): solo se completa al registrarse la baja. |
| `periodo` | `dni_admin` | `administrador.dni` | Obligatoria (trazabilidad RNF-11): administrador que configuró el ciclo. |
| `cuota` | `dni_alumno` | `alumno.dni` | Obligatoria: la deuda mensual corresponde obligatoriamente a un alumno. |
| `cuota` | `periodo` | `periodo.periodo` | Obligatoria: la cuota debe corresponder a un período válido y existente. |
| `evento` | `dni_admin` | `administrador.dni` | Obligatoria (trazabilidad RNF-11): administrador que creó el evento. |
| `participacion` | `dni_alumno` | `alumno.dni` | Obligatoria: la participación requiere la presencia de un alumno. |
| `participacion` | `(nombre_evento, anio_evento)` | `evento(nombre, anio)` | Obligatoria: la participación debe imputarse a un evento registrado. |
| `encargo` | `dni_alumno` | `alumno.dni` | Obligatoria: todo pedido de indumentaria pertenece a un alumno. |
| `encargo` | `dni_admin` | `administrador.dni` | Obligatoria (trazabilidad RNF-11): administrador que tomó el encargo. |
| `pago` | `dni_admin` | `administrador.dni` | Obligatoria (trazabilidad RNF-11): administrador que cobró y registró la operación. |
| `pago` | `dni_admin_anula` | `administrador.dni` | Opcional (trazabilidad RNF-11): solo se completa si el pago es anulado. |
| `pago` | `nombre_medio_pago` | `medio_pago.nombre` | Obligatoria: todo pago debe tener un canal de cobro válido (RN-27). |
| `pago` | `(cuota_dni_alumno, cuota_periodo)` | `cuota(dni_alumno, periodo)` | Opcional: solo se completa si el pago corresponde al saldo de una cuota (RN-25). |
| `pago` | `(participacion_dni_alumno, participacion_nombre_evento, participacion_anio_evento)` | `participacion(dni_alumno, nombre_evento, anio_evento)` | Opcional: solo se completa si el pago se imputa a un evento (RN-25). |
| `pago` | `(encargo_dni_alumno, encargo_fecha_hora)` | `encargo(dni_alumno, fecha_hora)` | Opcional: solo se completa si el pago se imputa a indumentaria (RN-25). |
| `recibo` | `id_pago` | `pago.id_pago` | Obligatoria: todo recibo acredita formalmente un pago efectuado. |


---

## 4. Restricciones de unicidad

Además de las garantías provistas por las claves primarias:

| Restricción | Tabla | Justificación | Regla |
|---|---|---|---|
| Un recibo por pago | `recibo` | Relación 1 a 1: cada pago confirmado genera exactamente un comprobante oficial. | RN-33 |
| Una permanencia activa por alumno | `permanencia` | Un alumno no puede tener dos períodos de actividad abiertos a la vez; para reincorporarse debe estar dado de baja. | RN-07 |
| Un solo pago vigente por cuota | `pago` | Una cuota se abona en su totalidad; puede tener registros anulados por error, pero solo un cobro activo válido. | RN-28, RN-29 |


**Garantía de unicidad sobre la persona:**  
El DNI actúa como clave primaria en `persona`, y es heredado por `alumno`, `responsable` y `administrador`. Esto asegura estructuralmente que no puedan duplicarse registros de personas ni asignarse identidades repetidas entre roles (RN-01, RN-04).

Las dos últimas restricciones no se resuelven con un `UNIQUE` simple sobre columnas de fecha: una fila vigente lleva su fecha en `NULL` (`fecha_baja` o `fecha_anulacion`), y en MySQL los valores `NULL` no colisionan en un índice único, por lo que dos
registros vigentes pasarían sin impedimento. Requieren una columna generada de vigencia (`vigente`) que tome valor `1` mientras el registro esté activo/vigente y `NULL` cuando esté cerrado o anulado. Al admitir MySQL múltiples valores `NULL`, se permite
almacenar todo el historial de bajas o anulaciones, pero al ser el valor `1` un valor escalar, el índice `UNIQUE` bloquea la coexistencia de dos registros vigentes para un mismo alumno o cuota. Su definición concreta queda para el diseño detallado (#24).

---

## 5. Restricciones derivadas de las reglas de negocio

| Regla | Descripción de la regla | Mecanismo de control |
|---|---|---|
| RN-01 | Una persona se registra una sola vez en el sistema | Clave primaria (`PK`) en `persona.dni` para evitar duplicados en la base. |
| RN-02 | Toda persona registrada debe tener al menos un rol asignado | Capa de servicios en el alta; en la base de datos, `TRIGGER` en `DELETE` sobre las tablas de roles para impedir dejar a una persona sin ningún rol. |
| RN-03 | Incompatibilidad: un alumno activo no puede ser responsable | `TRIGGER` en `responsable` y en `permanencia` (o validación de servicios) que cruza las tablas y bloquea asignar el rol si posee una permanencia activa. |
| RN-04 | Todo alumno debe contar con DNI | Clave primaria obligatoria (`PK` `NOT NULL`) en `alumno.dni`. |
| RN-05 | Todo alumno debe tener al menos un responsable a cargo | Capa de servicios en el alta; en la base de datos, `TRIGGER` en `DELETE` sobre `responsable_alumno` que impide desvincular al último tutor del alumno (RF-10). |
| RN-06 | La reincorporación conserva el historial anterior | Clave foránea `(FK)` de `permanencia` hacia `alumno` con `ON DELETE RESTRICT`, insertando una nueva fila por cada reingreso. |
| RN-07 | Un alumno no puede tener dos permanencias activas al mismo tiempo | Restricción de unicidad (`UNIQUE`) sobre `(dni_alumno, vigente)`, donde `vigente` vale `1` si está activo y `NULL` si está dado de baja: admite múltiples bajas históricas pero solo una permanencia activa por alumno. |
| RN-08 | La fecha de baja no puede ser anterior a la de alta | Restricción `CHECK (fecha_baja IS NULL OR fecha_baja >= fecha_alta)` en la tabla `permanencia`. |
| RN-09 | La baja lógica no elimina información ni historial | Integridad referencial con `ON DELETE RESTRICT`; la baja se asienta actualizando `fecha_baja`, sin borrado físico. |
| RN-10 | Igual jerarquía entre todos los responsables de un alumno | Por diseño estructural: la tabla `responsable_alumno` no define columna de rango o prioridad, tratando a todos por igual. |
| RN-11 | Un responsable puede estar a cargo de múltiples alumnos | Clave primaria compuesta (`PK`) en `responsable_alumno(dni_responsable, dni_alumno)`. |
| RN-12 | El vínculo de parentesco o tutoría es obligatorio | Restricción `NOT NULL` sobre la columna `vinculo` en `responsable_alumno`. |
| RN-13 | La categoría del alumno se determina por su año de nacimiento | Columna generada o cálculo derivado a partir de `fecha_nacimiento` (RF-16). |
| RN-14 | Las cuotas solo se generan para alumnos activos | `TRIGGER` en `cuota` que valida la existencia de una permanencia activa al momento de la inserción. |
| RN-15 | Las cuotas se generan el día 1 de cada mes | Proceso programado del sistema; regla operativa temporal que no requiere restricción estática en la base. |
| RN-16 | Un alumno solo puede tener una cuota por período | Clave primaria compuesta (`PK`) en `cuota(dni_alumno, periodo)`. |
| RN-17 | El valor general de la cuota corresponde al importe del período | Vista o cálculo derivado en `cuota.monto_a_pagar` que lee el valor general de la cuota desde `periodo.importe`. |
| RN-18 | Cada período tiene una única fecha de vencimiento | Una sola columna `fecha_vencimiento` en la tabla `periodo`. |
| RN-19 | El interés por mora aplica a cuotas vencidas e impagas | Vista o cálculo dinámico en `cuota.monto_a_pagar` que adiciona el recargo si la cuota permanece impaga después del vencimiento. |
| RN-20 | El interés por mora se aplica una sola vez (recargo único) | Lógica de cálculo en vista: aplica un porcentaje fijo sobre el valor base de la cuota, sin acumulación periódica. |
| RN-21 | El porcentaje de interés es parametrizable por período | Columna `porcentaje_interes` en la tabla `periodo`, que configura el Administrador. |
| RN-22 | El importe, la fecha de vencimiento y el porcentaje de interés de un período no pueden modificarse una vez generadas sus cuotas | `TRIGGER` en `UPDATE` sobre `periodo` con cláusula `EXISTS` sobre `cuota`, que bloquea cambios en esos tres atributos. |
| RN-23 | Condición de moroso: alumno con cuota vencida e impaga | Vista calculada (`alumno.moroso`) que evalúa la existencia de cuotas vencidas impagas asociadas al alumno. |
| RN-24 | La condición de morosidad no provoca la baja automática | Regla de proceso: el estado del alumno no se deriva de la vista de morosidad ni cierra la permanencia. |
| RN-25 | Cada pago se imputa exactamente a una única obligación | Restricción `CHECK` sobre las columnas de `FK` en `pago`: exige que exactamente un grupo de claves foráneas esté completo y los demás en `NULL`. |
| RN-26 | Cada pago posee numeración unívoca asignada por el sistema | Clave primaria artificial `(PK)` en `pago.id_pago`, con un valor que asigna el sistema.  |
| RN-27 | Solo se admiten pagos en efectivo o transferencia | Clave foránea (`FK`) de `pago` hacia `medio_pago`, restringiendo los valores al dominio habilitado. |
| RN-28 | El pago de una cuota debe cubrir la totalidad de la misma | `TRIGGER` en `INSERT` sobre `pago` que valida que el monto pagado sea exactamente igual al total adeudado de la cuota evaluado a la fecha del pago. |
| RN-29 | Una cuota admite como máximo un pago vigente activo | Restricción de unicidad (`UNIQUE`) sobre `(cuota_dni_alumno, cuota_periodo, vigente)`, donde `vigente` vale `1` si está activo y `NULL` si está anulado: admite múltiples pagos anulados pero a lo sumo un pago vigente. |
| RN-30 | Los pagos erróneos se anulan, nunca se eliminan físicamente | `TRIGGER BEFORE DELETE` sobre `pago` que rechaza cualquier eliminación física. La baja es lógica registrando anulación (`UPDATE`). |
| RN-31 | Toda anulación exige registrar fecha y motivo | Restricción `CHECK`: exige que `fecha_anulacion`, `motivo` y `dni_admin_anula` estén todos definidos o los tres en `NULL` (RNF-11). |
| RN-32 | Los pagos anulados no computan en recaudación ni saldos | Filtro en vistas y consultas contables que descarta filas con `fecha_anulacion IS NOT NULL`. |
| RN-33 | Todo pago confirmado genera automáticamente su recibo | `TRIGGER` o servicio transaccional que inserta la fila correspondiente en `recibo` cuando el pago queda confirmado (RF-29, RF-57). |
| RN-34 | Cada recibo cuenta con un número propio | Clave primaria artificial `(PK)` en `recibo.numero_recibo`.|
| RN-35 | El recibo de un pago parcial no extingue la obligación | Lógica de cálculo en vista: el recibo acredita solo el cobro ingresado y mantiene el saldo adeudado por la diferencia. |
| RN-36 | Los recibos emitidos no pueden borrarse | `TRIGGER BEFORE DELETE` sobre `recibo` que rechaza cualquier eliminación física en la base de datos. |
| RN-37 | El recibo de un pago anulado debe reflejar el estado anulado | Vista calculada (`recibo.anulado`) que deriva el estado leyendo la anulación del pago referenciado. |
| RN-38 | Cada evento posee un único importe común a todos | Una sola columna `importe` en la tabla `evento`. |
| RN-39 | El importe de un evento no cambia tras la primera inscripción | `TRIGGER` en `UPDATE` sobre `evento` con cláusula `EXISTS` sobre `participacion` para bloquear cambios de precio. |
| RN-40 | Toda participación y encargo exige vinculación a un alumno; la participación, además, a su evento | Claves foráneas obligatorias (`FK` `NOT NULL`) hacia `alumno.dni` en `participacion` y `encargo`, y hacia `evento` en `participacion`. |
| RN-41 | Eventos e indumentaria admiten pagos parciales | No aplican la restricción de unicidad de RN-29: esa cubre únicamente los pagos imputados a una cuota. |
| RN-42 | Conservación del saldo pendiente de eventos e indumentaria | Vistas calculadas (`participacion.saldo` y `encargo.saldo`): costo original menos sumatoria de pagos válidos. |
| RN-43 | Eventos e indumentaria no devengan recargo por mora | Por estructura relacional: las tablas de eventos y pedidos no se vinculan con `periodo` ni implementan cálculo de recargos. |

---

## 6. Justificaciones arquitectónicas del modelo

### Validación de altas y bajas en relaciones obligatorias
Las reglas exigen que toda persona tenga al menos un rol, y que todo alumno tenga al menos un responsable. Dado que las operaciones de
inserción en una base de datos relacional son secuenciales (primero se crea la persona y luego sus perfiles y asociaciones) y que
MySQL no admite restricciones diferidas, un trigger no podría esperar al cierre de la transacción para comprobar la existencia cruzada:
en el INSERT la fila que falta todavía no existe y la comprobación fallaría siempre. Por eso la integridad de estas altas se garantiza de
forma atómica en la capa de servicios del backend, dentro de una misma transacción. Complementariamente, en la base de datos se restringe el borrado
para impedir que un alumno quede huérfano de responsable o una persona sin roles.

### Pago asignado a una única obligación (RN-25)
Un pago solo puede saldar una de tres obligaciones: una cuota mensual, una inscripción a evento o un encargo de indumentaria. Para garantizar esto, la tabla `pago` contendrá las claves foráneas correspondientes a cada concepto y una **restricción de validación (`CHECK`)** que obliga a que
**exactamente una** de las opciones esté completada y las demás permanezcan vacías (`NULL`).

### Desvinculación de responsables y ciclo de vida de la persona (RF-10 y RN-02)
Todo responsable se registra siempre vinculado a un alumno (RF-06). Al desvincular a un responsable de un alumno (RF-10), 
la capa de negocio conserva el registro del adulto únicamente si continúa a cargo de algún otro alumno. Si se desvincula
de su último alumno y no posee otro rol en el sistema (alumno o administrador), la capa de servicios **elimina su registro** 
tanto de `responsable` como de `persona` en la misma transacción, evitando que el trigger de RN-02 bloquee la operación por dejar 
a una persona sin roles. Si en cambio la persona cumple además otro rol activo, solo se elimina su fila en `responsable`.

---

## 7. Criterio de finalización

- [x] Clave primaria de cada tabla definida y justificada (14 tablas).
- [x] Criterio de claves naturales vs. artificiales fundamentado.
- [x] Evitado el uso indiscriminado de identificadores `id` (solo los requeridos por RN-26 y RN-34).
- [x] Claves foráneas definidas y justificadas (23), coherentes con la participación de cada relación en el DER.
- [x] Restricciones de unicidad identificadas (DNI, permanencias y cuotas).
- [x] Trazabilidad completa de las 43 Reglas de Negocio a sus mecanismos de resolución.
- [x] Documentada la justificación de cada decisión arquitectónica y técnica.
