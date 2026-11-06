# Pruebas del Dashboard y del Portal del Responsable

## Sistema de Gestión Rojas FC — issues #66 y #67

Este documento registra las pruebas de la pantalla de inicio del Administrador y del portal de consulta del Responsable. Cubre los requisitos RF-45 a RF-48 y RF-12 a RF-14, y RNF-03.

Las pruebas automáticas del backend están en `DashboardPortalIntegracionTest` (8 casos, contra MySQL). Ver `backend/README.md`, sección "Tests".

### Cómo se prepararon

1. Base `rojasfc` creada desde `database/schema.sql`, con los datos de `database/datos-demo.sql`.
2. Backend y frontend levantados en local.
3. Ingreso por la pantalla de inicio de sesión con los usuarios de demostración.

Los casos se recorrieron en Microsoft Edge, en escritorio (1280 px) y en celular (390 px). El recorrido se automatizó con Playwright.

**Última ejecución:** 03/11/2026, rama `modulo8-3/dashboard-portal`: 21 de 21 casos correctos.

---

## Casos

| # | Caso | Pasos | Resultado esperado | Resultado |
|---|------|-------|--------------------|-----------|
| 1 | Inicio del Administrador | Ingresar como 20111222 | Entra al Dashboard | OK |
| 2 | Alumnos activos (RF-45) | Mirar el indicador | Muestra 7: no cuenta al alumno dado de baja | OK |
| 3 | Alumnos por categoría (RF-46) | Mirar la tabla | 2017: 2, 2016: 2, 2015: 1 y 2014: 2 | OK |
| 4 | Morosos (RF-47) | Mirar el indicador | Informa la cantidad de alumnos con deuda vencida | OK |
| 5 | Recaudación (RF-48) | Mirar el indicador | $ 50.000,00: los dos pagos del mes en curso | OK |
| 6 | Acceso al listado (RF-47) | Presionar "Ver el listado" | Abre Cuotas, con el listado de morosos | OK |
| 7 | Total con interés (RF-23) | Mirar a Julieta en el listado | Figura con dos cuotas vencidas y el total con interés | OK |
| 8 | Pago anulado (RN-32) | Abrir el detalle de Lucía | La cuota del pago anulado figura vencida | OK |
| 9 | Inicio del Responsable | Ingresar como 30111222 | Entra a su portal | OK |
| 10 | Menú del Responsable (RF-52) | Mirar el menú | Solo ve "Portal del Responsable" | OK |
| 11 | Alumnos a cargo (RF-12) | Mirar el portal | Ve a Mateo y a Sofía | OK |
| 12 | Datos de otra familia (RNF-03) | Mirar el portal | No ve a Lucía, de otro responsable | OK |
| 13 | Deuda | Mirar a Mateo | Figura "Con deuda vencida" | OK |
| 14 | Cuotas (RF-13) | Mirar las cuotas de Mateo | Se ven sus cuotas vencidas y pagadas | OK |
| 15 | Historial de pagos (RF-13) | Mirar los pagos de Sofía | Figura el pago de $ 27.500,00, con interés | OK |
| 16 | Recibos (RF-14) | Mirar los recibos de Sofía | Figuran sus 3 recibos | OK |
| 17 | Pago y recibo anulados (RF-33) | Ingresar como 30111223 y mirar a Lucía | El pago y el recibo figuran como anulados | OK |
| 18 | Otro responsable | Mirar el portal de 30111223 | No ve a Mateo | OK |
| 19 | Permisos (RF-52) | Pedir el Dashboard a la API con la sesión de un responsable | Responde 403 | OK |
| 20 | Celular | Abrir el portal y el Dashboard con 390 px de ancho | No hay desplazamiento horizontal | OK |
| 21 | Consola del navegador | Revisar la consola durante el recorrido | Sin errores de JavaScript | OK |

## Limitaciones conocidas

- El portal lista los recibos, pero todavía no los descarga (parte de RF-14): se suma cuando esté la descarga de pagos y recibos (#64).
- Los pagos y recibos de estos casos vienen de los datos de ejemplo, porque la pantalla para registrarlos es la del #64.
- Las cantidades de morosos varían con la fecha: desde el día 14 del mes, las cuotas impagas del mes en curso también cuentan como vencidas.
