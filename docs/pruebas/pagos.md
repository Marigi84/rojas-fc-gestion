# Pruebas de pagos y recibos

## Sistema de Gestión Rojas FC — issue #64

Este documento registra las pruebas del Módulo 5: registro y anulación de pagos de cuotas, historial de pagos y recibos. Cubre los requisitos RF-24, RF-25, RF-27 a RF-31 y RF-33, y las reglas RN-27 a RN-33.

Las pruebas automáticas del backend están en `PagoServiceTest` (9 casos, sin base). Ver `backend/README.md`, sección "Tests".

### Cómo se prepararon

1. Base `rojasfc_pagos` creada desde `database/schema.sql` y cargada con `database/datos-demo.sql`, para no tocar la base de trabajo.
2. Backend y frontend levantados en local, con el ingreso por la pantalla de inicio de sesión.
3. Se usaron los usuarios del seed, cuyas contraseñas son públicas: administrador DNI 20111222, todos con la clave `Demo12345`.
4. Alumno Mateo Gómez (45111222), activo, con la responsable Laura Gómez (30111222).
5. El seed deja tres cuotas de Mateo: septiembre pagada, octubre vencida con $ 27.500 (incluye el 10 % de interés) y noviembre pendiente con $ 25.000.

**Última ejecución:** 04/11/2026: 21 de 21 casos correctos.

---

## Casos

| # | Caso | Pasos | Resultado esperado | Resultado |
|---|------|-------|--------------------|-----------|
| 1 | Registrar el pago de una cuota vencida (RF-24, RF-25, RN-28) | Registrar el pago de octubre con medio efectivo | Responde 201 con el monto de $ 27.500, que incluye el interés | OK |
| 2 | Recibo automático (RF-29, RN-33) | Mirar el pago registrado | El pago trae el número de recibo emitido | OK |
| 3 | La cuota queda pagada (RN-29) | Consultar las cuotas del alumno | Octubre figura como pagada, con la fecha del pago | OK |
| 4 | Cuota pendiente sin interés | Registrar el pago de noviembre con medio transferencia | Responde 201 con el monto de $ 25.000, sin interés | OK |
| 5 | Historial de pagos (RF-27) | Consultar los pagos del alumno | Muestra los pagos nuevos y el de septiembre del seed, con fecha, período, monto, medio y recibo | OK |
| 6 | Datos del recibo (RF-29, RF-30) | Consultar el recibo de un pago nuevo | Muestra número, alumno, fecha de emisión, concepto, importe y medio de pago | OK |
| 7 | Anular un pago (RF-28, RN-30, RN-31) | Anular el pago de octubre con el motivo "se cobró dos veces" | Responde 204 y el historial muestra el motivo | OK |
| 8 | El recibo se conserva anulado (RF-33) | Consultar el recibo después de la anulación | Sigue existiendo, identificado como anulado | OK |
| 9 | La cuota vuelve a quedar impaga (RN-32) | Consultar las cuotas del alumno | Octubre vuelve a figurar como vencida, sin pago asociado | OK |
| 10 | Anular dos veces (RN-30) | Volver a anular el mismo pago | Responde 409: el pago ya estaba anulado | OK |
| 11 | Un solo pago vigente por cuota (RN-29, RN-32) | Pagar de nuevo octubre después de la anulación | Responde 201 con un recibo nuevo | OK |
| 12 | Cuota ya pagada (RF-25) | Intentar pagar otra vez una cuota con pago vigente | Responde 409: la cuota ya está pagada | OK |
| 13 | Medio de pago inválido (RN-27) | Registrar un pago con medio "cheque" | Responde 400: el medio tiene que ser efectivo o transferencia | OK |
| 14 | Período con formato inválido | Registrar un pago con período "2026-11-01" | Responde 400: el período tiene que tener el formato AAAA-MM | OK |
| 15 | Cuota de un período sin generar | Registrar un pago de un período que el alumno no tiene | Responde 404 | OK |
| 16 | Recibo inexistente | Consultar un recibo que no existe | Responde 404 | OK |
| 17 | Pantalla de pagos | Buscar al alumno 45111222 por DNI | Muestra sus cuotas y el historial de pagos | OK |
| 18 | Registrar desde la pantalla | Registrar el pago de la cuota vencida desde la tabla | Avisa con el número de recibo y la cuota pasa a Pagada | OK |
| 19 | Ver y descargar el recibo (RF-30, RF-31) | Abrir el recibo y presionar "Descargar" | Se descarga un archivo de texto con los datos del recibo | OK |
| 20 | Anular desde la pantalla | Anular el pago con un motivo | Avisa, el historial muestra "Anulado" con el motivo y la cuota vuelve a Vencida | OK |
| 21 | Consola del navegador | Revisar la consola durante el recorrido | Sin errores de JavaScript | OK |

### Verificación automática

- Backend: `mvn test` sin fallas.
- Frontend: `npm run lint`, `npm run format:check` y `npm run build` sin errores.

---

## Pendiente

- Probar el recorrido en celular (390 px de ancho), que en esta ejecución no se hizo.
- Probar la descarga del recibo desde el Portal del Responsable (RF-14), que queda para cuando el botón esté en esa pantalla.
