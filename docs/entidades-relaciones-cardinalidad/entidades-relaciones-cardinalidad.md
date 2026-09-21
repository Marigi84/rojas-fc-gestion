### Propuesta de Entidades, Relaciones y Cardinalidades del Dominio
Contrastando el antecedente de `schema.sql` con los requisitos funcionales, no funcionales y reglas de negocio consolidados, se presenta la siguiente propuesta conceptual para el dominio de Rojas FC.

#### 1. Entidades del Dominio

* **Alumno:** Representa al menor que asiste a la escuela de fútbol, sobre el cual se asientan el historial deportivo, el legajo administrativo y sus cuotas.
* **Responsable:** Adulto a cargo del alumno (madre, padre o tutor legal) y titular de acceso al portal de consultas.
* **Categoría:** Agrupación deportiva determinada por año de nacimiento.
* **Preinscripción:** Solicitud formal previa al alta definitiva del alumno.
* **Configuración Arancelaria:** Parámetros de cuota general, día de vencimiento y porcentaje de interés por mora fijados por la administración.
* **Cuota Mensual:** Obligación arancelaria mensual generada para un alumno activo, con su importe, fecha de vencimiento y recargo si lo hubiera.
* **Cobro Extraordinario:** Obligaciones no periódicas asignadas a un alumno (eventos, indumentaria con talle/prenda y matrícula) que admiten pagos parciales.
* **Pago:** Transacción económica mediante la cual se cancela una cuota o un cobro extraordinario.
* **Recibo:** Comprobante oficial numerado e inalterable derivado de un pago confirmado.
* **Usuario Interno:** Operador del sistema (Administrador o Coordinador) que realiza las gestiones operativas.

#### 2. Relaciones y Cardinalidades Propuestas

* **Alumno y Categoría (De Muchos a Uno):**  
  Muchos alumnos pertenecen a una misma categoría. Todo alumno debe tener asignada obligatoriamente una única categoría según su año de nacimiento.
* **Alumno y Responsable (De Muchos a Muchos):**  
  Un alumno puede tener entre uno y dos responsables obligatoriamente. Un responsable puede estar a cargo de uno o más alumnos. La relación lleva como dato propio el *vínculo* (madre, padre o tutor).
* **Preinscripción y Alumno (De Uno a Uno):**  
  Una preinscripción aprobada se convierte en exactamente un alumno registrado. Un alumno puede provenir de una preinscripción o haber sido cargado por alta directa.
* **Preinscripción y Usuario Interno (De Muchos a Uno):**  
  Un usuario revisa y valida muchas preinscripciones. Una preinscripción puede estar pendiente o haber sido revisada por un usuario.
* **Alumno y Cuota Mensual (De Uno a Muchos):**  
  Un alumno tiene muchas cuotas generadas a lo largo del tiempo. Cada cuota pertenece exclusivamente a un único alumno.
* **Alumno y Cobro Extraordinario (De Uno a Muchos):**  
  A un alumno se le pueden asignar varios cobros extraordinarios (eventos, indumentaria, matrícula). Cada cobro extraordinario está asignado a un solo alumno.
* **Cuota Mensual y Pago (De Uno a Uno):**  
  Una cuota se abona en su totalidad en un único pago. Un pago de cuota cancela una única cuota mensual. (La cuota puede no tener pago registrado mientras esté impaga).
* **Cobro Extraordinario y Pago (De Uno a Muchos):**  
  Un cobro extraordinario puede recibir uno o varios pagos parciales hasta cancelar el saldo. Cada uno de esos pagos se aplica a un único cobro extraordinario.
* **Pago y Recibo (De Uno a Uno):**  
  Todo pago confirmado genera obligatoriamente un único recibo oficial. Un recibo corresponde únicamente a un pago.
* **Usuario Interno y Pago (De Uno a Muchos):**  
  Un usuario registra muchos pagos en el sistema. Además, si un pago es anulado, se asienta qué usuario realizó la anulación.

####  Depuración de Entidades Respecto al Schema Anterior

Al contrastar el `schema.sql` anterior con los RF, RNF y RN consolidados, se detectaron dos tablas que no corresponderían a entidades del dominio:

* **Auditoría:** `auditoria` no figura en los Requisitos Funcionales ni en las Reglas de Negocio. No existe una gestión ni pantalla funcional de auditoría para los usuarios, por lo que no forma parte del modelo conceptual del dominio.
* **Constancia offline:** `constancia_offline` no figura en los requerimientos funcionales ni en las reglas de negocio. En el RF-64 se indica que la emisión del recibo se realiza luego de la sincronización.

---

Queda documentada la propuesta inicial para validación del equipo y posterior armado del DER.