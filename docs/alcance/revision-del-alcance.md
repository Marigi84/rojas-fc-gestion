# Revisión del Alcance

## Sistema de Gestión Rojas FC

Este documento registra los ajustes realizados sobre el alcance definido en la Primera Entrega, conforme a lo previsto en su sección 6.7 "Organización y seguimiento del trabajo":

> Durante el avance se revisará periódicamente el alcance y la planificación. En caso de surgir dificultades técnicas o desvíos respecto de los tiempos previstos, se priorizarán las funcionalidades centrales relacionadas con la gestión de alumnos, responsables, cuotas y pagos, evitando comprometer la estabilidad del producto final.

---

## 1. Preinscripción en línea

### Qué preveía la Primera Entrega

La Primera Entrega incluía un formulario propio de preinscripción, completado por el responsable del niño o niña, cuya información debía ser revisada y aprobada por el Administrador antes de convertirse en un alumno activo. Figura en:

- Sección 2, "Propuesta de solución y valor agregado".
- Sección 3, "Actores": validación de preinscripciones por el Administrador y aporte de información por el Responsable.
- Sección 4, "Alcance del proyecto": "Formulario propio de preinscripción y proceso de validación".
- Sección 6.3, "Etapa 3 — Desarrollo del núcleo funcional": "proceso de preinscripción y validación".

### Ajuste

La preinscripción en línea y su proceso de validación se reprograman como una evolución posterior del sistema.

### Motivo

Se priorizan las funcionalidades centrales de gestión de alumnos, responsables, cuotas y pagos, tal como prevé la sección 6.7 de la Primera Entrega.

### Cómo se cubre la inscripción

El Administrador registra a cada alumno junto con al menos un responsable (RF-01 y RF-06). Los datos se ingresan una sola vez y el sistema evita registros duplicados de personas (RN-01).

La preinscripción podrá incorporarse en un incremento posterior sin modificar la gestión de alumnos y responsables definida en los requisitos actuales.

---

## 2. Vencimiento único por período

### Qué preveía la Primera Entrega

La Primera Entrega preveía configurar "uno o más vencimientos" por período (sección 2, "Propuesta de solución y valor agregado").

### Ajuste

Cada período tiene una única fecha de vencimiento.

### Motivo

Simplifica la configuración de las cuotas y el cálculo del interés por mora.

### Cómo queda

El Administrador establece una única fecha de vencimiento para las cuotas de cada período (RF-19, RN-18).

---

## 3. Funcionalidades fuera de la entrega final

### Qué preveía la Primera Entrega

La Primera Entrega incluía cobros de eventos e indumentaria, reportes, exportación a Excel, envío de comprobantes por WhatsApp y funcionamiento sin conexión. Figuran en:

- Sección 4, "Alcance del proyecto".
- Sección 6.4, "Etapa 4 — Gestión económica": cobros de eventos e indumentaria.
- Sección 6.5, "Etapa 5 — Funcionalidades complementarias e integración": reportes, exportación, opciones para compartir comprobantes y funcionamiento sin conexión.

### Ajuste

Las siguientes funcionalidades se reprograman como evolución posterior del sistema y no forman parte de la entrega final:

| Funcionalidad | Requisitos | Reglas |
|---|---|---|
| Eventos e indumentaria, con sus pagos parciales | RF-26, RF-34 a RF-37 | RN-35, RN-38 a RN-43 |
| Exportación a Excel y reportes | RF-40 a RF-44 | |
| Compartir recibos por WhatsApp | RF-32 | |
| Funcionamiento sin conexión y sincronización | RF-53 a RF-57 | RNF-09 y RNF-10 |

Son 16 de los 57 requisitos funcionales. Registrar pagos (RF-24) y consultar su historial (RF-27) se limitan a las cuotas.

### Motivo

Se priorizan las funcionalidades centrales de gestión de alumnos, responsables, cuotas y pagos, tal como prevé la sección 6.7 de la Primera Entrega ante desvíos respecto de los tiempos previstos. Se prefiere entregar un circuito completo, probado y defendible antes que varios módulos incompletos.

### Cómo queda

Forman parte de la entrega final la autenticación, la gestión de alumnos y responsables, la búsqueda y el listado de alumnos, las cuotas, los pagos con sus recibos, el Dashboard (RF-45 a RF-48) y el Portal del Responsable (RF-12 a RF-14).

El esquema de la base de datos no cambia: conserva las tablas `evento`, `participacion` y `encargo`, y el estado de sincronización de los pagos, por lo que estas funcionalidades pueden incorporarse en un incremento posterior sin rediseñar el modelo. Los requisitos y las reglas de negocio tampoco se modifican, porque describen el sistema completo.

Las pantallas de estos módulos se quitan de la navegación, para que el sistema no ofrezca funciones que no están disponibles.
