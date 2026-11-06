-- Datos de ejemplo para la demo y el video (issue #74). SOLO PARA DEMOSTRACION.
--
-- Se cargan sobre una base recien creada con schema.sql:
--   mysql -u root -p rojasfc < database/datos-demo.sql
--
-- Las fechas son relativas al mes en curso, asi que la demo sigue mostrando
-- deudas y recaudacion aunque se cargue mas adelante.
--
-- Usuarios (contrasena de todos: Demo12345):
--   Administradora: DNI 20111222
--   Responsables:   DNI 30111222 (Laura, dos alumnos), 30111223 (Jorge, dos alumnos)
--
-- Estas contrasenas son publicas: no cargar estos datos en un sistema con
-- informacion real.

-- El archivo esta en UTF-8 (ver schema.sql).
SET NAMES utf8mb4;

SET @mes0 = DATE_FORMAT(CURDATE(), '%Y-%m-01');
SET @mes1 = DATE_SUB(@mes0, INTERVAL 1 MONTH);
SET @mes2 = DATE_SUB(@mes0, INTERVAL 2 MONTH);
SET @alta = DATE_SUB(@mes0, INTERVAL 6 MONTH);
SET @admin = 20111222;
SET @clave = '$2a$10$1cp0TOP/gIX1eQinmtwZ9.ktuVs9u7ECD56caUDekeyR/G0MivZYi';

INSERT INTO persona (dni, nombre, apellido) VALUES
  (20111222, 'Marta', 'Fernández'),
  (30111222, 'Laura', 'Gómez'),
  (30111223, 'Jorge', 'Pérez'),
  (30111224, 'Ana', 'Ruiz'),
  (30111225, 'Carlos', 'Díaz'),
  (30111226, 'Paula', 'Romero'),
  (30111227, 'Diego', 'Sosa'),
  (45111222, 'Mateo', 'Gómez'),
  (45111223, 'Sofía', 'Gómez'),
  (46555666, 'Lucía', 'Pérez'),
  (46555667, 'Bruno', 'Pérez'),
  (47000001, 'Valentina', 'Ruiz'),
  (47000002, 'Tomás', 'Díaz'),
  (47000003, 'Julieta', 'Romero'),
  (47000004, 'Nicolás', 'Sosa');

INSERT INTO administrador (dni, email, contrasena_hash, contrasena_temporal) VALUES
  (@admin, 'marta@rojasfc.demo', @clave, FALSE);

INSERT INTO responsable (dni, telefono, contrasena_hash, contrasena_temporal) VALUES
  (30111222, '3511234567', @clave, FALSE),
  (30111223, '3512345678', @clave, FALSE),
  (30111224, '3513456789', @clave, FALSE),
  (30111225, '3514567890', @clave, FALSE),
  (30111226, '3515678901', @clave, FALSE),
  (30111227, '3516789012', @clave, FALSE);

INSERT INTO alumno (dni, fecha_nacimiento, direccion) VALUES
  (45111222, '2015-03-10', 'San Martín 123'),
  (45111223, '2016-07-01', 'San Martín 123'),
  (46555666, '2016-05-20', 'Mitre 50'),
  (46555667, '2014-09-12', 'Mitre 50'),
  (47000001, '2017-01-15', 'Belgrano 456'),
  (47000002, '2015-11-03', 'Colón 789'),
  (47000003, '2014-04-22', 'Rivadavia 321'),
  (47000004, '2017-08-30', 'Sarmiento 654');

INSERT INTO responsable_alumno (dni_responsable, dni_alumno, vinculo) VALUES
  (30111222, 45111222, 'madre'),
  (30111222, 45111223, 'madre'),
  (30111223, 46555666, 'padre'),
  (30111223, 46555667, 'padre'),
  (30111224, 47000001, 'madre'),
  (30111225, 47000002, 'padre'),
  (30111226, 47000003, 'madre'),
  (30111227, 47000004, 'tutor');

INSERT INTO permanencia (dni_alumno, fecha_alta, dni_admin_alta)
SELECT dni, @alta, @admin FROM alumno;

-- Tres meses, con el vencimiento el dia 13 y 10 % de interes.
INSERT INTO periodo VALUES
  (@mes2, 25000, DATE_ADD(@mes2, INTERVAL 12 DAY), 10, @admin, @mes2),
  (@mes1, 25000, DATE_ADD(@mes1, INTERVAL 12 DAY), 10, @admin, @mes1),
  (@mes0, 25000, DATE_ADD(@mes0, INTERVAL 12 DAY), 10, @admin, @mes0);

INSERT INTO cuota (dni_alumno, periodo, fecha_generacion)
SELECT dni, @mes2, @mes2 FROM alumno;
INSERT INTO cuota (dni_alumno, periodo, fecha_generacion)
SELECT dni, @mes1, @mes1 FROM alumno;

-- Tomas se dio de baja el mes pasado: no tiene cuota de este mes.
UPDATE permanencia SET fecha_baja = DATE_ADD(@mes1, INTERVAL 20 DAY), dni_admin_baja = @admin
WHERE dni_alumno = 47000002;
INSERT INTO cuota (dni_alumno, periodo, fecha_generacion)
SELECT dni_alumno, @mes0, @mes0 FROM permanencia WHERE vigente = 1;

-- Hace dos meses: todos pagaron a tiempo, menos Julieta.
INSERT INTO pago (fecha, monto, nombre_medio_pago, dni_admin, dni_alumno, periodo)
SELECT DATE_ADD(@mes2, INTERVAL 4 DAY), 25000, 'efectivo', @admin, dni, @mes2
FROM alumno WHERE dni <> 47000003;

-- El mes pasado.
INSERT INTO pago (fecha, monto, nombre_medio_pago, dni_admin, dni_alumno, periodo) VALUES
  -- A tiempo.
  (DATE_ADD(@mes1, INTERVAL 4 DAY), 25000, 'transferencia', @admin, 46555667, @mes1),
  (DATE_ADD(@mes1, INTERVAL 4 DAY), 25000, 'efectivo', @admin, 47000001, @mes1),
  (DATE_ADD(@mes1, INTERVAL 4 DAY), 25000, 'transferencia', @admin, 47000004, @mes1),
  (DATE_ADD(@mes1, INTERVAL 4 DAY), 25000, 'efectivo', @admin, 47000002, @mes1),
  -- Fuera de termino: con el 10 % de interes.
  (DATE_ADD(@mes1, INTERVAL 19 DAY), 27500, 'efectivo', @admin, 45111223, @mes1),
  -- Se anula mas abajo: Lucia vuelve a tener la deuda.
  (DATE_ADD(@mes1, INTERVAL 4 DAY), 25000, 'efectivo', @admin, 46555666, @mes1);

-- Este mes: lo que suma la recaudacion del Dashboard.
INSERT INTO pago (fecha, monto, nombre_medio_pago, dni_admin, dni_alumno, periodo) VALUES
  (@mes0, 25000, 'transferencia', @admin, 45111223, @mes0),
  (@mes0, 25000, 'efectivo', @admin, 47000001, @mes0);

-- Todo pago confirmado tiene su recibo (RN-33).
INSERT INTO recibo (id_pago, fecha_emision)
SELECT id_pago, fecha FROM pago ORDER BY id_pago;

-- La anulacion conserva el pago y su recibo, que queda como anulado (RF-28, RF-33).
UPDATE pago
SET fecha_anulacion = DATE_ADD(@mes1, INTERVAL 5 DAY),
    motivo = 'Se registró en el alumno equivocado',
    dni_admin_anula = @admin
WHERE dni_alumno = 46555666 AND periodo = @mes1;
