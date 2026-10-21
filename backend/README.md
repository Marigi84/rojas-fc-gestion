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
| `ADMIN_INICIAL_DNI`, `ADMIN_INICIAL_NOMBRE`, `ADMIN_INICIAL_APELLIDO`, `ADMIN_INICIAL_EMAIL`, `ADMIN_INICIAL_CONTRASENA` | — | Primer administrador (ver "Autenticación") |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | Origen permitido para el frontend |
| `SERVER_PORT` | `8080` | Puerto del backend |

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
| `POST /api/auth/login` | público | Ingreso con `{ "dni", "contrasena" }`. Devuelve el token, el rol y si la contraseña es provisoria. |
| `PUT /api/auth/contrasena` | autenticado | Cambio de contraseña con `{ "actual", "nueva" }` (mínimo 8 caracteres). Devuelve un token nuevo. |

Reglas de acceso (`security/SecurityConfig.java`):

- `/api/health` y `/api/auth/login` son públicas.
- `/api/portal/**` es para el Responsable (y el Administrador).
- Todo lo demás es solo para el Administrador.
- Con la contraseña provisoria solo se puede cambiar la contraseña (RNF-01). El resto responde 403 hasta que se cambie.
- Una persona que es Administrador y Responsable ingresa como Administrador, con la contraseña de administrador.

**Primer administrador.** Si la tabla `administrador` está vacía y están definidas las variables `ADMIN_INICIAL_*`, el backend lo crea al arrancar, con la contraseña como provisoria. Por ejemplo, en PowerShell:

```powershell
$env:ADMIN_INICIAL_DNI = "30111222"
$env:ADMIN_INICIAL_NOMBRE = "Nombre"
$env:ADMIN_INICIAL_APELLIDO = "Apellido"
$env:ADMIN_INICIAL_EMAIL = "admin@ejemplo.com"
$env:ADMIN_INICIAL_CONTRASENA = "una-provisoria"
```

Después de crearlo, esas variables ya no hacen falta.

## Empaquetar

```bash
mvn clean package
java -jar target/gestion-backend-0.1.0.jar
```
