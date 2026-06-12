# FISIHUB

FISIHUB es una plataforma web para gestionar proyectos academicos y de
software de forma simple, visual y directa.

## Estado del proyecto

Sprint 2: autenticacion y usuarios con registro, login, JWT, roles basicos y
rutas frontend protegidas.

Todavia no se han implementado espacios de trabajo, CRUD de proyectos o tareas,
Kanban funcional, reportes reales, notificaciones ni panel administrativo.

## Stack

- Frontend: React, Vite y TailwindCSS.
- Backend: Spring Boot, Java 17+, Spring MVC, Spring Data JPA, Validation y
  Spring Security.
- Base de datos: PostgreSQL.
- Comunicacion prevista: API REST con JSON.
- Autenticacion: JWT con Spring Security y BCrypt.

## Componentes disponibles

- Layout responsive con sidebar fijo, topbar y area de contenido.
- Navegacion agrupada con iconos de Lucide React.
- Componentes UI: `Button`, `Input`, `Card`, `Badge`, `ProgressBar` y `Toast`.
- Barra de progreso con gradiente y shimmer violeta.
- Dashboard temporal con estadisticas y badges de ejemplo.
- Registro e inicio de sesion.
- Estado de autenticacion con Zustand y token en `localStorage`.
- Restauracion de sesion mediante `GET /api/auth/me`.
- Rutas privadas y cierre de sesion.
- Roles del sistema: `ADMIN`, `LIDER` y `MIEMBRO`.

## Rutas frontend

| Ruta | Vista |
| --- | --- |
| `/` | Landing publica |
| `/login` | Inicio de sesion |
| `/register` | Registro |
| `/dashboard` | Dashboard temporal protegido |
| `/proyectos` | Proyectos |
| `/tareas` | Mis tareas |
| `/kanban` | Tablero Kanban |
| `/miembros` | Miembros |
| `/reportes` | Reportes |
| `/configuracion` | Configuracion |

## Requisitos

- Node.js 20 o superior y npm.
- Java JDK 17 o superior.
- Maven 3.9 o superior.
- PostgreSQL para las futuras funciones que usen persistencia.

## Ejecutar el frontend

```powershell
cd frontend
npm install
npm run dev
```

Vite mostrara en la terminal la URL local, normalmente
`http://localhost:5173`.

Configura opcionalmente la URL del backend copiando el valor de
`frontend/.env.example`:

```text
VITE_API_URL=http://localhost:8080
```

## Ejecutar el backend

Configura las variables de entorno y ejecuta Spring Boot:

```powershell
$env:DB_HOST="localhost"
$env:DB_PORT="5432"
$env:DB_NAME="fisihub"
$env:DB_USER="postgres"
$env:DB_PASSWORD="<tu-password-de-postgresql>"
$env:JWT_SECRET="<secreto-aleatorio-de-al-menos-32-caracteres>"
$env:JWT_EXPIRATION_MS="86400000"
cd backend
mvn spring-boot:run
```

El backend queda disponible en `http://localhost:8080`. Su endpoint temporal
de estado es:

```text
GET http://localhost:8080/api/health
```

Tambien se puede probar desde PowerShell:

```powershell
Invoke-RestMethod http://localhost:8080/api/health
```

Respuesta esperada:

```json
{
  "status": "FISIHUB backend funcionando"
}
```

Durante el Sprint 2 PostgreSQL debe estar activo. Hibernate crea o actualiza
temporalmente las tablas `usuario`, `rol` y `usuario_rol`.

## Probar autenticacion

Registro:

```powershell
$register = Invoke-RestMethod `
  -Method Post `
  -Uri http://localhost:8080/api/auth/register `
  -ContentType "application/json" `
  -Body '{"nombre":"Fabrizio","correo":"fab@test.com","password":"Test1234"}'
```

Login:

```powershell
$login = Invoke-RestMethod `
  -Method Post `
  -Uri http://localhost:8080/api/auth/login `
  -ContentType "application/json" `
  -Body '{"correo":"fab@test.com","password":"Test1234"}'
```

Usuario autenticado:

```powershell
Invoke-RestMethod `
  -Uri http://localhost:8080/api/auth/me `
  -Headers @{ Authorization = "Bearer $($login.token)" }
```

## Variables de entorno

| Variable | Descripcion | Valor por defecto |
| --- | --- | --- |
| `DB_HOST` | Host de PostgreSQL | `localhost` |
| `DB_PORT` | Puerto de PostgreSQL | `5432` |
| `DB_NAME` | Nombre de la base de datos | `fisihub` |
| `DB_USER` | Usuario de PostgreSQL | `postgres` |
| `DB_PASSWORD` | Contrasena de PostgreSQL | Sin valor por defecto |
| `JWT_SECRET` | Clave de firma JWT, minimo 32 caracteres | Sin valor por defecto |
| `JWT_EXPIRATION_MS` | Duracion del token en milisegundos | `86400000` |

No se deben versionar credenciales reales ni archivos `.env`.
