# Requisitos Funcionales

## Sistema de Gestión Rojas FC

Este documento reúne los requisitos funcionales consolidados del Sistema de Gestión Rojas FC.

La definición se realizó tomando como base la Primera Entrega aprobada del Trabajo Final Integrador y el análisis posterior de las necesidades reales de la escuela.

En este documento, **Administrador** designa al actor "Administrador / Coordinador" definido en la Primera Entrega.

---

## 1. Gestión de alumnos

### RF-01 – Registrar alumno
El sistema deberá permitir al Administrador registrar un alumno ingresando nombre, apellido, DNI, fecha de nacimiento y dirección.

Si el DNI ya corresponde a un alumno registrado, el sistema no deberá crear un nuevo alumno. Si el registro existente se encuentra inactivo, deberá utilizarse para su eventual reactivación.

Si el DNI corresponde a una persona ya registrada con otro rol (por ejemplo, un responsable o un ex alumno), el sistema deberá reutilizar sus datos personales.

### RF-02 – Consultar alumno
El sistema deberá permitir al Administrador consultar los datos registrados y el historial de un alumno.

### RF-03 – Modificar alumno
El sistema deberá permitir al Administrador modificar los datos registrados de un alumno.

### RF-04 – Dar de baja un alumno
El sistema deberá permitir al Administrador dar de baja lógicamente a un alumno sin eliminar su información ni su historial.

### RF-05 – Reactivar alumno
El sistema deberá permitir al Administrador reactivar un alumno dado de baja conservando su registro e historial anteriores.

---

## 2. Gestión de responsables

### RF-06 – Registrar responsables
El sistema deberá permitir al Administrador registrar uno o más responsables de un alumno, indicando para cada uno nombre, apellido, DNI, teléfono y su vínculo con el alumno.

Si el DNI corresponde a una persona ya registrada, el sistema deberá reutilizar sus datos personales.

### RF-07 – Consultar responsable
El sistema deberá permitir al Administrador consultar los datos de un responsable y los alumnos que tenga asociados.

### RF-08 – Modificar responsable
El sistema deberá permitir al Administrador modificar los datos registrados de un responsable.

### RF-09 – Asociar responsables y alumnos
El sistema deberá permitir al Administrador asociar responsables a un alumno y asociar un mismo responsable a distintos alumnos.

### RF-10 – Desvincular responsable
El sistema deberá permitir al Administrador desvincular un responsable de un alumno sin eliminar su registro cuando continúe asociado a otro alumno.

---

## 3. Portal del responsable

### RF-11 – Acceder al portal del responsable
El sistema deberá permitir a los responsables registrados autenticarse en el portal mediante su DNI y una contraseña asignada por el Administrador.

### RF-12 – Visualizar alumnos asociados
El sistema deberá permitir al responsable autenticado visualizar los alumnos que tiene asociados.

### RF-13 – Consultar situación administrativa
El sistema deberá permitir al responsable consultar las cuotas adeudadas y el historial de pagos de cada alumno que tenga asociado.

### RF-14 – Consultar recibos
El sistema deberá permitir al responsable consultar y descargar los recibos correspondientes a los pagos de sus alumnos asociados.

### RF-15 – Restablecer contraseña del responsable
El sistema deberá permitir al Administrador restablecer la contraseña de acceso de un responsable.

---

## 4. Categorías

### RF-16 – Determinar categoría del alumno
El sistema deberá determinar la categoría correspondiente al alumno a partir de su año de nacimiento.

---

## 5. Cuotas

### RF-17 – Configurar valor general de la cuota
El sistema deberá permitir al Administrador establecer y modificar el valor general de la cuota mensual.

### RF-18 – Generar cuotas mensuales
El sistema deberá generar automáticamente las cuotas mensuales correspondientes a los alumnos activos.

### RF-19 – Establecer vencimiento
El sistema deberá permitir al Administrador establecer una única fecha de vencimiento para las cuotas de cada período.

### RF-20 – Configurar interés por mora
El sistema deberá permitir al Administrador establecer y modificar el porcentaje de interés por mora aplicable a las cuotas vencidas e impagas.

### RF-21 – Aplicar interés por mora
El sistema deberá aplicar el interés por mora configurado a las cuotas que hayan superado su fecha de vencimiento y permanezcan impagas.

### RF-22 – Consultar cuotas de un alumno
El sistema deberá permitir al Administrador consultar las cuotas de un alumno, identificando período, importe, vencimiento, estado (pagada, pendiente o vencida) y pago asociado.

### RF-23 – Consultar alumnos morosos
El sistema deberá permitir al Administrador acceder al listado de alumnos que posean deuda vencida, indicando el importe total adeudado por cada uno.

---

## 6. Pagos

### RF-24 – Registrar pago
El sistema deberá permitir al Administrador registrar un pago indicando fecha, importe, medio de pago (efectivo o transferencia) y la obligación a la que se imputa: una cuota, la participación de un alumno en un evento o un encargo de indumentaria.

### RF-25 – Registrar pago de cuota pendiente
El sistema deberá permitir al Administrador asociar el pago de una cuota mensual pendiente al período correspondiente, requiriendo el abono total del importe adeudado de esa cuota.

### RF-26 – Registrar pagos parciales de eventos e indumentaria
El sistema deberá permitir al Administrador registrar uno o más pagos parciales sobre la participación de un alumno en un evento o sobre un encargo de indumentaria, conservando el importe total, los pagos realizados y el saldo pendiente.

### RF-27 – Consultar historial de pagos
El sistema deberá permitir al Administrador consultar los pagos registrados para un alumno, incluyendo fecha, importe, la obligación a la que se imputó cada pago (cuota, participación en un evento o encargo de indumentaria) y medio de pago.

### RF-28 – Anular pago
El sistema deberá permitir al Administrador anular un pago registrado, indicando obligatoriamente el motivo de la anulación y conservando el registro de la operación.

---

## 7. Recibos

### RF-29 – Generar recibo de pago
El sistema deberá generar automáticamente un recibo por cada pago confirmado, indicando como mínimo:

- número de recibo;
- alumno;
- fecha;
- concepto;
- importe abonado;
- medio de pago.

Cuando el pago corresponda a una participación en un evento o a un encargo de indumentaria abonado parcialmente, el recibo deberá informar además el saldo pendiente luego de aplicar dicho pago.

### RF-30 – Consultar recibos
El sistema deberá permitir al Administrador consultar los recibos emitidos y su vinculación con los pagos correspondientes.

### RF-31 – Descargar recibo
El sistema deberá permitir descargar el recibo generado para su conservación, impresión o posterior envío.

### RF-32 – Compartir recibo mediante WhatsApp
El sistema deberá facilitar el envío de un recibo mediante WhatsApp sin requerir integración con WhatsApp Business.

### RF-33 – Conservar recibos anulados
Cuando se anule un pago que posea un recibo emitido, el sistema deberá conservar dicho recibo identificado como anulado.

---

## 8. Eventos e indumentaria

### RF-34 – Registrar evento
El sistema deberá permitir al Administrador registrar un evento indicando nombre, año e importe.

### RF-35 – Asignar alumnos a un evento
El sistema deberá permitir al Administrador asignar uno o más alumnos como participantes de un evento. Al asignar cada alumno, el sistema deberá registrar su participación; el importe a abonar será el definido para ese Evento.

### RF-36 – Registrar encargo de indumentaria
El sistema deberá permitir al Administrador registrar un encargo de indumentaria asociado a un alumno, indicando el importe y, opcionalmente, una descripción de lo solicitado.

### RF-37 – Consultar participaciones y encargos
El sistema deberá permitir al Administrador consultar las participaciones en eventos y los encargos de indumentaria asociados a un alumno, incluyendo concepto, importe total, pagos realizados y saldo pendiente.

---

## 9. Búsquedas, listados, exportación y reportes

### RF-38 – Buscar alumnos
El sistema deberá permitir al Administrador buscar alumnos por nombre, apellido o DNI, y filtrarlos por categoría, estado (activo o inactivo) y condición de morosidad, pudiendo combinar los criterios. Por defecto se muestran solo los alumnos activos; el Administrador puede incluir a los inactivos o listar solo a los inactivos.

### RF-39 – Visualizar listado de alumnos
El sistema deberá permitir al Administrador consultar de forma paginada el listado de alumnos, incluido el resultado de la búsqueda y los filtros de RF-38, mostrando nombre, apellido, DNI y año de nacimiento. Desde el listado podrá acceder al detalle de cada alumno (RF-02).

### RF-40 – Exportar listados de alumnos a Excel
El sistema deberá permitir al Administrador exportar a Excel los listados de alumnos obtenidos mediante búsquedas y filtros, con los datos del listado (RF-39).

### RF-41 – Exportar participaciones y encargos a Excel
El sistema deberá permitir al Administrador exportar a Excel la información correspondiente a participaciones en eventos y encargos de indumentaria, con sus saldos pendientes.

### RF-42 – Consultar recaudación entre fechas
El sistema deberá permitir al Administrador consultar lo recaudado entre una fecha desde y una fecha hasta que él indique, considerando la fecha en que se realizó cada pago, mostrando el total recaudado y su discriminación por tipo de obligación (cuotas, participaciones en eventos y encargos de indumentaria).

### RF-43 – Consultar deuda pendiente
El sistema deberá permitir al Administrador consultar la deuda pendiente a la fecha, mostrando el detalle por alumno y el total adeudado, discriminado por tipo de obligación (cuotas impagas, saldos de participaciones en eventos y saldos de encargos de indumentaria) y pudiendo filtrar por tipo.

### RF-44 – Consultar altas y bajas entre fechas
El sistema deberá permitir al Administrador consultar los alumnos dados de alta y de baja entre una fecha desde y una fecha hasta que él indique.

---

## 10. Dashboard

### RF-45 – Visualizar cantidad de alumnos activos
El sistema deberá mostrar en el dashboard la cantidad total de alumnos activos.

### RF-46 – Visualizar alumnos por categoría
El sistema deberá mostrar en el dashboard la cantidad de alumnos activos agrupados por categoría.

### RF-47 – Visualizar alumnos morosos
El sistema deberá informar en el dashboard la existencia de alumnos morosos y permitir acceder al listado correspondiente.

### RF-48 – Visualizar recaudación
El sistema deberá mostrar en el dashboard el total recaudado en el mes en curso hasta la fecha.

---

## 11. Autenticación y autorización

### RF-49 – Iniciar sesión como Administrador
El sistema deberá permitir al Administrador autenticarse mediante su DNI y contraseña para acceder a las funcionalidades de gestión.

### RF-50 – Recuperar contraseña del Administrador
El sistema deberá permitir al Administrador recuperar su contraseña mediante un enlace enviado a su correo electrónico registrado.

### RF-51 – Registrar administrador
El sistema deberá permitir al Administrador registrar a otro administrador indicando DNI, nombre, apellido y correo electrónico.

### RF-52 – Restringir funcionalidades según tipo de usuario
El sistema deberá limitar las funcionalidades disponibles según el tipo de usuario autenticado, diferenciando al Administrador del Responsable.

---

## 12. Funcionamiento sin conexión

### RF-53 – Consultar información básica sin conexión
El sistema deberá permitir al Administrador consultar, durante una pérdida de conectividad, información básica de alumnos que haya quedado disponible previamente en el dispositivo.

### RF-54 – Registrar pago provisional sin conexión
El sistema deberá permitir al Administrador registrar provisionalmente un pago asociado a una obligación previamente disponible cuando no exista conexión.

### RF-55 – Sincronizar pagos registrados sin conexión
El sistema deberá sincronizar los pagos registrados provisionalmente cuando se restablezca la conexión.

### RF-56 – Informar resultado de sincronización
El sistema deberá informar al Administrador si la sincronización fue realizada correctamente o si requiere intervención.

### RF-57 – Emitir recibo luego de la sincronización
El sistema deberá generar el recibo correspondiente una vez que el pago registrado sin conexión haya sido sincronizado y validado correctamente.
