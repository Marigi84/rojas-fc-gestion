# Propuesta de Proyecto y Repositorio

**1ª Entrega — Trabajo Final Integrador**
Tecnicatura Universitaria en Programación (UTN)

Sistema de Gestión Rojas FC

**Alumnos:** Marina Giselle Cordero, Silvia Giardini, Alex Pedro Dauria
**Tutor:** Sebastián Bruselario
**Fecha de Entrega:** 19 de Agosto de 2026

Link del Repositorio: <https://github.com/Marigi84/rojas-fc-gestion>

**Estado: aprobada por el tutor.**

---

## 1. Problemática identificada

El presente proyecto responde a una necesidad real de Rojas Fútbol Club, escuela de fútbol ubicada en Córdoba, Argentina.

La Escuela de Fútbol Rojas FC cuenta actualmente con aproximadamente 140 alumnos activos y realiza gran parte de su gestión administrativa mediante planillas de Excel y, para determinados procesos, registros en papel.

El proceso de incorporación de un nuevo alumno comienza generalmente mediante una consulta por WhatsApp, donde el coordinador brinda información sobre modalidad, horarios, costos y demás aspectos de la escuela. Posteriormente se ofrece una clase de prueba y, si la familia decide continuar, el coordinador solicita los datos básicos del niño o niña y de su responsable para registrarlos manualmente en una planilla de Excel.

Si bien este mecanismo permite mantener un registro de los alumnos, la información no se encuentra organizada dentro de un sistema que permita relacionarla y consultarla eficientemente. Esto dificulta tareas como la búsqueda y filtrado de alumnos, la organización por categorías y la utilización posterior de esos mismos datos en otros procesos administrativos.

Una situación similar ocurre con la gestión de cuotas. La escuela establece un valor general de cuota y una fecha de vencimiento, actualmente el día 13 de cada mes. Una vez superado el vencimiento se aplica un interés del 10 %, independientemente de la cantidad de días transcurridos. Los pagos se realizan mediante efectivo o transferencia y son controlados por el coordinador.

La identificación de alumnos que poseen cuotas pendientes y la determinación de cuándo corresponde aplicar intereses se realizan actualmente mediante revisión visual de la planilla. Esto requiere intervención permanente del coordinador y dificulta disponer de forma inmediata de información consolidada sobre cuotas pagadas, vencidas, deuda acumulada y recaudación.

Luego de verificar un pago, el coordinador lo registra y entrega un recibo en papel. Por lo tanto, tampoco existe actualmente un repositorio digital centralizado que permita consultar posteriormente los comprobantes asociados a cada alumno.

Además de las cuotas mensuales, la escuela administra otros conceptos económicos, principalmente eventos deportivos e indumentaria por encargo. Estos registros se realizan mediante Excel o papel. A diferencia de las cuotas, que deben abonarse en su totalidad, determinados eventos y pedidos de indumentaria pueden admitir señas o pagos parciales.

Como consecuencia, información relacionada con alumnos, responsables, cuotas, pagos, eventos e indumentaria se encuentra distribuida entre distintos registros y requiere numerosas tareas manuales para su actualización, búsqueda, control y análisis.

Esta situación también limita la obtención de información estadística. Datos como cantidad de nuevas inscripciones por período, evolución de alumnos activos, porcentaje de cuotas cobradas, deuda pendiente, recaudación mensual o participación en eventos no se encuentran disponibles automáticamente y requieren procesamiento manual de los registros existentes.

La problemática identificada, por lo tanto, no radica simplemente en el uso de Excel, sino en las limitaciones del proceso actual para centralizar, relacionar, consultar y procesar la información administrativa de una escuela que gestiona aproximadamente 140 alumnos y un flujo continuo de inscripciones, cuotas, pagos y actividades.

---

## 2. Propuesta de solución y valor agregado

Se propone desarrollar una aplicación web de gestión para Rojas FC que permita centralizar la información administrativa de la escuela y automatizar parte de los procesos que actualmente requieren registro, búsqueda y control manual.

El sistema permitirá gestionar alumnos, responsables, categorías, cuotas, pagos, recibos, eventos y otros conceptos de cobro desde una plataforma centralizada.

Para el proceso de inscripción se incorporará un formulario propio de preinscripción que podrá ser completado directamente por el responsable del niño o niña. La información ingresada generará una preinscripción que deberá ser revisada y aprobada por un usuario autorizado antes de convertirse en un alumno activo. De esta manera se reducirá la transcripción manual de información y se podrán validar datos incompletos, incorrectos o duplicados.

La categoría correspondiente al alumno se determinará de acuerdo con su año de nacimiento.

Las cuotas se generarán automáticamente para los alumnos activos. El sistema permitirá configurar su importe, una única fecha de vencimiento por período y los intereses correspondientes, conservando las condiciones históricas de las cuotas ya generadas cuando posteriormente se modifique la configuración.

*Nota de actualización: esta propuesta preveía originalmente la posibilidad de "uno o más vencimientos" por período. Tras la revisión de la 2ª Entrega, el equipo consensuó dejar una única fecha de vencimiento por período, por simplificar el esquema y facilitar el cálculo del interés por mora (ver discusión y acuerdo en los [Issues #5](https://github.com/Marigi84/rojas-fc-gestion/issues/5) y [#6](https://github.com/Marigi84/rojas-fc-gestion/issues/6), y la regla [RN-19](docs/reglas-negocio/reglas-de-negocio.md) / [RF-23](docs/requisitos/requisitos-funcionales.md)). El texto de esta sección se actualizó para reflejar esa decisión.*

Al registrarse y confirmarse un pago, el sistema actualizará la situación administrativa correspondiente y permitirá generar un recibo digital que podrá almacenarse, consultarse, imprimirse o compartirse.

También se contemplará el registro de cobros correspondientes a eventos deportivos e indumentaria. Estos conceptos podrán admitir pagos parciales cuando corresponda, manteniendo el registro del importe total, los pagos realizados y el saldo pendiente.

La plataforma contará además con herramientas de búsqueda, filtrado, generación de listados, reportes y exportación de información, así como un módulo de estadísticas que permita transformar los registros administrativos en información útil para la gestión de la escuela.

Entre otros indicadores, podrán analizarse la cantidad de alumnos activos, altas y bajas por período, distribución por categorías, situación de las cuotas, deuda pendiente y evolución de la recaudación.

El valor agregado de la propuesta no consiste únicamente en digitalizar las planillas actuales, sino en integrar información que hoy se encuentra distribuida, automatizar controles, reducir tareas manuales y permitir obtener información administrativa y estadística de forma más rápida y confiable.

---

## 3. Actores

### Administrador / Coordinador

Será el principal usuario de gestión del sistema.

Entre sus responsabilidades se encontrarán la administración de alumnos, responsables y categorías; validación de preinscripciones; configuración y seguimiento de cuotas; verificación y registro de pagos; gestión de recibos; eventos y otros conceptos de cobro; generación de reportes y consulta de estadísticas.

### Responsable / Tutor

Será el adulto responsable asociado a uno o más alumnos.

Podrá proporcionar información mediante el proceso de preinscripción y, mediante el portal destinado a las familias, acceder a la información que se determine sobre los alumnos a su cargo, situación administrativa, cuotas y comprobantes.

### Alumno

El alumno constituye una entidad central del dominio, pero inicialmente no será considerado un usuario directo de la aplicación. Su información será gestionada por usuarios autorizados y estará asociada a uno o más responsables.

---

## 4. Alcance del proyecto

El Trabajo Final comprenderá el desarrollo de una aplicación web responsive destinada inicialmente a la gestión de Rojas FC.

El alcance previsto incluye:

- Gestión de alumnos y bajas lógicas.
- Gestión de responsables o tutores.
- Gestión de categorías vinculadas al año de nacimiento.
- Formulario propio de preinscripción y proceso de validación.
- Generación automática de cuotas.
- Configuración de importes, vencimientos e intereses.
- Seguimiento automático de cuotas pagadas, pendientes y vencidas.
- Registro de pagos en efectivo y transferencia.
- Generación y conservación de recibos digitales.
- Gestión de cobros relacionados con eventos deportivos.
- Registro básico de cobros de indumentaria por encargo.
- Soporte para pagos parciales cuando el concepto lo permita.
- Autenticación y autorización mediante usuarios y roles.
- Portal de consulta para responsables.
- Búsquedas, filtros y paginación.
- Reportes y generación de listados.
- Exportación de información a Excel y formatos que se definan durante el diseño.
- Dashboard y estadísticas administrativas.
- Posibilidad de imprimir o compartir determinados comprobantes mediante WhatsApp.
- Funcionamiento web responsive.
- Funcionamiento offline controlado y posterior sincronización de las operaciones que se definan como aptas para realizarse sin conexión.
- Auditoría de operaciones relevantes.

### Fuera del alcance comprometido

Se consideran posibles evoluciones posteriores y no forman parte del alcance comprometido inicialmente para el Trabajo Final:

- Aplicación móvil nativa para Android o iOS.
- Soporte para múltiples escuelas o instituciones.
- Procesamiento de pagos online mediante Mercado Pago u otras pasarelas.
- Integración completa mediante WhatsApp Business API.
- Gestión avanzada del ciclo de pedidos de indumentaria.

La solución se diseñará procurando no impedir futuras ampliaciones, pero evitando incorporar al Trabajo Final complejidad correspondiente a funcionalidades que no resultan necesarias para resolver la problemática inicial.

---

## 5. Stack tecnológico y plataforma

Para el desarrollo del sistema se seleccionó un conjunto de tecnologías acorde con las características de la aplicación, los conocimientos previos del equipo y el tiempo disponible para la realización del Trabajo Final. Se priorizaron tecnologías conocidas y trabajadas durante la carrera, con el objetivo de reducir la curva de aprendizaje y concentrar los esfuerzos en el análisis, desarrollo e implementación de la solución.

### 5.1 Frontend: React + TypeScript + Vite

Para el desarrollo del frontend se utilizará **React con TypeScript**, ya que permite construir una interfaz web dinámica y basada en componentes reutilizables. TypeScript aporta tipado estático sobre JavaScript, facilitando la detección temprana de errores y favoreciendo la mantenibilidad del código a medida que el proyecto crece.

La elección también considera la experiencia previa del equipo con estas tecnologías y su incorporación dentro de los contenidos de la carrera, reduciendo la curva de aprendizaje durante el desarrollo.

Se utilizará **Vite** como herramienta de desarrollo y construcción del frontend, permitiendo gestionar el entorno del proyecto, sus dependencias y la generación de la versión destinada a producción.

Como herramientas complementarias se prevé utilizar **Tailwind CSS** para la construcción de estilos e interfaces, **React Router** para la navegación, **TanStack Query** para la gestión de datos provenientes del servidor y **Zustand** cuando resulte necesario administrar estado global de la aplicación. La incorporación de otras librerías específicas se evaluará durante el desarrollo de acuerdo con las necesidades que surjan.

### 5.2 Backend: Java + Spring Boot

Para el desarrollo del backend se utilizará **Java con Spring Boot**. Esta combinación resulta adecuada para una aplicación web con lógica de negocio y datos estructurados como la propuesta, permitiendo organizar el sistema en componentes con responsabilidades diferenciadas y desarrollar una API REST para la comunicación con el frontend.

Se utilizará **Spring Data JPA** para la capa de persistencia, facilitando el acceso y manejo de las entidades almacenadas en la base de datos relacional.

La elección también considera los conocimientos previos del equipo en Java y el trabajo con Spring Boot dentro de la carrera, reduciendo la curva de aprendizaje y permitiendo concentrar el esfuerzo en la implementación de las reglas de negocio propias del sistema.

La comunicación entre frontend y backend se realizará mediante una **API REST**, manteniendo separadas las responsabilidades correspondientes a la interfaz de usuario, la lógica de negocio y la persistencia de los datos.

Para los mecanismos de autenticación y autorización se prevé utilizar **JSON Web Tokens (JWT)**, permitiendo controlar el acceso a los recursos del sistema de acuerdo con los usuarios y roles definidos.

### 5.3 Base de datos: MySQL

Para la persistencia de la información se utilizará **MySQL**, un sistema gestor de bases de datos relacional.

La elección de un modelo relacional responde a la naturaleza del dominio, ya que la aplicación manejará información estructurada y con relaciones bien definidas entre entidades como alumnos, responsables, categorías, cuotas, pagos, recibos, eventos y usuarios.

Además, determinadas operaciones, especialmente aquellas vinculadas con pagos y actualización de estados de cuotas, requieren mantener la integridad y consistencia de la información mediante operaciones transaccionales.

Se selecciona específicamente MySQL porque el equipo posee experiencia previa con este motor, lo que reduce la curva de aprendizaje y los riesgos técnicos durante el desarrollo. Asimismo, proporciona las funcionalidades necesarias para implementar relaciones, restricciones de integridad, consultas con filtros, índices, transacciones y procesamiento de información destinada a reportes y estadísticas.

### 5.4 Plataforma de despliegue

Para el despliegue de la aplicación se prevé utilizar un servicio cloud, cuyo modelo específico (PaaS u otro) se definirá durante la etapa de implementación.

Esta alternativa reduce la complejidad de configurar y administrar servidores manualmente, permitiendo al equipo concentrarse en el desarrollo, la integración y las pruebas.

La elección resulta adecuada para la escala del proyecto y los tiempos disponibles del equipo, evitando incorporar tecnologías de infraestructura adicionales que no resultan indispensables para el Trabajo Final.

El proveedor específico de la plataforma se definirá durante la etapa de implementación, considerando su compatibilidad con las tecnologías utilizadas, los costos y los recursos disponibles.

---

## 6. Plan de trabajo

El desarrollo del proyecto se organizará de manera incremental, tomando como referencia las fechas de entrega establecidas por la cátedra y priorizando la construcción progresiva de funcionalidades completas.

La planificación contempla una etapa inicial de propuesta y preparación, otra de arquitectura y diseño, y luego distintas etapas de implementación, integración, pruebas y despliegue.

Durante el desarrollo se utilizará un repositorio único de GitHub como fuente central del proyecto. Las funcionalidades se implementarán procurando integrar progresivamente base de datos, backend y frontend, de manera que puedan obtenerse incrementos funcionales y verificables durante el avance del proyecto.

### 6.1 Etapa 1 — Propuesta y preparación del proyecto

**Período: 10/08 al 30/08**

Durante esta etapa se realizará la definición inicial del proyecto y la preparación del espacio de trabajo colaborativo.

Las principales actividades serán:

- definición y validación de la problemática;
- definición de la propuesta de solución y su valor agregado;
- identificación de actores;
- definición del alcance y funcionalidades fuera de alcance;
- selección y justificación del stack tecnológico;
- elaboración del plan inicial de trabajo;
- creación y configuración del repositorio único de GitHub;
- elaboración del README.md inicial;
- organización de la estructura destinada al código, base de datos y documentación.

**Entregable:** propuesta de proyecto, plan de trabajo y repositorio único de GitHub.

### 6.2 Etapa 2 — Arquitectura, módulos y diseño de datos

**Período: 31/08 al 27/09**

Esta etapa estará orientada a consolidar el análisis funcional y establecer la estructura técnica sobre la que se desarrollará la aplicación.

Las principales actividades serán:

- consolidación de requisitos y reglas de negocio;
- definición de los módulos que compondrán el sistema;
- modelado de entidades y relaciones;
- diseño del esquema relacional de la base de datos;
- definición de claves, restricciones e índices relevantes;
- definición inicial de la arquitectura de la aplicación;
- preparación de la estructura base del frontend y backend;
- documentación y publicación de los avances correspondientes en el repositorio.

**Entregable:** esquema de base de datos y listado de módulos documentados en el repositorio para su aprobación por el tutor y el comité.

### 6.3 Etapa 3 — Desarrollo del núcleo funcional

**Período: 28/09 al 18/10**

En esta etapa comenzará la implementación integrada de las principales funcionalidades administrativas del sistema.

Se priorizará el desarrollo de:

- autenticación, usuarios y roles;
- gestión de responsables o tutores;
- gestión de alumnos;
- gestión de categorías;
- proceso de preinscripción y validación;
- búsquedas, filtros y operaciones necesarias para la administración de la información.

Las funcionalidades se desarrollarán de manera incremental, integrando persistencia, lógica de negocio, API REST e interfaz de usuario.

**Entregable:** primera versión integrada y funcional del núcleo administrativo de la aplicación.

### 6.4 Etapa 4 — Gestión económica

**Período: 19/10 al 02/11**

Esta etapa estará enfocada principalmente en los procesos económicos y las reglas de negocio asociadas.

Se desarrollarán:

- configuración de valores de cuota;
- generación automática de cuotas;
- configuración y aplicación de vencimientos e intereses;
- seguimiento de cuotas pagadas, pendientes y vencidas;
- registro y validación de pagos;
- generación y consulta de recibos;
- registro de cobros correspondientes a eventos deportivos;
- registro de cobros de indumentaria;
- gestión de pagos parciales en aquellos conceptos que los admitan.

**Entregable:** circuito de gestión económica integrado y funcional.

### 6.5 Etapa 5 — Funcionalidades complementarias e integración

**Período: 03/11 al 09/11**

Una vez consolidado el núcleo administrativo y económico, se avanzará sobre las funcionalidades complementarias previstas en el alcance.

Entre ellas:

- portal de consulta para responsables;
- dashboard y estadísticas;
- reportes y listados;
- exportación de información a Excel;
- auditoría de operaciones relevantes;
- opciones para imprimir o compartir comprobantes;
- implementación y validación del funcionamiento offline y de los mecanismos de sincronización definidos para el sistema.

Durante esta etapa también se continuará con las pruebas de integración de las funcionalidades desarrolladas.

**Entregable:** versión funcional integrada con las principales herramientas administrativas, de consulta y análisis.

### 6.6 Etapa 6 — Pruebas, despliegue y entrega final

**Período: 10/11 al 14/11**

La última etapa estará destinada a estabilizar y preparar el producto para su presentación final.

Las principales actividades serán:

- ejecución de pruebas funcionales y de integración;
- corrección de errores detectados;
- revisión de seguridad y permisos;
- revisión del rendimiento de las operaciones principales;
- validación general de los flujos del sistema;
- despliegue de al menos uno de los componentes principales en un servicio en la nube;
- actualización del README.md y de la documentación técnica;
- preparación del informe final;
- preparación y grabación del video demostrativo.

**Entregable:** repositorio completo, versión final del sistema, componente desplegado online, documentación e informe final y video de presentación.

### 6.7 Organización y seguimiento del trabajo

El equipo utilizará **GitHub como repositorio central y único del proyecto**, manteniendo allí el código fuente, los elementos correspondientes a la base de datos y la documentación generada durante las distintas etapas.

El trabajo se organizará de manera incremental y colaborativa. Las tareas serán distribuidas entre los integrantes de acuerdo con las necesidades de cada etapa, procurando realizar revisiones cruzadas y mantener conocimiento compartido sobre las distintas partes del sistema.

Para el seguimiento de las tareas podrá utilizarse un tablero de gestión, como **GitHub Projects**, que permita visualizar actividades pendientes, en desarrollo y finalizadas.

Durante el avance se revisará periódicamente el alcance y la planificación. En caso de surgir dificultades técnicas o desvíos respecto de los tiempos previstos, se priorizarán las funcionalidades centrales relacionadas con la gestión de alumnos, responsables, cuotas y pagos, evitando comprometer la estabilidad del producto final.
