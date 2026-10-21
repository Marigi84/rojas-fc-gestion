# Diseño de Módulos y Modelo de Datos

**2ª Entrega — Trabajo Final Integrador**
Tecnicatura Universitaria en Programación (UTN)

Sistema de Gestión Rojas FC

**Alumnos:** Marina Giselle Cordero, Silvia Giardini, Alex Pedro Dauria

**Tutor:** Sebastián Bruselario

**Estado: entregada para revisión del tutor.**

---

## 1. Objetivo

Esta entrega consolida el diseño funcional y de datos del sistema: los módulos, el modelo
conceptual y su DER, y el esquema relacional. Define el diseño sobre el que se implementará el
sistema; la estructura base del frontend y del backend se prepara en paralelo y se detalla en la
sección 6.

## 2. Alcance revisado

Respecto de la Primera Entrega se aplicaron dos ajustes de alcance
([detalle](alcance/revision-del-alcance.md)):

- **Preinscripción en línea:** se posterga como evolución posterior. La inscripción se cubre con el
  alta de alumno junto a su responsable (RF-01, RF-06).
- **Vencimiento único por período:** cada período tiene una sola fecha de vencimiento, en lugar de
  varios (RF-19, RN-18).

## 3. Módulos funcionales

El sistema se organiza en **9 módulos**, que cubren la totalidad de los 57 requisitos funcionales:

1. Autenticación y Autorización.
2. Alumnos, Responsables y Categorías.
3. Portal del Responsable.
4. Cuotas.
5. Pagos y Recibos.
6. Eventos e Indumentaria.
7. Búsquedas, Listados, Exportación y Reportes.
8. Dashboard.
9. Funcionamiento sin Conexión y Sincronización.

Detalle y requisitos cubiertos en [propuesta-modulos.md](modulos/propuesta-modulos.md).

## 4. Modelo de datos

**Modelo conceptual y DER** (notación Chen): [entidades-relaciones-cardinalidad/](entidades-relaciones-cardinalidad/). Entidades
principales: Persona (con los roles Alumno, Responsable y Administrador), Permanencia, Período,
Cuota, Evento, Participación, Encargo, Pago, Medio de pago y Recibo.

**Esquema relacional**: [schema.sql](../database/schema.sql) — 14 tablas. Las claves y restricciones de integridad están en
[claves-restricciones.md](esquema-relacional/claves-restricciones.md).
**Índices**: criterios de indexación y rendimiento de las consultas principales
(`docs/esquema-relacional/indices.md`).

## 5. Coherencia

Se verificó la correspondencia entre módulos, DER, esquema, requisitos y reglas de negocio: los
9 módulos cubren los 57 RF, y las reglas de negocio se reflejan en las restricciones del esquema
(por ejemplo, RN-01 en la clave primaria `persona.dni`, RN-29 en la unicidad
`pago(dni_alumno, periodo, vigente)` y RN-26/RN-34 en las claves artificiales de `pago` y `recibo`;
RNF-11 en las claves de trazabilidad del administrador).

## 6. Arquitectura

- **Arquitectura inicial del sistema** (stack, componentes y criterios de simplicidad):
[arquitectura-inicial.md](arquitectura/arquitectura-inicial.md).

- **Estructura base del código:** [`backend/`](../backend/) (proyecto Spring Boot) y
  [`frontend/`](../frontend/) (proyecto React + TypeScript + Vite).

## Anexos

- Requisitos funcionales y no funcionales: [requisitos/](requisitos/)
- Reglas de negocio: [reglas-de-negocio.md](reglas-negocio/reglas-de-negocio.md)
- Revisión del alcance: [revision-del-alcance.md](alcance/revision-del-alcance.md)
- Módulos: [propuesta-modulos.md](modulos/propuesta-modulos.md)
- Modelo conceptual y DER: [entidades-relaciones-cardinalidad/](entidades-relaciones-cardinalidad/)
- Esquema relacional: [schema.sql](../database/schema.sql)
- Claves y restricciones: [claves-restricciones.md](esquema-relacional/claves-restricciones.md)
- Arquitectura: [arquitectura-inicial.md](arquitectura/arquitectura-inicial.md)
