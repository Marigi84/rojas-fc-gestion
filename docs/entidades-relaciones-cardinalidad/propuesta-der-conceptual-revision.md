# Modelo conceptual — revisión

**Trabajo Final Integrador — Sistema de Gestión Rojas FC**

Este documento presenta el **modelo conceptual** del sistema de gestión de Rojas FC. Se construye a partir de los requisitos funcionales, los requisitos no funcionales, las reglas de negocio y las devoluciones docentes recibidas sobre las versiones anteriores.

El modelo representa el dominio mediante **entidades, atributos, relaciones, cardinalidades, especializaciones y restricciones**. Para cada entidad se marca su **identificador**, que se convertirá en clave primaria al construir el modelo relacional. No se incluyen todavía decisiones del modelo lógico o físico: claves foráneas, tablas intermedias, tipos de datos, índices ni triggers.

El diagrama utiliza **notación Chen**: rectángulos para las entidades (doble para la entidad débil), rombos para las relaciones (doble para la relación identificadora) y óvalos para los atributos. El identificador se muestra subrayado, los atributos derivados con línea punteada y los multivaluados con doble óvalo. Las cardinalidades se expresan como **(mín, máx)** junto a cada entidad: indican en cuántas ocurrencias de la relación participa cada ocurrencia de esa entidad.

---

## 1. Criterio de modelado

Un elemento del dominio se modela como **entidad** cuando tiene varias ocurrencias, **atributos o relaciones propias que la distinguen del resto** y algo que permita identificar cada ocurrencia. Si no cumple esas condiciones, se modela como atributo, como relación o queda fuera del modelo.

Todo elemento del modelo se justifica con un requisito o una regla de negocio. La cantidad de ocurrencias que hoy tiene una entidad en la escuela no condiciona su estructura: el modelo no limita, por ejemplo, la cantidad de personas que integran el personal administrativo, porque ningún requisito lo establece.

---

## 2. Entidades y atributos

Referencias: *(c)* compuesto · *(m)* multivaluado · *(d)* derivado · *(opc.)* opcional.

### Persona

Representa a una persona registrada en el sistema.

| Atributo | Tipo |
|---|---|
| **DNI** | Identificador |
| Nombre | |
| Apellido | |

No deben existir dos Personas con el mismo DNI (RN-02, RN-08). El personal administrativo también se identifica por su DNI, al ser una Persona; ningún requisito lo exige ni lo prohíbe, y se adopta como supuesto del modelo.

### Alumno

Especialización de Persona. Representa al alumno registrado en la escuela (RF-01).

| Atributo | Tipo |
|---|---|
| Fecha de nacimiento | |
| Dirección: Domicilio, Barrio, Localidad | *(c)* |
| Colegio | |
| Importe de cuota particular | *(opc.)* — RF-24, RN-18 |
| Categoría | *(d)* — del año de nacimiento (RF-16, RN-10) |
| Estado (activo/inactivo) | *(d)* — del último Movimiento de Alumno |
| Moroso | *(d)* — si posee al menos una cuota vencida con saldo (RN-26) |

### Responsable

Especialización de Persona. Representa al adulto responsable de uno o más alumnos (RF-06).

| Atributo | Tipo |
|---|---|
| Teléfono | |
| Contraseña | *(opc.)* — acceso al portal con DNI y contraseña (RF-11, RF-15) |

La contraseña es opcional porque un Responsable puede estar registrado sin haber activado todavía su acceso. El vínculo madre/padre/tutor pertenece a la relación con el Alumno, no al Responsable.

### Personal Administrativo

Especialización de Persona. Representa el perfil que los requisitos denominan **Administrador/Coordinador**: se autentica para acceder a las funcionalidades de gestión (RF-58) y el sistema lo distingue del Responsable (RF-59).

| Atributo | Tipo |
|---|---|
| Correo electrónico | Clave candidata (único) — identificación de acceso |
| Contraseña | |

Se relaciona con las operaciones que realiza: revisa preinscripciones, registra y anula pagos, y registra los movimientos de los alumnos.

### Preinscripción

Representa un formulario de preinscripción recibido, que la administración revisa y luego aprueba o rechaza (RF-17 a RF-20, RN-11 a RN-14). Una Preinscripción aprobada da lugar al alta de un nuevo Alumno o a la reactivación de uno existente.

| Atributo | Tipo |
|---|---|
| **Número** | Identificador |
| Fecha | |
| Estado: Pendiente, Aprobada o Rechazada | |
| Fecha de resolución | *(opc.)* — se completa al aprobarla o rechazarla |
| Datos del alumno: Nombre, Apellido, DNI, Fecha de nacimiento, Domicilio, Barrio, Localidad, Colegio | *(c)* |
| Datos del responsable: Nombre, Apellido, DNI, Teléfono, Vínculo | *(c) (m)* — entre 1 y 2 ocurrencias |

La Preinscripción conserva la información **declarada** por la familia. Mientras está pendiente puede ser corregida por la administración (RF-19); una vez resuelta, queda como registro de lo recibido.

Los datos declarados no duplican los de Alumno y Responsable: representan un hecho distinto (lo declarado en una fecha determinada), mientras que Alumno y Responsable contienen los datos actuales y verificados. Además, una Preinscripción rechazada nunca corresponde a un Alumno, por lo que sus datos solo existen en ella.

### Movimiento de Alumno

**Entidad débil**, dependiente de Alumno. Representa cada cambio administrativo del alumno (RF-04, RF-05, RF-51).

| Atributo | Tipo |
|---|---|
| **Fecha y hora** | Identificador parcial (discriminador) |
| Tipo: Alta, Baja o Reactivación | |
| Observación | *(opc.)* |

Se identifica por el Alumno al que pertenece junto con su fecha y hora. Se registra la hora para distinguir dos movimientos del mismo día, por ejemplo una baja por error y su reactivación inmediata.

### Configuración de Cuota

Representa las condiciones generales utilizadas para generar las cuotas (RF-21, RF-26, RN-22).

| Atributo | Tipo |
|---|---|
| **Vigente desde** | Identificador |
| Importe general | |
| Porcentaje de interés | |

Un cambio realizado el mismo día de vigencia de una configuración la corrige, en lugar de crear una nueva; por eso la fecha de vigencia la identifica.

### Período de Cuota

Representa cada período mensual para el cual se generan cuotas.

| Atributo | Tipo |
|---|---|
| **Período** (por ejemplo, 2026-03) | Identificador |
| Fecha de vencimiento | |

Cada período tiene una única fecha de vencimiento común a todas sus cuotas (RF-23, RN-19). Se modela como entidad para registrar ese dato una sola vez y no repetirlo en cada cuota.

### Obligación de Pago

Superclase. Representa una obligación económica concreta de un Alumno.

| Atributo | Tipo |
|---|---|
| **Número** | Identificador |
| Importe | |
| Saldo | *(d)* — importe menos los importes aplicados por pagos no anulados |

Se especializa en Cuota, Matrícula y Cobro Extraordinario.

### Cuota

Especialización de Obligación de Pago. Representa la obligación mensual de un Alumno (RF-22).

| Atributo | Tipo |
|---|---|
| Recargo por mora | *(opc.)* |
| Estado: Pendiente, Vencida o Pagada | *(d)* — del vencimiento del período y de los pagos |

El **Recargo por mora** registra el monto del interés aplicado cuando la cuota vence impaga. Se registra en lugar de calcularse porque conserva el valor aplicado aunque luego cambie el porcentaje configurado (RN-23) y permite asegurar que el interés se aplique una sola vez (RN-21). El período y la fecha de vencimiento se obtienen a través de la relación con Período de Cuota.

### Matrícula

Especialización de Obligación de Pago. Representa la obligación económica correspondiente al primer ingreso del Alumno (RF-31, RN-29).

| Atributo | Tipo |
|---|---|
| Fecha de matriculación | *(d)* — fecha del Movimiento de Alta que la origina |

El importe es definido por la administración al momento de la inscripción de cada alumno (RN-48). Lo que distingue a la Matrícula de los demás subtipos es su relación con el **Movimiento de Alta**: solo ese movimiento la origina, por lo que una reactivación no genera una nueva (RN-30). Debe abonarse en su totalidad en una única operación (RN-31); habitualmente se abona al momento de la inscripción, aunque puede quedar pendiente.

### Cobro Extraordinario

Especialización de Obligación de Pago. Representa una obligación no periódica de un Alumno, correspondiente a un evento o a un pedido de indumentaria (RF-41, RN-42).

| Atributo | Tipo |
|---|---|
| Fecha de generación | |

Admite pagos parciales (RN-43) y no está sujeto al interés por mora (RN-46). El concepto cobrado se obtiene del Evento o del tipo de prenda.

Un cobro correspondiente a un evento representa la **participación confirmada** del alumno: no todos los alumnos participan de cada evento, y el cobro se asigna al confirmar la asistencia, aunque el pago se realice después.

### Cobro de Indumentaria

Especialización de Cobro Extraordinario. Representa el cobro de una prenda institucional solicitada para un Alumno.

| Atributo | Tipo |
|---|---|
| Tipo de prenda | |
| Talle | |

RF-44 solo exige identificar el tipo de prenda y el talle solicitado dentro del cobro, y RN-45 requiere definirlos para validar el pedido. Ningún requisito pide registrar el pedido por separado, con un ciclo de vida propio, por lo que el pedido y su cobro se modelan como una única entidad.

### Evento

Representa un evento de la escuela al que pueden corresponder cobros de distintos alumnos.

| Atributo | Tipo |
|---|---|
| **Nombre + Año** | Identificador compuesto |

El año se mantiene separado del nombre porque un mismo evento puede repetirse en distintas ediciones (por ejemplo, Mundialito 2026 y Mundialito 2027). Se modela como entidad, y no como texto dentro de cada cobro, porque un mismo evento genera cobros para muchos alumnos.

### Pago

Representa una operación económica registrada en el sistema (RF-32 a RF-35).

| Atributo | Tipo |
|---|---|
| **Número de operación** | Identificador |
| Fecha | |
| Medio de pago: Efectivo o Transferencia | *(opc.)* — RN-33, RN-34 |
| Anulado | |
| Motivo de anulación | *(opc.)* — obligatorio si el pago está anulado (RN-36) |
| Fecha de anulación | *(opc.)* — obligatoria si el pago está anulado |
| Importe total | *(d)* — suma de los importes aplicados |

La anulación se modela con atributos de Pago y no como una entidad propia, porque no tiene identidad independiente: existe solo si existe el Pago y como máximo una vez por pago.

### Recibo

Representa el comprobante correspondiente a un Pago (RF-36, RN-38, RN-39).

| Atributo | Tipo |
|---|---|
| **Número** | Identificador |
| Fecha de emisión | |

La información del alumno, los conceptos, los importes, el medio de pago y el saldo pendiente informado se obtienen a través del Pago y de las obligaciones a las que se aplica.

---

## 3. Especializaciones

| Superclase | Subclases | Tipo | Justificación |
|---|---|---|---|
| Persona | Alumno, Responsable, Personal Administrativo | **Total y solapada** | Toda persona registrada es al menos uno de los tres. Un Responsable puede integrar también el Personal Administrativo. Alumno y Responsable no se superponen: el responsable es un adulto a cargo del alumno (RN-07) y el alumno es el menor que asiste a la escuela. |
| Obligación de Pago | Cuota, Matrícula, Cobro Extraordinario | **Total y exclusiva** | Toda obligación es exactamente de uno de los tres tipos, cada uno con reglas propias. |
| Cobro Extraordinario | Cobro de Indumentaria | **Parcial** | Los cobros que no son de indumentaria corresponden a un Evento. No se modela un subtipo "Cobro de Evento" porque no tendría atributos propios. |

---

## 4. Relaciones y cardinalidades

Cada cardinalidad indica en cuántas ocurrencias de la relación participa cada ocurrencia de la entidad.

| # | Relación | Cardinalidades | Justificación |
|---|---|---|---|
| R1 | Alumno — **tiene como responsable** — Responsable | Alumno (1,2) · Responsable (1,N) | RN-01, RN-05, RN-07. Atributo de la relación: **Vínculo** |
| R2 | Preinscripción — **corresponde a** — Alumno | Preinscripción (0,1) · Alumno (0,N) | RF-01 (alta directa sin preinscripción), RF-17, RF-20 (alta o reactivación) |
| R3 | Personal Administrativo — **revisa** — Preinscripción | Personal (0,N) · Preinscripción (0,1) | RF-19, RF-20, RN-14. Una preinscripción pendiente aún no tiene revisor |
| R4 | Alumno — **registra** — Movimiento de Alumno *(identificadora)* | Alumno (1,N) · Movimiento (1,1) | RF-04, RF-05, RF-51 |
| R5 | Personal Administrativo — **realiza** — Movimiento de Alumno | Personal (0,N) · Movimiento (1,1) | RF-04, RF-05, RNF-14 (la baja es una operación sensible) |
| R6 | Alumno — **posee** — Obligación de Pago | Alumno (0,N) · Obligación (1,1) | RN-42, RF-28 |
| R7 | Movimiento de Alumno — **origina** — Matrícula | Movimiento (0,1) · Matrícula (1,1) | RN-29, RN-30 |
| R8 | Configuración de Cuota — **rige** — Período de Cuota | Configuración (0,N) · Período (1,1) | RN-17, RN-23 |
| R9 | Período de Cuota — **comprende** — Cuota | Período (0,N) · Cuota (1,1) | RF-23, RN-19 |
| R10 | Evento — **corresponde a** — Cobro Extraordinario | Evento (0,N) · Cobro (0,1) | RF-41. Un evento puede existir antes de que los alumnos confirmen su participación |
| R11 | Pago — **se aplica a** — Obligación de Pago | Pago (1,N) · Obligación (0,N) | RF-34, RF-42. Atributo de la relación: **Importe aplicado** |
| R12 | Personal Administrativo — **registra** — Pago | Personal (0,N) · Pago (1,1) | RF-32 |
| R13 | Personal Administrativo — **anula** — Pago | Personal (0,N) · Pago (0,1) | RF-35, RN-36, RNF-14 |
| R14 | Pago — **genera** — Recibo | Pago (1,1) · Recibo (1,1) | RN-38, RF-64 |

**Relaciones que no se dibujan a propósito:**

- **Alumno — Pago:** sería redundante, porque el alumno de un pago se obtiene a través de las obligaciones a las que se aplica.
- **Personal Administrativo — Configuración de Cuota y — Cuota:** las cuotas se generan automáticamente (RF-22), y ningún requisito pide registrar quién modificó la configuración.

**Pago y Recibo 1:1.** Un pago registrado sin conexión se conserva en el dispositivo hasta su sincronización (RNF-10) y recién entonces se incorpora al sistema como Pago, junto con su Recibo (RF-64). Por eso, dentro del modelo conceptual, no existe un Pago sin Recibo. Si el Pago se anula, el Recibo se conserva (RF-40, RN-41).

---

## 5. Atributos de relaciones

- **Vínculo** (Madre, Padre, Tutor), en la relación Alumno — tiene como responsable — Responsable (RN-09). No pertenece al Responsable, porque una misma persona puede tener distinto vínculo con distintos alumnos.
- **Importe aplicado**, en la relación Pago — se aplica a — Obligación de Pago. Indica cuánto de un pago corresponde a cada obligación. En el modelo relacional, esta relación N:M se resolverá con una tabla intermedia.

---

## 6. Restricciones

Reglas que el diagrama no puede expresar. En la etapa de claves y restricciones se definirá cómo se garantiza cada una.

### A. Dominio

- Vínculo: Madre, Padre o Tutor (RN-09).
- Tipo de Movimiento: Alta, Baja o Reactivación.
- Estado de Preinscripción: Pendiente, Aprobada o Rechazada.
- Medio de pago: Efectivo o Transferencia (RN-33).
- Los importes son mayores que cero; el porcentaje de interés es mayor o igual que cero (RN-22).

### B. Unicidad

1. No hay dos Personas con el mismo DNI (RN-02, RN-08).
2. No hay dos integrantes del Personal Administrativo con el mismo Correo electrónico.
3. Un Alumno tiene como máximo una Cuota por Período de Cuota.
4. Existe una sola Configuración de Cuota por fecha de vigencia.

### C. Personas

5. Una Persona no puede ser Alumno y Responsable a la vez (RN-07).

### D. Alumnos y movimientos

6. Cada Alumno tiene un único Movimiento de tipo Alta, que es el primero de su historial.
7. Una Baja solo puede registrarse si el alumno está activo, y una Reactivación solo si está dado de baja (RN-03, RN-04).

### E. Preinscripción

8. Solo una Preinscripción Aprobada puede corresponder a un Alumno.
9. La Fecha de resolución y el revisor existen solo si la Preinscripción no está Pendiente.
10. Si el DNI declarado corresponde a un Alumno registrado, no se crea un nuevo Alumno: se reactiva el existente con los datos actualizados (RF-17, RF-20).
11. Una Preinscripción no genera obligaciones económicas (RN-47).

### F. Cuotas

12. Las cuotas se generan el día 1 de cada mes, solo para los alumnos activos (RN-15, RN-16).
13. El importe de la cuota es el importe particular del alumno, si lo tiene; si no, el importe general de la Configuración que rige el período (RN-17, RN-18).
14. El Recargo por mora se aplica una sola vez, solo si la cuota venció impaga, con el porcentaje de la Configuración que rige el período (RN-20, RN-21).
15. Solo puede corregirse el importe de las cuotas pendientes del período en curso; una cuota pagada no se modifica (RN-24, RN-25).
16. Una Cuota se abona en una única operación cuyo importe aplicado es exactamente el total adeudado (importe más recargo, si corresponde) (RN-32). Por eso no admite pagos parciales ni saldo a favor.

### G. Matrícula y cobros extraordinarios

17. Solo un Movimiento de tipo Alta origina una Matrícula (RN-30).
18. La Matrícula se abona en una única operación cuyo importe aplicado es exactamente su importe (RN-31).
19. Todo Cobro Extraordinario corresponde a un Evento o es un Cobro de Indumentaria, nunca ambos ni ninguno.
20. Un Cobro Extraordinario admite pagos parciales (RN-43), pero la suma de los importes aplicados por pagos no anulados no puede superar su importe. Si se recibe un importe mayor, la administración devuelve la diferencia y solo se registra lo efectivamente cobrado.

### H. Pagos y recibos

21. Un Pago corresponde a exactamente un Alumno, determinado a través de las Obligaciones de Pago a las que se aplica. Por eso todas las obligaciones alcanzadas por un mismo Pago deben pertenecer al mismo Alumno.
22. Si el Pago está anulado, son obligatorios el Motivo de anulación, la Fecha de anulación y el integrante del Personal Administrativo que lo anuló; si no está anulado, no existen (RN-35, RN-36).
23. La Fecha de anulación es igual o posterior a la Fecha del Pago.
24. La anulación de un Pago no elimina su registro ni su Recibo; el Recibo pasa a considerarse anulado (RN-37, RN-41, RF-40).

---

## 7. Datos derivados

Los atributos derivados se representan en el diagrama, marcados como tales, porque forman parte del dominio aunque se calculen a partir de otros datos. Al construir el modelo relacional, en general no se almacenarán.

| Atributo derivado | Se obtiene de |
|---|---|
| Categoría del Alumno | Año de nacimiento (RN-10) |
| Estado del Alumno | Último Movimiento de Alumno |
| Moroso | Cuotas vencidas con saldo (RN-26) |
| Saldo de una Obligación | Importe menos importes aplicados por pagos no anulados |
| Estado de una Cuota | Vencimiento del período y pagos aplicados |
| Fecha de matriculación | Fecha del Movimiento de Alta |
| Importe total de un Pago | Suma de los importes aplicados |

**Saldo pendiente y RN-44.** RN-44 exige conservar el saldo pendiente de un cobro extraordinario no abonado en su totalidad. Esa exigencia se cumple aunque el saldo sea derivado: se obtiene siempre del importe de la obligación menos los importes aplicados por pagos no anulados, información que nunca se elimina (RN-35, RN-37). Calcularlo evita que un saldo registrado y los pagos queden inconsistentes. Lo mismo vale para el saldo informado en el recibo de un pago parcial (RF-36).

---

## 8. Elementos que no forman parte del modelo

- **Usuario** como entidad separada: el acceso del Responsable se resuelve con su DNI y su contraseña, y el del Personal Administrativo con su correo y su contraseña.
- **Pedido de Indumentaria** separado del cobro: ver Cobro de Indumentaria.
- **Anulación** como entidad: ver Pago.
- **Cobro de Evento** como subtipo: no tendría atributos propios.
- **Condición Particular de Cuota** como entidad: el importe particular es un atributo opcional de Alumno.
- **Categoría, Colegio, Barrio y Localidad** como entidades: son atributos del Alumno.
- **Saldo a favor:** las reglas de pago total de cuotas y matrícula lo impiden, y en los cobros extraordinarios la administración devuelve el excedente.
- **Funcionamiento offline y auditoría técnica:** se resuelven en etapas posteriores, como decisiones de arquitectura e implementación.

---

## 9. DER conceptual

![DER conceptual Rojas FC](./img/der-conceptual.svg)

El diagrama se genera con PlantUML a partir de [`der-conceptual.puml`](./der-conceptual.puml).

---

## 10. Cambios respecto de la versión anterior

A partir de la devolución docente ("entidades sin atributos" y "relaciones o entidades que lógicamente no tienen sentido") y de la revisión del equipo, el modelo se reconstruyó desde los requisitos y se comparó con la versión anterior.

| # | Cambio | Motivo |
|---|---|---|
| 1 | Se incorpora **Personal Administrativo** como subtipo de Persona, con Correo electrónico y Contraseña | El sistema debe autenticarlo y distinguirlo del Responsable (RF-58, RF-59). Reemplaza a la entidad Administrador, que no tenía atributos ni relaciones |
| 2 | Se agregan las relaciones **revisa**, **realiza**, **registra** y **anula** | El modelo no representaba quién realiza las operaciones (RF-20, RF-32, RF-35, RNF-14) |
| 3 | La especialización de Persona pasa a ser **total y solapada** | Un Responsable puede integrar también el Personal Administrativo |
| 4 | Se elimina **Usuario**; cada tipo de persona con acceso tiene su contraseña | Usuario tenía un único atributo y no tenía identificador propio |
| 5 | **Matrícula** vuelve a ser un subtipo, relacionado con el **Movimiento de Alta** | La relación debe dibujarse sobre el subtipo al que corresponde, y la matrícula no puede quedar definida por exclusión |
| 6 | Se elimina **Pedido de Indumentaria**; se incorpora el subtipo **Cobro de Indumentaria** | El pedido y su cobro estaban en relación 1:1 obligatoria |
| 7 | **Evento** se relaciona directamente con Cobro Extraordinario | Un subtipo "Cobro de Evento" habría quedado sin atributos |
| 8 | **Cuota** incorpora Recargo por mora | Conserva el interés aplicado (RN-21, RN-23) |
| 9 | **Preinscripción** incorpora Número, Estado y Fecha de resolución, y se relaciona con Alumno y con Personal Administrativo | Antes no tenía relaciones |
| 10 | Se marcan los **identificadores**; **Movimiento de Alumno** es entidad débil | Faltaban identificadores |
| 11 | Los **atributos derivados** se representan en el diagrama | Forman parte del modelo conceptual |
| 12 | **Dirección** se modela como atributo compuesto | Agrupa Domicilio, Barrio y Localidad |
| 13 | **Pago — Recibo** pasa a 1:1 | El funcionamiento offline no puede justificar una cardinalidad del modelo conceptual |
| 14 | Se agregan restricciones de unicidad, de secuencia de movimientos y de importes aplicados | El modelo no las expresaba |
| 15 | El diagrama pasa a **notación Chen** | Es la notación de referencia de la cátedra |

---

## 11. Decisiones de etapas posteriores

Se tratarán al trabajar claves, restricciones, modelo relacional y modelo físico:

- claves primarias, candidatas y foráneas, y uso de claves sustitutas;
- resolución de las relaciones N:M, de las especializaciones y de los atributos multivaluados;
- implementación de las restricciones de la sección 6;
- tipos de datos e índices;
- auditoría técnica y sincronización offline.

---

## 12. Próximos pasos

1. Validar este modelo conceptual con el equipo y el tutor.
2. Definir claves y restricciones de integridad (#13).
3. Construir el modelo relacional y actualizar `schema.sql` (#14).
4. Generar el diagrama de tablas con MySQL Workbench mediante ingeniería inversa.
