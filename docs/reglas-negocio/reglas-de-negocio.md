# Reglas de Negocio

## Sistema de Gestión Rojas FC

Este documento reúne las reglas de negocio consolidadas del Sistema de Gestión Rojas FC.

Las reglas fueron definidas a partir de la Primera Entrega aprobada y del análisis posterior de las necesidades reales de Rojas FC.

Cada regla expresa una condición propia del dominio de la escuela y se mantiene separada de los requisitos funcionales, requisitos no funcionales y decisiones técnicas de implementación.

La versión actual incorpora las decisiones consensuadas por el equipo durante la revisión de los Issues correspondientes.

---

## 1. Alumnos

### RN-01 – Identificación y vinculación inicial del alumno:

«Todo alumno deberá contar con un número de DNI y tener asociado al menos un responsable para formalizar su inscripción en la institución.

### RN-02 – Unicidad del alumno
Un mismo alumno no podrá registrarse más de una vez.

### RN-03 – Reincorporación de alumno
El reingreso de un alumno inactivo reactiva su legajo histórico, conservando sus antecedentes y trayectoria institucional.

### RN-04 – Conservación del historial
La baja de un alumno no deberá eliminar la información ni el historial generado durante su permanencia en la escuela.

---

## 2. Responsables

### RN-05 – Cantidad máxima de responsables
Cada alumno podrá tener asociados como máximo dos responsables.

### RN-06 – Igual jerarquía entre responsables
Los responsables asociados a un alumno tendrán la misma jerarquía.

### RN-07 – Responsable de varios alumnos
Un mismo adulto puede figurar como responsable de más de un alumno.

### RN-08 – Unicidad del responsable
Un adulto responsable ya registrado reutiliza su legajo institucional al vincularse con un nuevo alumno, evitando registros duplicados.

### RN-09 – Vínculo del responsable
Todo responsable asociado a un alumno deberá identificarse mediante su vínculo con este, contemplando madre, padre o tutor.

---

## 3. Categorías

### RN-10 – Determinación de categoría
La categoría asignada al alumno está determinada por su año de nacimiento.

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
Las cuotas se generan el día 1 de cada mes.

### RN-17 – Valor general vigente
Las cuotas mensuales deberán generarse utilizando el valor general vigente para el período correspondiente.

### RN-18 – Importe particular por alumno
Un alumno podrá tener un importe de cuota diferente del valor general cuando exista una condición particular definida por la administración.

### RN-19 – Vencimiento de la cuota
Cada período tendrá una única fecha de vencimiento definida por la administración.

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

### RN-30 – Pago total de la cuota mensual
Cada cuota mensual deberá abonarse en su totalidad en una sola operación.

---

## 9. Pagos

### RN-31 – Medios de pago admitidos
Los pagos podrán registrarse en efectivo o mediante transferencia.

### RN-32 – Medio de pago opcional
El registro del medio de pago podrá omitirse cuando la administración no disponga de esa información.

### RN-33 – Anulación de pagos
Un pago registrado incorrectamente deberá anularse en lugar de eliminarse.

### RN-34 – Motivo de anulación
Toda anulación de pago deberá registrar un motivo.

### RN-35 – Conservación del historial de pagos
Los pagos anulados deberán conservarse como parte del historial administrativo.

---

## 10. Recibos

### RN-36 – Emisión de recibo
Todo pago confirmado deberá generar un recibo.

### RN-37 – Identificación del recibo
Cada recibo deberá contar con un número identificatorio propio.

### RN-38 – Comprobante de pago parcial
La emisión de un recibo por pago parcial respalda únicamente el importe efectivamente percibido y no extingue la obligación económica pendiente.

### RN-39 – Inalterabilidad de comprobantes
Los recibos emitidos no podrán destruirse ni eliminarse del registro histórico; ante la anulación de una cobranza, el comprobante asociado pierde validez fiscal/administrativa pero manteniendo su trazabilidad.

---

## 11. Cobros extraordinarios

### RN-40 – Asociación del cobro extraordinario
Todo cobro extraordinario deberá estar asociado a un alumno y a un concepto determinado.

### RN-41 – Pagos parciales en cobros extraordinarios
Los cobros correspondientes a eventos o indumentaria podrán recibir uno o más pagos parciales.

### RN-42 – Conservación del saldo
Cuando un cobro extraordinario no haya sido abonado en su totalidad, deberá conservarse el saldo pendiente.

### RN-43 – Especificación de pedidos de indumentaria
La gestión y cobro de indumentaria institucional requiere la definición previa del tipo de prenda y talle correspondiente para validar el pedido.

### RN-44 – Interés en cobros extraordinarios
Los cobros extraordinarios correspondientes a eventos o indumentaria no estarán sujetos al interés por mora definido para las cuotas mensuales.

---

## 12. Preinscripción y obligaciones económicas

### RN-45 – Inicio de obligaciones económicas
Una preinscripción no generará cuotas ni otras obligaciones económicas hasta que el alumno haya sido dado de alta de manera definitiva.

---

## Decisiones consensuadas durante la revisión

1. Cada período tendrá una única fecha de vencimiento.
2. Las cuotas mensuales deberán abonarse en su totalidad; no se habilitarán pagos parciales de cuotas.
3. Los pagos parciales quedarán limitados a eventos e indumentaria.
4. Los recibos correspondientes a pagos parciales de cobros extraordinarios deberán informar el saldo pendiente.

---

## Estado del documento

**Estado:** Actualizado tras la revisión del equipo. Pendiente de verificación final por los integrantes.

Una vez verificada esta versión, podrá utilizarse junto con los requisitos funcionales y no funcionales como base para la definición de módulos y el posterior modelado de entidades y relaciones.
