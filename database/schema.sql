-- Esquema relacional del Sistema de Gestión Rojas FC (MySQL 8)
-- Basado en el modelo conceptual (docs/entidades-relaciones-cardinalidad/modelo-conceptual.md).
-- Los atributos derivados del modelo conceptual no se almacenan: se calculan en las vistas.

-- =====================================================================
-- 1. Personas y roles (RN-01, RN-02, RN-04)
-- =====================================================================
CREATE TABLE persona (
  dni       INT UNSIGNED NOT NULL,
  nombre    VARCHAR(60)  NOT NULL,
  apellido  VARCHAR(60)  NOT NULL,
  PRIMARY KEY (dni)
);

CREATE TABLE alumno (
  dni               INT UNSIGNED  NOT NULL,
  fecha_nacimiento  DATE          NOT NULL,
  direccion         VARCHAR(120)  NOT NULL,
  PRIMARY KEY (dni),
  FOREIGN KEY (dni) REFERENCES persona (dni)
);

CREATE TABLE responsable (
  dni                  INT UNSIGNED NOT NULL,
  telefono             VARCHAR(20)  NOT NULL,
  contrasena_hash      VARCHAR(255) NOT NULL,
  contrasena_temporal  BOOLEAN      NOT NULL DEFAULT TRUE,
  PRIMARY KEY (dni),
  FOREIGN KEY (dni) REFERENCES persona (dni)
);

CREATE TABLE administrador (
  dni                  INT UNSIGNED NOT NULL,
  email                VARCHAR(120) NOT NULL,
  contrasena_hash      VARCHAR(255) NOT NULL,
  contrasena_temporal  BOOLEAN      NOT NULL DEFAULT TRUE,
  PRIMARY KEY (dni),
  UNIQUE (email),
  FOREIGN KEY (dni) REFERENCES persona (dni)
);

-- Responsables a cargo de cada alumno (RN-05, RN-10, RN-11, RN-12)
CREATE TABLE responsable_alumno (
  dni_responsable  INT UNSIGNED NOT NULL,
  dni_alumno       INT UNSIGNED NOT NULL,
  vinculo          ENUM('madre', 'padre', 'tutor', 'otro') NOT NULL,
  PRIMARY KEY (dni_responsable, dni_alumno),
  FOREIGN KEY (dni_responsable) REFERENCES responsable (dni),
  FOREIGN KEY (dni_alumno)      REFERENCES alumno (dni)
);

-- =====================================================================
-- 2. Permanencia del alumno (RN-06 a RN-09)
-- =====================================================================
CREATE TABLE permanencia (
  dni_alumno      INT UNSIGNED NOT NULL,
  fecha_alta      DATE         NOT NULL,
  fecha_baja      DATE         NULL,
  dni_admin_alta  INT UNSIGNED NOT NULL,
  dni_admin_baja  INT UNSIGNED NULL,
  vigente         TINYINT GENERATED ALWAYS AS
                    (IF(fecha_baja IS NULL, 1, NULL)) STORED,
  PRIMARY KEY (dni_alumno, fecha_alta),
  UNIQUE (dni_alumno, vigente),
  FOREIGN KEY (dni_alumno)     REFERENCES alumno (dni),
  FOREIGN KEY (dni_admin_alta) REFERENCES administrador (dni),
  FOREIGN KEY (dni_admin_baja) REFERENCES administrador (dni),
  CHECK (fecha_baja IS NULL OR fecha_baja >= fecha_alta),
  CHECK ((fecha_baja IS NULL) = (dni_admin_baja IS NULL))
);

-- =====================================================================
-- 3. Períodos y cuotas (RN-15 a RN-18, RN-21)
-- =====================================================================
CREATE TABLE periodo (
  periodo              DATE          NOT NULL,
  importe              DECIMAL(10,2) NOT NULL,
  fecha_vencimiento    DATE          NOT NULL,
  porcentaje_interes   DECIMAL(5,2)  NOT NULL,
  dni_admin            INT UNSIGNED  NOT NULL,
  fecha_configuracion  DATE          NOT NULL,
  PRIMARY KEY (periodo),
  FOREIGN KEY (dni_admin) REFERENCES administrador (dni),
  CHECK (DAY(periodo) = 1),
  CHECK (importe > 0),
  CHECK (porcentaje_interes BETWEEN 0 AND 100),
  CHECK (fecha_vencimiento >= periodo)
);

CREATE TABLE cuota (
  dni_alumno        INT UNSIGNED NOT NULL,
  periodo           DATE         NOT NULL,
  fecha_generacion  DATE         NOT NULL,
  PRIMARY KEY (dni_alumno, periodo),
  FOREIGN KEY (dni_alumno) REFERENCES alumno (dni),
  FOREIGN KEY (periodo)    REFERENCES periodo (periodo),
  CHECK (fecha_generacion >= periodo)
);

-- =====================================================================
-- 4. Eventos e indumentaria (RN-38, RN-40, RN-43)
-- =====================================================================
CREATE TABLE evento (
  nombre          VARCHAR(80)   NOT NULL,
  anio            YEAR          NOT NULL,
  importe         DECIMAL(10,2) NOT NULL,
  dni_admin       INT UNSIGNED  NOT NULL,
  fecha_registro  DATE          NOT NULL,
  PRIMARY KEY (nombre, anio),
  FOREIGN KEY (dni_admin) REFERENCES administrador (dni),
  CHECK (importe > 0)
);

CREATE TABLE participacion (
  dni_alumno         INT UNSIGNED NOT NULL,
  nombre_evento      VARCHAR(80)  NOT NULL,
  anio_evento        YEAR         NOT NULL,
  fecha_inscripcion  DATE         NOT NULL,
  PRIMARY KEY (dni_alumno, nombre_evento, anio_evento),
  FOREIGN KEY (dni_alumno) REFERENCES alumno (dni),
  FOREIGN KEY (nombre_evento, anio_evento) REFERENCES evento (nombre, anio)
);

CREATE TABLE encargo (
  dni_alumno   INT UNSIGNED  NOT NULL,
  fecha_hora   DATETIME      NOT NULL,
  monto        DECIMAL(10,2) NOT NULL,
  descripcion  VARCHAR(200)  NULL,
  dni_admin    INT UNSIGNED  NOT NULL,
  PRIMARY KEY (dni_alumno, fecha_hora),
  FOREIGN KEY (dni_alumno) REFERENCES alumno (dni),
  FOREIGN KEY (dni_admin)  REFERENCES administrador (dni),
  CHECK (monto > 0)
);

-- =====================================================================
-- 5. Pagos y recibos (RN-25 a RN-27, RN-29 a RN-31, RN-33, RN-34)
-- =====================================================================
CREATE TABLE medio_pago (
  nombre  VARCHAR(20) NOT NULL,
  PRIMARY KEY (nombre)
);
INSERT INTO medio_pago VALUES ('efectivo'), ('transferencia');

CREATE TABLE pago (
  id_pago             INT UNSIGNED  NOT NULL AUTO_INCREMENT,
  fecha               DATE          NOT NULL,
  monto               DECIMAL(10,2) NOT NULL,
  medio_pago          VARCHAR(20)   NOT NULL,
  dni_admin           INT UNSIGNED  NOT NULL,
  dni_alumno          INT UNSIGNED  NOT NULL,
  periodo             DATE          NULL,
  nombre_evento       VARCHAR(80)   NULL,
  anio_evento         YEAR          NULL,
  fecha_hora_encargo  DATETIME      NULL,
  estado_sincronizacion ENUM('confirmado', 'requiere_revision')
                        NOT NULL DEFAULT 'confirmado',
  fecha_anulacion     DATE          NULL,
  motivo_anulacion    VARCHAR(200)  NULL,
  dni_admin_anula     INT UNSIGNED  NULL,
  vigente_cuota       TINYINT GENERATED ALWAYS AS
    (IF(periodo IS NOT NULL AND fecha_anulacion IS NULL
        AND estado_sincronizacion = 'confirmado', 1, NULL)) STORED,
  PRIMARY KEY (id_pago),
  UNIQUE (dni_alumno, periodo, vigente_cuota),
  FOREIGN KEY (medio_pago)      REFERENCES medio_pago (nombre),
  FOREIGN KEY (dni_admin)       REFERENCES administrador (dni),
  FOREIGN KEY (dni_admin_anula) REFERENCES administrador (dni),
  FOREIGN KEY (dni_alumno, periodo) REFERENCES cuota (dni_alumno, periodo),
  FOREIGN KEY (dni_alumno, nombre_evento, anio_evento)
    REFERENCES participacion (dni_alumno, nombre_evento, anio_evento),
  FOREIGN KEY (dni_alumno, fecha_hora_encargo)
    REFERENCES encargo (dni_alumno, fecha_hora),
  CHECK (monto > 0),
  CHECK ((nombre_evento IS NULL) = (anio_evento IS NULL)),
  CHECK ((periodo IS NOT NULL) + (nombre_evento IS NOT NULL)
         + (fecha_hora_encargo IS NOT NULL) = 1),
  CHECK ((fecha_anulacion IS NULL) = (motivo_anulacion IS NULL)
     AND (fecha_anulacion IS NULL) = (dni_admin_anula IS NULL))
);

CREATE TABLE recibo (
  numero_recibo  INT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_pago        INT UNSIGNED NOT NULL,
  fecha_emision  DATE         NOT NULL,
  PRIMARY KEY (numero_recibo),
  UNIQUE (id_pago),
  FOREIGN KEY (id_pago) REFERENCES pago (id_pago)
);

-- =====================================================================
-- 6. Triggers: operaciones no permitidas
--    (RN-05, RN-22, RN-30, RN-36, RN-39, RF-10)
-- =====================================================================
DELIMITER //

CREATE TRIGGER pago_no_borrar BEFORE DELETE ON pago
FOR EACH ROW
  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Los pagos no se eliminan, se anulan';//

CREATE TRIGGER recibo_no_borrar BEFORE DELETE ON recibo
FOR EACH ROW
  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Los recibos no se eliminan';//

CREATE TRIGGER pago_solo_anular BEFORE UPDATE ON pago
FOR EACH ROW
BEGIN
  IF NOT (NEW.fecha <=> OLD.fecha AND NEW.monto <=> OLD.monto
      AND NEW.medio_pago <=> OLD.medio_pago AND NEW.dni_admin <=> OLD.dni_admin
      AND NEW.dni_alumno <=> OLD.dni_alumno AND NEW.periodo <=> OLD.periodo
      AND NEW.nombre_evento <=> OLD.nombre_evento
      AND NEW.anio_evento <=> OLD.anio_evento
      AND NEW.fecha_hora_encargo <=> OLD.fecha_hora_encargo) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Un pago no se modifica; se anula y se registra uno nuevo';
  END IF;
  IF OLD.fecha_anulacion IS NOT NULL THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El pago ya está anulado';
  END IF;
END;//

CREATE TRIGGER periodo_condiciones_fijas BEFORE UPDATE ON periodo
FOR EACH ROW
BEGIN
  IF (NEW.importe <> OLD.importe
      OR NEW.fecha_vencimiento <> OLD.fecha_vencimiento
      OR NEW.porcentaje_interes <> OLD.porcentaje_interes)
     AND EXISTS (SELECT 1 FROM cuota WHERE periodo = OLD.periodo) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El período ya tiene cuotas generadas';
  END IF;
END;//

CREATE TRIGGER evento_importe_fijo BEFORE UPDATE ON evento
FOR EACH ROW
BEGIN
  IF NEW.importe <> OLD.importe
     AND EXISTS (SELECT 1 FROM participacion
                 WHERE nombre_evento = OLD.nombre AND anio_evento = OLD.anio) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El evento ya tiene participantes';
  END IF;
END;//

CREATE TRIGGER responsable_alumno_no_dejar_sin BEFORE DELETE ON responsable_alumno
FOR EACH ROW
BEGIN
  IF NOT EXISTS (SELECT 1 FROM responsable_alumno
                 WHERE dni_alumno = OLD.dni_alumno
                   AND dni_responsable <> OLD.dni_responsable) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El alumno debe conservar al menos un responsable';
  END IF;
END;//

DELIMITER ;

-- =====================================================================
-- 7. Triggers: validaciones al registrar datos
--    (RN-03, RN-14, RN-28, RN-33, RF-57)
-- =====================================================================
DELIMITER //

CREATE TRIGGER pago_cuota_monto_exacto BEFORE INSERT ON pago
FOR EACH ROW
BEGIN
  DECLARE v_importe DECIMAL(10,2);
  DECLARE v_venc DATE;
  DECLARE v_pct DECIMAL(5,2);
  IF NEW.periodo IS NOT NULL THEN
    SELECT importe, fecha_vencimiento, porcentaje_interes
      INTO v_importe, v_venc, v_pct
      FROM periodo WHERE periodo = NEW.periodo;
    IF NEW.monto <> v_importe
         + IF(NEW.fecha > v_venc, ROUND(v_importe * v_pct / 100, 2), 0) THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El pago de la cuota debe ser por el monto total';
    END IF;
  END IF;
END;//

CREATE TRIGGER recibo_solo_confirmado BEFORE INSERT ON recibo
FOR EACH ROW
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pago
                 WHERE id_pago = NEW.id_pago
                   AND estado_sincronizacion = 'confirmado'
                   AND fecha_anulacion IS NULL) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Solo se emite recibo de un pago confirmado y no anulado';
  END IF;
END;//

CREATE TRIGGER responsable_no_alumno_activo BEFORE INSERT ON responsable_alumno
FOR EACH ROW
BEGIN
  IF EXISTS (SELECT 1 FROM permanencia
             WHERE dni_alumno = NEW.dni_responsable AND vigente = 1) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Un alumno activo no puede ser responsable';
  END IF;
END;//

CREATE TRIGGER alumno_activo_no_responsable BEFORE INSERT ON permanencia
FOR EACH ROW
BEGIN
  IF EXISTS (SELECT 1 FROM responsable_alumno
             WHERE dni_responsable = NEW.dni_alumno) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Un responsable no puede ser alumno activo';
  END IF;
END;//

CREATE TRIGGER cuota_solo_alumno_activo BEFORE INSERT ON cuota
FOR EACH ROW
BEGIN
  IF NOT EXISTS (SELECT 1 FROM permanencia
                 WHERE dni_alumno = NEW.dni_alumno AND vigente = 1) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Solo se generan cuotas para alumnos activos';
  END IF;
END;//

DELIMITER ;

-- =====================================================================
-- 8. Vistas: datos derivados
--    (RN-13, RN-19, RN-20, RN-23, RN-24, RN-32, RN-37, RN-42)
-- =====================================================================
CREATE VIEW vista_cuota AS
SELECT c.dni_alumno, c.periodo, p.importe, p.fecha_vencimiento,
       pg.id_pago, pg.fecha AS fecha_pago,
       p.importe
         + IF(COALESCE(pg.fecha, CURDATE()) > p.fecha_vencimiento,
              ROUND(p.importe * p.porcentaje_interes / 100, 2), 0)
         AS monto_a_pagar,
       CASE
         WHEN pg.id_pago IS NOT NULL          THEN 'pagada'
         WHEN CURDATE() > p.fecha_vencimiento THEN 'vencida'
         ELSE 'pendiente'
       END AS estado
FROM cuota c
JOIN periodo p ON p.periodo = c.periodo
LEFT JOIN pago pg ON pg.dni_alumno = c.dni_alumno
                 AND pg.periodo = c.periodo
                 AND pg.vigente_cuota = 1;

CREATE VIEW vista_participacion AS
SELECT pa.dni_alumno, pa.nombre_evento, pa.anio_evento, e.importe,
       e.importe - COALESCE(SUM(pg.monto), 0) AS saldo
FROM participacion pa
JOIN evento e ON e.nombre = pa.nombre_evento AND e.anio = pa.anio_evento
LEFT JOIN pago pg ON pg.dni_alumno = pa.dni_alumno
                 AND pg.nombre_evento = pa.nombre_evento
                 AND pg.anio_evento = pa.anio_evento
                 AND pg.fecha_anulacion IS NULL
                 AND pg.estado_sincronizacion = 'confirmado'
GROUP BY pa.dni_alumno, pa.nombre_evento, pa.anio_evento, e.importe;

CREATE VIEW vista_encargo AS
SELECT en.dni_alumno, en.fecha_hora, en.descripcion, en.monto,
       en.monto - COALESCE(SUM(pg.monto), 0) AS saldo
FROM encargo en
LEFT JOIN pago pg ON pg.dni_alumno = en.dni_alumno
                 AND pg.fecha_hora_encargo = en.fecha_hora
                 AND pg.fecha_anulacion IS NULL
                 AND pg.estado_sincronizacion = 'confirmado'
GROUP BY en.dni_alumno, en.fecha_hora, en.descripcion, en.monto;

CREATE VIEW vista_alumno AS
SELECT a.dni, pe.nombre, pe.apellido, a.fecha_nacimiento,
       YEAR(a.fecha_nacimiento) AS categoria,
       IF(EXISTS (SELECT 1 FROM permanencia
                  WHERE dni_alumno = a.dni AND vigente = 1),
          'activo', 'inactivo') AS estado,
       EXISTS (SELECT 1 FROM vista_cuota
               WHERE dni_alumno = a.dni AND estado = 'vencida') AS moroso
FROM alumno a
JOIN persona pe ON pe.dni = a.dni;

CREATE VIEW vista_recibo AS
SELECT r.numero_recibo, r.id_pago, r.fecha_emision,
       pg.fecha_anulacion IS NOT NULL AS anulado
FROM recibo r
JOIN pago pg ON pg.id_pago = r.id_pago;
