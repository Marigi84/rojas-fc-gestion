# Sistema de Gestión Rojas FC

Aplicación web para la gestión administrativa de la Escuela de Fútbol Rojas FC, desarrollada como Trabajo Final Integrador de la Tecnicatura Universitaria en Programación.

## Integrantes

- Marina Giselle Cordero
- Silvia Giardini
- Alex Pedro Dauria

## Cliente

**Rojas Fútbol Club**, escuela de fútbol ubicada en Córdoba, Argentina.

El proyecto surge a partir de una necesidad real de la institución y busca optimizar procesos administrativos que actualmente se realizan principalmente mediante planillas de Excel y registros en papel.

## Problemática

Rojas FC cuenta actualmente con aproximadamente 140 alumnos activos y gran parte de su gestión administrativa se realiza de manera manual.

Las inscripciones se registran en planillas de Excel y el seguimiento de cuotas requiere revisar manualmente quién realizó el pago, si existen cuotas pendientes y si corresponde aplicar intereses.

Los pagos se realizan mediante efectivo o transferencia y, una vez verificados, el coordinador entrega recibos en papel.

La escuela también gestiona cobros correspondientes a eventos deportivos e indumentaria por encargo, cuya información puede encontrarse distribuida entre planillas de Excel y registros en papel.

Esta modalidad dificulta la búsqueda y relación de la información, aumenta las tareas administrativas y limita la posibilidad de obtener de forma inmediata datos consolidados y estadísticas sobre alumnos, inscripciones, cuotas, deuda, pagos y recaudación.

## Propuesta de solución

Se propone desarrollar una aplicación web de gestión que permita centralizar la información administrativa de Rojas FC y automatizar parte de los procesos que actualmente requieren registro, búsqueda y control manual.

El sistema permitirá gestionar alumnos, responsables, categorías, preinscripciones, cuotas, pagos, recibos, eventos y otros conceptos de cobro desde una plataforma centralizada.

Entre las principales funcionalidades previstas se encuentran:

- Gestión de alumnos y responsables.
- Organización de alumnos por categorías según año de nacimiento.
- Formulario propio de preinscripción y posterior validación.
- Generación automática de cuotas.
- Configuración de importes, vencimientos e intereses.
- Seguimiento de cuotas pagadas, pendientes y vencidas.
- Registro de pagos en efectivo y transferencia.
- Generación y conservación de recibos digitales.
- Registro de cobros correspondientes a eventos deportivos.
- Registro de cobros de indumentaria por encargo.
- Soporte para pagos parciales cuando el concepto lo permita.
- Autenticación y autorización mediante usuarios y roles.
- Portal de consulta para responsables.
- Búsquedas, filtros y paginación.
- Reportes y exportación de información.
- Dashboard y estadísticas administrativas.
- Auditoría de operaciones relevantes.
- Funcionamiento responsive.
- Funcionamiento offline controlado y sincronización posterior de las operaciones definidas para este modo.

## Valor agregado

La propuesta no busca únicamente reemplazar las planillas actuales por una aplicación digital, sino mejorar el proceso administrativo mediante la integración y procesamiento de la información.

La solución permitirá reducir tareas manuales, facilitar la búsqueda y actualización de datos, automatizar controles relacionados con cuotas y vencimientos, mantener un historial de pagos y comprobantes y generar información estadística útil para la gestión de la escuela.

## Alcance

La primera versión estará destinada a la gestión de una única escuela, Rojas FC, y será desarrollada como una aplicación web responsive.

### Fuera del alcance inicial

Se consideran posibles evoluciones posteriores:

- Aplicación móvil nativa para Android o iOS.
- Soporte para múltiples escuelas o instituciones.
- Procesamiento de pagos online mediante pasarelas de pago.
- Integración completa mediante WhatsApp Business API.
- Gestión avanzada del ciclo de pedidos de indumentaria.

La arquitectura se diseñará procurando permitir futuras ampliaciones sin incorporar al Trabajo Final complejidad que no resulte necesaria para resolver la problemática actual.

## Stack tecnológico

La selección tecnológica considera las características del sistema, los conocimientos previos del equipo y los tiempos disponibles para el desarrollo.

### Frontend

- **Lenguaje:** TypeScript
- **Biblioteca:** React
- **Herramienta de desarrollo y construcción:** Vite
- **Estilos:** Tailwind CSS
- **Navegación:** React Router
- **Gestión de datos del servidor:** TanStack Query
- **Estado global:** Zustand

React con TypeScript permitirá desarrollar una interfaz basada en componentes reutilizables y mantener un mayor control sobre los tipos de datos utilizados en la aplicación. La elección también considera la experiencia previa del equipo y las tecnologías trabajadas durante la carrera.

### Backend

- **Lenguaje:** Java
- **Framework:** Spring Boot
- **Persistencia:** Spring Data JPA
- **Comunicación:** API REST
- **Autenticación y autorización:** JWT

Java con Spring Boot resulta adecuado para una aplicación con lógica de negocio y datos estructurados como la propuesta. Spring Data JPA será utilizado para gestionar la capa de persistencia. La comunicación entre el frontend y el backend se realizará mediante una API REST.

### Base de datos

- **Motor:** MySQL
- **Modelo:** Relacional

El dominio presenta información estructurada y relaciones bien definidas entre alumnos, responsables, categorías, cuotas, pagos, eventos y demás entidades.
El modelo relacional permitirá mantener la integridad y consistencia de los datos, especialmente en operaciones vinculadas con pagos y estados administrativos.
Se seleccionó MySQL debido a que el equipo posee experiencia previa con este motor y proporciona las funcionalidades necesarias para las características del proyecto.

### Plataforma de despliegue

Se prevé realizar el despliegue de al menos uno de los componentes principales de la aplicación en un **servicio cloud**, de acuerdo con los requisitos establecidos para el Trabajo Final.
El servicio específico se definirá durante la etapa de implementación, considerando su compatibilidad con el stack tecnológico seleccionado, facilidad de despliegue, costos y recursos disponibles.
Se priorizarán alternativas que reduzcan la complejidad asociada a la administración manual de infraestructura y permitan al equipo concentrar sus esfuerzos en el desarrollo, integración y pruebas de la aplicación.

## Plan de trabajo

El proyecto se desarrollará de manera incremental, tomando como referencia las fechas de entrega establecidas para el Trabajo Final.

### Etapa 1 — Propuesta y preparación del proyecto
**10/08 al 30/08**

- Definición de la problemática.
- Propuesta de solución y valor agregado.
- Definición del alcance.
- Selección y justificación del stack tecnológico.
- Elaboración del plan de trabajo.
- Creación y configuración del repositorio único de GitHub.
- Organización inicial de la documentación y estructura del proyecto.

**Entregable:** propuesta, plan de trabajo y repositorio único de GitHub.

### Etapa 2 — Arquitectura, módulos y diseño de datos
**31/08 al 27/09**

- Consolidación de requisitos y reglas de negocio.
- Definición de módulos.
- Modelado de entidades y relaciones.
- Diseño del esquema relacional de la base de datos.
- Definición de claves, restricciones e índices relevantes.
- Definición inicial de la arquitectura.
- Preparación de las estructuras base del frontend y backend.
- Documentación de los avances en el repositorio.

**Entregable:** esquema de base de datos y listado de módulos para su aprobación.

### Etapa 3 — Desarrollo del núcleo funcional
**28/09 al 18/10**

- Autenticación, usuarios y roles.
- Gestión de responsables.
- Gestión de alumnos.
- Gestión de categorías.
- Preinscripción y validación.
- Búsquedas y filtros.
- Integración progresiva entre base de datos, backend y frontend.

**Entregable:** primera versión integrada del núcleo administrativo.

### Etapa 4 — Gestión económica
**19/10 al 02/11**

- Configuración y generación de cuotas.
- Vencimientos e intereses.
- Seguimiento de deuda.
- Registro de pagos.
- Generación y consulta de recibos.
- Cobros correspondientes a eventos.
- Cobros correspondientes a indumentaria.
- Pagos parciales para los conceptos que los admitan.

**Entregable:** circuito de gestión económica integrado y funcional.

### Etapa 5 — Funcionalidades complementarias e integración
**03/11 al 09/11**

- Portal de consulta para responsables.
- Dashboard y estadísticas.
- Reportes y exportación de información.
- Auditoría.
- Opciones para imprimir o compartir comprobantes.
- Implementación y validación del funcionamiento offline y sincronización.
- Pruebas de integración.

**Entregable:** versión integrada con funcionalidades administrativas, de consulta y análisis.

### Etapa 6 — Pruebas, despliegue y entrega final
**10/11 al 14/11**

- Pruebas funcionales y de integración.
- Corrección de errores.
- Revisión de seguridad y permisos.
- Revisión de rendimiento.
- Validación general del sistema.
- Despliegue de al menos un componente principal en un servicio online.
- Actualización de documentación.
- Elaboración del informe final.
- Preparación y grabación del video demostrativo.

**Entregable:** repositorio completo, aplicación final, despliegue online, documentación, informe y video.

## Organización del trabajo

El proyecto se desarrollará de manera colaborativa utilizando **Git y GitHub**.

Este repositorio será la fuente central del proyecto y contendrá el código fuente, los elementos relacionados con la base de datos y la documentación generada durante las distintas etapas.

Las tareas se distribuirán entre los integrantes según las necesidades de cada etapa, procurando realizar revisiones cruzadas y mantener conocimiento compartido de las diferentes partes del sistema.

Para el seguimiento de las actividades se prevé utilizar **GitHub Projects** mediante un tablero de tareas pendientes, en desarrollo y finalizadas.

La planificación será revisada durante el avance del proyecto. Ante posibles desvíos de tiempo o dificultades técnicas se priorizarán las funcionalidades centrales de gestión de alumnos, responsables, cuotas y pagos, procurando mantener la estabilidad y calidad de la versión final.

## Estructura inicial del repositorio

```text
rojas-fc-gestion/
├── frontend/
├── backend/
├── database/
├── docs/
├── .gitignore
└── README.md
```

- `frontend/`: aplicación desarrollada con React y TypeScript.
- `backend/`: API y lógica de negocio desarrolladas con Java y Spring Boot.
- `database/`: scripts, esquemas y recursos relacionados con MySQL.
- `docs/`: documentación, diagramas, informes y entregas del proyecto.

## Documentación de las entregas

- [`docs/1ra Entrega - Propuesta de Proyecto.md`](docs/1ra%20Entrega%20-%20Propuesta%20de%20Proyecto.md) — Propuesta, alcance y stack tecnológico. **Aprobada por el tutor.**
- [`docs/requisitos/requisitos-funcionales.md`](docs/requisitos/requisitos-funcionales.md) — Requisitos funcionales consolidados.
- [`docs/requisitos/requisitos-no-funcionales.md`](docs/requisitos/requisitos-no-funcionales.md) — Requisitos no funcionales consolidados.
- [`docs/reglas-negocio/reglas-de-negocio.md`](docs/reglas-negocio/reglas-de-negocio.md) — Reglas de negocio consolidadas.
- [`docs/modulos/propuesta-modulos.md`](docs/modulos/propuesta-modulos.md) — Propuesta de módulos del sistema.
- [`docs/2da Entrega - Diseño y Modulos.md`](docs/2da%20Entrega%20-%20Diseño%20y%20Modulos.md) — Esquema de base de datos (diagrama entidad-relación).
- [`database/schema.sql`](database/schema.sql) — Script DDL completo del esquema relacional (MySQL).

## Estado del proyecto

🟡 **En desarrollo — Segunda etapa: diseño y módulos, en revisión previa a la aprobación del tutor.**
