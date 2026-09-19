# Requisitos Funcionales

## Sistema de Gestión Rojas FC

Este documento reúne los requisitos funcionales consolidados del Sistema de Gestión Rojas FC.

La definición se realizó tomando como base la Primera Entrega aprobada del Trabajo Final Integrador y el análisis posterior de las necesidades reales de la escuela.

La versión actual incorpora las decisiones consensuadas por el equipo durante la revisión de los Issues correspondientes.

---

## 1. Gestión de alumnos

### RF-01 – Registrar alumno
El sistema deberá permitir al Administrador/Coordinador registrar directamente un alumno ingresando nombre, apellido, DNI, fecha de nacimiento, domicilio, barrio, localidad y colegio, sin requerir una preinscripción previa.

Si el DNI ya corresponde a un alumno registrado, el sistema no deberá crear un nuevo alumno. Si el registro existente se encuentra inactivo, deberá utilizarse para su eventual reactivación.

### RF-02 – Consultar alumno
El sistema deberá permitir al Administrador/Coordinador consultar los datos registrados y el historial de un alumno.

### RF-03 – Modificar alumno
El sistema deberá permitir al Administrador/Coordinador modificar los datos registrados de un alumno.

### RF-04 – Dar de baja un alumno
El sistema deberá permitir al Administrador/Coordinador dar de baja lógicamente a un alumno sin eliminar su información ni su historial.

### RF-05 – Reactivar alumno
El sistema deberá permitir al Administrador/Coordinador reactivar un alumno dado de baja conservando su registro e historial anteriores.

---

## 2. Gestión de responsables

### RF-06 – Registrar responsable
El sistema deberá permitir al Administrador/Coordinador registrar un responsable indicando nombre, apellido, DNI y teléfono, y asociarlo a un alumno indicando el vínculo correspondiente.

### RF-07 – Consultar responsable
El sistema deberá permitir al Administrador/Coordinador consultar los datos de un responsable y los alumnos que tenga asociados.

### RF-08 – Modificar responsable
El sistema deberá permitir al Administrador/Coordinador modificar los datos registrados de un responsable.

### RF-09 – Asociar responsables y alumnos
El sistema deberá permitir al Administrador/Coordinador asociar responsables a un alumno y asociar un mismo responsable a distintos alumnos.

### RF-10 – Desvincular responsable
El sistema deberá permitir al Administrador/Coordinador desvincular un responsable de un alumno sin eliminar su registro cuando continúe asociado a otro alumno.

---

## 3. Portal del responsable

### RF-11 – Acceder al portal del responsable
El sistema deberá permitir a los responsables registrados autenticarse en el portal utilizando su DNI como identificación de usuario.

### RF-12 – Visualizar alumnos asociados
El sistema deberá permitir al responsable autenticado visualizar los alumnos que tiene asociados.

### RF-13 – Consultar situación administrativa
El sistema deberá permitir al responsable consultar las cuotas adeudadas y el historial de pagos de cada alumno que tenga asociado.

### RF-14 – Consultar recibos
El sistema deberá permitir al responsable consultar y descargar los recibos correspondientes a los pagos de sus alumnos asociados.

### RF-15 – Restablecer contraseña
El sistema deberá proporcionar un mecanismo para restablecer la contraseña de acceso del responsable.

---

## 4. Categorías

### RF-16 – Determinar categoría del alumno
El sistema deberá determinar la categoría correspondiente al alumno a partir de su año de nacimiento.

---

## 5. Preinscripción

### RF-17 – Registrar preinscripción
El sistema deberá permitir que un responsable complete y envíe, sin necesidad de autenticarse previamente, un formulario de preinscripción con los datos del alumno y de sus responsables.

El formulario deberá contemplar:

**Datos del alumno:**
- nombre;
- apellido;
- DNI;
- fecha de nacimiento;
- domicilio;
- barrio;
- localidad;
- colegio.

**Datos del responsable principal:**
- nombre;
- apellido;
- DNI;
- teléfono;
- vínculo con el alumno.

El formulario podrá incluir un segundo responsable de manera opcional, con los mismos datos.

Si el DNI ingresado corresponde a un alumno ya registrado, la preinscripción no deberá generar un nuevo registro de alumno. Si el alumno se encuentra inactivo, su registro existente deberá poder utilizarse para una eventual reactivación.

### RF-18 – Consultar preinscripciones
El sistema deberá permitir al Administrador/Coordinador consultar los formularios de preinscripción recibidos y la información contenida en ellos.

### RF-19 – Corregir preinscripción
El sistema deberá permitir al Administrador/Coordinador corregir los datos recibidos en una preinscripción antes de utilizarlos para registrar al alumno.

### RF-20 – Registrar alumno desde una preinscripción validada
El sistema deberá permitir al Administrador/Coordinador revisar la información recibida y, una vez validada, utilizarla para registrar al alumno y asociar sus responsables sin volver a ingresar manualmente los datos.

---

## 6. Cuotas

### RF-21 – Configurar valor general de la cuota
El sistema deberá permitir al Administrador/Coordinador establecer y modificar el valor general de la cuota mensual.

### RF-22 – Generar cuotas mensuales
El sistema deberá generar automáticamente las cuotas mensuales correspondientes a los alumnos activos.

### RF-23 – Establecer vencimiento
El sistema deberá permitir al Administrador/Coordinador establecer una única fecha de vencimiento para las cuotas de cada período.

### RF-24 – Establecer importe particular para un alumno
El sistema deberá permitir al Administrador/Coordinador establecer un importe de cuota diferente del valor general para un alumno determinado.

### RF-25 – Corregir importe del período actual
El sistema deberá permitir al Administrador/Coordinador corregir el importe de las cuotas pendientes del período en curso cuando el valor utilizado en su generación automática sea incorrecto.

### RF-26 – Configurar interés por mora
El sistema deberá permitir al Administrador/Coordinador establecer y modificar el porcentaje de interés por mora aplicable a las cuotas vencidas e impagas.

### RF-27 – Aplicar interés por mora
El sistema deberá aplicar el interés por mora configurado a las cuotas que hayan superado su fecha de vencimiento y permanezcan impagas.

### RF-28 – Consultar cuotas de un alumno
El sistema deberá permitir al Administrador/Coordinador consultar las cuotas de un alumno, identificando período, importe, vencimiento, pagos realizados y saldo pendiente.

### RF-29 – Consultar deuda de un alumno
El sistema deberá permitir al Administrador/Coordinador consultar las cuotas vencidas e impagas de un alumno y el importe total adeudado.

### RF-30 – Consultar alumnos morosos
El sistema deberá permitir al Administrador/Coordinador acceder al listado de alumnos que posean deuda vencida.

### RF-31 – Registrar cobro de matrícula
El sistema deberá permitir al Administrador/Coordinador registrar el cobro correspondiente a la matrícula de un alumno.

---

## 7. Pagos

### RF-32 – Registrar pago
El sistema deberá permitir al Administrador/Coordinador registrar un importe recibido de un alumno indicando la fecha y, opcionalmente, el medio de pago utilizado, contemplando efectivo y transferencia.

### RF-33 – Registrar pago de cuota pendiente
El sistema deberá permitir al Administrador/Coordinador asociar el pago de una cuota mensual pendiente al período correspondiente, requiriendo el abono total del importe adeudado de esa cuota.

### RF-34 – Consultar historial de pagos
El sistema deberá permitir al Administrador/Coordinador consultar los pagos registrados para un alumno, incluyendo fecha, importe, obligaciones a las que fueron aplicados y medio de pago cuando haya sido informado.

### RF-35 – Anular pago
El sistema deberá permitir al Administrador/Coordinador anular un pago registrado, indicando obligatoriamente el motivo de la anulación y conservando el registro de la operación.

---

## 8. Recibos

### RF-36 – Generar recibo de pago
El sistema deberá generar automáticamente un recibo por cada pago confirmado, indicando como mínimo:

- número de recibo;
- alumno;
- fecha;
- concepto;
- importe abonado;
- medio de pago, cuando haya sido informado.

Cuando el pago corresponda a un cobro extraordinario abonado parcialmente, el recibo deberá informar además el saldo pendiente luego de aplicar dicho pago.

### RF-37 – Consultar recibos
El sistema deberá permitir al Administrador/Coordinador consultar los recibos emitidos y su vinculación con los pagos correspondientes.

### RF-38 – Descargar recibo
El sistema deberá permitir descargar el recibo generado para su conservación, impresión o posterior envío.

### RF-39 – Compartir recibo mediante WhatsApp
El sistema deberá facilitar el envío de un recibo mediante WhatsApp sin requerir integración con WhatsApp Business.

### RF-40 – Conservar recibos anulados
Cuando se anule un pago que posea un recibo emitido, el sistema deberá conservar dicho recibo identificado como anulado.

---

## 9. Cobros extraordinarios

### RF-41 – Registrar cobro extraordinario
El sistema deberá permitir al Administrador/Coordinador registrar un cobro extraordinario asociado a un alumno correspondiente a un evento o pedido de indumentaria.

### RF-42 – Registrar pagos parciales de cobros extraordinarios
El sistema deberá permitir registrar uno o más pagos parciales sobre un cobro extraordinario, conservando el importe total, los pagos realizados y el saldo pendiente.

### RF-43 – Consultar cobros extraordinarios
El sistema deberá permitir al Administrador/Coordinador consultar los cobros extraordinarios asociados a un alumno, incluyendo concepto, importe total, pagos realizados y saldo pendiente.

### RF-44 – Registrar datos básicos de indumentaria
Cuando el cobro extraordinario corresponda a indumentaria, el sistema deberá permitir identificar al menos el tipo de prenda y el talle solicitado.

---

## 10. Búsquedas, listados, exportación y reportes

### RF-45 – Buscar alumnos
El sistema deberá permitir al Administrador/Coordinador buscar alumnos por nombre, apellido o DNI.

### RF-46 – Filtrar alumnos
El sistema deberá permitir al Administrador/Coordinador filtrar alumnos por categoría, barrio, localidad, colegio, estado activo/inactivo y condición de morosidad.

### RF-47 – Visualizar listado de alumnos
El sistema deberá permitir al Administrador/Coordinador consultar de forma paginada el listado de alumnos mostrando como mínimo:

- nombre;
- apellido;
- año de nacimiento;
- DNI;
- responsable asociado;
- teléfono de contacto.

### RF-48 – Exportar listados de alumnos a Excel
El sistema deberá permitir exportar a Excel los listados de alumnos obtenidos mediante búsquedas y filtros.

### RF-49 – Exportar cobros extraordinarios a Excel
El sistema deberá permitir exportar a Excel la información correspondiente a cobros extraordinarios y sus saldos pendientes.

### RF-50 – Consultar pagos por período
El sistema deberá permitir al Administrador/Coordinador consultar los pagos registrados dentro de un período determinado, mostrando su detalle y el total recaudado.

### RF-51 – Consultar altas y bajas por período
El sistema deberá permitir al Administrador/Coordinador consultar los alumnos dados de alta y de baja dentro de un período determinado.

---

## 11. Dashboard

### RF-52 – Visualizar cantidad de alumnos activos
El sistema deberá mostrar en el dashboard la cantidad total de alumnos activos.

### RF-53 – Visualizar alumnos por categoría
El sistema deberá mostrar en el dashboard la cantidad de alumnos activos agrupados por categoría.

### RF-54 – Visualizar alumnos morosos
El sistema deberá informar en el dashboard la existencia de alumnos morosos y permitir acceder al listado correspondiente.

### RF-55 – Visualizar preinscripciones pendientes
El sistema deberá informar en el dashboard la existencia de preinscripciones pendientes de revisión y permitir acceder a ellas.

### RF-56 – Visualizar recaudación
El sistema deberá mostrar en el dashboard un resumen de la recaudación correspondiente al período seleccionado.

### RF-57 – Visualizar altas y bajas
El sistema deberá mostrar en el dashboard un resumen de las altas y bajas correspondientes al período seleccionado.

---

## 12. Autenticación y autorización

### RF-58 – Iniciar sesión como Administrador/Coordinador
El sistema deberá permitir al Administrador/Coordinador autenticarse para acceder a las funcionalidades de gestión.

### RF-59 – Restringir funcionalidades según tipo de usuario
El sistema deberá limitar las funcionalidades disponibles según el tipo de usuario autenticado, diferenciando al Administrador/Coordinador del Responsable.

---

## 13. Funcionamiento sin conexión

### RF-60 – Consultar información básica sin conexión
El sistema deberá permitir al Administrador/Coordinador consultar, durante una pérdida de conectividad, información básica de alumnos que haya quedado disponible previamente en el dispositivo.

### RF-61 – Registrar pago provisional sin conexión
El sistema deberá permitir al Administrador/Coordinador registrar provisionalmente un pago asociado a una obligación previamente disponible cuando no exista conexión.

### RF-62 – Sincronizar pagos registrados sin conexión
El sistema deberá sincronizar los pagos registrados provisionalmente cuando se restablezca la conexión.

### RF-63 – Informar resultado de sincronización
El sistema deberá informar al Administrador/Coordinador si la sincronización fue realizada correctamente o si requiere intervención.

### RF-64 – Emitir recibo luego de la sincronización
El sistema deberá generar el recibo correspondiente una vez que el pago registrado sin conexión haya sido sincronizado y validado correctamente.

---

## Decisiones consensuadas durante la revisión

1. Se utilizará una única fecha de vencimiento por período.
2. Las cuotas mensuales deberán abonarse en su totalidad; no se habilitarán pagos parciales de cuotas.
3. Los pagos parciales quedarán limitados a cobros extraordinarios correspondientes a eventos e indumentaria.
4. Los recibos de pagos parciales de cobros extraordinarios deberán informar el saldo pendiente.
5. La existencia previa de un DNI no deberá generar un alumno duplicado; si corresponde a un alumno inactivo, se reutilizará su registro para una eventual reactivación.

---

## Estado del documento

**Estado:** Actualizado tras la revisión del equipo. Pendiente de verificación final por los integrantes.

Una vez verificada esta versión, podrá utilizarse junto con los requisitos no funcionales y las reglas de negocio como base para las siguientes etapas del proyecto.
