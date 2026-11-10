# Sistema de Gestión Rojas FC

**Universidad Tecnológica Nacional (UTN)**
Tecnicatura Universitaria en Programación a Distancia

**Materia:** Proyecto Final — Trabajo Final Integrador

**Proyecto:** Sistema de Gestión Rojas FC

**Grupo:** 142

**Integrantes:** Marina Giselle Cordero, Silvia Giardini y Alex Pedro Dauria

**Tutor:** Sebastián Bruselario

**Año:** 2026

**Repositorio:** https://github.com/Marigi84/rojas-fc-gestion
**Sistema en línea:** https://rojasfc-gestion.vercel.app
**Video de presentación:** [enlace a completar]

---

## 1. Problema y contexto

### 1.1. Situación que dio origen al proyecto

Rojas Fútbol Club es una escuela de fútbol de Córdoba, Argentina, con aproximadamente 140 alumnos activos. Su gestión administrativa se realiza con planillas de Excel y, en algunos procesos, con registros en papel.

La incorporación de un alumno comienza por una consulta por WhatsApp. Si la familia decide continuar después de una clase de prueba, el coordinador pide los datos del niño o niña y de su responsable, y los carga a mano en una planilla. La información queda registrada, pero no relacionada: buscar y filtrar alumnos, organizarlos por categoría o reutilizar esos datos en otros procesos requiere trabajo manual.

Con las cuotas ocurre algo similar. La escuela fija un valor general y un vencimiento (el día 13 de cada mes), y después del vencimiento aplica un interés del 10 %, sin importar los días de atraso. Los pagos se hacen en efectivo o por transferencia. Saber quién debe y a quién corresponde cobrarle interés se resuelve revisando la planilla a ojo, y cada pago se acredita con un recibo en papel, sin un registro digital que permita consultarlo después.

### 1.2. Usuarios y actores

- **Administrador / Coordinador:** usuario principal. Registra alumnos y responsables, configura y controla las cuotas, registra pagos y emite recibos.
- **Responsable / Tutor:** adulto a cargo de uno o más alumnos. Consulta la situación de sus alumnos (cuotas, pagos y recibos) desde un portal propio.
- **Alumno:** entidad central del dominio, pero no usuario del sistema. Sus datos los gestiona el Administrador.

### 1.3. Evidencia utilizada para validar el problema

- El relevamiento del proceso real del club, descripto en la Primera Entrega (sección 1), hecho con el coordinador de la escuela.
- La conformidad explícita del coordinador de Rojas FC con el sistema, dada por el chat del grupo el 04/11/2026 (registrada en `docs/validacion.md`).

[A completar si se agrega: qué se le mostró al coordinador y qué comentó.]

### 1.4. Necesidad concreta

El problema no es el uso de Excel en sí, sino que el proceso actual no permite centralizar, relacionar y consultar la información: el coordinador no puede saber de inmediato qué alumnos están activos, quién debe cuotas, cuánto se recaudó o qué recibos se emitieron, y las familias no tienen forma de consultar su situación sin preguntarle a él.

---

## 2. Objetivos y alcance

### 2.1. Objetivo general

Desarrollar una aplicación web que centralice la información administrativa de Rojas FC y automatice los controles que hoy son manuales: el registro de alumnos y responsables, la generación de cuotas con su interés por mora, el registro de pagos con su recibo digital y la consulta de esa información por parte del club y de las familias.

### 2.2. Alcance implementado

| Módulo | Qué hace |
|---|---|
| Autenticación y autorización | Ingreso con DNI y contraseña, roles Administrador y Responsable, contraseña provisoria que se cambia en el primer ingreso, recuperación de contraseña por correo y configuración del primer administrador. |
| Alumnos, responsables y categorías | Alta, consulta, modificación, baja lógica y reactivación de alumnos; alta, consulta y modificación de responsables; asociación y desvinculación; categoría calculada por año de nacimiento. |
| Búsqueda y listado de alumnos | Búsqueda por nombre, apellido o DNI, filtros combinables (año de nacimiento, estado, morosidad) y paginación. |
| Cuotas | Configuración de importe, vencimiento e interés por período; generación automática mensual para los alumnos activos; cálculo del interés por mora; listado de morosos. |
| Pagos y recibos | Registro del pago total de una cuota en efectivo o transferencia, emisión automática del recibo, historial de pagos y anulación con motivo, conservando el recibo como anulado. |
| Dashboard | Alumnos activos, alumnos por categoría, cantidad de morosos y recaudación del mes. |
| Portal del Responsable | Consulta de los alumnos a cargo con sus cuotas, pagos y recibos, y descarga del recibo. |

El sistema está desplegado en línea (Vercel, Render y Aiven) y se puede probar con los usuarios de demostración que figuran en el README.

### 2.3. Funcionalidades que quedaron fuera y por qué

Durante el desarrollo se ajustó el alcance de la Primera Entrega, según lo previsto en su sección 6.7: ante desvíos en los tiempos se priorizan las funcionalidades centrales de alumnos, responsables, cuotas y pagos. Los ajustes están registrados en `docs/alcance/revision-del-alcance.md`.

| Funcionalidad | Decisión | Motivo |
|---|---|---|
| Preinscripción en línea | Se reprograma como evolución posterior. El Administrador registra a los alumnos. | Priorizar el circuito central. |
| Varios vencimientos por período | Se usa un único vencimiento por período. | Simplifica la configuración y el cálculo del interés, y coincide con la práctica real del club (día 13). |
| Eventos e indumentaria, con pagos parciales | Se reprograma. | Priorizar el circuito central y entregar un sistema completo y probado. |
| Reportes y exportación a Excel | Se reprograma. | Ídem. |
| Compartir recibos por WhatsApp | Se reprograma. | Ídem. |
| Funcionamiento sin conexión y sincronización | Se reprograma. | Ídem. |

En total quedan fuera 16 de los 57 requisitos funcionales. El esquema de la base no cambia: conserva las tablas de eventos, participaciones y encargos, y el estado de sincronización de los pagos, de modo que estas funcionalidades pueden agregarse más adelante sin rediseñar el modelo.

---

## 3. Análisis y modelo

### 3.1. Principales entidades y relaciones

El modelo conceptual se representó con un Diagrama Entidad-Relación en notación de Chen (`docs/entidades-relaciones-cardinalidad/`).

- **Persona** (clave: DNI) se especializa en **Alumno**, **Responsable** y **Administrador**. La especialización es superpuesta, porque una misma persona puede cumplir más de un rol, y total, porque toda persona registrada cumple al menos uno. Así una persona se registra una sola vez aunque, por ejemplo, sea responsable y administrador.
- **Responsable** "está a cargo de" **Alumno** (M:N), con el vínculo (madre, padre, tutor u otro) como atributo de la relación.
- **Permanencia** (entidad débil de Alumno) registra cada período en que el alumno estuvo activo, con fecha de alta y de baja. Permite conservar el historial ante bajas y reincorporaciones.
- **Período** (mes y año) guarda las condiciones de las cuotas: importe, vencimiento y porcentaje de interés.
- **Cuota** (entidad débil de Alumno y Período): un alumno no puede tener dos cuotas del mismo período.
- **Pago** (número de pago asignado por el sistema) salda una cuota, se realiza con un **Medio de pago** (efectivo o transferencia) y genera un **Recibo**.
- El modelo incluye además **Evento**, **Participación** y **Encargo**, previstos para la gestión de eventos e indumentaria.

[Insertar aquí la imagen del DER: `docs/entidades-relaciones-cardinalidad/img/der-conceptual.svg`, y si hace falta las vistas 1 y 2.]

### 3.2. Reglas de negocio relevantes

Las reglas completas están en `docs/reglas-negocio/reglas-de-negocio.md`. Las más importantes para el sistema implementado son:

- **RN-01:** una persona se identifica por su DNI y se registra una sola vez.
- **RN-03:** una persona no puede ser alumno activo y responsable al mismo tiempo.
- **RN-05:** todo alumno tiene al menos un responsable.
- **RN-13:** la categoría se determina por el año de nacimiento.
- **RN-14 y RN-16:** las cuotas se generan solo para los alumnos activos, y una por alumno y período.
- **RN-19 y RN-20:** el interés por mora se aplica una sola vez sobre el importe, después del vencimiento.
- **RN-22:** las condiciones de un período no se pueden modificar una vez generadas sus cuotas.
- **RN-28 y RN-29:** la cuota se paga completa, y tiene a lo sumo un pago vigente.
- **RN-30 a RN-32:** los pagos no se borran, se anulan con motivo, y un pago anulado no cuenta para el estado ni la recaudación.
- **RN-33:** todo pago confirmado genera su recibo.

### 3.3. Decisiones de modelado importantes

- **El DNI como clave de la persona.** Evita personas duplicadas (una de las observaciones del tutor sobre versiones anteriores del modelo) y es el dato con el que el club identifica a alumnos y responsables.
- **Atributos derivados que no se guardan.** La categoría, el estado del alumno, la morosidad y el monto a pagar de una cuota se calculan, no se almacenan. Así nunca quedan desactualizados. En el modelo físico se resuelven con vistas (`vista_alumno`, `vista_cuota`).
- **Historial en lugar de borrado.** Las bajas se registran en la permanencia y los pagos se anulan; ni los pagos, ni los recibos, ni las permanencias se pueden borrar.
- **Atributos técnicos fuera del modelo conceptual.** La contraseña y el estado de sincronización del pago no son datos del negocio, por eso aparecen recién en el modelo físico.
- **Reglas controladas en la base.** Las reglas que no se pueden expresar en el diagrama se controlan en MySQL con restricciones y triggers, de modo que se cumplen aunque alguien acceda a la base sin pasar por la aplicación. El esquema final tiene 14 tablas, 15 triggers, 5 vistas y 1 procedimiento (`database/schema.sql`).

---

## 4. Arquitectura y tecnologías

### 4.1. Arquitectura final

El sistema sigue una arquitectura web cliente-servidor. El frontend se encarga de la interacción con el usuario y consume una API REST. El backend concentra la lógica de negocio, la seguridad, las validaciones y el acceso a datos, y es el único que se conecta con la base.

El backend está organizado en capas: controladores REST, servicios y persistencia. Cada capa se comunica solo con la siguiente:

```
Usuario → Frontend (React) → API REST (Spring Boot) → Servicios → Persistencia (JPA) → MySQL
```

[Insertar diagrama de arquitectura con los tres servicios del despliegue.]

### 4.2. Tecnologías y responsabilidad de cada componente

| Componente | Tecnología | Responsabilidad | Por qué se eligió |
|---|---|---|---|
| Frontend | React + TypeScript + Vite | Pantallas, formularios y navegación. | Interfaz basada en componentes; TypeScript detecta errores antes de ejecutar; el equipo ya la conocía de la carrera. |
| Estilos | Tailwind CSS | Diseño responsive con la paleta del club. | Rapidez para construir pantallas consistentes. |
| Datos del servidor | TanStack Query | Pedidos a la API, caché y actualización de datos. | Evita manejar a mano los estados de carga y error. |
| Estado global | Zustand | Sesión del usuario. | Simple y liviano. |
| Backend | Java 17 + Spring Boot 3 | API REST, reglas de negocio y seguridad. | Adecuado para lógica de negocio y datos estructurados; experiencia previa del equipo. |
| Persistencia | Spring Data JPA / Hibernate | Acceso a la base desde los servicios. | Separa la lógica de negocio del SQL. |
| Base de datos | MySQL 8 | Datos, integridad, triggers y vistas. | El dominio es relacional y los pagos necesitan transacciones; experiencia previa del equipo. |
| Autenticación | JWT + BCrypt | Sesión sin estado y contraseñas cifradas. | Estándar para APIs REST. |

### 4.3. Servicios externos e integraciones

| Servicio | Uso | Plan |
|---|---|---|
| Vercel | Publica el frontend (https://rojasfc-gestion.vercel.app). | Gratuito |
| Render | Ejecuta el backend en un contenedor Docker (https://rojasfc-backend.onrender.com). | Gratuito |
| Aiven | Base MySQL 8.4 administrada, con conexión cifrada obligatoria. | Gratuito |
| Correo SMTP | Envío del enlace para recuperar la contraseña del Administrador (RF-50). | Gmail del club |
| SonarCloud | Análisis de calidad y seguridad del código en cada PR. | Gratuito |

Se eligieron servicios administrados (PaaS) para no tener que configurar servidores, como preveía la Primera Entrega (sección 5.4). Vercel publica automáticamente cada cambio que entra a `main`, y Render hace lo mismo con el backend.

### 4.4. Consideraciones de seguridad

- **Contraseñas** guardadas con BCrypt, nunca en texto plano.
- **Sesión con JWT** que el frontend envía en cada pedido. Vence a las 8 horas.
- **Autorización en el backend:** el frontend oculta las opciones que no corresponden, pero la validación definitiva la hace siempre el backend. El Portal del Responsable toma el DNI del token, nunca de un parámetro, así que un responsable no puede ver datos de alumnos que no tiene a cargo, ni saber si existe un recibo de otra familia (RNF-03).
- **Contraseña provisoria:** el Administrador asigna una contraseña provisoria a cada responsable y a cada administrador nuevo, que deben cambiarla en el primer ingreso. Hasta entonces, no pueden hacer otra cosa (RNF-01).
- **Recuperación de contraseña:** la respuesta es la misma aunque el correo no esté registrado, así no se puede averiguar qué correos son de administradores. El enlace vence a los 30 minutos y sirve una sola vez.
- **Configuración fuera del código:** las credenciales de la base, la clave del JWT y el correo se cargan como variables de entorno en cada servicio, nunca en el repositorio.
- **Despliegue:** la conexión con la base es cifrada (SSL obligatorio), el backend solo acepta pedidos del dominio del frontend (CORS) y el contenedor corre con un usuario sin privilegios de administrador.

### 4.5. Consistencia de los datos

Las operaciones que modifican varias tablas se hacen en una sola transacción: por ejemplo, el alta de un alumno con su responsable y su permanencia, o el registro de un pago con su recibo. Si algo falla, no queda nada a medias. Además, los triggers de la base rechazan cualquier operación que viole una regla (por ejemplo, un pago por un monto distinto del de la cuota), y el backend traduce ese rechazo en un mensaje claro para el usuario.

---

## 5. Implementación

[Esta sección la escribe cada integrante sobre los módulos que desarrolló. Para cada módulo: qué hace, un recorrido importante con capturas, y las dificultades técnicas y cómo se resolvieron.]

### 5.1. Autenticación y autorización
[A completar por quien lo desarrolló.]

### 5.2. Alumnos, responsables y categorías
[A completar.]

### 5.3. Búsqueda y listado de alumnos
[A completar.]

### 5.4. Cuotas
[A completar.]

### 5.5. Pagos y recibos
[A completar.]

### 5.6. Dashboard y Portal del Responsable
[A completar.]

### 5.7. Despliegue en línea
[A completar.]

### 5.8. Dificultades técnicas y cómo se resolvieron

Algunas para elegir o ampliar:

- **Búsqueda sin tildes:** buscar "Gomez" tenía que encontrar "Gómez". Se resolvió en la consulta a la base (#59).
- **Alta en una sola transacción:** el alta de un alumno crea la persona, el alumno, la permanencia y el vínculo con su responsable. Se hizo en una sola transacción para que un error no deje datos a medias (#51).
- **Tildes rotas al cargar la base en la nube:** la consola de Windows enviaba los datos en otra codificación. Se resolvió agregando `SET NAMES utf8mb4` a los scripts y cargándolos con la codificación indicada.
- **Zona horaria de la base:** el servidor de la base en la nube estaba en hora UTC, mientras que el sistema usa la hora argentina. El día del vencimiento, entre las 21 y las 24 h, el monto calculado y el validado por la base no coincidían. Se resolvió configurando la base en hora argentina (-03:00).
- **Correo bloqueado en el plan gratuito:** Render no permite enviar correos en su plan gratuito, así que la recuperación de contraseña funciona en local pero no en línea. Se documentó como limitación.
- **Contenedor con permisos de administrador:** SonarCloud marcó que el contenedor del backend corría como root. Se agregó un usuario sin privilegios.

---

## 6. Pruebas y validación

### 6.1. Qué se probó

Cada módulo tiene un documento de pruebas en `docs/pruebas/`, con los pasos, el resultado esperado y el obtenido:

| Documento | Qué cubre |
|---|---|
| `ingreso.md` | Configuración inicial, ingreso, cambio y recuperación de contraseña, protección de rutas. |
| `gestion-alumnos.md` | Alta, consulta, modificación, baja y reactivación de alumnos. |
| `gestion-responsables.md` | Alta, consulta, modificación, asociación y desvinculación de responsables. |
| `listado-alumnos.md` | Búsqueda, filtros combinados y paginación. |
| `cuotas.md` | Períodos, generación, interés por mora y morosos. |
| `pagos.md` | Registro y anulación de pagos, historial y recibos. |
| `dashboard-portal.md` | Dashboard y Portal del Responsable. |
| `despliegue.md` | Sistema en línea: base, backend, frontend, CORS, tildes y recorrido completo. |

### 6.2. Pruebas automáticas

- **Backend, unitarias** (`mvn test`): prueban la lógica de los servicios sin base de datos, por ejemplo `CuotaServiceTest` y `PagoServiceTest`.
- **Backend, integración contra MySQL** (`TESTS_MYSQL=true`): crean una base aparte desde `schema.sql` y prueban las reglas que controla la base, por ejemplo `AlumnoIntegracionTest`, `CuotaIntegracionTest` y `DashboardPortalIntegracionTest`. En total pasan 92 de 92 pruebas del backend.
- **Navegador** (`pruebas/navegador/`): recorridos automatizados que usan el sistema como lo haría un usuario, sobre una base de pruebas propia. Resultados: alumnos 22 de 22, responsables y listado 19 de 19, cuotas 22 de 22, Dashboard y Portal 23 de 23, pagos 22 de 22.

[Verificar los números al cierre, después de mergear los últimos PR.]

### 6.3. Cómo se comprueban las reglas críticas

- **Pagos:** el importe no lo envía la pantalla, lo calcula la base con el interés si corresponde, y un trigger rechaza un pago por un monto distinto. Se prueba que una cuota no se pueda pagar dos veces, que anular un pago la deje impaga, y que el recibo anulado se conserve.
- **Cuotas:** se prueba que se generen solo para alumnos activos, una por período, y que no se puedan cambiar las condiciones de un período con cuotas generadas.
- **Seguridad:** se prueba que un responsable no pueda ver ni descargar datos de otra familia, y que con la contraseña provisoria no se pueda hacer otra cosa que cambiarla.
- **Integridad de personas:** se prueba que un alumno activo no pueda ser responsable, y que un alumno no quede sin responsable.

### 6.4. Validación con el caso real

El coordinador de Rojas FC dio su conformidad con el sistema por el chat del grupo el 04/11/2026 (`docs/validacion.md`).

[A completar: qué se le mostró y qué comentó. Agregar captura del chat si se puede.]

### 6.5. Evidencia de funcionamiento

[Insertar capturas: ingreso, dashboard, alta de alumno, listado, cuotas y morosos, registro de pago y recibo, Portal del Responsable, versión en celular.]

---

## 7. Resultados y conclusiones

### 7.1. Qué resolvió el proyecto

[A completar entre los tres. Ideas: la información del club queda en un solo lugar y relacionada; las cuotas se generan solas y el interés se calcula sin revisar la planilla; cada pago tiene su recibo digital; el coordinador ve morosos y recaudación al instante; las familias consultan su situación sin preguntar.]

### 7.2. Qué cambió respecto de la situación inicial

[A completar: comparar el proceso con Excel y papel contra el proceso con el sistema.]

### 7.3. Limitaciones conocidas

- Al cambiar una contraseña, una sesión ya abierta en otro dispositivo sigue valiendo hasta que vence.
- Quien se da de alta después de generadas las cuotas del mes empieza a pagar el mes siguiente.
- La fecha del pago es la del día en que se registra: un pago cargado después del vencimiento lleva interés aunque se haya hecho antes.
- La búsqueda en Pagos y en Responsables es solo por DNI.
- La recuperación de contraseña por correo no funciona en línea, porque el plan gratuito de Render bloquea el envío de correos.
- En línea se usan planes gratuitos: el primer pedido después de un rato sin uso tarda alrededor de un minuto, y los datos son de demostración.

### 7.4. Trabajo futuro

- Eventos e indumentaria con pagos parciales; reportes y exportación a Excel; compartir recibos por WhatsApp; funcionamiento sin conexión. El modelo de datos ya los contempla.
- Preinscripción en línea por parte de las familias.
- Búsqueda por nombre en Pagos y en Responsables.
- Mejoras de uso: secciones desplegables en el Portal del Responsable, ir del Dashboard al listado filtrado por categoría, ocultar el monto de la recaudación.
- Para el uso real en el club: planes pagos en la nube, envío de correos por una API y adecuación a la Ley 25.326 de protección de datos personales.

### 7.5. Aprendizajes y decisiones relevantes

[A completar entre los tres. Ideas: trabajo con ramas, PR y revisión cruzada; rehacer el DER a partir de las devoluciones del tutor; controlar reglas en la base además del backend; ajustar el alcance a tiempo para entregar un sistema completo y probado; primer despliegue con base de datos en la nube.]

### 7.6. Implementado y probado frente a lo proyectado

Todo lo descripto en las secciones 2.2, 5 y 6 está implementado, probado y disponible en línea. Lo listado en 2.3 y 7.4 es trabajo proyectado y no forma parte de la entrega.

---

## Enlace al video

[Enlace al video final, a completar.]
