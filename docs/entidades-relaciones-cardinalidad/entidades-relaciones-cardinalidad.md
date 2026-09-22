### Propuesta de Entidades, Relaciones y Cardinalidades del Dominio
Contrastando el antecedente de `schema.sql` con los requisitos funcionales, no funcionales y reglas de negocio consolidados, se presenta la siguiente propuesta conceptual para el dominio de Rojas FC.

#### 1. Entidades del Dominio

* **Alumno:** Representa al menor que asiste a la escuela de fútbol, sobre el cual se asienta su información personal, legajo, historial administrativo y sus obligaciones arancelarias.
* **Responsable:** Adulto a cargo del alumno (madre, padre o tutor) y titular de acceso al portal de consultas.
* **Categoría:** Agrupación deportiva determinada por año de nacimiento.
* **Preinscripción:** Solicitud formal previa al alta definitiva o reactivación del alumno en la institución.
* **Configuración Arancelaria:** Parámetros institucionales definidos por la administración (importe general de cuota, día de vencimiento y porcentaje de interés por mora). Sirve de base para generar las cuotas mensuales, preservando las condiciones históricas de cada cuota una vez generada.
* **Cuota Mensual:** Obligación arancelaria mensual generada para un alumno activo, con su importe, fecha de vencimiento y recargo si lo hubiera.
* **Cobro Extraordinario:** Cobro no periódico asociado a un alumno, correspondiente a eventos, indumentaria o matrícula. Los cobros correspondientes a eventos e indumentaria pueden admitir pagos parciales.
* **Pago:** Transacción económica registrada para abonar una cuota mensual o imputarse total o parcialmente a un cobro extraordinario.
* **Recibo:** Comprobante numerado asociado a un pago confirmado, que se conserva como parte del historial administrativo.
* **Usuario:** Identidad de acceso al sistema que centraliza las credenciales de ingreso y permite diferenciar los perfiles operativos (Administrador, Coordinador o Responsable).

#### 2. Relaciones y Cardinalidades Propuestas

* **Alumno y Categoría (De Muchos a Uno):**  
  Muchos alumnos pertenecen a una misma categoría. Todo alumno debe tener asignada obligatoriamente una única categoría según su año de nacimiento.
* **Alumno y Responsable (De Muchos a Muchos):**  
  Un alumno puede tener entre uno y dos responsables obligatoriamente. Un responsable puede estar a cargo de uno o más alumnos. La relación lleva como dato propio el *vínculo* (madre, padre o tutor).
* **Preinscripción y Alumno (De Cero o Uno a Uno):**
  Una preinscripción aprobada se asocia a un único alumno (sea por alta nueva o reactivación de legajo). Un alumno puede existir en el sistema sin preinscripción previa si ingresó por alta directa.
* **Responsable y Usuario (De Uno a Cero o Uno):**
  Un responsable puede tener asociada una cuenta de usuario con rol responsable para autenticarse en el portal de consultas (utilizando su DNI). Cada usuario con rol responsable corresponde a una única persona en el dominio (un responsable existe en el sistema aunque aún no posea usuario activo).
* **Preinscripción y Usuario (De Cero o Uno a Uno):**  
  Una preinscripción permanece sin usuario revisor asignado mientras se encuentre en estado pendiente, o queda asociada a un único usuario con rol administrativo al momento de su revisión y resolución. A su vez, un usuario con rol administrativo puede revisar y validar múltiples preinscripciones.
* **Configuración Arancelaria y Cuota Mensual (De Uno a Muchos):**
  Una Configuración Arancelaria puede utilizarse para generar muchas Cuotas Mensuales. Cada cuota toma los valores vigentes al momento de su generación y luego conserva esas condiciones históricas aunque la configuración cambie.
* **Alumno y Cuota Mensual (De Uno a Muchos):**  
  Un alumno tiene muchas cuotas generadas a lo largo del tiempo. Cada cuota pertenece exclusivamente a un único alumno.
* **Alumno y Cobro Extraordinario (De Uno a Muchos):**  
  A un alumno se le pueden asignar varios cobros extraordinarios (eventos, indumentaria, matrícula). Cada cobro extraordinario está asignado a un solo alumno.
* **Cuota Mensual y Pago (De Uno a Cero o Muchos Históricos):**
  Una cuota mensual puede registrar cero pagos mientras esté impaga. Para considerarse cancelada, requiere un único pago válido por el total de la obligación; no obstante, a nivel histórico puede acumular más de un registro de pago si existieron pagos previos anulados.
* **Cobro Extraordinario y Pago (De Uno a Cero o Muchos):**  
  Un cobro extraordinario puede recibir cero, uno o varios pagos parciales hasta cancelar el saldo. Cada uno de esos pagos se aplica a un único cobro extraordinario.
* **Pago y Recibo (De Uno a Cero o Uno):**  
  Un pago confirmado genera obligatoriamente un único recibo. Un pago registrado provisionalmente sin conexión permanece temporalmente con 0 recibos hasta su sincronización y validación efectiva (RF-64).
* **Usuario y Pago (De Uno a Muchos):**  
  Un usuario con rol administrativo registra múltiples pagos en el sistema. Asimismo, en caso de anulación, queda asentado qué usuario con rol administrativo ejecutó dicha acción.

####  Depuración de Entidades Respecto al Schema Anterior y Puntos a Definir para el DER

Al contrastar el `schema.sql` anterior con los RF, RNF y RN consolidados, se definen los siguientes criterios:

* **Trazabilidad y Auditoría:**
  Auditoría no se considera una entidad funcional principal del dominio ni un módulo independiente. La trazabilidad requerida por RNF-12 deberá resolverse posteriormente como una capacidad transversal de la arquitectura y/o del modelo físico.
* **Constancia offline:** `constancia_offline` no figura en los requerimientos funcionales ni en las reglas de negocio. En el RF-64 se indica que la emisión del recibo se realiza luego de la sincronización.
* **Historial de Estados del Alumno (Alta, Baja y Reactivación):**
  Conviene evaluar la necesidad de representar el historial de movimientos (alta, baja y reactivación) para dar soporte a RF-51 y a la reactivación conservando legajo (RF-04, RF-05; RN-04). Dado que un alumno puede experimentar múltiples cambios a lo largo del tiempo, un único campo de estado o fecha resultaría insuficiente para reconstruir su evolución histórica.
---

Queda documentada la propuesta inicial para validación del equipo y posterior armado del DER.