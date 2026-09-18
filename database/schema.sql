-- ============================================================
-- Sistema de Gestión Rojas FC
-- Esquema de base de datos relacional (MySQL)
-- Entrega 2 - Diseño y Módulos
--
-- Diagrama entidad-relación y listado de módulos en
-- "docs/2da Entrega - Diseño y Modulos.md". Reglas de negocio que
-- sustentan este diseño en "docs/2da Entrega - Reglas de Negocio.md"
-- (los comentarios RN-XX de este archivo refieren a esa numeración).
--
-- Clave primaria, tabla por tabla: se usa id autoincremental en
-- las entidades con muchas tablas dependientes (usuario, alumno,
-- responsable, cuota, pago, etc.), donde un candidato natural
-- (dni, email) podria corregirse con el tiempo y forzaria propagar
-- ese cambio a cada fila que lo referencia. barrio/localidad/colegio
-- son catalogos hoja sin ese problema (nombre ya era UNIQUE y nada
-- mas los referencia salvo alumno), asi que usan el nombre como PK
-- en vez de sumar un id que no aportaria nada.
--
-- Requiere MySQL 8.0.16 o superior: los CONSTRAINT ... CHECK de
-- este archivo (chk_pago_un_concepto, chk_co_un_concepto, etc.) no
-- se validan en versiones anteriores (se aceptan pero se ignoran).
-- ============================================================

CREATE DATABASE IF NOT EXISTS rojas_fc_gestion
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE rojas_fc_gestion;

-- ------------------------------------------------------------
-- Usuarios administradores del sistema.
-- El rol "RESPONSABLE" NO es un valor de esta tabla: se implementa
-- como login propio en la tabla `responsable` (ver mas abajo), ya
-- que son actores con acceso muy distinto. Es una decision de
-- diseño, no responde a ninguna regla de negocio puntual.
-- ------------------------------------------------------------
CREATE TABLE usuario (
  id              INT AUTO_INCREMENT PRIMARY KEY,
  nombre          VARCHAR(100)  NOT NULL,
  email           VARCHAR(150)  NOT NULL UNIQUE,
  password_hash   VARCHAR(255)  NOT NULL,
  rol             ENUM('ADMINISTRADOR') NOT NULL DEFAULT 'ADMINISTRADOR',
  debe_cambiar_password BOOLEAN NOT NULL DEFAULT FALSE,
  activo          BOOLEAN       NOT NULL DEFAULT TRUE,
  fecha_creacion  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- Catalogos normalizados de procedencia del alumno.
-- Usan el nombre como PK natural (en vez de un id autoincremental):
-- son catalogos "hoja", sin atributos propios mas alla del nombre,
-- que ya era UNIQUE, y nada mas que `alumno` los referencia. Un id
-- surrogate no aportaria nada aqui.
-- ------------------------------------------------------------
CREATE TABLE barrio (
  nombre  VARCHAR(100) PRIMARY KEY
);

CREATE TABLE localidad (
  nombre  VARCHAR(100) PRIMARY KEY
);

CREATE TABLE colegio (
  nombre  VARCHAR(150) PRIMARY KEY
);

-- ------------------------------------------------------------
-- Responsables (adultos a cargo de uno o más alumnos).
-- Tiene su propio login para el portal: el DNI funciona como
-- nombre de usuario (ya es UNIQUE, ver RN-06) y la contraseña se
-- guarda hasheada. portal_habilitado distingue a un responsable ya
-- registrado y validado de uno cargado sin acceso aun. El login en
-- si es una decision de diseño (portal de responsables), no una
-- regla de negocio puntual.
-- ------------------------------------------------------------
CREATE TABLE responsable (
  id                      INT AUTO_INCREMENT PRIMARY KEY,
  nombre                  VARCHAR(100) NOT NULL,
  apellido                VARCHAR(100) NOT NULL,
  dni                     VARCHAR(20)  NOT NULL UNIQUE,
  telefono                VARCHAR(30),
  email                   VARCHAR(150),
  password_hash           VARCHAR(255) NULL,
  portal_habilitado       BOOLEAN      NOT NULL DEFAULT FALSE,
  debe_cambiar_password   BOOLEAN      NOT NULL DEFAULT FALSE,
  fecha_creacion          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- Categorías (determinadas por año de nacimiento).
-- Los triggers de abajo impiden que dos categorías tengan rangos de
-- año solapados: un CHECK de MySQL no puede comparar contra otras
-- filas de la misma tabla, así que la validación cruzada necesita
-- trigger. Sin esto, la asignación automática por año de nacimiento
-- (RN-14) podría volverse ambigua.
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
-- Alumnos (entidad central del dominio).
-- dni agregado por RN-04/RN-06 (no duplicidad, DNI como identificador).
-- barrio/localidad/colegio normalizados via catalogo para que las
-- estadisticas de procedencia (RN-11) sean consistentes.
-- ------------------------------------------------------------
CREATE TABLE alumno (
  id                 INT AUTO_INCREMENT PRIMARY KEY,
  nombre             VARCHAR(100) NOT NULL,
  apellido           VARCHAR(100) NOT NULL,
  dni                VARCHAR(20)  NOT NULL UNIQUE,
  fecha_nacimiento   DATE         NOT NULL,
  categoria_id       INT          NOT NULL,
  barrio             VARCHAR(100) NULL,
  localidad          VARCHAR(100) NULL,
  colegio            VARCHAR(150) NULL,
  activo             BOOLEAN      NOT NULL DEFAULT TRUE,   -- baja lógica
  fecha_alta         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_baja         DATETIME     NULL,
  CONSTRAINT fk_alumno_categoria  FOREIGN KEY (categoria_id) REFERENCES categoria(id),
  CONSTRAINT fk_alumno_barrio     FOREIGN KEY (barrio)       REFERENCES barrio(nombre),
  CONSTRAINT fk_alumno_localidad  FOREIGN KEY (localidad)    REFERENCES localidad(nombre),
  CONSTRAINT fk_alumno_colegio    FOREIGN KEY (colegio)      REFERENCES colegio(nombre)
);

-- ------------------------------------------------------------
-- Relación N:M entre Alumno y Responsable
-- (un alumno puede tener uno o más responsables)
-- ------------------------------------------------------------
CREATE TABLE alumno_responsable (
  alumno_id       INT NOT NULL,
  responsable_id  INT NOT NULL,
  PRIMARY KEY (alumno_id, responsable_id),
  CONSTRAINT fk_ar_alumno      FOREIGN KEY (alumno_id)      REFERENCES alumno(id),
  CONSTRAINT fk_ar_responsable FOREIGN KEY (responsable_id) REFERENCES responsable(id)
);

-- ------------------------------------------------------------
-- Preinscripciones (formulario propio, previo a alumno activo).
-- dni_alumno agregado por RN-06/RN-08 (DNI como identificador,
-- unicidad de solicitudes pendientes). barrio/localidad/colegio se
-- registran como texto libre (tal como los carga la familia, ver
-- RN-11) y se normalizan hacia los catálogos recién al aprobarse.
-- dni_alumno_pendiente es una columna generada que solo replica
-- el DNI cuando estado='PENDIENTE': al ser la única columna con
-- UNIQUE, MySQL impide dos filas PENDIENTE con el mismo DNI de
-- alumno (RN-08) sin bloquear DNIs ya aprobados o rechazados.
-- ------------------------------------------------------------
CREATE TABLE preinscripcion (
  id                        INT AUTO_INCREMENT PRIMARY KEY,
  nombre_alumno             VARCHAR(100) NOT NULL,
  apellido_alumno           VARCHAR(100) NOT NULL,
  dni_alumno                VARCHAR(20)  NOT NULL,
  fecha_nacimiento_alumno   DATE         NOT NULL,
  barrio_nombre             VARCHAR(100),
  localidad_nombre          VARCHAR(100),
  colegio_nombre            VARCHAR(150),
  nombre_responsable        VARCHAR(100) NOT NULL,
  apellido_responsable      VARCHAR(100) NOT NULL,
  dni_responsable           VARCHAR(20)  NOT NULL,
  telefono_responsable      VARCHAR(30),
  email_responsable         VARCHAR(150),
  estado                    ENUM('PENDIENTE', 'APROBADA', 'RECHAZADA') NOT NULL DEFAULT 'PENDIENTE',
  fecha_solicitud           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  usuario_revisor_id        INT          NULL,
  fecha_revision            DATETIME     NULL,
  alumno_id                 INT          NULL,  -- se completa al aprobarse
  dni_alumno_pendiente VARCHAR(20) AS (CASE WHEN estado = 'PENDIENTE' THEN dni_alumno ELSE NULL END) STORED,
  CONSTRAINT fk_pre_usuario FOREIGN KEY (usuario_revisor_id) REFERENCES usuario(id),
  CONSTRAINT fk_pre_alumno  FOREIGN KEY (alumno_id)          REFERENCES alumno(id),
  CONSTRAINT uq_pre_alumno  UNIQUE (alumno_id),
  CONSTRAINT uq_pre_dni_pendiente UNIQUE (dni_alumno_pendiente)
);

-- ------------------------------------------------------------
-- Configuración de cuotas (histórica: cada cambio genera un
-- nuevo registro; las cuotas ya generadas no se ven afectadas)
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
-- Cuotas. Se abonan en su totalidad (aprobado en la 1ª Entrega,
-- confirmado tras revisión de reglas de negocio) -- por eso no
-- tienen saldo_pendiente, a diferencia de cobro_evento e
-- indumentaria. importe e interés quedan congelados al generarse.
-- estado incluye ANULADA para el caso de RN-20 (baja del alumno
-- con una cuota del período ya generada; el administrador decide
-- si corresponde anularla).
-- ------------------------------------------------------------
CREATE TABLE cuota (
  id                    INT AUTO_INCREMENT PRIMARY KEY,
  alumno_id             INT           NOT NULL,
  periodo               CHAR(7)       NOT NULL,  -- formato 'YYYY-MM'
  importe               DECIMAL(10,2) NOT NULL,
  fecha_vencimiento     DATE          NOT NULL,
  porcentaje_interes    DECIMAL(5,2)  NOT NULL,
  interes_aplicado      BOOLEAN       NOT NULL DEFAULT FALSE,  -- RN-27: el recargo no se acumula
  estado                ENUM('PENDIENTE', 'PAGADA', 'VENCIDA', 'ANULADA') NOT NULL DEFAULT 'PENDIENTE',
  CONSTRAINT fk_cuota_alumno FOREIGN KEY (alumno_id) REFERENCES alumno(id),
  CONSTRAINT uq_cuota_alumno_periodo UNIQUE (alumno_id, periodo)
);

-- ------------------------------------------------------------
-- Eventos deportivos
-- ------------------------------------------------------------
CREATE TABLE evento (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  nombre        VARCHAR(150) NOT NULL,
  fecha         DATE         NOT NULL,
  descripcion   VARCHAR(255)
);

-- ------------------------------------------------------------
-- Cobros asociados a eventos (admiten pagos parciales).
-- UNIQUE(alumno_id, evento_id): un alumno tiene a lo sumo un cobro
-- por evento. Si en algún momento se necesita cobrarle dos veces el
-- mismo evento al mismo alumno, esta restricción hay que sacarla
-- explícitamente (no es una regla de negocio confirmada, es la
-- opción más segura por default).
-- ------------------------------------------------------------
CREATE TABLE cobro_evento (
  id                INT AUTO_INCREMENT PRIMARY KEY,
  alumno_id         INT           NOT NULL,
  evento_id         INT           NOT NULL,
  importe_total     DECIMAL(10,2) NOT NULL,
  saldo_pendiente   DECIMAL(10,2) NOT NULL,
  CONSTRAINT fk_ce_alumno FOREIGN KEY (alumno_id) REFERENCES alumno(id),
  CONSTRAINT fk_ce_evento FOREIGN KEY (evento_id) REFERENCES evento(id),
  CONSTRAINT uq_ce_alumno_evento UNIQUE (alumno_id, evento_id)
);

-- ------------------------------------------------------------
-- Cobros de indumentaria por encargo (admiten pagos parciales).
-- talle es un dato propio del producto, no responde a ninguna
-- regla de negocio puntual.
-- ------------------------------------------------------------
CREATE TABLE indumentaria (
  id                INT AUTO_INCREMENT PRIMARY KEY,
  alumno_id         INT           NOT NULL,
  descripcion       VARCHAR(255)  NOT NULL,
  talle             VARCHAR(10),
  importe_total     DECIMAL(10,2) NOT NULL,
  saldo_pendiente   DECIMAL(10,2) NOT NULL,
  CONSTRAINT fk_ind_alumno FOREIGN KEY (alumno_id) REFERENCES alumno(id)
);

-- ------------------------------------------------------------
-- Pagos (referencia a exactamente UN concepto: cuota, cobro de
-- evento o indumentaria -- ver CHECK de concepto unico).
-- anulado/fecha_anulacion/usuario_anulador_id agregados por RN-35:
-- un pago incorrecto se anula, nunca se borra fisicamente.
-- ------------------------------------------------------------
CREATE TABLE pago (
  id                    INT AUTO_INCREMENT PRIMARY KEY,
  cuota_id              INT           NULL,
  cobro_evento_id       INT           NULL,
  indumentaria_id       INT           NULL,
  monto                 DECIMAL(10,2) NOT NULL,
  medio_pago            ENUM('EFECTIVO', 'TRANSFERENCIA') NOT NULL,
  fecha_pago            DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  usuario_id            INT           NOT NULL,
  anulado               BOOLEAN       NOT NULL DEFAULT FALSE,
  fecha_anulacion       DATETIME      NULL,
  usuario_anulador_id   INT           NULL,
  CONSTRAINT fk_pago_cuota      FOREIGN KEY (cuota_id)        REFERENCES cuota(id),
  CONSTRAINT fk_pago_evento     FOREIGN KEY (cobro_evento_id) REFERENCES cobro_evento(id),
  CONSTRAINT fk_pago_indum      FOREIGN KEY (indumentaria_id) REFERENCES indumentaria(id),
  CONSTRAINT fk_pago_usuario    FOREIGN KEY (usuario_id)      REFERENCES usuario(id),
  CONSTRAINT fk_pago_anulador   FOREIGN KEY (usuario_anulador_id) REFERENCES usuario(id),
  CONSTRAINT chk_pago_un_concepto CHECK (
    (cuota_id IS NOT NULL) + (cobro_evento_id IS NOT NULL) + (indumentaria_id IS NOT NULL) = 1
  ),
  CONSTRAINT chk_pago_monto_positivo CHECK (monto > 0)
);

-- ------------------------------------------------------------
-- Recibos digitales (uno por pago confirmado)
-- ------------------------------------------------------------
CREATE TABLE recibo (
  id              INT AUTO_INCREMENT PRIMARY KEY,
  pago_id         INT          NOT NULL UNIQUE,
  numero          VARCHAR(30)  NOT NULL UNIQUE,
  fecha_emision   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  archivo_url     VARCHAR(255),
  CONSTRAINT fk_recibo_pago FOREIGN KEY (pago_id) REFERENCES pago(id)
);

-- ------------------------------------------------------------
-- Constancias de pago offline (RN-39, RN-40). Se cargan sin
-- conexión y NO modifican la obligación ni generan recibo hasta
-- sincronizarse: por eso pago_id queda NULL hasta ese momento.
-- Mismo patron de "concepto unico" que la tabla pago. pago_id es
-- UNIQUE (igual que en recibo) porque cada pago sincronizado debe
-- provenir de una única constancia offline.
-- ------------------------------------------------------------
CREATE TABLE constancia_offline (
  id                      INT AUTO_INCREMENT PRIMARY KEY,
  alumno_id               INT           NOT NULL,
  cuota_id                INT           NULL,
  cobro_evento_id         INT           NULL,
  indumentaria_id         INT           NULL,
  importe                 DECIMAL(10,2) NOT NULL,
  medio_pago              ENUM('EFECTIVO', 'TRANSFERENCIA') NOT NULL,
  fecha_hora_registro     DATETIME      NOT NULL,
  estado                  ENUM('PENDIENTE_SINCRONIZACION', 'SINCRONIZADA', 'CONFLICTO') NOT NULL DEFAULT 'PENDIENTE_SINCRONIZACION',
  pago_id                 INT           NULL,  -- se completa al sincronizar OK (RN-40)
  observacion_conflicto   TEXT          NULL,  -- motivo si estado = CONFLICTO (detalle tecnico de sincronizacion, no una regla de negocio)
  usuario_id              INT           NOT NULL,
  CONSTRAINT fk_co_alumno   FOREIGN KEY (alumno_id)       REFERENCES alumno(id),
  CONSTRAINT fk_co_cuota    FOREIGN KEY (cuota_id)        REFERENCES cuota(id),
  CONSTRAINT fk_co_evento   FOREIGN KEY (cobro_evento_id) REFERENCES cobro_evento(id),
  CONSTRAINT fk_co_indum    FOREIGN KEY (indumentaria_id) REFERENCES indumentaria(id),
  CONSTRAINT fk_co_pago     FOREIGN KEY (pago_id)         REFERENCES pago(id),
  CONSTRAINT fk_co_usuario  FOREIGN KEY (usuario_id)      REFERENCES usuario(id),
  CONSTRAINT uq_co_pago     UNIQUE (pago_id),
  CONSTRAINT chk_co_un_concepto CHECK (
    (cuota_id IS NOT NULL) + (cobro_evento_id IS NOT NULL) + (indumentaria_id IS NOT NULL) = 1
  ),
  CONSTRAINT chk_co_importe_positivo CHECK (importe > 0)
);

-- ------------------------------------------------------------
-- Auditoría de operaciones relevantes
-- ------------------------------------------------------------
CREATE TABLE auditoria (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  usuario_id    INT          NOT NULL,
  accion        VARCHAR(100) NOT NULL,
  entidad       VARCHAR(50)  NOT NULL,
  entidad_id    INT          NOT NULL,
  detalle       TEXT,
  fecha         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_auditoria_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- ------------------------------------------------------------
-- Índices adicionales para las columnas que va a filtrar el módulo
-- de Reportes y Estadísticas (alumnos activos, deuda por estado y
-- vencimiento, recaudación por fecha). Las columnas de PK/FK/UNIQUE
-- ya quedan indexadas automáticamente por esas restricciones; estos
-- índices son aparte, sobre columnas que no tienen ninguna.
-- ------------------------------------------------------------
CREATE INDEX idx_alumno_activo         ON alumno(activo);
CREATE INDEX idx_cuota_estado          ON cuota(estado);
CREATE INDEX idx_cuota_fecha_vencimiento ON cuota(fecha_vencimiento);
CREATE INDEX idx_pago_fecha_pago       ON pago(fecha_pago);
