# FISIHUB

FISIHUB es una plataforma web colaborativa de gestion de proyectos academicos
y de software, pensada para equipos universitarios. Permite organizar espacios
de trabajo, crear proyectos con tareas, visualizar avance en tableros Kanban,
colaborar con miembros mediante invitaciones y permisos, y monitorear
productividad con reportes y dashboards en tiempo real.

El stack es React + Vite + TailwindCSS en el frontend, Spring Boot con Java 17
en el backend, y PostgreSQL como base de datos. La autenticacion se maneja con
JWT y BCrypt.

---

## Inicio rapido con Docker

> Solo necesitas [Docker](https://docs.docker.com/get-docker/) y
> [Docker Compose](https://docs.docker.com/compose/install/) instalados.

### 1. Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd Fisi_Hub
```

### 2. Levantar todos los servicios

```bash
docker compose up --build
```

Esto levanta tres contenedores:

| Servicio | Puerto | Descripcion |
| --- | --- | --- |
| `fisihub_db` | 5432 | PostgreSQL 16 con la base de datos precargada |
| `fisihub_backend` | 8080 | API Spring Boot |
| `fisihub_frontend` | 80 | SPA React servida con Nginx |

La base de datos se inicializa automaticamente con el dump
`database/fisihub_dump.sql` en el primer arranque. Incluye esquema, datos de
ejemplo y un usuario demo.

### 3. Abrir la aplicacion

- Frontend: [http://localhost](http://localhost)
- Backend API: [http://localhost:8080/api/health](http://localhost:8080/api/health)

### 4. Usuario demo

```text
Correo:   demo.fisihub@example.com
Password: Demo1234
```

### 5. Detener los servicios

```bash
docker compose down
```

Para eliminar tambien los datos persistentes de la base de datos:

```bash
docker compose down -v
```

### Personalizar variables

Edita directamente el `docker-compose.yml` o usa un archivo `.env` en la raiz:

```dotenv
POSTGRES_PASSWORD=mi-password-seguro
JWT_SECRET=un-secreto-aleatorio-de-al-menos-32-caracteres
```

---

## Desarrollo local (sin Docker)

### Requisitos

- Node.js 20+ y npm
- Java JDK 17+
- Maven 3.9+
- PostgreSQL 14+

### Base de datos

Crea la base de datos e importa el dump:

```bash
createdb fisihub
psql -d fisihub -f database/fisihub_dump.sql
```

O desde pgAdmin, crea la base `fisihub` y ejecuta el contenido del archivo SQL.

### Backend

```powershell
$env:DB_HOST="localhost"
$env:DB_PORT="5432"
$env:DB_NAME="fisihub"
$env:DB_USER="postgres"
$env:DB_PASSWORD="<tu-password-de-postgresql>"
$env:JWT_SECRET="<secreto-aleatorio-de-al-menos-32-caracteres>"
$env:JWT_EXPIRATION_MS="86400000"
$env:APP_FRONTEND_URL="http://localhost:5173"
cd backend
mvn spring-boot:run
```

El backend queda en `http://localhost:8080`.

### Frontend

```powershell
cd frontend
npm install
npm run dev
```

Vite sirve la app en `http://localhost:5173`.

Opcionalmente, copia `frontend/.env.example` a `frontend/.env` para configurar
la URL del backend:

```text
VITE_API_URL=http://localhost:8080
```

---

## Estado del proyecto

MVP base finalizado y Fase 2 iniciada: Sprints 0 al 9 completados.

FISIHUB incluye autenticacion, espacios, proyectos, tareas, Kanban, dashboard,
colaboracion, notificaciones basicas, reportes simples y administracion minima.
Sprint 9 agrega colaboracion multiusuario real mediante invitaciones por correo,
equipos de espacio, asignaciones reales y permisos internos.

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
- Dashboard con datos reales y aislamiento por usuario autenticado.
- Bandeja personal `/mi-trabajo` con foco diario, prioridades y alertas.
- Registro e inicio de sesion.
- Estado de autenticacion con Zustand y token en `localStorage`.
- Restauracion de sesion mediante `GET /api/auth/me`.
- Rutas privadas y cierre de sesion.
- Roles del sistema: `ADMIN`, `LIDER` y `MIEMBRO`.
- CRUD de espacios de trabajo con membresia automatica del creador.
- CRUD de proyectos asociados a espacios.
- Estados y prioridades de proyecto.
- Cards de proyecto con fechas, badges y avance inicial en `0%`.
- Filtros frontend por estado.
- CRUD de tareas dentro de proyectos accesibles.
- Responsable opcional validado como miembro del proyecto.
- Cambio rapido de estado y filtros por estado, prioridad y proyecto.
- Recalculo automatico del avance del proyecto.
- Tablero Kanban protegido con cinco columnas de estado.
- Drag and drop con `@dnd-kit/core` y selector de estado alternativo.
- Selector de proyecto, estados vacios, loading y errores visuales.
- Sincronizacion del progreso del proyecto despues de mover una tarea.
- Estadisticas de proyectos activos, tareas pendientes, completadas y vencidas.
- Proyectos activos recientes con avance real.
- Tareas para hoy, proximas y vencidas.
- Gestion de miembros por proyecto con roles internos `LIDER` y `MIEMBRO`.
- Carga basica por cantidad de tareas activas asignadas.
- Comentarios persistentes en tareas con autoria y fecha.
- Historial de creacion de proyectos y tareas, cambios de estado, miembros
  agregados y comentarios.
- Notificaciones persistentes por asignacion, membresia y vencimiento manana.
- Contador de no leidas y marcado individual o masivo.
- Reporte real de avance y productividad por miembro.
- Panel `ADMIN` con usuarios, proyectos y estadisticas globales.
- Sidebar colapsable y tablas con desplazamiento seguro en movil y tablet.
- Invitaciones internas a espacios para usuarios registrados, con notificacion
  persistente y expiracion.
- Equipo por espacio con roles `LIDER` y `MIEMBRO`.
- Incorporacion de miembros del espacio a proyectos mediante selector.
- Asignacion de tareas solo a miembros activos del proyecto.
- Permisos de tarea aplicados en backend y controles visibles del frontend.

## Rutas frontend

| Ruta | Vista |
| --- | --- |
| `/` | Landing publica |
| `/login` | Inicio de sesion |
| `/register` | Registro |
| `/forgot-password` | Solicitar recuperacion |
| `/reset-password/:token` | Restablecer contrasena |
| `/dashboard` | Dashboard real protegido |
| `/mi-trabajo` | Bandeja personal del usuario |
| `/espacios` | Espacios de trabajo del usuario |
| `/proyectos` | Proyectos |
| `/tareas` | Vista general de tareas |
| `/kanban` | Tablero Kanban |
| `/miembros` | Miembros y actividad por proyecto |
| `/reportes` | Reporte real por proyecto |
| `/notificaciones` | Notificaciones persistentes |
| `/administracion` | Panel exclusivo para `ADMIN` |
| `/configuracion` | Configuracion |

## Endpoints API

### Autenticacion

| Metodo | Endpoint | Descripcion |
| --- | --- | --- |
| `POST` | `/api/auth/register` | Registro de usuario |
| `POST` | `/api/auth/login` | Inicio de sesion, devuelve JWT |
| `GET` | `/api/auth/me` | Usuario autenticado |
| `POST` | `/api/auth/forgot-password` | Solicita enlace de recuperacion |
| `GET` | `/api/auth/reset-password/{token}` | Valida token de recuperacion |
| `POST` | `/api/auth/reset-password` | Actualiza la contrasena |

### Espacios y proyectos

Todos requieren `Authorization: Bearer <token>`.

| Metodo | Endpoint | Descripcion |
| --- | --- | --- |
| `GET` | `/api/espacios` | Lista espacios del usuario |
| `POST` | `/api/espacios` | Crea un espacio |
| `GET` | `/api/espacios/{id}` | Obtiene un espacio accesible |
| `PUT` | `/api/espacios/{id}` | Edita un espacio propio |
| `DELETE` | `/api/espacios/{id}` | Elimina un espacio propio |
| `GET` | `/api/espacios/{id}/proyectos` | Lista proyectos del espacio |
| `GET` | `/api/proyectos` | Lista proyectos del usuario |
| `POST` | `/api/proyectos` | Crea un proyecto |
| `GET` | `/api/proyectos/{id}` | Obtiene un proyecto accesible |
| `PUT` | `/api/proyectos/{id}` | Edita un proyecto liderado |
| `DELETE` | `/api/proyectos/{id}` | Elimina un proyecto liderado |

### Tareas

| Metodo | Endpoint | Descripcion |
| --- | --- | --- |
| `GET` | `/api/tareas` | Lista tareas accesibles (filtros: estado, prioridad, proyectoId, responsableId) |
| `GET` | `/api/tareas/mi-trabajo` | Resume trabajo personal |
| `POST` | `/api/tareas` | Crea una tarea |
| `GET` | `/api/tareas/{id}` | Detalle de una tarea |
| `PUT` | `/api/tareas/{id}` | Edita una tarea |
| `PATCH` | `/api/tareas/{id}/estado` | Cambia estado |
| `DELETE` | `/api/tareas/{id}` | Elimina una tarea |
| `GET` | `/api/proyectos/{id}/tareas` | Tareas de un proyecto |

### Colaboracion

| Metodo | Endpoint | Descripcion |
| --- | --- | --- |
| `GET` | `/api/proyectos/{id}/miembros` | Lista miembros y carga activa |
| `POST` | `/api/proyectos/{id}/miembros` | Agrega usuario por correo |
| `PATCH` | `/api/proyectos/{id}/miembros/{usuarioId}/rol` | Cambia rol interno |
| `DELETE` | `/api/proyectos/{id}/miembros/{usuarioId}` | Quita un miembro |
| `GET` | `/api/tareas/{id}/comentarios` | Lista comentarios |
| `POST` | `/api/tareas/{id}/comentarios` | Crea un comentario |
| `DELETE` | `/api/comentarios/{id}` | Elimina un comentario |
| `GET` | `/api/proyectos/{id}/actividad` | Actividad reciente |

### Invitaciones y equipo de espacio

| Metodo | Endpoint | Descripcion |
| --- | --- | --- |
| `POST` | `/api/espacios/{id}/invitaciones` | Invita un usuario al espacio |
| `GET` | `/api/espacios/{id}/invitaciones` | Lista invitaciones del espacio |
| `GET` | `/api/espacios/{id}/usuarios-disponibles` | Usuarios invitables |
| `POST` | `/api/invitaciones/{id}/aceptar` | Acepta la invitacion |
| `POST` | `/api/invitaciones/{id}/rechazar` | Rechaza la invitacion |
| `POST` | `/api/invitaciones/{id}/reenviar` | Renueva una invitacion |
| `DELETE` | `/api/invitaciones/{id}` | Revoca una invitacion |
| `GET` | `/api/espacios/{id}/miembros` | Equipo del espacio |
| `PATCH` | `/api/espacios/{id}/miembros/{usuarioId}/rol` | Cambia rol |
| `DELETE` | `/api/espacios/{id}/miembros/{usuarioId}` | Quita un miembro |

### Dashboard, notificaciones, reportes y admin

| Metodo | Endpoint | Descripcion |
| --- | --- | --- |
| `GET` | `/api/dashboard/resumen` | Estadisticas del dashboard |
| `GET` | `/api/notificaciones` | Lista notificaciones |
| `PATCH` | `/api/notificaciones/{id}/leida` | Marca como leida |
| `PATCH` | `/api/notificaciones/leer-todas` | Marca todas como leidas |
| `GET` | `/api/proyectos/{id}/reportes/avance` | Avance y productividad |
| `GET` | `/api/admin/usuarios` | Lista usuarios (ADMIN) |
| `PATCH` | `/api/admin/usuarios/{id}/activar` | Activa usuario |
| `PATCH` | `/api/admin/usuarios/{id}/desactivar` | Desactiva usuario |
| `GET` | `/api/admin/proyectos` | Lista todos los proyectos (ADMIN) |
| `GET` | `/api/admin/estadisticas` | Resumen global (ADMIN) |

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
| `APP_FRONTEND_URL` | URL permitida para CORS del frontend | `http://localhost:5173` |
| `PASSWORD_RESET_EXPIRATION_MINUTES` | Vigencia del enlace de recuperacion | `30` |
| `PASSWORD_RESET_EXPOSE_LINK` | Expone `previewUrl` para probar sin SMTP | `true` |
| `MAIL_FROM` | Remitente de correos de recuperacion | `no-reply@fisihub.local` |
| `MAIL_HOST` | Host SMTP | Sin valor por defecto |
| `MAIL_PORT` | Puerto SMTP | `587` |
| `MAIL_USERNAME` | Usuario SMTP | Sin valor por defecto |
| `MAIL_PASSWORD` | Password SMTP | Sin valor por defecto |
| `MAIL_SMTP_AUTH` | Activa autenticacion SMTP | `false` |
| `MAIL_STARTTLS_ENABLE` | Activa STARTTLS | `false` |

No se deben versionar credenciales reales ni archivos `.env`.
