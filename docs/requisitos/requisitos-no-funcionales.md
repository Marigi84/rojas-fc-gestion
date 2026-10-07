# Requisitos No Funcionales

## Sistema de Gestión Rojas FC

Este documento reúne los requisitos no funcionales consolidados del Sistema de Gestión Rojas FC.

La definición se realizó tomando como base la Primera Entrega aprobada y las decisiones posteriores acordadas durante el análisis funcional.

Los requisitos aquí definidos establecen condiciones de calidad, seguridad, rendimiento, integridad, usabilidad y operación que deberá cumplir el sistema.

En este documento, **Administrador** designa al actor "Administrador / Coordinador" definido en la Primera Entrega.

---

## 1. Seguridad

### RNF-01 – Protección de credenciales
El sistema deberá almacenar las contraseñas de los usuarios de forma segura, evitando su almacenamiento en texto plano. Las contraseñas asignadas o restablecidas por el Administrador serán provisorias y el usuario deberá cambiarlas en su primer ingreso.

### RNF-02 – Protección de funcionalidades restringidas
El acceso a las funcionalidades administrativas deberá validarse en el backend, evitando que puedan utilizarse únicamente mediante manipulación de la interfaz del cliente.

### RNF-03 – Protección de datos según usuario
El sistema deberá impedir que un responsable consulte información correspondiente a alumnos con los que no se encuentre asociado.

---

## 2. Integridad y consistencia de la información

### RNF-04 – Integridad de la información
El sistema deberá preservar la consistencia de los datos ante operaciones relacionadas entre sí, especialmente en cuotas, pagos, saldos y recibos.

### RNF-05 – Consistencia de operaciones económicas
Una operación económica no deberá dejar registros parciales o inconsistentes cuando ocurra un error durante su procesamiento.

---

## 3. Rendimiento

### RNF-06 – Gestión eficiente de listados
El sistema deberá utilizar paginación y consultas acotadas para evitar la carga innecesaria de grandes volúmenes de registros y favorecer tiempos de respuesta adecuados.

### RNF-07 – Tiempo de respuesta
El sistema deberá responder a las operaciones habituales de búsqueda, filtrado y consulta de listados en un tiempo objetivo no superior a 2 segundos, bajo condiciones normales de uso y conectividad, aun con el crecimiento del historial de alumnos, cuotas, pagos y recibos.

---

## 4. Adaptabilidad de la interfaz

### RNF-08 – Diseño responsive
La interfaz deberá adaptarse a distintos tamaños de pantalla y permitir la utilización de las funcionalidades principales desde computadoras, tablets y teléfonos móviles.

---

## 5. Funcionamiento sin conexión y sincronización

### RNF-09 – Preservación de operaciones offline
Las operaciones permitidas sin conexión deberán conservarse localmente hasta que puedan ser sincronizadas, evitando su pérdida ante interrupciones temporales de conectividad.

### RNF-10 – Consistencia de sincronización
La sincronización de operaciones pendientes deberá evitar la generación involuntaria de registros duplicados y detectar situaciones que requieran intervención del Administrador.

---

## 6. Trazabilidad

### RNF-11 – Trazabilidad de operaciones
El sistema deberá registrar qué administrador realizó cada una de las siguientes operaciones y en qué fecha: alta y baja de alumnos, registro y anulación de pagos, configuración de las cuotas de cada período, y registro de eventos y de encargos de indumentaria.

---

## 7. Usabilidad

### RNF-12 – Claridad de la interfaz
La interfaz deberá presentar las funcionalidades administrativas de manera clara y consistente, utilizando mensajes comprensibles para informar resultados, errores o acciones que requieran intervención del usuario.

### RNF-13 – Confirmación de operaciones sensibles
Las operaciones que puedan modificar significativamente el estado de la información, como una baja o la anulación de un pago, deberán solicitar confirmación antes de ejecutarse.

---

## 8. Plataforma y disponibilidad

### RNF-14 – Aplicación web
El sistema deberá poder utilizarse desde un navegador web sin requerir la instalación de una aplicación nativa.

### RNF-15 – Disponibilidad
El sistema deberá estar disponible para su utilización mientras los servicios de infraestructura y conectividad requeridos se encuentren operativos, contemplando las funcionalidades offline definidas para interrupciones temporales de conexión.

### RNF-16 – Despliegue en la nube
La solución deberá contemplar el despliegue de al menos uno de sus componentes principales en un servicio en la nube.

---

## Consideraciones fuera del alcance actual

Las copias de seguridad automáticas de la base de datos no se incorporan como requisito obligatorio del alcance actual del Trabajo Final Integrador.

Se consideran una mejora futura recomendable para la etapa de operación real del sistema.
