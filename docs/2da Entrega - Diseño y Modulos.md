# Diseño de Base de Datos — Sistema de Gestión Rojas FC

**2ª Entrega — Trabajo Final Integrador**
Tecnicatura Universitaria en Programación (UTN)

El listado de módulos vive en [`docs/modulos/propuesta-modulos.md`](modulos/propuesta-modulos.md). Este documento cubre solo el modelado de entidades y el esquema de base de datos, construidos contra [`docs/requisitos/requisitos-funcionales.md`](requisitos/requisitos-funcionales.md), [`docs/requisitos/requisitos-no-funcionales.md`](requisitos/requisitos-no-funcionales.md) y [`docs/reglas-negocio/reglas-de-negocio.md`](reglas-negocio/reglas-de-negocio.md).

---

## 1. Esquema de base de datos

Modelo relacional (MySQL). El script DDL completo se encuentra en [`database/schema.sql`](../database/schema.sql).

```mermaid
erDiagram
  CATEGORIA ||--o{ ALUMNO : agrupa
  RESPONSABLE ||--o{ ALUMNO_RESPONSABLE : tiene
  ALUMNO ||--o{ ALUMNO_RESPONSABLE : tiene
  ALUMNO o|--o| PREINSCRIPCION : origina
  USUARIO o|--o{ PREINSCRIPCION : revisa
  ALUMNO ||--o{ CUOTA : genera
  ALUMNO ||--o{ COBRO_EXTRAORDINARIO : registra
  CUOTA o|--o{ PAGO : recibe
  COBRO_EXTRAORDINARIO o|--o{ PAGO : recibe
  USUARIO ||--o{ PAGO : registra
  USUARIO o|--o{ PAGO : anula
  PAGO ||--o| RECIBO : genera
  ALUMNO ||--o{ CONSTANCIA_OFFLINE : origina
  CUOTA o|--o{ CONSTANCIA_OFFLINE : recibe
  COBRO_EXTRAORDINARIO o|--o{ CONSTANCIA_OFFLINE : recibe
  USUARIO ||--o{ CONSTANCIA_OFFLINE : carga
  PAGO o|--o| CONSTANCIA_OFFLINE : sincroniza
  USUARIO ||--o{ AUDITORIA : realiza

  USUARIO {
    int id PK
    string nombre
    string email
    string rol
  }
  CATEGORIA {
    int id PK
    string nombre
    int anio_nacimiento_desde
    int anio_nacimiento_hasta
  }
  RESPONSABLE {
    int id PK
    string nombre
    string dni
    string telefono
  }
  ALUMNO {
    int id PK
    string nombre
    string dni
    date fecha_nacimiento
    string domicilio
    string barrio
    string localidad
    string colegio
    int categoria_id FK
    boolean activo
  }
  ALUMNO_RESPONSABLE {
    int alumno_id FK
    int responsable_id FK
    string vinculo
  }
  PREINSCRIPCION {
    int id PK
    string dni_alumno
    string estado
    int usuario_revisor_id FK
    int alumno_id FK
  }
  CONFIGURACION_CUOTA {
    int id PK
    decimal importe
    int dia_vencimiento
    decimal porcentaje_interes
    date vigente_desde
    date vigente_hasta
  }
  CUOTA {
    int id PK
    int alumno_id FK
    string periodo
    decimal importe
    string estado
  }
  COBRO_EXTRAORDINARIO {
    int id PK
    int alumno_id FK
    string tipo
    string concepto
    string talle
    decimal saldo_pendiente
  }
  PAGO {
    int id PK
    int cuota_id FK
    int cobro_extraordinario_id FK
    decimal monto
    string medio_pago
    int usuario_id FK
    boolean anulado
  }
  RECIBO {
    int id PK
    int pago_id FK
    string numero
    decimal saldo_pendiente_informado
  }
  CONSTANCIA_OFFLINE {
    int id PK
    int alumno_id FK
    int pago_id FK
    string estado
  }
  AUDITORIA {
    int id PK
    int usuario_id FK
    string accion
    string entidad
  }
```

### Decisiones de diseño relevantes

- **Sin catálogos de procedencia:** `barrio`, `localidad` y `colegio` son texto libre en `alumno` y en `preinscripcion`, porque ningún requisito o regla de negocio pide normalizarlos en tablas propias.
- **`cobro_extraordinario` unifica evento, indumentaria y matrícula:** una sola tabla con `tipo` (`EVENTO`/`INDUMENTARIA`/`MATRICULA`) en vez de tablas separadas. Ningún RF pide un catálogo reutilizable de eventos — solo registrar y consultar el cobro asociado a un alumno con su concepto — y la matrícula (RN-29) tiene exactamente la misma forma (cobro puntual, importe, saldo), así que se modela ahí en vez de en su propia tabla.
- **Máximo 2 responsables por alumno (RN-05):** `alumno_responsable` tiene un trigger en `INSERT` y otro en `UPDATE` que rechazan el tercer responsable (el de `UPDATE` cubre el caso de reasignar una fila existente a otro alumno). `vinculo` (madre/padre/tutor) es obligatorio por RN-09; no hay jerarquía entre responsables (RN-06), por eso no hay un flag de "principal".
- **Cuotas sin pago parcial (RN-30):** `cuota` no tiene `saldo_pendiente`, a diferencia de `cobro_extraordinario`, que sí admite pagos parciales (RN-41) y por eso lo tiene.
- **Cuota pagada, protegida (RN-25):** un trigger rechaza modificar el `importe` de una cuota cuyo estado ya es `PAGADA`.
- **`medio_pago` opcional (RN-32):** no es obligatorio — se puede omitir cuando la administración no dispone de ese dato.
- **Anulación exige motivo (RN-34):** `pago.motivo_anulacion` es obligatorio cuando `anulado = TRUE`, validado con `CHECK`.
- **Recibo informa saldo solo en extraordinarios parciales (RN-38):** `recibo.saldo_pendiente_informado` es `NULL` salvo cuando el pago corresponde a un cobro extraordinario abonado parcialmente.
- **Categorías sin solapamiento:** dos triggers (`BEFORE INSERT`/`BEFORE UPDATE` sobre `categoria`) rechazan un rango de año que se superponga con el de otra categoría existente — un `CHECK` no puede comparar contra otras filas de la misma tabla.
- **Pagos con concepto único:** `pago` y `constancia_offline` referencian exactamente un concepto (`cuota` o `cobro_extraordinario`) mediante `CHECK`.
- **Baja lógica de alumnos:** `alumno.activo` + `fecha_baja`, en vez de eliminar filas, así se conserva el historial de cuotas y pagos.
- **Sin módulo de auditoría, con tabla de auditoría:** `auditoria` existe para dar soporte a RNF-12 (trazabilidad), aunque no hay un módulo funcional propio con pantalla.
- **Requisito de motor:** MySQL 8.0.16 o superior — los `CHECK` no se validan en versiones anteriores. Los triggers usan el meta-comando `DELIMITER`, propio del cliente `mysql`: si el script se ejecuta vía JDBC en modo batch hay que crearlos aparte.
