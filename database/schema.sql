-- ============================================================
-- Sistema de Gestión Rojas FC
-- Esquema de base de datos relacional (MySQL)
-- Entrega 2 - Diseño y Módulos
--
-- Construido contra la documentación consolidada del equipo:
-- docs/requisitos/requisitos-funcionales.md,
-- docs/requisitos/requisitos-no-funcionales.md y
-- docs/reglas-negocio/reglas-de-negocio.md (los comentarios RN-XX
-- y RF-XX de este archivo refieren a esa numeración).
--
-- Requiere MySQL 8.0.16 o superior: los CONSTRAINT ... CHECK de
-- este archivo no se validan en versiones anteriores. Los bloques
-- DELIMITER de los triggers son un meta-comando del cliente mysql,
-- no SQL estándar: si el script se corre vía JDBC en modo batch en
-- vez del cliente de línea de comandos, hay que crearlos aparte.
-- ============================================================

CREATE DATABASE IF NOT EXISTS rojas_fc_gestion
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE rojas_fc_gestion;

-- ------------------------------------------------------------
-- Responsables (RF-06 a RF-10). Entidad de dominio pura: los
-- datos de acceso al portal (RF-11, RF-15) viven en `usuario`,
-- no acá -- ver esa tabla más abajo y la relación Responsable-
-- Usuario (0..1) en el diagrama. barrio, localidad, colegio y
-- email NO se incluyen porque ningún RF los pide para responsable.
-- ------------------------------------------------------------
CREATE TABLE responsable (
  id                      INT AUTO_INCREMENT PRIMARY KEY,
  nombre                  VARCHAR(100) NOT NULL,
  apellido                VARCHAR(100) NOT NULL,
  dni                     VARCHAR(20)  NOT NULL UNIQUE,
  telefono                VARCHAR(30)  NOT NULL,
  fecha_creacion          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- Categorías (RN-10, RF-16). Los triggers de abajo impiden que
-- dos categorías tengan rangos de año solapados: un CHECK no
-- puede comparar contra otras filas de la misma tabla, así que
-- la validación cruzada necesita trigger.
-- ------------------------------------------------------------
CREATE TABLE categoria (
  id                      INT AUTO_INCREMENT PRIMARY KEY,
  nombre                  VARCHAR(50) NOT NULL,
  anio_nacimiento_desde   YEAR        NOT NULL,
  anio_nacimiento_hasta   YEAR        NOT NULL,
  CONSTRAINT chk_categoria_anios CHECK (anio_nacimiento_desde <= anio_nacimiento_hasta)
);

DELIMITER $$

CREATE TRIGGER trg_categoria_solapamiento_ins
BEFORE INSERT ON categoria
FOR EACH ROW
BEGIN
  IF EXISTS (
    SELECT 1 FROM categoria c
    WHERE NEW.anio_nacimiento_desde <= c.anio_nacimiento_hasta
      AND c.anio_nacimiento_desde <= NEW.anio_nacimiento_hasta
  ) THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'El rango de anios se solapa con una categoria existente';
  END IF;
END$$

CREATE TRIGGER trg_categoria_solapamiento_upd
BEFORE UPDATE ON categoria
FOR EACH ROW
BEGIN
  IF EXISTS (
    SELECT 1 FROM categoria c
    WHERE c.id <> NEW.id
      AND NEW.anio_nacimiento_desde <= c.anio_nacimiento_hasta
      AND c.anio_nacimiento_desde <= NEW.anio_nacimiento_hasta
  ) THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'El rango de anios se solapa con una categoria existente';
  END IF;
END$$

DELIMITER ;

-- ------------------------------------------------------------
-- Usuario unificado (RF-58/RF-59, RNF-01): una sola identidad de
-- acceso para los tres perfiles (ADMINISTRADOR, COORDINADOR,
-- RESPONSABLE), en vez de credenciales separadas en `usuario` y
-- `responsable`. Administrador/Coordinador se identifican por
-- email (RF-58); Responsable se identifica con el DNI de su fila
-- en `responsable` (RF-11) -- por eso no se duplica el DNI acá,
-- se llega a él vía responsable_id. responsable_id es NULL y
-- UNIQUE: un responsable puede no tener todavía cuenta de acceso
-- (existe en el sistema sin haberse autenticado nunca), y cuando
-- la tiene, es una sola. El CHECK obliga a que cada fila tenga
-- exactamente los datos que corresponden a su rol.
-- ------------------------------------------------------------
CREATE TABLE usuario (
  id              INT AUTO_INCREMENT PRIMARY KEY,
  rol             ENUM('ADMINISTRADOR', 'COORDINADOR', 'RESPONSABLE') NOT NULL,
  nombre          VARCHAR(100)  NULL,
  email           VARCHAR(150)  NULL UNIQUE,
  password_hash   VARCHAR(255)  NOT NULL,
  activo          BOOLEAN       NOT NULL DEFAULT TRUE,
  fecha_creacion  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  responsable_id  INT           NULL UNIQUE,
  CONSTRAINT fk_usuario_responsable FOREIGN KEY (responsable_id) REFERENCES responsable(id),
  CONSTRAINT chk_usuario_datos_segun_rol CHECK (
    (rol = 'RESPONSABLE' AND responsable_id IS NOT NULL AND email IS NULL AND nombre IS NULL)
    OR
    (rol IN ('ADMINISTRADOR', 'COORDINADOR') AND responsable_id IS NULL AND email IS NOT NULL AND nombre IS NOT NULL)
  )
);

-- ------------------------------------------------------------
-- Alumnos (RF-01 a RF-05). dni UNIQUE resuelve RN-01/RN-02
-- (identificación y no duplicidad). barrio/localidad/colegio son
-- texto libre: ningún RN pide normalizarlos en catálogos propios.
-- ------------------------------------------------------------
CREATE TABLE alumno (
  id                 INT           AUTO_INCREMENT PRIMARY KEY,
  nombre             VARCHAR(100)  NOT NULL,
  apellido           VARCHAR(100)  NOT NULL,
  dni                VARCHAR(20)   NOT NULL UNIQUE,
  fecha_nacimiento   DATE          NOT NULL,
  domicilio          VARCHAR(255),
  barrio             VARCHAR(100),
  localidad          VARCHAR(100),
  colegio            VARCHAR(150),
  categoria_id       INT           NOT NULL,
  activo             BOOLEAN       NOT NULL DEFAULT TRUE,   -- baja lógica (RN-04)
  fecha_alta         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- alta/reactivacion mas reciente
  fecha_baja         DATETIME      NULL,                                -- baja mas reciente
  CONSTRAINT fk_alumno_categoria FOREIGN KEY (categoria_id) REFERENCES categoria(id)
);

-- ------------------------------------------------------------
-- Historial de altas y bajas (RF-51: consultar altas y bajas por
-- período). fecha_alta/fecha_baja en `alumno` solo guardan el
-- movimiento mas reciente; un alumno que se dio de baja y
-- reingreso mas de una vez (RN-03) pierde esa informacion si solo
-- se consulta `alumno`. Esta tabla registra cada transicion por
-- separado para poder reconstruir el historial completo. La
-- reactivacion (RN-03) se registra como un evento mas de tipo
-- ALTA sobre el mismo alumno_id, no como un tipo aparte.
-- ------------------------------------------------------------
CREATE TABLE alumno_movimiento (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  alumno_id   INT      NOT NULL,
  tipo        ENUM('ALTA', 'BAJA') NOT NULL,
  fecha       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_am_alumno FOREIGN KEY (alumno_id) REFERENCES alumno(id)
);

-- ------------------------------------------------------------
-- Relación N:M entre Alumno y Responsable (RF-09/RF-10).
-- vinculo es obligatorio por RN-09 (madre/padre/tutor, sin
-- jerarquía entre ellos según RN-06). El trigger de abajo aplica
-- RN-05: máximo 2 responsables por alumno.
-- ------------------------------------------------------------
CREATE TABLE alumno_responsable (
  alumno_id       INT NOT NULL,
  responsable_id  INT NOT NULL,
  vinculo         ENUM('MADRE', 'PADRE', 'TUTOR') NOT NULL,
  PRIMARY KEY (alumno_id, responsable_id),
  CONSTRAINT fk_ar_alumno      FOREIGN KEY (alumno_id)      REFERENCES alumno(id),
  CONSTRAINT fk_ar_responsable FOREIGN KEY (responsable_id) REFERENCES responsable(id)
);

DELIMITER $$

CREATE TRIGGER trg_alumno_responsable_maximo_ins
BEFORE INSERT ON alumno_responsable
FOR EACH ROW
BEGIN
  IF (SELECT COUNT(*) FROM alumno_responsable WHERE alumno_id = NEW.alumno_id) >= 2 THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'Un alumno no puede tener mas de dos responsables (RN-05)';
  END IF;
END$$

-- Cubre tambien el UPDATE: como alumno_id es parte de la PK
-- compuesta, se puede reasignar una fila existente a otro
-- alumno_id sin pasar por el trigger de INSERT.
CREATE TRIGGER trg_alumno_responsable_maximo_upd
BEFORE UPDATE ON alumno_responsable
FOR EACH ROW
BEGIN
  IF NEW.alumno_id <> OLD.alumno_id
     AND (SELECT COUNT(*) FROM alumno_responsable WHERE alumno_id = NEW.alumno_id) >= 2 THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'Un alumno no puede tener mas de dos responsables (RN-05)';
  END IF;
END$$

DELIMITER ;

-- ------------------------------------------------------------
-- Preinscripciones (RF-17 a RF-20, RN-11 a RN-14). Mismos campos
-- de alumno y responsable que sus tablas definitivas, ya que
-- ninguna se normaliza contra un catálogo (ver alumno). El
-- segundo responsable es opcional (RN-12); su vínculo también.
-- ------------------------------------------------------------
CREATE TABLE preinscripcion (
  id                        INT           AUTO_INCREMENT PRIMARY KEY,
  nombre_alumno             VARCHAR(100)  NOT NULL,
  apellido_alumno           VARCHAR(100)  NOT NULL,
  dni_alumno                VARCHAR(20)   NOT NULL,
  fecha_nacimiento_alumno   DATE          NOT NULL,
  domicilio_alumno          VARCHAR(255),
  barrio_alumno             VARCHAR(100),
  localidad_alumno          VARCHAR(100),
  colegio_alumno            VARCHAR(150),
  nombre_responsable        VARCHAR(100)  NOT NULL,
  apellido_responsable      VARCHAR(100)  NOT NULL,
  dni_responsable           VARCHAR(20)   NOT NULL,
  telefono_responsable      VARCHAR(30)   NOT NULL,
  vinculo_responsable       ENUM('MADRE', 'PADRE', 'TUTOR') NOT NULL,
  nombre_responsable_2      VARCHAR(100)  NULL,
  apellido_responsable_2    VARCHAR(100)  NULL,
  dni_responsable_2         VARCHAR(20)   NULL,
  telefono_responsable_2    VARCHAR(30)   NULL,
  vinculo_responsable_2     ENUM('MADRE', 'PADRE', 'TUTOR') NULL,
  estado                    ENUM('PENDIENTE', 'APROBADA', 'RECHAZADA') NOT NULL DEFAULT 'PENDIENTE',
  fecha_solicitud           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  usuario_revisor_id        INT           NULL,
  fecha_revision            DATETIME      NULL,
  alumno_id                 INT           NULL,  -- se completa al aprobarse
  CONSTRAINT fk_pre_usuario FOREIGN KEY (usuario_revisor_id) REFERENCES usuario(id),
  CONSTRAINT fk_pre_alumno  FOREIGN KEY (alumno_id)          REFERENCES alumno(id),
  CONSTRAINT uq_pre_alumno  UNIQUE (alumno_id)
);

-- ------------------------------------------------------------
-- Configuración de cuotas (RF-21, RF-23, RF-26). Histórica: cada
-- cambio genera un nuevo registro; RN-23 exige que las cuotas ya
-- generadas no se vean afectadas por cambios posteriores.
-- ------------------------------------------------------------
CREATE TABLE configuracion_cuota (
  id                    INT AUTO_INCREMENT PRIMARY KEY,
  importe               DECIMAL(10,2) NOT NULL,
  dia_vencimiento       TINYINT       NOT NULL,
  porcentaje_interes    DECIMAL(5,2)  NOT NULL,
  vigente_desde         DATE          NOT NULL,
  vigente_hasta         DATE          NULL,
  CONSTRAINT chk_cc_dia_vencimiento CHECK (dia_vencimiento BETWEEN 1 AND 31)
);

-- ------------------------------------------------------------
-- Cuotas (RF-22, RF-24, RF-25, RF-28 a RF-30; RN-15 a RN-24,
-- RN-26 a RN-28). Se pagan en su totalidad (RN-30): no tiene
-- saldo_pendiente, a diferencia de cobro_extraordinario. importe y
-- porcentaje_interes quedan congelados al generarse (RN-23).
-- interes_aplicado impide que el recargo se aplique más de una vez
-- (RN-21). "moroso" (RN-26 a RN-28) es un estado derivado -- un
-- alumno con al menos una cuota en estado VENCIDA -- no se guarda
-- como columna propia. El trigger de abajo aplica RN-25: una cuota
-- ya pagada no puede tener su importe modificado por una
-- corrección posterior (RF-25).
-- ------------------------------------------------------------
CREATE TABLE cuota (
  id                    INT AUTO_INCREMENT PRIMARY KEY,
  alumno_id             INT           NOT NULL,
  periodo               CHAR(7)       NOT NULL,  -- formato 'YYYY-MM'
  importe               DECIMAL(10,2) NOT NULL,
  fecha_vencimiento     DATE          NOT NULL,
  porcentaje_interes    DECIMAL(5,2)  NOT NULL,
  interes_aplicado      BOOLEAN       NOT NULL DEFAULT FALSE,  -- RN-21
  estado                ENUM('PENDIENTE', 'PAGADA', 'VENCIDA') NOT NULL DEFAULT 'PENDIENTE',
  CONSTRAINT fk_cuota_alumno FOREIGN KEY (alumno_id) REFERENCES alumno(id),
  CONSTRAINT uq_cuota_alumno_periodo UNIQUE (alumno_id, periodo)
);

DELIMITER $$

CREATE TRIGGER trg_cuota_protege_pagada
BEFORE UPDATE ON cuota
FOR EACH ROW
BEGIN
  IF OLD.estado = 'PAGADA' AND NEW.importe <> OLD.importe THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'No se puede modificar el importe de una cuota ya pagada (RN-25)';
  END IF;
END$$

DELIMITER ;

-- ------------------------------------------------------------
-- Cobros extraordinarios: eventos, indumentaria y matrícula
-- (RF-31, RF-41 a RF-44; RN-29, RN-40 a RN-44). Se modelan en una
-- sola tabla con `tipo`, no en tablas separadas: ningún RF pide un
-- catálogo reutilizable de eventos, solo registrar y consultar el
-- cobro asociado a un alumno con su concepto. talle y tipo_prenda
-- son obligatorios únicamente cuando tipo = 'INDUMENTARIA' (RN-43).
-- La matrícula (RN-29: "cobro vinculado al ingreso del alumno") se
-- registra acá como tipo = 'MATRICULA' en vez de en su propia
-- tabla, porque es la misma forma (un cobro puntual asociado a un
-- alumno, con importe y saldo) sin ningún atributo propio adicional
-- que justifique una tabla aparte. Admiten pagos parciales (RN-41
-- los define para eventos e indumentaria; para matrícula no hay
-- una regla que lo pida ni que lo prohíba, así que queda permitido
-- por consistencia de la tabla, a confirmar con el equipo). Por
-- eso esta tabla sí tiene saldo_pendiente, a diferencia de cuota.
-- ------------------------------------------------------------
CREATE TABLE cobro_extraordinario (
  id                INT AUTO_INCREMENT PRIMARY KEY,
  alumno_id         INT           NOT NULL,
  tipo              ENUM('EVENTO', 'INDUMENTARIA', 'MATRICULA') NOT NULL,
  concepto          VARCHAR(255)  NOT NULL,
  tipo_prenda       VARCHAR(50)   NULL,
  talle             VARCHAR(10)   NULL,
  importe_total     DECIMAL(10,2) NOT NULL,
  saldo_pendiente   DECIMAL(10,2) NOT NULL,
  CONSTRAINT fk_coex_alumno FOREIGN KEY (alumno_id) REFERENCES alumno(id),
  CONSTRAINT chk_coex_importe_positivo CHECK (importe_total > 0),
  CONSTRAINT chk_coex_indumentaria_datos CHECK (
    tipo <> 'INDUMENTARIA' OR (tipo_prenda IS NOT NULL AND talle IS NOT NULL)
  )
);

-- ------------------------------------------------------------
-- Pagos (RF-32 a RF-35; RN-30 a RN-35). Referencia a exactamente
-- UN concepto: cuota o cobro_extraordinario (RN-30 ya excluye el
-- pago parcial de cuotas, así que cuota_id nunca convive con un
-- saldo). medio_pago es opcional (RN-32: "podrá omitirse cuando la
-- administración no disponga de esa información"). motivo_anulacion
-- es obligatorio al anular (RN-34). cuota_id_activo es una columna
-- generada que solo replica cuota_id cuando anulado=FALSE: al ser
-- la única columna con UNIQUE, impide dos pagos válidos (no
-- anulados) para la misma cuota, sin bloquear que existan varios
-- pagos anulados históricos para esa misma cuota (mismo patrón que
-- dni_alumno_pendiente en preinscripcion).
-- ------------------------------------------------------------
CREATE TABLE pago (
  id                      INT           AUTO_INCREMENT PRIMARY KEY,
  cuota_id                INT           NULL,
  cobro_extraordinario_id INT           NULL,
  monto                   DECIMAL(10,2) NOT NULL,
  medio_pago              ENUM('EFECTIVO', 'TRANSFERENCIA') NULL,
  fecha_pago              DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  usuario_id              INT           NOT NULL,
  anulado                 BOOLEAN       NOT NULL DEFAULT FALSE,
  motivo_anulacion        VARCHAR(255)  NULL,
  fecha_anulacion         DATETIME      NULL,
  usuario_anulador_id     INT           NULL,
  cuota_id_activo         INT AS (CASE WHEN anulado = FALSE THEN cuota_id ELSE NULL END) STORED,
  CONSTRAINT fk_pago_cuota      FOREIGN KEY (cuota_id)                REFERENCES cuota(id),
  CONSTRAINT fk_pago_coex       FOREIGN KEY (cobro_extraordinario_id) REFERENCES cobro_extraordinario(id),
  CONSTRAINT fk_pago_usuario    FOREIGN KEY (usuario_id)              REFERENCES usuario(id),
  CONSTRAINT fk_pago_anulador   FOREIGN KEY (usuario_anulador_id)     REFERENCES usuario(id),
  CONSTRAINT chk_pago_un_concepto CHECK (
    (cuota_id IS NOT NULL) + (cobro_extraordinario_id IS NOT NULL) = 1
  ),
  CONSTRAINT chk_pago_monto_positivo CHECK (monto > 0),
  CONSTRAINT chk_pago_anulacion_con_motivo CHECK (
    anulado = FALSE OR motivo_anulacion IS NOT NULL
  ),
  CONSTRAINT uq_pago_cuota_activo UNIQUE (cuota_id_activo)
);

-- ------------------------------------------------------------
-- Recibos digitales (RF-36 a RF-40; RN-36 a RN-39). Uno por pago
-- confirmado. saldo_pendiente_informado guarda el saldo que el
-- recibo mostró en el momento de emitirse (RN-38): solo aplica a
-- pagos parciales de cobros extraordinarios, por eso es NULL en
-- el resto de los casos.
-- ------------------------------------------------------------
CREATE TABLE recibo (
  id                         INT           AUTO_INCREMENT PRIMARY KEY,
  pago_id                    INT           NOT NULL UNIQUE,
  numero                     VARCHAR(30)   NOT NULL UNIQUE,
  fecha_emision              DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  saldo_pendiente_informado  DECIMAL(10,2) NULL,
  archivo_url                VARCHAR(255),
  anulado                    BOOLEAN       NOT NULL DEFAULT FALSE,  -- RN-39
  CONSTRAINT fk_recibo_pago FOREIGN KEY (pago_id) REFERENCES pago(id)
);

-- ------------------------------------------------------------
-- Constancias de pago offline (RF-60 a RF-64). Se cargan sin
-- conexión y no generan recibo hasta sincronizarse: por eso
-- pago_id queda NULL hasta ese momento. Mismo patrón de "concepto
-- único" que pago, pero con solo dos opciones. pago_id es UNIQUE
-- porque cada pago sincronizado proviene de una única constancia.
-- ------------------------------------------------------------
CREATE TABLE constancia_offline (
  id                       INT           AUTO_INCREMENT PRIMARY KEY,
  alumno_id                INT           NOT NULL,
  cuota_id                 INT           NULL,
  cobro_extraordinario_id  INT           NULL,
  importe                  DECIMAL(10,2) NOT NULL,
  medio_pago               ENUM('EFECTIVO', 'TRANSFERENCIA') NULL,
  fecha_hora_registro      DATETIME      NOT NULL,
  estado                   ENUM('PENDIENTE_SINCRONIZACION', 'SINCRONIZADA', 'CONFLICTO') NOT NULL DEFAULT 'PENDIENTE_SINCRONIZACION',
  pago_id                  INT           NULL,
  observacion_conflicto    TEXT          NULL,
  usuario_id               INT           NOT NULL,
  CONSTRAINT fk_co_alumno   FOREIGN KEY (alumno_id)               REFERENCES alumno(id),
  CONSTRAINT fk_co_cuota    FOREIGN KEY (cuota_id)                REFERENCES cuota(id),
  CONSTRAINT fk_co_coex     FOREIGN KEY (cobro_extraordinario_id) REFERENCES cobro_extraordinario(id),
  CONSTRAINT fk_co_pago     FOREIGN KEY (pago_id)                 REFERENCES pago(id),
  CONSTRAINT fk_co_usuario  FOREIGN KEY (usuario_id)              REFERENCES usuario(id),
  CONSTRAINT uq_co_pago     UNIQUE (pago_id),
  CONSTRAINT chk_co_un_concepto CHECK (
    (cuota_id IS NOT NULL) + (cobro_extraordinario_id IS NOT NULL) = 1
  ),
  CONSTRAINT chk_co_importe_positivo CHECK (importe > 0)
);

-- ------------------------------------------------------------
-- Auditoría (RNF-12: trazabilidad de operaciones relevantes). No
-- corresponde a un módulo funcional propio -- ningún RF pide una
-- pantalla para consultarla -- pero la tabla existe para dar
-- soporte a ese requisito no funcional.
-- ------------------------------------------------------------
CREATE TABLE auditoria (
  id            INT          AUTO_INCREMENT PRIMARY KEY,
  usuario_id    INT          NOT NULL,
  accion        VARCHAR(100) NOT NULL,
  entidad       VARCHAR(50)  NOT NULL,
  entidad_id    INT          NOT NULL,
  detalle       TEXT,
  fecha         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_auditoria_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- ------------------------------------------------------------
-- Índices adicionales (issue #15). Las columnas de PK/FK/UNIQUE ya
-- quedan indexadas automáticamente por esas restricciones -- por
-- ejemplo cuota.alumno_id (FK) y uq_cuota_alumno_periodo ya cubren
-- "cuotas de un alumno por período" (RF-28) sin necesitar nada
-- nuevo acá, y lo mismo el historial de pagos de un alumno (RF-34):
-- se llega por join a través de cuota/cobro_extraordinario, ambos
-- ya indexados por sus FK, sin que pago necesite una columna ni un
-- índice propio hacia alumno.
--
-- idx_cuota_estado es compuesto (no solo `estado`) a propósito:
-- así cubre por completo la consulta de morosidad (RN-26: alumnos
-- con alguna cuota VENCIDA) sin tener que ir a buscar la fila
-- completa de cuota, solo leer del índice.
--
-- No se indexan barrio/localidad/colegio (RF-46) ni tipo en
-- cobro_extraordinario: son filtros sobre una tabla de pocas filas
-- (la escuela tiene ~140 alumnos activos, no miles), donde un
-- índice más agrega costo de escritura sin mejora real de lectura.
-- ------------------------------------------------------------
CREATE INDEX idx_alumno_activo           ON alumno(activo);
CREATE INDEX idx_alumno_apellido_nombre  ON alumno(apellido, nombre);        -- RF-45: buscar por nombre/apellido
CREATE INDEX idx_cuota_estado            ON cuota(estado, alumno_id);        -- RF-30/RN-26: morosidad, indice cubriente
CREATE INDEX idx_cuota_fecha_vencimiento ON cuota(fecha_vencimiento);
CREATE INDEX idx_pago_fecha_pago         ON pago(fecha_pago);
CREATE INDEX idx_am_fecha                ON alumno_movimiento(fecha);
CREATE INDEX idx_preinscripcion_estado   ON preinscripcion(estado);          -- RF-18/RF-55: preinscripciones pendientes
