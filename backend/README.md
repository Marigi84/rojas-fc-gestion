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

## Empaquetar

```bash
mvn clean package
java -jar target/gestion-backend-0.1.0.jar
```
