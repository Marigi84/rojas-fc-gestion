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
conceptual y su DER, y el esquema relacional. No incluye código; define el diseño sobre el que
se implementará el sistema.

## 2. Módulos funcionales

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

Detalle y requisitos cubiertos en `docs/modulos/propuesta-modulos.md`.

## 3. Modelo de datos

**Modelo conceptual y DER** (notación Chen): `docs/entidades-relaciones-cardinalidad/`. Entidades
principales: Persona (con los roles Alumno, Responsable y Administrador), Permanencia, Período,
Cuota, Evento, Participación, Encargo, Pago, Medio de pago y Recibo.

**Esquema relacional**: `database/schema.sql` — 14 tablas, Claves y restricciones de integridad en
`docs/esquema-relacional/claves-restricciones.md`.

## 4. Coherencia

Se verificó la correspondencia entre módulos, DER, esquema, requisitos y reglas de negocio: los
9 módulos cubren los 57 RF, y las reglas de negocio se reflejan en las restricciones del esquema
(por ejemplo, RN-26/RN-34 en las claves artificiales de `pago` y `recibo`, RN-18 en las
restricciones del período y RNF-11 en las claves de trazabilidad del administrador).

## 5. Arquitectura

Arquitectura inicial del sistema (stack, componentes y criterios de simplicidad):
`docs/arquitectura/arquitectura-inicial.md`.

## Anexos

- Requisitos funcionales y no funcionales: `docs/requisitos/`
- Reglas de negocio: `docs/reglas-negocio/reglas-de-negocio.md`
- Revisión del alcance: `docs/alcance/revision-del-alcance.md`
- Módulos: `docs/modulos/propuesta-modulos.md`
- Modelo conceptual y DER: `docs/entidades-relaciones-cardinalidad/`
- Esquema relacional: `database/schema.sql`
- Claves y restricciones: `docs/esquema-relacional/claves-restricciones.md`
- Arquitectura: `docs/arquitectura/arquitectura-inicial.md`
