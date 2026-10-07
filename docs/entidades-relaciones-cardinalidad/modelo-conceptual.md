# Modelo Conceptual

## Sistema de Gestión Rojas FC

Este documento presenta el modelo conceptual del Sistema de Gestión Rojas FC, representado mediante un Diagrama Entidad-Relación (DER) en notación de Chen.

El modelo se construyó a partir de la Primera Entrega aprobada, los requisitos funcionales (RF), los requisitos no funcionales (RNF) y las reglas de negocio (RN) del proyecto. Representa entidades, atributos, relaciones, cardinalidades y especializaciones. No incluye decisiones del modelo lógico o físico, como claves foráneas, tablas intermedias o tipos de datos.

En este documento, **Administrador** designa al actor "Administrador / Coordinador" definido en la Primera Entrega.

---

## 1. Diagramas

| Diagrama | Código | Imagen |
|---|---|---|
| DER completo | [der-conceptual.puml](der-conceptual.puml) | [der-conceptual.svg](img/der-conceptual.svg) |
| Vista 1 – Personas y permanencia | [1-personas.puml](vistas/1-personas.puml) | [1-personas.svg](vistas/1-personas.svg) |
| Vista 2 – Cuotas y pagos | [2-cuotas-pagos.puml](vistas/2-cuotas-pagos.puml) | [2-cuotas-pagos.svg](vistas/2-cuotas-pagos.svg) |
| Vista 3 – Eventos e indumentaria | [3-eventos-indumentaria.puml](vistas/3-eventos-indumentaria.puml) | [3-eventos-indumentaria.svg](vistas/3-eventos-indumentaria.svg) |

Las vistas muestran partes del DER completo para facilitar su lectura; no agregan elementos.

![DER conceptual](img/der-conceptual.svg)

### Notación

| Símbolo | Significado |
|---|---|
| Rectángulo | Entidad |
| Rectángulo doble | Entidad débil |
| Rombo | Relación |
| Rombo doble | Relación identificadora |
| Óvalo | Atributo |
| Óvalo con nombre subrayado | Clave |
| Óvalo con subrayado punteado | Discriminador de una entidad débil |
| Óvalo punteado | Atributo derivado |
| Línea doble | Participación total |
| Línea simple | Participación parcial |
| Triángulo con "o" | Especialización superpuesta |

---

## 2. Entidades y atributos

### Persona

Representa a toda persona registrada en el sistema (RN-01).

| Atributo | Observación |
|---|---|
| **DNI** | Clave (RN-01, RN-04) |
| Nombre | |
| Apellido | |

**Especialización:** Persona se especializa en Alumno, Responsable y Administrador. Es **superpuesta**, porque una misma persona puede cumplir más de un rol (RN-01), y **total**, porque toda persona registrada cumple al menos uno (RN-02).

### Alumno

Especialización de Persona (RF-01).

| Atributo | Observación |
|---|---|
| Fecha de nacimiento | RF-01 |
| Dirección | RF-01 |
| Categoría | Derivado del año de nacimiento (RF-16, RN-13) |
| Estado | Derivado: activo si tiene una permanencia sin fecha de baja (RF-04, RF-05) |
| Moroso | Derivado: tiene al menos una cuota vencida con saldo pendiente (RN-23) |

### Responsable

Especialización de Persona (RF-06).

| Atributo | Observación |
|---|---|
| Teléfono | RF-06 |

### Administrador

Especialización de Persona (RF-51).

| Atributo | Observación |
|---|---|
| Email | Permite recuperar la contraseña (RF-50) |

### Permanencia (entidad débil de Alumno)

Representa cada período en que el alumno estuvo activo en la escuela. Permite conservar el historial ante bajas y reincorporaciones (RF-04, RF-05, RN-06, RN-09).

| Atributo | Observación |
|---|---|
| *Fecha de alta* | Discriminador |
| Fecha de baja | Vacía mientras la permanencia está vigente |

Se identifica por el Alumno y la Fecha de alta.

### Período

Representa el mes y año para el que se generan las cuotas, con las condiciones configuradas por el Administrador (RF-17, RF-19, RF-20).

| Atributo | Observación |
|---|---|
| **Mes y año** | Clave |
| Importe | Valor general de la cuota (RF-17, RN-17) |
| Fecha de vencimiento | RF-19, RN-18 |
| Porcentaje de interés | RF-20, RN-21 |

### Cuota (entidad débil de Alumno y Período)

Representa la cuota mensual de un alumno en un período (RF-18).

| Atributo | Observación |
|---|---|
| Fecha de generación | RN-15 |
| Monto a pagar | Derivado: importe del período más el interés por mora si corresponde (RF-21, RN-19, RN-20) |
| Estado | Derivado: pendiente, vencida o pagada (RF-22) |

Se identifica por el Alumno y el Período, por lo que un alumno no puede tener dos cuotas del mismo período (RN-16).

### Evento

Representa un evento organizado por la escuela con un importe propio (RF-34).

| Atributo | Observación |
|---|---|
| **Nombre** | Clave compuesta junto con Año |
| **Año** | Clave compuesta junto con Nombre |
| Importe | Único para todos los participantes (RN-38) |

### Participación (entidad débil de Alumno y Evento)

Representa la inscripción de un alumno en un evento (RF-35, RN-40).

| Atributo | Observación |
|---|---|
| Fecha de inscripción | |
| Saldo | Derivado: importe del evento menos los pagos vigentes (RN-42) |

Se identifica por el Alumno y el Evento.

### Encargo (entidad débil de Alumno)

Representa un encargo de indumentaria realizado para un alumno (RF-36, RN-40).

| Atributo | Observación |
|---|---|
| *Fecha y hora* | Discriminador |
| Monto | RF-36 |
| Descripción | Opcional (RF-36) |
| Saldo | Derivado: monto menos los pagos vigentes (RN-42) |

Se identifica por el Alumno y la Fecha y hora.

### Pago

Representa cada pago registrado por el Administrador (RF-24).

| Atributo | Observación |
|---|---|
| **Número de pago** | Clave, asignada por el sistema (RN-26) |
| Fecha | RF-24 |
| Monto | RF-24 |

### Medio de pago

Representa los medios admitidos para registrar un pago (RN-27).

| Atributo | Observación |
|---|---|
| **Nombre** | Clave (efectivo, transferencia) |

### Recibo

Representa el comprobante generado por un pago (RF-29).

| Atributo | Observación |
|---|---|
| **Número de recibo** | Clave (RN-34) |
| Fecha de emisión | RF-29 |

---

## 3. Relaciones y cardinalidades

### Personas y alumnos

| Relación | Entidades | Cardinalidad | Participación | Atributos | Referencia |
|---|---|---|---|---|---|
| está a cargo de | Responsable – Alumno | M:N | Alumno total, Responsable parcial | Vínculo (madre, padre, tutor u otro) | RF-06, RF-09, RF-10, RN-05, RN-10, RN-11, RN-12 |
| registra *(identificadora)* | Alumno – Permanencia | 1:N | Permanencia total | | RN-06, RN-09 |

### Cuotas

| Relación | Entidades | Cardinalidad | Participación | Atributos | Referencia |
|---|---|---|---|---|---|
| debe *(identificadora)* | Alumno – Cuota | 1:N | Cuota total | | RF-18, RF-22 |
| corresponde *(identificadora)* | Período – Cuota | 1:N | Cuota total | | RF-18, RN-16 |

### Eventos e indumentaria

| Relación | Entidades | Cardinalidad | Participación | Atributos | Referencia |
|---|---|---|---|---|---|
| se anota *(identificadora)* | Alumno – Participación | 1:N | Participación total | | RF-35, RN-40 |
| de *(identificadora)* | Evento – Participación | 1:N | Participación total | | RF-35, RN-40 |
| encarga *(identificadora)* | Alumno – Encargo | 1:N | Encargo total | | RF-36, RN-40 |

### Pagos y recibos

| Relación | Entidades | Cardinalidad | Participación | Atributos | Referencia |
|---|---|---|---|---|---|
| salda | Pago – Cuota | N:1 | Ambas parciales | | RF-25, RN-25, RN-28, RN-29 |
| abona | Pago – Participación | N:1 | Ambas parciales | | RF-26, RN-25, RN-41 |
| abona | Pago – Encargo | N:1 | Ambas parciales | | RF-26, RN-25, RN-41 |
| se realiza con | Pago – Medio de pago | N:1 | Pago total | | RF-24, RN-27 |
| genera | Pago – Recibo | 1:1 | Recibo total | | RF-29, RN-33, RF-57 |

Pago tiene participación parcial en "genera" porque un pago registrado sin conexión es provisional y su recibo se emite luego de la sincronización (RF-54, RF-57).

### Operaciones del Administrador

Estas relaciones registran qué administrador realizó cada operación y en qué fecha (RNF-11).

| Relación | Entidades | Cardinalidad | Participación | Atributos | Referencia |
|---|---|---|---|---|---|
| da de alta | Administrador – Permanencia | 1:N | Permanencia total | | RF-01, RF-05 |
| da de baja | Administrador – Permanencia | 1:N | Permanencia parcial | | RF-04 |
| configura | Administrador – Período | 1:N | Período total | Fecha | RF-17, RF-19, RF-20 |
| registra | Administrador – Pago | 1:N | Pago total | | RF-24 |
| anula | Administrador – Pago | 1:N | Pago parcial | Fecha de anulación, Motivo | RF-28, RN-30, RN-31 |
| registra | Administrador – Evento | 1:N | Evento total | Fecha | RF-34 |
| registra | Administrador – Encargo | 1:N | Encargo total | | RF-36 |

La fecha del alta y de la baja se registra en la Permanencia, la del pago en el Pago y la del encargo en su Fecha y hora.

---

## 4. Restricciones no representables en el diagrama

Las siguientes reglas no pueden expresarse con la notación del DER y se controlan como restricciones del modelo:

| Regla | Restricción |
|---|---|
| RN-03 | Una persona no puede ser alumno con permanencia vigente y responsable al mismo tiempo. |
| RN-07 | Un alumno no puede tener más de una permanencia sin fecha de baja. |
| RN-08 | La fecha de baja de una permanencia no puede ser anterior a su fecha de alta. |
| RN-22 | El importe, la fecha de vencimiento y el porcentaje de interés de un período no pueden modificarse una vez generadas sus cuotas. |
| RN-25 | Cada pago participa exactamente en una de las relaciones "salda", "abona" (participación) o "abona" (encargo). |
| RN-28 | El monto del pago de una cuota debe ser igual al monto a pagar de la cuota. |
| RN-29 | Una cuota puede tener pagos anulados, pero a lo sumo un pago no anulado. |
| RN-32 | Un pago anulado no se considera para el estado de la obligación, el saldo ni la recaudación. |
| RN-39 | El importe de un evento no puede modificarse una vez que tiene al menos una participación. |
