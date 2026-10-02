# Modelo conceptual — revisión

**Trabajo Final Integrador — Sistema de Gestión Rojas FC**

Este documento presenta el **modelo conceptual** revisado del sistema de gestión de Rojas FC. Se construye a partir de los requisitos funcionales, las reglas de negocio y las devoluciones docentes recibidas sobre el modelo anterior.

El objetivo de esta etapa es representar el dominio mediante **entidades, atributos, especializaciones, relaciones, cardinalidades y restricciones conceptuales**, sin incorporar todavía decisiones propias del modelo lógico o físico.

Por lo tanto, en esta instancia no se definen IDs técnicos, PK, FK, tipos SQL, índices, triggers ni mecanismos de implementación. Sí se indica, para cada entidad, el atributo o la combinación de atributos que la **identifica** conceptualmente (marcado como *identificador*).

---

## 1. Entidades identificadas

### Persona

Representa a una persona registrada definitivamente en el sistema.

**Atributos:**
- DNI *(identificador)*
- Nombre
- Apellido

No deben existir dos Personas distintas con el mismo DNI.

### Alumno

Especialización de Persona. Representa al alumno registrado en la escuela.

**Atributos:**
- Fecha de nacimiento
- Colegio
- Domicilio
- Barrio
- Localidad
- Importe de cuota particular *(opcional)*

El colegio, barrio y localidad se mantienen como datos del Alumno para permitir búsquedas y filtros; no se modelan como entidades independientes.

La categoría del Alumno se deriva de su fecha de nacimiento.

### Responsable

Especialización de Persona. Representa al adulto responsable de uno o más alumnos.

**Atributos:**
- Teléfono
- Contraseña *(opcional)*

La contraseña permite al Responsable acceder al portal utilizando su DNI como identificación (RF-11). Es opcional porque un Responsable puede estar registrado sin haber activado todavía su acceso.

El vínculo madre/padre/tutor no pertenece al Responsable de manera aislada, sino a la relación entre Alumno y Responsable.

### Preinscripción

Representa un formulario de preinscripción recibido, que es revisado por la administración y luego aprobado o rechazado. Una Preinscripción aprobada da lugar al alta de un nuevo Alumno o a la reactivación de un Alumno existente.

**Atributos:**
- Número de preinscripción *(identificador)*
- Fecha
- Estado: Pendiente, Aprobada o Rechazada
- Fecha de resolución *(opcional; se completa al aprobarla o rechazarla)*
- Datos declarados del alumno *(atributo compuesto)*:
  - Nombre
  - Apellido
  - DNI
  - Fecha de nacimiento
  - Domicilio
  - Barrio
  - Localidad
  - Colegio
- Datos declarados de responsable *(atributo compuesto multivaluado, entre 1 y 2 ocurrencias)*:
  - Nombre
  - Apellido
  - DNI
  - Teléfono
  - Vínculo

La Preinscripción conserva la información **declarada** por la familia. Mientras está pendiente puede ser corregida por la administración (RF-19); una vez resuelta, queda como registro de lo recibido.

Los datos declarados no constituyen una duplicación de los datos de Alumno y Responsable: representan un hecho distinto (lo declarado en una fecha determinada), mientras que Alumno y Responsable contienen los datos actuales y verificados, que se actualizan con el tiempo. Además, una Preinscripción rechazada nunca corresponde a un Alumno, por lo que sus datos solo existen en ella.

Si el DNI del alumno ya corresponde a un Alumno inactivo, no se crea un nuevo Alumno: al aprobarse la Preinscripción, se reactiva el Alumno existente utilizando los datos actualizados del formulario (RF-17, RF-20). Habitualmente la reactivación la realiza directamente la administración, pero puede ocurrir que la familia complete nuevamente el formulario.

Una Preinscripción no genera cuotas ni otras obligaciones económicas.

### Movimiento de Alumno

Entidad débil. Representa los cambios administrativos del Alumno a lo largo del tiempo.

**Atributos:**
- Fecha y hora *(identificador parcial)*
- Tipo: Alta, Baja o Reactivación
- Observación

Se identifica por el Alumno al que pertenece junto con su fecha y hora. Se registra la hora para distinguir dos movimientos realizados el mismo día (por ejemplo, una baja por error y su reactivación inmediata).

Todo Alumno registra al menos un movimiento. El estado activo o inactivo del Alumno se deriva de su último movimiento.

El Movimiento de tipo **Alta**, que corresponde al primer ingreso del Alumno, origina su Matrícula (ver Obligación de Pago).

### Configuración de Cuota

Representa las condiciones generales utilizadas para generar cuotas mensuales.

**Atributos:**
- Vigente desde *(identificador)*
- Importe general
- Porcentaje de interés

Una nueva configuración afecta únicamente a los períodos futuros. Cada Período de Cuota conserva la relación con la Configuración que lo rige.

### Período de Cuota

Representa cada período mensual para el cual se generan cuotas.

**Atributos:**
- Período *(identificador; por ejemplo, 2026-03)*
- Fecha de vencimiento

Cada Período de Cuota posee una única fecha de vencimiento común para todas las cuotas de ese período (RF-23, RN-19). Se modela como entidad para registrar ese dato una sola vez, en lugar de repetirlo en cada cuota.

### Obligación de Pago

Representa una obligación económica concreta de un Alumno.

**Atributos:**
- Número de obligación *(identificador)*
- Importe

Toda Obligación de Pago corresponde exactamente a uno de estos tipos:
- Cuota *(subtipo)*
- Cobro Extraordinario *(subtipo)*
- Matrícula *(originada por el Movimiento de Alta)*

**Matrícula.** Es la obligación económica correspondiente al primer ingreso del Alumno. No se modela como subtipo porque no posee datos propios: su importe es el de toda Obligación de Pago y su fecha coincide con la del alta. Lo que la distingue es su origen, por lo que se representa mediante la relación **Movimiento de Alumno — origina matrícula — Obligación de Pago**:
- solo el Movimiento de tipo Alta origina matrícula; una Reactivación no genera una nueva (RN-30);
- como cada Alumno tiene un único Movimiento de Alta, no puede tener más de una Matrícula;
- debe abonarse en su totalidad en una única operación, sin pagos parciales (RN-31);
- habitualmente se abona al momento de la inscripción, aunque puede quedar pendiente.

### Cuota

Especialización de Obligación de Pago. Representa la obligación mensual de un Alumno.

**Atributos:**
- Recargo por mora *(opcional)*

Hereda de Obligación de Pago el atributo **Importe**, que queda fijado al generarse la cuota.

El **recargo por mora** registra el monto del interés aplicado cuando la cuota vence impaga. Se guarda en lugar de calcularse porque:
- conserva el valor aplicado aunque luego cambie el porcentaje configurado (RN-23);
- permite asegurar que el interés se aplique una sola vez (RN-21).

Permanece vacío mientras no corresponda aplicar interés.

El período y la fecha de vencimiento se obtienen a través de la relación con Período de Cuota. El saldo, el estado de la cuota y la condición de morosidad son datos derivados.

### Cobro Extraordinario

Especialización de Obligación de Pago. Representa una obligación no periódica de un Alumno.

**Atributos:**
- Fecha

Admite pagos parciales y no está sujeto al interés por mora definido para las cuotas mensuales.

Todo Cobro Extraordinario corresponde a un **Evento** o es un **Cobro de Indumentaria**, nunca ambos ni ninguno. El concepto cobrado se obtiene del Evento o del tipo de prenda.

### Cobro de Indumentaria

Especialización de Cobro Extraordinario. Representa el pedido y cobro de una prenda institucional a un Alumno.

**Atributos:**
- Tipo de prenda
- Talle

El pedido y su cobro se registran en el mismo momento (habitualmente con una seña), por lo que constituyen una única entidad. El tipo de prenda y el talle se conservan para validar el pedido y resolver posibles reclamos (RF-44, RN-45).

### Evento

Representa un evento de la escuela al que pueden corresponder cobros extraordinarios de distintos alumnos.

**Atributos:**
- Nombre *(identificador, junto con Año)*
- Año *(identificador, junto con Nombre)*

El Año se mantiene separado del Nombre porque un mismo evento puede repetirse en distintas ediciones (por ejemplo, Mundialito 2026 y Mundialito 2027).

### Pago

Representa una operación económica registrada en el sistema.

**Atributos:**
- Número de operación *(identificador)*
- Fecha
- Medio de pago *(opcional)*
- Anulado
- Motivo de anulación *(obligatorio cuando el Pago está anulado)*
- Fecha de anulación *(obligatoria cuando el Pago está anulado)*

El importe total del Pago no se almacena: se obtiene como la suma de los importes aplicados a cada obligación.

El medio de pago se mantiene como atributo controlado porque, en el alcance actual, únicamente contempla Efectivo y Transferencia y no posee información propia adicional que justifique una entidad independiente.

### Recibo

Representa el comprobante correspondiente a un Pago.

**Atributos:**
- Número *(identificador)*
- Fecha de emisión

La información del Alumno, los conceptos abonados, los importes aplicados, el importe total, el medio de pago y el saldo pendiente informado se obtienen a través del Pago y de las Obligaciones a las que se aplica.

---

## 2. Especializaciones

### Persona

Persona se especializa de forma **total y exclusiva** en:

- Alumno
- Responsable

Toda Persona registrada definitivamente es Alumno o Responsable, y no ambas a la vez.

### Obligación de Pago

Obligación de Pago se especializa de forma **parcial y exclusiva** en:

- Cuota
- Cobro Extraordinario

Los subtipos heredan Número de obligación e Importe. Las obligaciones que no pertenecen a ninguno de los dos subtipos son Matrículas, originadas por el Movimiento de Alta del Alumno.

### Cobro Extraordinario

Cobro Extraordinario se especializa de forma **parcial** en:

- Cobro de Indumentaria

Los cobros extraordinarios que no son de indumentaria corresponden a un Evento. No se modela un subtipo "Cobro de Evento" porque no posee atributos propios: el alumno, el evento y el importe ya se obtienen de sus relaciones y de Obligación de Pago, y su condición de pago es derivada.

---

## 3. Relaciones y cardinalidades

### Alumno — Responsable: tiene como responsable

- Todo Alumno debe tener entre 1 y 2 Responsables.
- Todo Responsable se encuentra asociado al menos a 1 Alumno y puede estar asociado a varios.
- Es una relación N:M.
- La relación posee el atributo **Vínculo**: madre, padre o tutor.

### Preinscripción — Alumno: corresponde a

- Una Preinscripción corresponde a 0..1 Alumno: a uno si fue aprobada (alta nueva o reactivación), a ninguno si está pendiente o fue rechazada.
- A un Alumno pueden corresponder 0..N Preinscripciones: ninguna si fue registrado directamente por la administración (RF-01), una por su alta y otra por cada reactivación realizada mediante formulario.

### Alumno — Movimiento de Alumno: registra

- Un Alumno registra 1..N Movimientos.
- Cada Movimiento pertenece exactamente a 1 Alumno, del cual depende para su identificación.

### Movimiento de Alumno — Obligación de Pago: origina matrícula

- Un Movimiento de Alumno origina 0..1 Obligación de Pago: solo el Movimiento de tipo Alta origina la Matrícula.
- Una Obligación de Pago es originada por 0..1 Movimiento: por uno si es la Matrícula, por ninguno si es Cuota o Cobro Extraordinario.

### Alumno — Obligación de Pago: posee

- Un Alumno puede poseer 0..N Obligaciones de Pago.
- Cada Obligación de Pago pertenece exactamente a 1 Alumno.

### Configuración de Cuota — Período de Cuota: rige

- Una Configuración de Cuota puede regir 0..N Períodos de Cuota.
- Cada Período de Cuota es regido por exactamente 1 Configuración de Cuota.

### Período de Cuota — Cuota: comprende

- Un Período de Cuota puede comprender 0..N Cuotas.
- Cada Cuota pertenece exactamente a 1 Período de Cuota.

### Evento — Cobro Extraordinario: corresponde a

- A un Evento pueden corresponder 0..N Cobros Extraordinarios.
- Un Cobro Extraordinario corresponde a 0..1 Evento: a uno si es un cobro de evento, a ninguno si es un Cobro de Indumentaria.

### Pago — Obligación de Pago: se aplica a

- Todo Pago se aplica a 1..N Obligaciones de Pago.
- Una Obligación de Pago puede tener 0..N Pagos relacionados a lo largo de su historial.
- Es una relación N:M.
- La relación posee el atributo **Importe aplicado**.

### Pago — Recibo: genera

- Cada Pago genera exactamente 1 Recibo.
- Cada Recibo corresponde exactamente a 1 Pago.

Un pago registrado sin conexión se conserva en el dispositivo hasta su sincronización (RNF-10) y recién entonces se incorpora al sistema como Pago, junto con su Recibo (RF-64). Por eso, dentro del modelo conceptual, no existe un Pago sin Recibo.

Si el Pago se anula, el Recibo se conserva (RF-40, RN-41); su condición de anulado se deriva del Pago.

---

## 4. Atributos de relaciones

### Vínculo

Pertenece a la relación **Alumno — tiene como responsable — Responsable**.

Valores previstos:
- Madre
- Padre
- Tutor

No se almacena como atributo propio de Responsable porque una misma persona puede cumplir distintos vínculos respecto de distintos alumnos.

### Importe aplicado

Pertenece a la relación **Pago — se aplica a — Obligación de Pago**.

Permite expresar cuánto de un Pago corresponde a cada Obligación alcanzada.

En el modelo conceptual se conserva como atributo de la relación N:M. Su transformación a una estructura intermedia se resolverá al construir el modelo relacional.

---

## 5. Restricciones conceptuales

**Personas**
- No deben existir dos Personas distintas con el mismo DNI.
- La especialización de Persona es total y exclusiva.
- Un Alumno debe tener entre 1 y 2 Responsables.

**Alumnos y preinscripciones**
- El estado activo/inactivo del Alumno se deriva de su último Movimiento.
- La categoría del Alumno se deriva de su fecha de nacimiento.
- Una Preinscripción solo puede corresponder a un Alumno si su estado es Aprobada.
- Mientras se encuentra pendiente, una Preinscripción no representa todavía un Alumno o Responsable definitivo.
- Una Preinscripción no genera cuotas ni otras obligaciones económicas.

**Cuotas**
- El importe de cuota particular es opcional. Cuando no existe, se utiliza el importe general de la Configuración que rige el período.
- Cada Período de Cuota posee una única fecha de vencimiento aplicable a todas sus cuotas.
- Un Alumno no puede tener más de una Cuota para un mismo Período de Cuota.
- Los cambios posteriores de configuración no modifican los Períodos de Cuota ya generados ni las Cuotas comprendidas en ellos.
- Una Cuota ya pagada no puede modificar su importe (RN-25).
- El recargo por mora se aplica una sola vez, utilizando el porcentaje de la Configuración que rige el período de la cuota.
- La Cuota debe abonarse en una única operación válida por el total adeudado.

**Matrícula y cobros extraordinarios**
- Toda Obligación de Pago es una Cuota, un Cobro Extraordinario o una Matrícula, y solo una de ellas.
- Solo un Movimiento de tipo Alta puede originar una Matrícula; una Reactivación no genera una nueva.
- La Matrícula debe abonarse en una única operación válida por el total adeudado y no admite pagos parciales.
- Todo Cobro Extraordinario corresponde a un Evento o es un Cobro de Indumentaria, nunca ambos ni ninguno.
- Los Cobros Extraordinarios admiten pagos parciales y no están sujetos al interés por mora.

**Pagos y recibos**
- Toda Obligación de Pago pertenece exactamente a un Alumno.
- Todas las Obligaciones alcanzadas por un mismo Pago deben pertenecer al mismo Alumno.
- El motivo y la fecha de anulación son obligatorios cuando el Pago está anulado.
- La anulación de un Pago no elimina su registro histórico ni el Recibo emitido.
- El medio de pago es opcional y, en el alcance actual, admite Efectivo o Transferencia.

---

## 6. Datos derivados

Los siguientes datos se obtienen a partir de información ya modelada y no se consideran atributos almacenados:

- Categoría del Alumno.
- Estado activo/inactivo del Alumno.
- Fecha de la Matrícula (se obtiene del Movimiento de Alta).
- Importe total de un Pago (suma de los importes aplicados).
- Saldo de una obligación.
- Saldo pendiente informado en el Recibo de un pago parcial.
- Estado de una cuota (pendiente, vencida, pagada).
- Deuda total.
- Morosidad.
- Condición de anulado de un Recibo (se obtiene del Pago).

---

## 7. Elementos que no forman parte del modelo conceptual actual

Luego de la revisión se excluyen deliberadamente:

- **Administrador** como entidad: la escuela cuenta con una única persona a cargo de la gestión (el coordinador). Es un **actor** del sistema, que se autentica y opera, pero no una entidad del dominio: no posee atributos ni relaciones que aporten información, ya que toda operación administrativa es realizada por la misma persona. Su credencial de acceso es un dato de configuración del sistema. Si en el futuro hubiera varias personas que registren pagos, se incorporaría como entidad relacionada con Pago.
- **Usuario** como entidad: sin Administrador, solo el Responsable accede al sistema. Su identificación es el DNI, que ya pertenece a Persona, y su contraseña se modela como atributo opcional de Responsable.
- **Pedido de Indumentaria** como entidad separada: se relacionaba 1:1 de forma obligatoria con su cobro. Se integra como subtipo **Cobro de Indumentaria**.
- **Matrícula** como subtipo: no posee datos propios; se representa como la Obligación de Pago originada por el Movimiento de Alta.
- **Condición Particular de Cuota** como entidad: el importe particular es un atributo opcional de Alumno.
- **Aplicación de Pago** como entidad: se representa mediante la relación N:M Pago — Obligación de Pago con el atributo Importe aplicado.
- Relación directa **Alumno — Pago**: el Alumno se determina a través de las Obligaciones alcanzadas por el Pago.
- **Importe total** como atributo de Pago: es un dato derivado.
- Período y Fecha de vencimiento como atributos propios de Cuota: ambos se obtienen mediante la relación con Período de Cuota.
- Concepto y Saldo como atributos propios de Cobro Extraordinario: el concepto se obtiene del Evento o del tipo de prenda, y el saldo es derivado.
- Categoría como entidad independiente.
- Colegio, Barrio y Localidad como entidades independientes.
- Entidades específicas para funcionamiento offline o auditoría técnica.

---

## 8. DER conceptual

El siguiente diagrama representa gráficamente el modelo conceptual consolidado en este documento.

![DER conceptual Rojas FC](./img/der-conceptual.svg)

El archivo fuente del diagrama se conserva en [`der-conceptual.puml`](./der-conceptual.puml). La representación visual en SVG se genera a partir de este modelo PlantUML.

---

## 9. Cambios respecto de la versión anterior

A partir de la devolución docente ("entidades sin atributos" y "relaciones o entidades que lógicamente no tienen sentido") se aplicaron los siguientes cambios:

| # | Cambio | Motivo |
|---|---|---|
| 1 | Se elimina **Administrador** | Entidad sin atributos ni relaciones, con una única ocurrencia. Es un actor, no una entidad. |
| 2 | Se elimina **Usuario**; la contraseña pasa a **Responsable** | Relación 1:0..1 con un único atributo y sin identificador propio. |
| 3 | Se elimina **Pedido de Indumentaria**; se incorpora el subtipo **Cobro de Indumentaria**, y Evento se relaciona directamente con Cobro Extraordinario | El pedido y su cobro eran una misma cosa unida 1:1 obligatoria. No se crea un subtipo "Cobro de Evento" porque quedaría sin atributos. |
| 4 | Se elimina el subtipo **Matrícula**; se modela mediante la relación *origina matrícula* entre Movimiento de Alumno (Alta) y Obligación de Pago | Subtipo sin atributos: su importe es el de toda obligación y su fecha es la del alta. La relación garantiza que solo el primer ingreso genere matrícula y que haya una sola por alumno. |
| 5 | **Cuota** incorpora Recargo por mora | Subtipo sin atributos. Conserva el interés aplicado (RN-21, RN-23). |
| 6 | **Preinscripción** incorpora Número, Estado y Fecha de resolución, y se relaciona con **Alumno** (0..N — 0..1), tanto para altas como para reactivaciones | Entidad aislada, sin relaciones. |
| 7 | Todas las entidades indican su identificador; **Movimiento de Alumno** se identifica como entidad débil | Faltaban identificadores. |
| 8 | Se quita **Importe total** de Pago | Dato derivado de los importes aplicados. |
| 9 | **Pago — Recibo** pasa de 1:0..1 a 1:1 | El caso offline se excluyó del modelo conceptual y no puede justificar la cardinalidad. |
| 10 | Pago incorpora **Fecha de anulación** | Trazabilidad de la anulación (RNF-12). |
| 11 | Se agregan restricciones: una Cuota por Alumno y Período, y protección del importe de cuotas pagadas (RN-25) | El modelo no las expresaba. |

---

## 10. Decisiones que se resolverán en etapas posteriores

Quedan fuera de este documento y se tratarán al trabajar claves, restricciones, modelo relacional y modelo físico:

- claves primarias, candidatas y foráneas;
- necesidad de identificadores técnicos;
- resolución física de relaciones N:M;
- restricciones de integridad para especializaciones y exclusiones;
- mecanismos para garantizar unicidad;
- restricciones temporales de vigencia;
- tipos de datos;
- índices;
- auditoría técnica;
- mecanismos de sincronización offline;
- credencial de acceso del Administrador/Coordinador.

---

## 11. Próximos pasos

Una vez validado este modelo conceptual y su DER por el equipo y el tutor:

1. incorporar las correcciones que pudieran surgir de la revisión;
2. definir claves y restricciones de integridad;
3. construir el modelo relacional;
4. recién después ajustar el modelo físico y el esquema MySQL.
