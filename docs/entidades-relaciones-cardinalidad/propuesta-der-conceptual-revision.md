# Modelo conceptual — revisión

**Trabajo Final Integrador — Sistema de Gestión Rojas FC**

Este documento presenta el **modelo conceptual** revisado del sistema de gestión de Rojas FC. Se construye a partir de los requisitos funcionales, las reglas de negocio y las devoluciones docentes recibidas sobre el modelo anterior.

El objetivo de esta etapa es representar el dominio mediante **entidades, atributos, especializaciones, relaciones, cardinalidades y restricciones conceptuales**, sin incorporar todavía decisiones propias del modelo lógico o físico.

Por lo tanto, en esta instancia no se definen IDs técnicos, PK, FK, tipos SQL, índices, triggers ni mecanismos de implementación.

---

## 1. Entidades identificadas

### Persona

Representa a una persona registrada definitivamente en el sistema.

**Atributos:**
- DNI
- Nombre
- Apellido

El DNI identifica conceptualmente a la persona dentro del dominio y no deben existir dos Personas distintas con el mismo DNI.

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

El vínculo madre/padre/tutor no pertenece al Responsable de manera aislada, sino a la relación entre Alumno y Responsable.

### Administrador

Especialización de Persona. Representa el perfil de gestión que en los requisitos se denomina Administrador/Coordinador.

Actualmente no posee atributos propios adicionales a los heredados de Persona.

### Usuario

Representa una cuenta de acceso asociada a una Persona habilitada para autenticarse.

**Atributos:**
- Contraseña

El DNI utilizado para autenticarse pertenece a Persona y no se duplica en Usuario.

Solo una Persona de subtipo Responsable o Administrador puede poseer una cuenta de Usuario. Un Alumno no posee cuenta de acceso.

### Preinscripción

Representa un formulario recibido y pendiente de revisión antes de crear o reutilizar registros definitivos.

**Atributos:**
- Fecha
- Observaciones
- Datos declarados del alumno *(atributo compuesto)*:
  - Nombre
  - Apellido
  - DNI
  - Fecha de nacimiento
  - Domicilio
  - Barrio
  - Localidad
  - Colegio
- Datos declarados de responsable *(atributo compuesto, entre 1 y 2 ocurrencias)*:
  - Nombre
  - Apellido
  - DNI
  - Teléfono
  - Vínculo

La Preinscripción conserva información declarada y todavía no validada, por lo que mientras se encuentra pendiente no se relaciona con Persona, Alumno o Responsable definitivos.

Una vez procesada, sus datos permiten crear o reutilizar los registros definitivos correspondientes. Si el DNI del alumno ya existe, no se crea un nuevo Alumno; si se encuentra inactivo, se reutiliza el registro existente para una eventual reactivación.

No se conserva historial de preinscripciones procesadas o descartadas según el alcance actual.

### Movimiento de Alumno

Representa los cambios administrativos del Alumno a lo largo del tiempo.

**Atributos:**
- Tipo
- Fecha
- Observación

Tipos previstos:
- Alta
- Baja
- Reactivación

Todo Alumno registra al menos un movimiento. El estado activo o inactivo del Alumno se deriva del último movimiento válido.

### Configuración de Cuota

Representa las condiciones generales vigentes utilizadas para generar cuotas mensuales.

**Atributos:**
- Importe general
- Porcentaje de interés
- Vigente desde

Una nueva configuración afecta únicamente a las cuotas futuras. Cada Cuota conserva la relación con la Configuración que la rigió al momento de su generación.

### Período de Cuota

Representa cada período mensual para el cual se generan cuotas.

**Atributos:**
- Período
- Fecha de vencimiento

Cada Período de Cuota posee una única fecha de vencimiento común para las cuotas correspondientes a ese período.

### Obligación de Pago

Representa una obligación económica concreta de un Alumno.

**Atributos:**
- Importe

Toda Obligación de Pago corresponde exactamente a uno de estos tipos:
- Cuota
- Matrícula
- Cobro Extraordinario

La especialización es total y exclusiva.

### Cuota

Especialización de Obligación de Pago. Representa la obligación mensual de un Alumno.

Actualmente no posee atributos propios adicionales a los heredados de Obligación de Pago.

El período y la fecha de vencimiento se obtienen a través de la relación con Período de Cuota.

El interés por mora, el saldo, el estado de la cuota y la condición de morosidad son datos derivados.

### Matrícula

Especialización de Obligación de Pago. Representa la obligación económica correspondiente al primer ingreso del Alumno.

Actualmente no posee atributos propios adicionales a los heredados de Obligación de Pago.

La Matrícula corresponde únicamente al primer alta del Alumno. Una reactivación no genera una nueva Matrícula.

### Cobro Extraordinario

Especialización de Obligación de Pago. Representa una obligación no periódica correspondiente a un Evento o a un Pedido de Indumentaria.

Actualmente no posee atributos propios adicionales a los heredados de Obligación de Pago.

### Evento

Representa un evento de la escuela que puede originar cobros extraordinarios para distintos alumnos.

**Atributos:**
- Nombre
- Año

El año se mantiene separado del nombre porque un mismo evento puede repetirse en distintas ediciones.

### Pedido de Indumentaria

Representa un pedido individual de indumentaria.

**Atributos:**
- Tipo de prenda
- Talle

### Pago

Representa una operación económica registrada en el sistema.

**Atributos:**
- Fecha
- Importe total
- Medio de pago *(opcional)*
- Anulado
- Motivo de anulación *(obligatorio cuando el Pago está anulado)*

El medio de pago se mantiene como atributo controlado porque, en el alcance actual, únicamente contempla Efectivo y Transferencia y no posee información propia adicional que justifique una entidad independiente.

### Recibo

Representa el comprobante correspondiente a un Pago confirmado.

**Atributos:**
- Número
- Fecha de emisión

La información del Alumno, los conceptos abonados, los importes aplicados, el importe total y el medio de pago se obtiene a través del Pago y de las Obligaciones a las que se aplica.

---

## 2. Especializaciones

### Persona

Persona se especializa de forma **total y exclusiva** en:

- Alumno
- Responsable
- Administrador

Esto significa que toda Persona registrada definitivamente pertenece exactamente a uno de esos tres subtipos.

### Obligación de Pago

Obligación de Pago se especializa de forma **total y exclusiva** en:

- Cuota
- Matrícula
- Cobro Extraordinario

Toda Obligación de Pago pertenece exactamente a uno de esos tres subtipos.

---

## 3. Relaciones y cardinalidades

### Persona — Usuario: posee cuenta

- Una Persona puede poseer 0..1 Usuario.
- Cada Usuario corresponde exactamente a 1 Persona.
- Solo Responsable y Administrador pueden poseer Usuario.

### Alumno — Responsable: tiene como responsable

- Todo Alumno debe tener entre 1 y 2 Responsables.
- Todo Responsable se encuentra asociado al menos a 1 Alumno y puede estar asociado a varios.
- Es una relación N:M.
- La relación posee el atributo **Vínculo**: madre, padre o tutor.

### Alumno — Movimiento de Alumno: registra

- Un Alumno registra 1..N Movimientos.
- Cada Movimiento pertenece exactamente a 1 Alumno.

### Alumno — Obligación de Pago: posee

- Un Alumno puede poseer 0..N Obligaciones de Pago.
- Cada Obligación de Pago pertenece exactamente a 1 Alumno.

### Configuración de Cuota — Cuota: rige

- Una Configuración de Cuota puede regir 0..N Cuotas.
- Cada Cuota es regida por exactamente 1 Configuración de Cuota.

### Período de Cuota — Cuota: comprende

- Un Período de Cuota puede comprender 0..N Cuotas.
- Cada Cuota pertenece exactamente a 1 Período de Cuota.
- Todas las Cuotas de un mismo período comparten la única fecha de vencimiento definida para ese Período de Cuota.

### Evento — Cobro Extraordinario: origina

- Un Evento puede originar 0..N Cobros Extraordinarios.
- Un Cobro Extraordinario puede corresponder a 0..1 Evento.

### Pedido de Indumentaria — Cobro Extraordinario: origina

- Cada Pedido de Indumentaria origina exactamente 1 Cobro Extraordinario.
- Un Cobro Extraordinario puede corresponder a 0..1 Pedido de Indumentaria.

Todo Cobro Extraordinario debe corresponder exactamente a un Evento **o** a un Pedido de Indumentaria, nunca a ambos.

### Pago — Obligación de Pago: se aplica a

- Todo Pago se aplica a 1..N Obligaciones de Pago.
- Una Obligación de Pago puede tener 0..N Pagos relacionados a lo largo de su historial.
- Es una relación N:M.
- La relación posee el atributo **Importe aplicado**.

Todas las Obligaciones alcanzadas por un mismo Pago deben pertenecer al mismo Alumno.

### Pago — Recibo: genera

- Un Pago puede generar 0..1 Recibo.
- Cada Recibo corresponde exactamente a 1 Pago.
- Un Pago confirmado genera exactamente 1 Recibo.

El valor 0..1 permite contemplar el pago provisional registrado offline, que todavía no genera recibo hasta ser sincronizado y validado.

---

## 4. Atributos de relaciones

### Vínculo

Pertenece a la relación **Alumno — tiene como responsable — Responsable**.

Valores previstos:
- Madre
- Padre
- Tutor

No se almacena como atributo propio de Responsable porque una misma Persona puede cumplir distintos vínculos respecto de distintos alumnos.

### Importe aplicado

Pertenece a la relación **Pago — se aplica a — Obligación de Pago**.

Permite expresar cuánto del Importe total de un Pago corresponde a cada Obligación alcanzada.

En el modelo conceptual se conserva como atributo de la relación N:M. Su transformación a una estructura intermedia se resolverá al construir el modelo relacional.

---

## 5. Restricciones conceptuales

- No deben existir dos Personas distintas con el mismo DNI.
- La especialización de Persona es total y exclusiva.
- Alumno, Responsable y Administrador no pueden superponerse para una misma Persona.
- Solo Responsable y Administrador pueden poseer Usuario.
- Un Alumno debe tener entre 1 y 2 Responsables.
- El estado activo/inactivo del Alumno se deriva de su último Movimiento válido.
- La categoría del Alumno se deriva de su fecha de nacimiento.
- El importe de cuota particular es opcional. Cuando no existe, se utiliza el importe general vigente de Configuración de Cuota para las futuras cuotas.
- Cada Período de Cuota posee una única fecha de vencimiento aplicable a todas sus cuotas.
- Los cambios de configuración, importe particular o fecha de vencimiento de períodos futuros no modifican cuotas ni períodos ya generados.
- Toda Obligación de Pago pertenece exactamente a un Alumno.
- La especialización de Obligación de Pago es total y exclusiva.
- La Matrícula corresponde únicamente al primer ingreso del Alumno; una reactivación no genera una nueva.
- Cada Cobro Extraordinario corresponde exactamente a un Evento o a un Pedido de Indumentaria, nunca a ambos.
- Todas las Obligaciones alcanzadas por un mismo Pago deben pertenecer al mismo Alumno.
- La suma de los Importes aplicados debe coincidir con el Importe total del Pago.
- Cuota y Matrícula se cancelan en una única operación válida por el total adeudado.
- Los Cobros Extraordinarios admiten pagos parciales.
- El motivo de anulación es obligatorio cuando el Pago está anulado.
- La anulación de un Pago no elimina su registro histórico ni el Recibo previamente emitido.
- El medio de pago es opcional y, en el alcance actual, admite Efectivo o Transferencia.
- La Preinscripción es temporal e independiente de las entidades definitivas mientras se encuentra pendiente.

---

## 6. Datos derivados

Los siguientes datos se obtienen a partir de información ya modelada y no se consideran atributos almacenados independientes en esta etapa conceptual:

- Categoría del Alumno.
- Estado activo/inactivo del Alumno.
- Interés por mora.
- Saldo de una obligación.
- Deuda total.
- Morosidad.
- Condición de Recibo asociado a un Pago posteriormente anulado.

---

## 7. Elementos que no forman parte del modelo conceptual actual

Luego de la revisión se excluyen deliberadamente:

- **Condición Particular de Cuota** como entidad: el importe particular pasa a ser un atributo opcional de Alumno.
- **Aplicación de Pago** como entidad conceptual: se representa mediante la relación N:M Pago — Obligación de Pago con el atributo Importe aplicado.
- Relación directa **Alumno — Pago**: el Alumno correspondiente se determina a través de las Obligaciones alcanzadas por el Pago.
- Rol y estado de cuenta en Usuario.
- Mes y Año en Matrícula.
- Período y Fecha de vencimiento como atributos propios de Cuota: ambos se obtienen mediante la relación con Período de Cuota.
- Interés aplicado como atributo almacenado de Cuota.
- Concepto y Saldo como atributos propios de Cobro Extraordinario.
- Categoría como entidad independiente.
- Colegio, Barrio y Localidad como entidades independientes.
- Entidades específicas para offline o auditoría.

---

## 8. Decisiones que se resolverán en etapas posteriores

Quedan fuera de este documento y se tratarán al trabajar claves, restricciones, modelo relacional y modelo físico:

- claves primarias, candidatas y foráneas;
- necesidad de identificadores técnicos;
- resolución física de relaciones N:M;
- análisis formal de entidades débiles;
- restricciones de integridad para especializaciones y exclusiones;
- mecanismos para garantizar unicidad;
- restricciones temporales de vigencia;
- tipos de datos;
- índices;
- auditoría técnica;
- mecanismos de sincronización offline.

---

## 9. Próximos pasos

Una vez validado este modelo conceptual:

1. construir el DER conceptual a partir de estas entidades, atributos, especializaciones, relaciones y cardinalidades;
2. revisar el DER contra los requisitos funcionales y reglas de negocio;
3. definir claves y restricciones de integridad;
4. construir el modelo relacional;
5. recién después ajustar el modelo físico y el esquema MySQL.
