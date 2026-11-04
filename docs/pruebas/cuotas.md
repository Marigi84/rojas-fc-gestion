# Pruebas de las cuotas

## Sistema de Gestión Rojas FC — issue #63

Este documento registra las pruebas del Módulo 4: configuración de períodos, generación de cuotas, interés por mora, cuotas de un alumno y alumnos morosos. Cubre los requisitos RF-17 a RF-23 y las reglas RN-14 a RN-24.

Las pruebas automáticas del backend están en `CuotaServiceTest` (10 casos, sin base) y `CuotaIntegracionTest` (13 casos, contra MySQL). Ver `backend/README.md`, sección "Tests".

### Cómo se prepararon

1. Base `rojasfc` vacía, creada desde `database/schema.sql`.
2. Backend y frontend levantados en local.
3. Configuración inicial con el administrador DNI 20111222 e ingreso por la pantalla de inicio de sesión.
4. Tres alumnos: Mateo Gómez (45111222) y Sofía López (45111223), activos, y Tomás Ruiz (45111224), dado de baja.
5. Las fechas de alta de esos alumnos se adelantaron en la base al 01/01/2026, porque no se le cobra un mes pasado a quien todavía no estaba inscripto.

Los casos se recorrieron en Microsoft Edge, en escritorio (1280 px) y en celular (390 px). El recorrido se automatizó con Playwright. El mes vencido del ejemplo es octubre de 2026.

**Última ejecución:** 03/11/2026, con todos los PRs abiertos combinados sobre `main`: 23 de 23 casos correctos.

---

## Casos

| # | Caso | Pasos | Resultado esperado | Resultado |
|---|------|-------|--------------------|-----------|
| 1 | Pantalla vacía | Entrar a Cuotas sin datos | Avisa que no hay períodos ni alumnos con deuda vencida | OK |
| 2 | Mes propuesto | Mirar el formulario | Propone el mes actual | OK |
| 3 | Configurar un período (RF-17, RF-19, RF-20) | Cargar octubre de 2026, $ 1.000, vencimiento 13/10 e interés 10 %, y guardar | Aparece en la tabla de períodos | OK |
| 4 | Datos del período | Mirar la fila | Muestra importe, vencimiento e interés con formato local | OK |
| 5 | Modificar: carga de datos | Presionar "Modificar" | El formulario trae los datos del período | OK |
| 6 | Modificar antes de generar (RF-17) | Cambiar el importe a $ 1.500 y guardar | La tabla muestra el importe nuevo | OK |
| 7 | Vencimiento inválido | Pedir a la API un vencimiento anterior al período | Responde 400 | OK |
| 8 | Mes futuro | Configurar un mes que todavía no empezó | Se guarda, pero "Generar cuotas" queda desactivado | OK |
| 9 | Confirmación (RNF-13) | Presionar "Generar cuotas" en octubre | Pide confirmación y avisa que después no se puede cambiar | OK |
| 10 | Solo alumnos activos (RF-18, RN-14) | Aceptar | Se generan 2 cuotas de los 3 alumnos: el dado de baja no tiene | OK |
| 11 | Período generado | Mirar la fila | Ya no ofrece modificar ni generar | OK |
| 12 | Morosos (RF-23, RN-23) | Mirar "Alumnos morosos" | Mateo figura con $ 1.650,00: $ 1.500 más el 10 % | OK |
| 13 | Quién es moroso | Mirar la lista | Figura Sofía; el alumno dado de baja, sin cuotas, no | OK |
| 14 | Condiciones fijas en pantalla (RN-22) | Elegir octubre en el formulario | Avisa que ya tiene cuotas y desactiva "Guardar período" | OK |
| 15 | Condiciones fijas en la API (RN-22) | Pedir a la API que cambie el período | Responde 409 | OK |
| 16 | Cuotas del alumno (RF-21, RF-22) | Abrir el detalle de Mateo | Muestra período, importe, vencimiento, $ 1.650,00 a pagar y Vencida | OK |
| 17 | Morosidad y estado (RN-24) | Mirar el encabezado del detalle | Figura Moroso y sigue Activo | OK |
| 18 | Alumno sin cuotas | Abrir el detalle de Tomás | Indica que todavía no tiene cuotas | OK |
| 19 | Filtro del listado (RF-38) | En Alumnos, filtrar por morosos | Muestra a Mateo y a Sofía | OK |
| 20 | Celular | Abrir Cuotas y el detalle con 390 px de ancho | No hay desplazamiento horizontal | OK |
| 21 | Permisos (RF-52) | Pedir los períodos con la sesión de un responsable | Responde 403 | OK |
| 22 | Consola del navegador | Revisar la consola durante el recorrido | Sin errores de JavaScript; solo las respuestas que los casos provocan a propósito | OK |
| 23 | Generación automática (RF-18, RN-15, RN-17) | Reiniciar el backend con noviembre sin configurar | Crea noviembre con el valor de octubre y vencimiento el 13/11, y genera 2 cuotas pendientes, sin interés | OK |

---

## Lo que cubren las pruebas automáticas

Estos casos se prueban contra MySQL en `CuotaIntegracionTest`, porque dependen de `vista_cuota` y de los triggers:

- Una cuota vencida lleva el interés una sola vez, calculado sobre el importe (RN-19, RN-20).
- Una cuota pagada muestra su pago y deja de contar como deuda; si el pago se anula, vuelve a estar vencida (RF-22, RN-32).
- El trigger rechaza cambiar un período con cuotas, aunque no se pase por el servicio (RN-22).
- El total adeudado suma las cuotas vencidas con su interés (RF-23).
- Un mes pasado no se le cobra a quien se inscribió después de ese mes (RN-14).
- Un responsable no accede a los períodos, las cuotas ni los morosos (RF-52).

El pago de una cuota desde la interfaz se prueba con el issue #64.

## Limitaciones conocidas

- Las altas quedan con la fecha del día en que se registran, así que no se pueden cargar deudas de meses anteriores a esa fecha. Para mostrar una cuota vencida en una demo hace falta configurar el mes en curso con un vencimiento ya pasado, o datos de ejemplo con altas anteriores.
- Quien se da de alta después de generadas las cuotas del mes empieza a pagar desde el mes siguiente, porque las cuotas son para quienes están activos al generarlas (RN-14).
- `vista_cuota` usa la fecha del servidor de MySQL para decidir si una cuota venció. En un servidor con otra zona horaria, una cuota puede figurar vencida unas horas antes o después. Hay que tenerlo en cuenta al desplegar (#60).
