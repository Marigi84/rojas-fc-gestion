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

`service`, `repository`, `model` y `security` están preparados como paquetes vacíos: las entidades y endpoints de cada módulo se incorporan en la Etapa 3, a partir de `database/schema.sql`.

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
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | Origen permitido para el frontend |
| `SERVER_PORT` | `8080` | Puerto del backend |

Ejemplo en PowerShell antes de levantar el backend:

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "tu_password"
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

## Empaquetar

```bash
mvn clean package
java -jar target/gestion-backend-0.1.0.jar
```
