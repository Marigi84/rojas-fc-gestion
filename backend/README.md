# Backend — Sistema de Gestión Rojas FC

API REST en Java 17 + Spring Boot 3, según `docs/arquitectura/arquitectura-inicial.md`.

## Estructura de paquetes

```
com.rojasfc.gestion
├── controller    # Controladores REST (sección 5.1)
├── service       # Lógica de negocio (sección 5.2)
├── repository    # Acceso a datos con Spring Data JPA (sección 5.3)
├── model         # Entidades JPA del dominio (sección 5.4)
├── dto           # DTOs de entrada/salida de la API (sección 5.4)
├── security      # Autenticación, JWT y autorización (sección 5.5)
├── exception     # Manejo uniforme de errores (sección 5.6)
└── config        # Configuración transversal (CORS, etc.)
```

Las entidades y endpoints de cada módulo se incorporan en la Etapa 3, a partir de `database/schema.sql`. El primero es la autenticación (ver más abajo).

## Requisitos

- Java 17
- Maven 3.9+
- MySQL 8, con el esquema de `database/schema.sql` ya aplicado

## Configuración

La conexión a MySQL se toma de variables de entorno, nunca de código:

| Variable | Default | Descripción |
|---|---|---|
| `DB_HOST` | `localhost` | Host de MySQL |
| `DB_PORT` | `3306` | Puerto de MySQL |
| `DB_NAME` | `rojasfc` | Nombre de la base |
| `DB_USERNAME` | — (obligatoria) | Usuario de MySQL |
| `DB_PASSWORD` | — (obligatoria) | Contraseña de MySQL |
| `JWT_SECRET` | — (obligatoria) | Clave para firmar los tokens, de al menos 32 caracteres |
| `JWT_DURACION_MINUTOS` | `480` | Duración de la sesión |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | Origen permitido para el frontend |
| `SERVER_PORT` | `8080` | Puerto del backend |
| `ZONA_HORARIA` | `America/Argentina/Cordoba` | Zona horaria con la que se fechan altas y bajas |

Ejemplo en PowerShell antes de levantar el backend:

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "tu_password"
$env:JWT_SECRET = "una-clave-larga-de-al-menos-32-caracteres"
```

(En Linux/Mac, usar `export DB_USERNAME=root` en lugar de `$env:`.)

## Levantar el backend

```bash
cd backend
mvn spring-boot:run
```

Por defecto queda disponible en `http://localhost:8080`.

## Verificar que está funcionando

```bash
curl http://localhost:8080/api/health
```

Responde `status: ok` y, además, el resultado de probar la conexión a la base (`database: ok` si MySQL respondió correctamente).

## Autenticación

El ingreso usa DNI y contraseña y devuelve un JWT que el frontend envía en el encabezado `Authorization: Bearer <token>` (arquitectura, sección 8).

| Método y ruta | Acceso | Uso |
|---|---|---|
| `GET /api/configuracion-inicial` | público | Indica si el sistema ya tiene administrador (`{ "configurado": true/false }`). |
| `POST /api/configuracion-inicial` | público, solo sin administradores | Registra el primer administrador y devuelve su sesión iniciada. Después responde 409. |
| `POST /api/auth/login` | público | Ingreso con `{ "dni", "contrasena" }`. Devuelve el token, el rol y si la contraseña es provisoria. |
| `PUT /api/auth/contrasena` | autenticado | Cambio de contraseña con `{ "actual", "nueva" }` (mínimo 8 caracteres). Devuelve un token nuevo. |

Reglas de acceso (`security/SecurityConfig.java`):

- `/api/health`, `/api/auth/login` y `/api/configuracion-inicial` son públicas.
- `/api/portal/**` es para el Responsable (y el Administrador).
- Todo lo demás es solo para el Administrador.
- Con la contraseña provisoria solo se puede cambiar la contraseña (RNF-01). El resto responde 403 hasta que se cambie.
- Una persona que es Administrador y Responsable ingresa como Administrador, con la contraseña de administrador.

**Primer administrador (puesta en marcha).** Con la base recién instalada no hay administradores. La primera vez que se abre la aplicación, el frontend muestra la configuración inicial en lugar del ingreso: se cargan DNI, nombre, apellido, correo y contraseña, y queda creado el primer administrador. Conviene hacerlo apenas se instala, porque hasta ese momento cualquiera que abra la aplicación podría hacerlo. Los demás administradores se registran desde el sistema (RF-51).

- Si algo falla al guardar, no queda nada a medias y se puede volver a intentar.
- Si el DNI ya estaba cargado (por ejemplo, como responsable), se reutiliza esa persona con su nombre y apellido.
- Para volver a empezar (por ejemplo, si se olvidó la contraseña antes de tener la recuperación por correo), se borra el administrador en MySQL y la configuración inicial vuelve a aparecer:

```sql
DELETE FROM administrador WHERE dni = <dni>;
```

**Otros administradores y contraseñas de responsables.** Solo para el Administrador:

| Método y ruta | Uso |
|---|---|
| `GET /api/administradores` | Administradores registrados, ordenados por apellido. |
| `POST /api/administradores` | Registra otro administrador con `{ "dni", "nombre", "apellido", "email", "contrasenaProvisoria" }` (RF-51). Responde 201. |
| `PUT /api/responsables/{dni}/contrasena` | Asigna al responsable una contraseña provisoria nueva con `{ "contrasenaProvisoria" }` (RF-15). Responde 204. |

- Como en el alta de responsables, la contraseña del nuevo administrador la asigna quien lo registra y es provisoria: la cambia en su primer ingreso (RNF-01). El correo sirve para recuperarla (RF-50).
- Si el DNI ya es una persona del sistema (por ejemplo, un responsable), se reutilizan su nombre y apellido (RN-01): `nombre` y `apellido` se piden solo para personas nuevas.
- Responde 409 con `error: "Administrador ya registrado"` si el DNI ya es administrador, o `"Correo en uso"` si el correo es de otro administrador.
- Al restablecer la contraseña de un responsable, queda provisoria y la tiene que cambiar en su próximo ingreso. Si ese responsable tenía una sesión abierta en otro dispositivo, sigue valiendo hasta que vence su token.

## Alumnos

Gestión de alumnos (Módulo 2). Todas las rutas son solo para el Administrador.

| Método y ruta | Uso |
|---|---|
| `POST /api/alumnos` | Alta de un alumno con al menos un responsable, en una sola operación (RF-01, RN-05). Responde 201 con el detalle. |
| `GET /api/alumnos` | Listado paginado con búsqueda y filtros (RF-38, RF-39). Parámetros: `busqueda`, `anioNacimiento`, `estado` (`activo` por defecto, `inactivo` o `todos`), `moroso` (`true`/`false`), `pagina` (1) y `tamano` (20). |
| `GET /api/alumnos/{dni}` | Datos, categoría, estado, morosidad, historial de permanencias y responsables (RF-02, RF-16). |
| `PUT /api/alumnos/{dni}` | Modificación de nombre, apellido, fecha de nacimiento y dirección (RF-03). |
| `POST /api/alumnos/{dni}/baja` | Baja lógica: cierra la permanencia vigente con la fecha de hoy (RF-04). |
| `POST /api/alumnos/{dni}/reactivacion` | Abre una nueva permanencia y conserva las anteriores (RF-05). Si la baja fue hoy, la deshace. |
| `GET /api/personas/{dni}` | Persona ya registrada y sus roles, para reutilizar sus datos en los formularios (RN-01). |

Ejemplo de alta:

```json
{
  "dni": 45111222,
  "nombre": "Mateo",
  "apellido": "Gómez",
  "fechaNacimiento": "2015-03-10",
  "direccion": "San Martín 123",
  "responsables": [
    {
      "dni": 30111222,
      "nombre": "Laura",
      "apellido": "Gómez",
      "telefono": "3511234567",
      "vinculo": "MADRE",
      "contrasenaProvisoria": "provisoria123"
    }
  ]
}
```

- `vinculo` admite `MADRE`, `PADRE`, `TUTOR` u `OTRO`.
- Si el DNI de una persona ya está registrado, se reutilizan su nombre y apellido (RN-01): `nombre` y `apellido` se piden solo para personas nuevas. Si un responsable ya existe, alcanza con su `dni` y el `vinculo`: `telefono` y `contrasenaProvisoria` se piden únicamente para quien todavía no es responsable. La contraseña la asigna el Administrador y es provisoria (RF-11, RNF-01).
- **Reactivar el mismo día de la baja** la deshace (se toma como una baja por error) y el alumno vuelve a quedar activo en la misma permanencia, porque la clave de `permanencia` no admite dos altas el mismo día. En ese caso no queda registro de la baja anulada. Antes se controla RN-03, que el trigger de la base no cubre en esta reapertura.
- Si el DNI ya es un alumno, no se crea otro: responde 409 con `error: "Alumno ya registrado"` si está activo, o `"Alumno inactivo"` si está dado de baja, para ofrecer la reactivación.
- Cuando un trigger de la base rechaza la operación (por ejemplo, RN-03: un alumno activo no puede ser responsable), responde 409 con `error: "Regla de negocio"` y el mensaje del trigger.

## Tests

```bash
mvn test
```

Corre los tests unitarios, que no necesitan base de datos.

Las reglas que controla la base (triggers de RN-03, que un alta fallida no deje nada a medias) se prueban con tests de integración contra MySQL. Se activan con `TESTS_MYSQL=true` y usan las mismas variables `DB_*`. Crean desde `database/schema.sql` una base aparte, `rojasfc_test`, que se borra y se vuelve a crear en cada corrida, así que no tocan la base de desarrollo:

```powershell
$env:TESTS_MYSQL = "true"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "tu_password"
mvn test
```

## Empaquetar

```bash
mvn clean package
java -jar target/gestion-backend-0.1.0.jar
```
