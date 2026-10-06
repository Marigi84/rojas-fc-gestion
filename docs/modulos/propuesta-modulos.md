# Propuesta de Módulos

## Sistema de Gestión Rojas FC

Este documento propone los módulos funcionales del Sistema de Gestión Rojas FC, construidos contra los requisitos funcionales, requisitos no funcionales y reglas de negocio consolidados por el equipo, y contra el alcance aprobado en la Primera Entrega.

Esta etapa no define tablas, claves, entidades ni atributos — eso corresponde al modelado de entidades y relaciones, que sigue a esta propuesta.

---

## 1. Autenticación y Autorización

**Responsabilidad principal:** gestionar el acceso al sistema para los dos tipos de usuario (Administrador/Coordinador y Responsable), y restringir qué funcionalidades ve cada uno.

**Requisitos funcionales cubiertos:** RF-11, RF-15, RF-58, RF-59.

---

## 2. Alumnos, Responsables y Categorías

**Responsabilidad principal:** ABM de alumnos y de responsables, la asociación entre ambos, y la determinación de categoría según año de nacimiento. Se agrupan los tres porque comparten el mismo actor (Administrador/Coordinador) y porque un alumno no tiene sentido administrativo sin sus responsables asociados ni su categoría asignada. El alta de un alumno —sea directa o por preinscripción validada— exige asociar al menos un responsable (RN-01); no existe alta de alumno sin responsable. Las categorías quedan fijas, definidas por año de nacimiento: esta entrega no incluye un ABM para administrarlas, por no formar parte del alcance comprometido en la Primera Entrega.

**Requisitos funcionales cubiertos:** RF-01, RF-02, RF-03, RF-04, RF-05, RF-06, RF-07, RF-08, RF-09, RF-10, RF-16.

---

## 3. Preinscripción y Validación

**Responsabilidad principal:** formulario público de preinscripción y su revisión/validación posterior por parte de la administración, incluyendo la conversión en alumno dado de alta.

**Requisitos funcionales cubiertos:** RF-17, RF-18, RF-19, RF-20.

---

## 4. Cuotas

**Responsabilidad principal:** configuración del valor general, vencimiento e interés por mora; generación mensual para alumnos activos; consulta de cuotas, deuda y morosidad.

**Requisitos funcionales cubiertos:** RF-21, RF-22, RF-23, RF-26, RF-27, RF-28, RF-29, RF-30.

---

## 5. Pagos y Recibos

**Responsabilidad principal:** registrar pagos (de cuotas y de cobros extraordinarios), anularlos cuando corresponda, y generar, consultar, descargar y compartir el recibo asociado a cada pago confirmado.

**Requisitos funcionales cubiertos:** RF-32, RF-33, RF-34, RF-35, RF-36, RF-37, RF-38, RF-39, RF-40.

---

## 6. Cobros Extraordinarios (Eventos e Indumentaria)

**Responsabilidad principal:** registrar eventos, asignarles alumnos participantes y registrar y consultar cobros puntuales asociados a un alumno — eventos deportivos y pedidos de indumentaria —, admitiendo pagos parciales y conservando el saldo pendiente.

**Requisitos funcionales cubiertos:** RF-41, RF-42, RF-43, RF-44, RF-65, RF-66.

---

## 7. Portal de Responsables

**Responsabilidad principal:** una vez autenticado, permitir a un responsable consultar (solo lectura) los alumnos a su cargo, su situación administrativa y sus recibos.

**Requisitos funcionales cubiertos:** RF-12, RF-13, RF-14.

> Nota: el login y el restablecimiento de contraseña del responsable (RF-11, RF-15) quedan en el módulo de Autenticación, no acá, porque son el mismo mecanismo de acceso que usa el Administrador/Coordinador — separar "cómo entro" de "qué puedo ver una vez adentro" evita mezclar responsabilidades.

---

## 8. Búsquedas, Reportes y Dashboard

**Responsabilidad principal:** búsqueda, filtrado y listados paginados de alumnos; exportación a Excel; consultas por período (pagos, altas y bajas); y el dashboard con indicadores agregados (alumnos activos, por categoría, morosos, preinscripciones pendientes, recaudación, altas/bajas). Se agrupan búsquedas/listados y dashboard en un mismo módulo porque ambos son vistas derivadas de datos que pertenecen a otros módulos (no generan ni modifican información propia).

**Requisitos funcionales cubiertos:** RF-45, RF-46, RF-47, RF-48, RF-49, RF-50, RF-51, RF-52, RF-53, RF-54, RF-55, RF-56, RF-57.

---

## 9. Consulta sin Conexión

**Responsabilidad principal:** consulta de información básica de alumnos previamente disponible en el dispositivo durante una pérdida de conexión. Sin conexión no se registran operaciones, por lo que no se requiere sincronización.

**Requisitos funcionales cubiertos:** RF-60.

---

## Cobertura

Los 9 módulos cubren la totalidad de los requisitos funcionales vigentes (RF-01 a RF-66, excepto los eliminados en la revisión de alcance del 06/10/2026: RF-24, RF-25, RF-31 y RF-61 a RF-64), cada uno con un módulo principal responsable. Esto no implica que los módulos operen de forma aislada: varios interactúan entre sí (por ejemplo, Pagos y Recibos con Cuotas y con Cobros Extraordinarios, o Búsquedas/Reportes leyendo datos de casi todos los demás), pero cada RF tiene un único módulo dueño de su implementación.

## Requisitos no funcionales

Los requisitos no funcionales (seguridad, integridad, rendimiento, usabilidad, disponibilidad — ver `docs/requisitos/requisitos-no-funcionales.md`) no generan módulos propios: son condiciones de calidad que atraviesan a todos los módulos de arriba, no funcionalidades independientes con pantalla propia.

Esto incluye específicamente **RNF-12 (Trazabilidad de operaciones relevantes)**: no se propone un módulo de "Auditoría" separado porque ningún requisito funcional pide una pantalla dedicada para consultarla — es una capacidad transversal (registro interno) que da soporte a los demás módulos, no una funcionalidad propia con la que interactúe un usuario.

---

**Estado:** Propuesta consolidada, con los ajustes acordados en el Issue #7 (responsable obligatorio en alta directa, categorías fijas sin ABM, y aclaración sobre interacción entre módulos).
