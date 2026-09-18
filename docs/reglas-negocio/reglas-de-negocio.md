# Reglas de Negocio

## Sistema de Gestión Rojas FC

Este documento reúne las reglas de negocio consolidadas del Sistema de Gestión Rojas FC.

Las reglas fueron definidas a partir de la Primera Entrega aprobada y del análisis posterior de las necesidades reales de Rojas FC.

Cada regla expresa una condición propia del dominio de la escuela y se mantiene separada de los requisitos funcionales, requisitos no funcionales y decisiones técnicas de implementación.

Las reglas identificadas con 🟡 requieren revisión y consenso explícito del equipo debido a que refinan o modifican aspectos planteados en la Primera Entrega.

---

## 1. Alumnos

### RN-01 – Identificación del alumno
Todo alumno deberá contar con un número de DNI para realizar su inscripción.

### RN-02 – Unicidad del alumno
Un mismo alumno no podrá registrarse más de una vez.

### RN-03 – Reincorporación de alumno
Cuando un alumno previamente dado de baja regrese a la escuela, deberá reactivarse su registro existente en lugar de generarse uno nuevo.

### RN-04 – Conservación del historial
La baja de un alumno no deberá eliminar la información ni el historial generado durante su permanencia en la escuela.

---

## 2. Responsables

### RN-05 – Cantidad máxima de responsables
Cada alumno podrá tener asociados como máximo dos responsables.

### RN-06 – Igual jerarquía entre responsables
Los responsables asociados a un alumno tendrán la misma jerarquía.

### RN-07 – Responsable de varios alumnos
Un mismo responsable podrá estar asociado a más de un alumno.

### RN-08 – Unicidad del responsable
Cuando una persona ya registrada como responsable deba asociarse a otro alumno, deberá reutilizarse su registro existente en lugar de crear uno nuevo.

### RN-09 – Vínculo del responsable
Todo responsable asociado a un alumno deberá identificarse mediante su vínculo con este, contemplando madre, padre o tutor.

---

## 3. Categorías

### RN-10 – Determinación de categoría
La categoría de un alumno estará determinada por su año de nacimiento.

---

## 4. Preinscripción

### RN-11 – Responsable principal obligatorio
Toda preinscripción deberá incluir los datos de al menos un responsable.

### RN-12 – Segundo responsable opcional
La incorporación de un segundo responsable en la preinscripción será opcional.

### RN-13 – Preinscripción sin alta automática
La presentación de una preinscripción no implicará automáticamente el alta definitiva del alumno.

### RN-14 – Revisión previa al alta
Los datos recibidos mediante una preinscripción deberán ser revisados antes de convertirla en una inscripción definitiva.

---

## 5. Cuotas

### RN-15 – Generación para alumnos activos
Las cuotas mensuales deberán generarse para los alumnos que se encuentren activos al momento de la generación correspondiente.

### RN-16 – Momento de generación
Las cuotas mensuales deberán generarse el día 1 de cada mes.

### RN-17 – Valor general vigente
Las cuotas mensuales deberán generarse utilizando el valor general vigente para el período correspondiente.

### RN-18 – Importe particular por alumno
Un alumno podrá tener un importe de cuota diferente del valor general cuando exista una condición particular definida por la administración.

### RN-19 – Vencimiento de la cuota 🟡
Cada período tendrá una única fecha de vencimiento definida por la administración.

> **Decisión pendiente de consenso:** esta regla simplifica lo expresado en la Primera Entrega, donde se contemplaba la posibilidad de uno o más vencimientos.

### RN-20 – Aplicación del interés por mora
Toda cuota que permanezca impaga después de su fecha de vencimiento deberá incorporar el interés por mora vigente.

### RN-21 – Aplicación única del interés
El interés por mora se aplicará una sola vez sobre la cuota vencida y no se acumulará de forma periódica.

### RN-22 – Porcentaje de interés configurable
El porcentaje de interés por mora será definido por la administración y podrá ser modificado.

### RN-23 – Conservación histórica de condiciones
Los cambios posteriores en el valor general de la cuota, vencimiento o porcentaje de interés no deberán modificar las condiciones correspondientes a cuotas ya generadas.

### RN-24 – Corrección del importe del período actual
Cuando una cuota pendiente del período en curso haya sido generada con un importe incorrecto, su valor podrá ser corregido por la administración.

### RN-25 – Protección de cuotas ya pagadas
Una cuota ya abonada no deberá ser modificada como consecuencia de una corrección posterior del importe del período.

---

## 6. Morosidad

### RN-26 – Condición de morosidad
Un alumno será considerado moroso cuando posea al menos una cuota vencida con saldo pendiente.

### RN-27 – Cuota pendiente no vencida
Una cuota pendiente cuya fecha de vencimiento aún no haya transcurrido no convertirá al alumno en moroso.

### RN-28 – Morosidad y estado del alumno
La condición de morosidad no provocará automáticamente la baja del alumno.

---

## 7. Matrícula

### RN-29 – Cobro de matrícula
La matrícula constituye un cobro vinculado al ingreso del alumno a la escuela.

---

## 8. Pagos de cuotas

### RN-30 – Pago parcial de cuotas 🟡
Una cuota mensual podrá recibir uno o más pagos parciales hasta completar el importe total adeudado.

> **Decisión pendiente de consenso:** esta regla modifica lo expresado inicialmente en la Primera Entrega, donde las cuotas debían abonarse en su totalidad.

### RN-31 – Imputación del pago 🟡
Cuando un alumno posea más de una cuota pendiente, la administración podrá determinar a qué cuota o cuotas se aplicará el importe recibido.

### RN-32 – Saldo pendiente 🟡
Cuando el importe recibido no cubra completamente una cuota, la diferencia permanecerá como saldo pendiente.

### RN-33 – Cancelación de la cuota 🟡
Una cuota se considerará totalmente abonada cuando la suma de los pagos aplicados alcance el importe total adeudado correspondiente a esa cuota.

---

## 9. Pagos

### RN-34 – Medios de pago admitidos
Los pagos podrán registrarse en efectivo o mediante transferencia.

### RN-35 – Medio de pago opcional
El registro del medio de pago podrá omitirse cuando la administración no disponga de esa información.

### RN-36 – Anulación de pagos
Un pago registrado incorrectamente deberá anularse en lugar de eliminarse.

### RN-37 – Motivo de anulación
Toda anulación de pago deberá registrar un motivo.

### RN-38 – Conservación del historial de pagos
Los pagos anulados deberán conservarse como parte del historial administrativo.

---

## 10. Recibos

### RN-39 – Emisión de recibo
Todo pago confirmado deberá generar un recibo.

### RN-40 – Identificación del recibo
Cada recibo deberá contar con un número identificatorio propio.

### RN-41 – Saldo pendiente en pagos parciales 🟡
Cuando un pago no cubra el total adeudado de una obligación, el recibo deberá informar el saldo pendiente luego de aplicar dicho pago.

### RN-42 – Conservación de recibos anulados
Cuando se anule un pago que posea un recibo emitido, dicho recibo deberá conservarse identificado como anulado.

---

## 11. Cobros extraordinarios

### RN-43 – Asociación del cobro extraordinario
Todo cobro extraordinario deberá estar asociado a un alumno y a un concepto determinado.

### RN-44 – Pagos parciales en cobros extraordinarios
Los cobros correspondientes a eventos o indumentaria podrán recibir uno o más pagos parciales.

### RN-45 – Conservación del saldo
Cuando un cobro extraordinario no haya sido abonado en su totalidad, deberá conservarse el saldo pendiente.

### RN-46 – Datos de indumentaria
Cuando el cobro extraordinario corresponda a indumentaria, deberá identificarse al menos el tipo de prenda y el talle solicitado.

### RN-47 – Interés en cobros extraordinarios
Los cobros extraordinarios correspondientes a eventos o indumentaria no estarán sujetos al interés por mora definido para las cuotas mensuales.

---

## 12. Preinscripción y obligaciones económicas

### RN-48 – Inicio de obligaciones económicas
Una preinscripción no generará cuotas ni otras obligaciones económicas hasta que el alumno haya sido dado de alta de manera definitiva.

---

## Decisiones pendientes de consenso del equipo

Antes de considerar este documento definitivo, deberán revisarse especialmente las siguientes decisiones:

1. **RN-19 – Vencimiento de cuotas:** utilizar una única fecha de vencimiento por período.
2. **RN-30 – Pagos parciales de cuotas:** permitir pagos parciales de cuotas mensuales.
3. **RN-31 – Imputación de pagos:** permitir que la administración determine cómo distribuir un pago entre cuotas pendientes.
4. **RN-32 y RN-33 – Saldo y cancelación:** conservar el saldo pendiente hasta completar el importe total de la cuota.
5. **RN-41 – Recibos parciales:** informar en el recibo el saldo pendiente luego de aplicar un pago parcial.

---

## Estado del documento

**Estado:** En revisión por el equipo.

Las reglas identificadas con 🟡 deberán contar con consenso de Marina, Silvia y Alex antes de considerar consolidada la versión definitiva.

Una vez aprobadas las reglas de negocio, este documento podrá utilizarse junto con los requisitos funcionales y no funcionales como base para la definición de módulos y el posterior modelado de entidades y relaciones.
