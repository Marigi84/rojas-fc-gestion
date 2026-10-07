# Reglas de Negocio

## Sistema de Gestión Rojas FC

Este documento reúne las reglas de negocio consolidadas del Sistema de Gestión Rojas FC.

Las reglas fueron definidas a partir de la Primera Entrega aprobada y del análisis posterior de las necesidades reales de Rojas FC.

Cada regla expresa una condición propia del dominio de la escuela y se mantiene separada de los requisitos funcionales, requisitos no funcionales y decisiones técnicas de implementación.

En este documento, **Administrador** designa al actor "Administrador / Coordinador" definido en la Primera Entrega.

---

## 1. Personas y roles

### RN-01 – Unicidad de la persona
Una persona se registra una sola vez, aunque cumpla más de un rol en la escuela (por ejemplo, un ex alumno que vuelve como responsable de su hijo), evitando registros duplicados.

### RN-02 – Rol de las personas registradas
Toda persona registrada deberá cumplir al menos un rol en la escuela: alumno, responsable o administrador.

### RN-03 – Alumno y responsable en simultáneo
Una persona no podrá ser alumno activo y responsable al mismo tiempo.

---

## 2. Alumnos

### RN-04 – Identificación del alumno
Todo alumno deberá contar con un número de DNI.

### RN-05 – Responsable obligatorio
Todo alumno deberá tener asociado al menos un responsable.

### RN-06 – Reincorporación de alumno
El reingreso de un alumno inactivo registra un nuevo período de permanencia en la escuela, conservando los períodos anteriores, sus antecedentes y su trayectoria institucional.

### RN-07 – Una permanencia vigente
Un alumno no podrá tener dos permanencias vigentes al mismo tiempo; cada reincorporación requiere una baja previa.

### RN-08 – Fechas de la permanencia
La fecha de baja de un alumno no podrá ser anterior a su fecha de alta.

### RN-09 – Conservación del historial
La baja de un alumno no deberá eliminar la información ni el historial generado durante su permanencia en la escuela.

---

## 3. Responsables

### RN-10 – Igual jerarquía entre responsables
Los responsables asociados a un alumno tendrán la misma jerarquía.

### RN-11 – Responsable de varios alumnos
Un mismo adulto puede figurar como responsable de más de un alumno.

### RN-12 – Vínculo del responsable
Al asociar un responsable a un alumno se registrará su vínculo con este, que podrá ser madre, padre, tutor u otro.

---

## 4. Categorías

### RN-13 – Determinación de categoría
La categoría asignada al alumno está determinada por su año de nacimiento.

---

## 5. Cuotas

### RN-14 – Generación para alumnos activos
Las cuotas mensuales deberán generarse para los alumnos que se encuentren activos al momento de la generación correspondiente.

### RN-15 – Momento de generación
Las cuotas se generan el día 1 de cada mes.

### RN-16 – Una cuota por alumno y período
Un alumno no podrá tener más de una cuota correspondiente al mismo período.

### RN-17 – Valor general vigente
Las cuotas mensuales deberán generarse utilizando el valor general vigente para el período correspondiente.

### RN-18 – Vencimiento de la cuota
Cada período tendrá una única fecha de vencimiento definida por la administración.

### RN-19 – Aplicación del interés por mora
Toda cuota que permanezca impaga después de su fecha de vencimiento deberá incorporar el interés por mora definido en la configuración que rige el período de dicha cuota.

### RN-20 – Aplicación única del interés
El interés por mora se aplicará una sola vez sobre la cuota vencida y no se acumulará de forma periódica.

### RN-21 – Porcentaje de interés configurable
El porcentaje de interés por mora será definido por la administración y podrá ser modificado.

### RN-22 – Condiciones del período
El importe, la fecha de vencimiento y el porcentaje de interés de un período no podrán modificarse una vez generadas sus cuotas; los cambios regirán a partir del período siguiente.

---

## 6. Morosidad

### RN-23 – Condición de morosidad
Un alumno será considerado moroso cuando posea al menos una cuota vencida con saldo pendiente.

### RN-24 – Morosidad y estado del alumno
La condición de morosidad no provocará automáticamente la baja del alumno.

---

## 7. Pagos

### RN-25 – Un pago, una obligación
Cada pago corresponde exactamente a una obligación: una cuota, la participación de un alumno en un evento o un encargo de indumentaria.

### RN-26 – Identificación del pago
Cada pago deberá contar con un número identificatorio propio, asignado por el sistema.

### RN-27 – Medios de pago admitidos
Los pagos podrán registrarse en efectivo o mediante transferencia.

### RN-28 – Pago total de la cuota mensual
Cada cuota mensual deberá abonarse en su totalidad en una sola operación.

### RN-29 – Un pago vigente por cuota
Una cuota podrá tener pagos anulados, pero solo un pago vigente.

### RN-30 – Anulación de pagos
Un pago registrado incorrectamente deberá anularse; los pagos no podrán eliminarse.

### RN-31 – Motivo de anulación
Toda anulación de pago deberá registrar un motivo.

### RN-32 – Efecto de la anulación
Un pago anulado no cancela la obligación a la que estaba asociado ni se computa en la recaudación.

---

## 8. Recibos

### RN-33 – Emisión de recibo
Todo pago confirmado deberá generar un recibo.

### RN-34 – Identificación del recibo
Cada recibo deberá contar con un número identificatorio propio.

### RN-35 – Comprobante de pago parcial
La emisión de un recibo por pago parcial respalda únicamente el importe efectivamente percibido y no extingue la obligación económica pendiente.

### RN-36 – Conservación de recibos
Los recibos emitidos no podrán eliminarse.

### RN-37 – Recibo de un pago anulado
Cuando se anule un pago, su recibo quedará identificado como anulado.

---

## 9. Eventos e indumentaria

### RN-38 – Importe único del evento
Cada evento tendrá un único importe, aplicable por igual a todos los alumnos participantes.

### RN-39 – Inmutabilidad del importe del evento
El importe de un evento no podrá modificarse una vez asignado el primer participante.

### RN-40 – Asociación de participaciones y encargos
Toda participación en un evento deberá estar asociada a un alumno y a un evento, y todo encargo de indumentaria deberá estar asociado a un alumno.

### RN-41 – Pagos parciales en eventos e indumentaria
La participación en un evento y los encargos de indumentaria podrán recibir uno o más pagos parciales.

### RN-42 – Conservación del saldo
Cuando una participación en un evento o un encargo de indumentaria no haya sido abonado en su totalidad, deberá conservarse el saldo pendiente.

### RN-43 – Interés en eventos e indumentaria
La participación en eventos y los encargos de indumentaria no estarán sujetos al interés por mora definido para las cuotas mensuales.
