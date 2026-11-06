# Checklist de la entrega final

Checklist orientativo de la cátedra, con el estado de cada punto en nuestro proyecto. La página original guarda los checks solo en el navegador de cada uno, así que este archivo es la referencia común del grupo.

**Estado al 06/11/2026. Fecha de entrega final estimada: 14/11/2026.**

| Marca | Significa | ¿Se tilda? |
|---|---|---|
| ✅ | Hecho: comprobado en el repositorio o confirmado por el grupo | Sí |
| 🟡 | Hecho en parte: la nota dice qué falta | No |
| ❌ | Falta | No |
| 👤 | No se puede comprobar desde el repositorio: lo confirma el grupo | Si corresponde |
| ⏳ | Todavía no corresponde: es para el final, el video o la mesa | No |

Las notas en cursiva son nuestras; el resto es el texto de la cátedra. Si cambia el estado de un punto, se actualiza acá por PR.

## Resumen

| Sección | ✅ | 🟡 | ❌ | 👤 | ⏳ | Total |
|---|---|---|---|---|---|---|
| 1. Requisitos generales y cronograma | 8 | 1 | 1 | 3 | 1 | 14 |
| 2. Repositorio único en GitHub | 13 | 3 | 0 | 0 | 0 | 16 |
| 3. Sistema final, despliegue y README | 15 | 2 | 2 | 0 | 0 | 19 |
| 4. Informe Final en PDF | 0 | 0 | 42 | 0 | 0 | 42 |
| 5. Video final | 0 | 0 | 31 | 1 | 14 | 46 |
| 6. Checklist final para finalizar la cursada | 20 | 5 | 19 | 0 | 3 | 47 |
| 7. Checklist para la presentación / defensa final en mesa | 5 | 1 | 5 | 4 | 33 | 48 |
| 8. Criterios finales importantes | 1 | 0 | 0 | 3 | 2 | 6 |
| **Total** | **62** | **12** | **100** | **11** | **53** | **238** |

La página de la cátedra indica 236 puntos; acá hay 238 porque algunas frases pueden no ser casilleros en el original. Se transcribieron todas para no perder ninguna.

## Lo más urgente

Cada tarea tiene su issue y su responsable en el tablero.

| Tarea | Issue | Responsable |
|---|---|---|
| Cuotas | #63 | Alex |
| Pagos y recibos | #64 | Silvia |
| Revisión del alcance | #65 | Alex |
| Dashboard | #66 | Alex |
| Portal del Responsable | #67 | Alex |
| Despliegue | #60 | Marina |
| Informe Final | #68 | Marina arma la estructura; cada uno escribe su parte |
| README final | #71 | Alex |
| Datos de ejemplo para la demo | #74 | Alex |
| Pruebas de navegador en el repositorio | #75 | Alex |
| Prueba final, pruebas cruzadas y corrección de errores | #73 | Los tres |
| Video | #69 | Los tres |
| Entrega en el aula | #76 | Alex |
| Defensa en mesa, después de la entrega | #77 | Los tres |

Fecha de corte, desde la que se deja de agregar funciones: [A completar].

---

## 1. Requisitos generales y cronograma

### 1.1 Requisitos académicos y administrativos

- [x] ✅ Grupo de 2 o 3 integrantes. *Somos 3.*
- [x] ✅ Tutor/a elegido/a en la plataforma. *Sebastián Bruselario. Confirmado por el grupo.*
- [x] ✅ Confirmación de lectura y aceptación de condiciones marcada como finalizada. *Confirmado por el grupo.*
- [ ] 👤 Para presentar informe final, proyecto y defensa oral: acreditar la totalidad de las asignaturas de la carrera aprobadas, conforme al Plan de Estudios vigente. *Cada integrante.*
- [ ] 👤 Cursada aprobada y todas las instancias previas del TFI realizadas y aprobadas para acceder a la mesa.

### 1.2 Entregas del aula

**1.ª entrega: Propuesta de Proyecto y Repositorio (fecha máxima 30/08)**

- [x] ✅ Propuesta del proyecto con cliente real o inventiva propia. *Cliente real: Rojas FC. `docs/1ra Entrega - Propuesta de Proyecto.md`.*
- [x] ✅ Plan de trabajo con tecnologías: lenguajes Frontend/Backend, Frameworks, Base de Datos y Plataforma. *Secciones 5 y 6 de la 1ra entrega.*
- [x] ✅ Enlace al repositorio único de GitHub declarado formalmente. *Está en la 1ra entrega.*

**2.ª entrega: Diseño y Módulos (fecha máxima 27/09, condición de Regular)**

- [x] ✅ Esquema de base de datos relacional o documental. *`database/schema.sql` y `docs/esquema-relacional/`.*
- [x] ✅ Listado de módulos a desarrollar en el repositorio. *`docs/modulos/propuesta-modulos.md`.*
- [ ] 👤 Aprobación previa y explícita del tutor y luego del comité. *En el repositorio figura "entregada para revisión del tutor".*

**Entrega final: Informe y Video**

- [ ] 🟡 Repositorio completo con código, bases de datos, despliegue online y documentación escrita. *Falta terminar el código y el informe.*
- [ ] ❌ Video explicativo, preferentemente en inglés.
- [ ] ⏳ Defensa oral: presentación y justificación ante el comité de trabajo final en mesa de examen.

---

## 2. Repositorio único en GitHub

- [x] ✅ Todo el proyecto vive en un único repositorio de GitHub, declarado formalmente en la 1.ª entrega.
- [x] ✅ El historial permite reconstruir claramente la evolución y el estado final del trabajo. *Commits, PRs revisados, issues y tablero.*

El repositorio incluye:

- [x] ✅ README.md principal con documentación, instrucciones de instalación, tecnologías utilizadas e integrantes. *Existe; ver abajo "README actualizado".*
- [x] ✅ Código fuente estructurado en carpetas claras según la arquitectura: /src, /frontend, /backend, etc.
- [x] ✅ Base de datos: scripts DDL/DML, esquemas o migraciones, relacionales o documentales. *`database/schema.sql`.*
- [x] ✅ Código actualizado. *Sin PRs abiertos. Revisar de nuevo al entregar.*
- [x] ✅ Instrucciones claras de instalación y/o ejecución. *README principal, `backend/README.md` y `frontend/README.md`.*
- [x] ✅ Configuración necesaria. *Tabla de variables en `backend/README.md`.*
- [x] ✅ Base de datos y migraciones cuando corresponda. *No hay migraciones: `schema.sql` crea todo.*
- [x] ✅ Variables de entorno documentadas en un .env.example o similar. *`frontend/.env.example` y la tabla de `backend/README.md`.*
- [x] ✅ Sin credenciales, tokens ni secretos publicados. *Revisado por patrones en el código y el historial. Repetir al entregar.*
- [x] ✅ Pruebas relevantes incluidas. *50 tests de backend y `docs/pruebas/`.*
- [ ] 🟡 README actualizado. *Refleja el estado real; faltan los enlaces al informe y al video (#71).*
- [x] ✅ Enlace al sistema desplegado. *Al principio del README.*
- [ ] 🟡 Documentación necesaria para comprender y evaluar el proyecto. *Hay mucha en `docs/`; falta el informe.*
- [ ] 🟡 Evitar enlaces dispersos: todo debe estar referenciado desde el README principal. *No enlaza `docs/pruebas/` ni la 2da entrega.*

---

## 3. Sistema final, despliegue y README

### 3.1 Sistema y despliegue

- [ ] 🟡 Versión definitiva y funcional del sistema. *Falta descargar el recibo desde el Portal (#67) y la prueba final (#73).*
- [x] ✅ Instrucciones claras de instalación y/o ejecución.
- [x] ✅ Configuración necesaria documentada.
- [x] ✅ Base de datos y migraciones listas para reconstruir el entorno. *Probado: base recreada desde `schema.sql` el 03/11.*
- [x] ✅ Pruebas relevantes ejecutables. *`mvn test`; con `TESTS_MYSQL=true` suma los de integración. 50 de 50 el 03/11.*
- [x] ✅ Al menos un componente principal desplegado y funcionando online: *están los tres. Ver `docs/pruebas/despliegue.md`.*
  - [x] ✅ Frontend en Vercel / Netlify / similar, o
  - [x] ✅ Backend en Render / Railway / similar, o
  - [x] ✅ Base de datos en MongoDB Atlas / Supabase / PlanetScale / Aiven / similar.
- [x] ✅ Enlace al sistema desplegado incluido en el README.
- [x] ✅ Limitaciones, dependencias externas o funcionalidades simuladas documentadas con claridad. *README, "Estado del proyecto". Repasar al entregar.*

### 3.2 README final

Debe permitir encontrar fácilmente:

- [x] ✅ Descripción del proyecto.
- [x] ✅ Instrucciones de ejecución.
- [x] ✅ Sistema desplegado, si corresponde.
- [ ] ❌ Informe Final en PDF.
- [ ] ❌ Video de presentación final.
- [x] ✅ Integrantes del grupo.
- [x] ✅ Tecnologías utilizadas.
- [ ] 🟡 Todo referenciado desde el README, sin enlaces dispersos.

---

## 4. Informe Final en PDF

*El informe no está empezado. Las notas indican de dónde sacar el material.*

- [ ] ❌ PDF incorporado dentro del repositorio.
- [ ] ❌ Permite comprender el proyecto sin necesidad de revisar inicialmente todo el código.

**Portada**

- [ ] ❌ Universidad y carrera.
- [ ] ❌ Materia: Proyecto Final.
- [ ] ❌ Nombre del proyecto.
- [ ] ❌ Número de grupo. *Somos el grupo 142.*
- [ ] ❌ Nombre completo de todos los integrantes.
- [ ] ❌ Año.

**Problema y contexto**

- [ ] ❌ Situación que dio origen al proyecto. *1ra entrega, sección 1.*
- [ ] ❌ Usuarios o actores involucrados. *1ra entrega, sección 3.*
- [ ] ❌ Evidencia utilizada para validar el problema: entrevistas, encuestas, observaciones, casos reales, documentación, etc. *No hay entrevistas ni encuestas documentadas. Se usa lo relevado en la 1ra entrega, sección 1.*
- [ ] ❌ Necesidad concreta que se buscó resolver. *1ra entrega, secciones 1 y 2.*

**Objetivos y alcance**

- [ ] ❌ Objetivo general. *1ra entrega, sección 2.*
- [ ] ❌ Alcance finalmente implementado. *Se define al cerrar el sistema.*
- [ ] ❌ Funcionalidades que quedaron fuera y razones del recorte cuando corresponda. *`docs/alcance/revision-del-alcance.md`.*

**Análisis y modelo**

- [ ] ❌ Principales entidades y relaciones. *`docs/entidades-relaciones-cardinalidad/modelo-conceptual.md`.*
- [ ] ❌ Reglas de negocio relevantes. *`docs/reglas-negocio/reglas-de-negocio.md`.*
- [ ] ❌ Decisiones de modelado importantes. *`docs/esquema-relacional/claves-restricciones.md`.*
- [ ] ❌ Diagramas que ayuden a comprender el dominio. *DER y diagrama relacional en SVG, ya en `docs/`.*

**Arquitectura y tecnologías**

- [ ] ❌ Arquitectura final. *`docs/arquitectura/arquitectura-inicial.md`, a actualizar con lo real.*
- [ ] ❌ Frontend, backend, base de datos y servicios externos utilizados. *Incluye los del despliegue y el correo.*
- [ ] ❌ Motivo de las principales decisiones tecnológicas. *1ra entrega, sección 5.*
- [ ] ❌ Integraciones realizadas. *Correo SMTP para recuperar la contraseña (RF-50).*
- [ ] ❌ Consideraciones de seguridad, autenticación, autorización, concurrencia, sincronización u otras relevantes. *Arquitectura, secciones 5.5 y 8; `backend/README.md`, "Autenticación".*
- [ ] ❌ Explicar por qué se usó cada tecnología y qué responsabilidad cumple cada componente.

**Implementación**

- [ ] ❌ Principales módulos desarrollados. *Según lo que quede hecho.*
- [ ] ❌ Recorridos o casos de uso importantes. *`docs/pruebas/`.*
- [ ] ❌ Capturas o diagramas cuando faciliten la explicación. *Se suman al informe, no al repositorio. `pruebas/navegador` las genera en su carpeta `capturas/`.*
- [ ] ❌ Dificultades técnicas relevantes y forma en que fueron resueltas. *Ejemplos: búsqueda sin tilde (`LOCATE` y `LIKE`, #59), alta en una sola transacción (#51).*

**Pruebas y validación**

- [ ] ❌ Qué aspectos fueron probados. *`docs/pruebas/`.*
- [ ] ❌ Casos de prueba relevantes. *`docs/pruebas/`, `backend/src/test/` y `pruebas/navegador/`.*
- [ ] ❌ Evidencia de funcionamiento. *Capturas en el informe y casos en `docs/pruebas/`.*
- [ ] ❌ Validación realizada con usuarios o con el caso real cuando corresponda. *No se hizo (#70, cerrado: el coordinador no respondió). Se declara como limitación; se puede retomar si responde.*
- [ ] ❌ Resultados obtenidos.
- [ ] ❌ Demostración de cómo se comprueban reglas críticas: concurrencia, stock, pagos, sincronización, algoritmos, seguridad, estados, integraciones externas, etc. *`AlumnoIntegracionTest` y `CuotaIntegracionTest` cubren las reglas de la base, y `pruebas/navegador/pagos.js` el circuito de pago.*

**Resultados y conclusiones**

- [ ] ❌ Qué logró resolver efectivamente el proyecto.
- [ ] ❌ Qué cambió respecto de la situación inicial.
- [ ] ❌ Limitaciones conocidas.
- [ ] ❌ Aspectos que quedarían como evolución futura.
- [ ] ❌ Aprendizajes o decisiones relevantes.
- [ ] ❌ Diferenciar claramente lo efectivamente implementado y probado de lo proyectado como trabajo futuro.

**Enlace**

- [ ] ❌ Enlace directo al video final incluido en el PDF.

---

## 5. Video final

*El video no está empezado. Conviene grabarlo con el sistema ya cerrado.*

### 5.1 Propósito y preparación

El video no es para leer el informe, el README ni el checklist. Es una explicación hablada y natural del proyecto.

- [ ] ❌ Preparar con bullets cortos e ideas fuerza; grabar explicando con palabras propias.
- [ ] ❌ Elegir 3 a 5 ideas fuerza por bloque, no un texto palabra por palabra.
- [ ] ❌ Escribir bullets cortos, no párrafos para leer.
- [ ] ❌ Ensayar en voz alta y con cronómetro, varias veces.
- [ ] ❌ Asignar a cada integrante partes que pueda explicar con sus propias palabras.
- [ ] ❌ Preparar ejemplos concretos del problema, de las decisiones y de las pruebas.
- [ ] ❌ Ensayar transiciones entre integrantes: "Ahora X les va a mostrar…", "Les cuento por qué elegimos…".
- [ ] ❌ Probar la demo y narrarla mientras se usa, no mostrar pantallas en silencio.

### 5.2 En cámara

- [ ] ⏳ Hablar mirando a cámara, sin leer.
- [ ] ⏳ Explicar con frases propias: "El problema que encontramos fue…", "Elegimos esta tecnología porque…", "Acá lo que sucede es…".
- [ ] ⏳ Usar las diapositivas solo como apoyo visual, no como texto para leer.
- [ ] ⏳ Contar por qué se tomó cada decisión, no solo qué se usó.
- [ ] ⏳ Mostrar la demo narrando qué se hace y por qué es importante.
- [ ] ⏳ Decir las limitaciones con naturalidad.
- [ ] ⏳ Cerrar con una conclusión propia, no leída.

### 5.3 Lo que no conviene hacer

*Se tildan al revisar el video, si no pasó.*

- [ ] ⏳ Leer el informe, el README o el checklist.
- [ ] ⏳ Leer las diapositivas.
- [ ] ⏳ Memorizar un guion palabra por palabra.
- [ ] ⏳ Dejar que hable una sola persona.
- [ ] ⏳ Mostrar pantallas sin explicar qué se está viendo.

### 5.4 Contenido mínimo

- [ ] ❌ Presentación del equipo y del proyecto.
- [ ] ❌ Problema que motivó el desarrollo.
- [ ] ❌ Usuarios o contexto real.
- [ ] ❌ Objetivo y alcance.
- [ ] ❌ Principales reglas del dominio.
- [ ] ❌ Arquitectura y tecnologías utilizadas.
- [ ] ❌ Decisiones técnicas relevantes.
- [ ] ❌ Demostración del sistema funcionando.
- [ ] ❌ Recorrido por uno o más casos de uso significativos.
- [ ] ❌ Pruebas o situaciones especiales importantes.
- [ ] ❌ Dificultades encontradas y cómo fueron resueltas.
- [ ] ❌ Resultados.
- [ ] ❌ Limitaciones actuales.
- [ ] ❌ Posibles evoluciones.
- [ ] ❌ Conclusión final.

### 5.5 Preguntas que el video debería responder

- [ ] ❌ ¿Qué problema resolvieron?
- [ ] ❌ ¿Por qué tomaron esas decisiones?
- [ ] ❌ ¿Cómo sabemos que la solución funciona?
- [ ] ❌ ¿Qué parte del proyecto representa el mayor desafío técnico o de dominio?
- [ ] ❌ ¿Qué harían diferente o qué continuarían desarrollando?

### 5.6 Participación e idioma

- [ ] ❌ Todos los integrantes participan y explican alguna parte sustancial con naturalidad.
- [ ] 👤 Cualquiera del grupo debería poder responder preguntas sobre el proyecto completo.
- [ ] ❌ El video preferentemente en inglés.
- [ ] ⏳ Importa comunicar y defender técnicamente con claridad, no la pronunciación perfecta.
- [ ] ⏳ Pueden usar subtítulos cuando ayuden.
- [ ] ❌ Enlace al video incluido en el PDF y en el README.

---

## 6. Checklist final para finalizar la cursada

### 6.1 Repositorio

- [x] ✅ Repositorio único en GitHub declarado formalmente en la primer entrega.
- [ ] 🟡 README principal actualizado. *Faltan los enlaces (#71).*
- [x] ✅ Código fuente ordenado en carpetas claras.
- [x] ✅ Base de datos incluida: scripts DDL/DML, esquema o migraciones.
- [ ] 🟡 Documentación e informes dentro del repositorio. *La documentación está; falta el informe.*
- [x] ✅ Variables de entorno documentadas sin secretos.
- [x] ✅ Sin credenciales, tokens ni secretos publicados.
- [x] ✅ Pruebas relevantes incluidas.
- [x] ✅ Historial que permita reconstruir la evolución y el estado final.

### 6.2 Sistema y despliegue

- [ ] 🟡 Versión definitiva y funcional del sistema. *Ver la sección 3.1.*
- [x] ✅ Instrucciones claras de instalación y/o ejecución.
- [x] ✅ Configuración necesaria documentada.
- [x] ✅ Base de datos y migraciones listas para reconstruir el entorno.
- [x] ✅ Pruebas relevantes ejecutables.
- [x] ✅ Al menos un componente principal desplegado y funcionando online.
- [x] ✅ Enlace al sistema desplegado incluido en el README.
- [x] ✅ Limitaciones, dependencias externas o funcionalidades simuladas documentadas.

### 6.3 README

- [x] ✅ Descripción del proyecto.
- [x] ✅ Instrucciones de ejecución.
- [x] ✅ Sistema desplegado, si corresponde.
- [ ] ❌ Informe Final en PDF.
- [ ] ❌ Video de presentación final.
- [x] ✅ Integrantes del grupo.
- [x] ✅ Tecnologías utilizadas.
- [ ] 🟡 Todo referenciado desde el README, sin enlaces dispersos.

### 6.4 Informe Final

- [ ] ❌ PDF dentro del repositorio.
- [ ] ❌ Portada completa.
- [ ] ❌ Problema y contexto con evidencia de validación.
- [ ] ❌ Objetivos y alcance.
- [ ] ❌ Análisis y modelo.
- [ ] ❌ Arquitectura y tecnologías con justificación.
- [ ] ❌ Implementación.
- [ ] ❌ Pruebas y validación, incluyendo reglas críticas.
- [ ] ❌ Resultados y conclusiones.
- [ ] ❌ Diferenciación entre implementado/probado y trabajo futuro.
- [ ] ❌ Enlace directo al video final dentro del PDF.

### 6.5 Video

- [ ] ❌ Video explicativo y demostrativo, hablado con naturalidad, no leído.
- [ ] ❌ Todos los integrantes participan y explican.
- [ ] ❌ Cubre problema, usuarios, objetivo, alcance, reglas, arquitectura, decisiones, demo, casos de uso, pruebas, dificultades, resultados, limitaciones, evolución y conclusión.
- [ ] ❌ Responde las preguntas clave.
- [ ] ❌ Enlace al video en el PDF y en el README.
- [ ] ❌ Preferentemente en inglés, o en español, o con subtítulos.

### 6.6 Consistencia final

- [ ] ⏳ El informe describe el sistema que realmente existe.
- [ ] ⏳ El video muestra el sistema que realmente existe.
- [ ] 🟡 El README coincide con el sistema que realmente existe. *Hoy sí; revisarlo al cerrar el sistema (#73).*
- [ ] ⏳ No se documentaron funcionalidades no implementadas como si estuvieran terminadas. *Controlarlo al cerrar el informe y el README.*
- [x] ✅ Las limitaciones están declaradas con claridad.

---

## 7. Checklist para la presentación / defensa final en mesa

*Es una instancia posterior a la entrega final.*

### 7.1 Requisitos habilitantes

- [ ] 👤 Cursada aprobada.
- [ ] 👤 Todas las instancias previas del TFI realizadas y aprobadas.
- [ ] 👤 Totalidad de las asignaturas de la carrera aprobadas, conforme al plan vigente.
- [ ] 👤 Grupo habilitado para presentarse a la mesa de examen final.

### 7.2 Entrega previa a la mesa

- [ ] ⏳ TFI actualizado: GitHub + Video.
- [ ] ⏳ Entrega enviada dos semanas antes de la mesa.
- [x] ✅ Repositorio accesible para el comité. *El repositorio es público.*
- [x] ✅ Sistema desplegado online funcionando. *Repasar antes de la mesa.*
- [ ] ❌ Informe Final en PDF localizable desde el README.
- [ ] ❌ Video final localizable desde el README y enlazado en el PDF.
- [x] ✅ Credenciales y secretos fuera del repositorio.
- [x] ✅ Variables de entorno documentadas sin exponer datos sensibles.

### 7.3 Preparación del coloquio individual

- [ ] ⏳ Repasar problema, contexto y evidencia de validación.
- [ ] ⏳ Repasar objetivos y alcance real.
- [ ] ⏳ Repasar modelo de datos, entidades, relaciones y reglas de negocio.
- [ ] ⏳ Repasar arquitectura y justificación de tecnologías.
- [ ] ⏳ Repasar módulos implementados y casos de uso principales.
- [ ] ⏳ Repasar pruebas realizadas y evidencia de reglas críticas.
- [ ] ⏳ Repasar dificultades técnicas y soluciones aplicadas.
- [ ] ⏳ Repasar resultados, limitaciones y trabajo futuro.
- [ ] ⏳ Poder explicar decisiones técnicas sin depender de leer el código.
- [ ] ⏳ Todos los integrantes pueden explicar el proyecto completo.
- [ ] ⏳ Cada integrante preparó una parte sustancial para exponer.
- [ ] ⏳ Cada integrante puede responder preguntas sobre cualquier módulo, no solo el propio.
- [ ] ⏳ Ensayo grupal realizado.
- [ ] ⏳ Ensayo individual tipo coloquio realizado.
- [ ] ⏳ Preparar una explicación breve de algún fragmento significativo: código, consulta, algoritmo, prueba o diseño.

Preparar respuestas a:

- [ ] ⏳ ¿Qué problema resolvieron?
- [ ] ⏳ ¿Por qué tomaron esas decisiones?
- [ ] ⏳ ¿Cómo sabemos que la solución funciona?
- [ ] ⏳ ¿Qué parte fue el mayor desafío técnico o de dominio?
- [ ] ⏳ ¿Qué harían diferente o qué continuarían desarrollando?
- [ ] ⏳ ¿Qué limitaciones tiene el sistema?
- [ ] ⏳ ¿Qué quedó como trabajo futuro?

### 7.4 Preparación de la demostración

- [ ] ⏳ Demo del sistema funcionando preparada.
- [ ] ⏳ Recorrido por uno o más casos de uso significativos.
- [ ] ⏳ Casos especiales o pruebas importantes listos para mostrar.

Plan B:

- [ ] ⏳ Video grabado.
- [ ] ⏳ Capturas.
- [ ] ⏳ Entorno local de respaldo. *Ya funciona en local; falta dejarlo preparado para la mesa.*

Además:

- [ ] ⏳ Cámara, micrófono y conexión verificados.
- [ ] ⏳ Puntualidad y disponibilidad para la videollamada.

### 7.5 Documentación y enlaces listos para la mesa

- [ ] ❌ README con descripción, ejecución, despliegue, informe, video e integrantes.
- [ ] ❌ PDF del Informe Final actualizado.
- [ ] ❌ Enlace al video final dentro del PDF.
- [x] ✅ Enlace al sistema desplegado funcionando. *Repasar antes de la mesa.*
- [ ] 🟡 Repositorio limpio, ordenado y profesional. *Quedan por borrar las ramas ya mergeadas `gestion-responsables` y `busqueda-listado-alumnos`.*
- [ ] ⏳ Informe, video y README coherentes con el sistema real.

---

## 8. Criterios finales importantes

- [ ] 👤 No agregar funcionalidades solo para agrandar el sistema. *A tener en cuenta al decidir el recorte.*
- [ ] 👤 Priorizar: estabilidad, coherencia, pruebas, documentación y capacidad de explicar las decisiones.
- [ ] 👤 Una funcionalidad sencilla pero bien implementada, validada y defendible vale más que muchos módulos incompletos.
- [ ] ⏳ El informe, el video y el README deben describir el sistema que realmente existe al momento de entregar.
- [x] ✅ Documentar claramente limitaciones, dependencias externas o funcionalidades simuladas.
- [ ] ⏳ Preparar la entrega final no solo como documentación, sino como preparación directa para la defensa.
