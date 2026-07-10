# FISIHUB

FISIHUB es una plataforma web para gestionar proyectos academicos y de
software de forma simple, visual y directa.

## Estado del proyecto

MVP base finalizado y Fase 2 iniciada: Sprints 0 al 9 completados.

FISIHUB incluye autenticacion, espacios, proyectos, tareas, Kanban, dashboard,
colaboracion, notificaciones basicas, reportes simples y administracion minima.
Sprint 9 agrega colaboracion multiusuario real mediante invitaciones por correo,
equipos de espacio, asignaciones reales y permisos internos.

## Usuario demo local

Si solo quieres abrir la app con datos cargados, usa:

```text
Correo: demo.fisihub@example.com
Password: Demo1234
```

Ese usuario se crea automaticamente en perfiles `dev`, `local` o `default`
cuando no existe aun. Tiene un espacio, un proyecto y tareas de ejemplo.

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

## Requisitos

- Node.js 20 o superior y npm.
- Java JDK 17 o superior.
- Maven 3.9 o superior.
- PostgreSQL 14 o superior.

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
$env:APP_FRONTEND_URL="http://localhost:5173"
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

Durante el Sprint 3 PostgreSQL debe estar activo. Hibernate crea o actualiza
temporalmente las tablas de usuarios, espacios, membresias y proyectos.

Durante el Sprint 4 se agrega la tabla `tarea` y el avance del proyecto se
calcula como:

```text
tareas completadas / total de tareas * 100
```

Si un proyecto no tiene tareas, su avance es `0`.

Sprint 5 no agrega tablas ni endpoints. El Kanban consume tareas reales de
PostgreSQL mediante los endpoints de Sprint 4.

Sprint 6 agrega un resumen de dashboard. Sprint 7 agrega las tablas
`comentario` e `historial_actividad`; la actividad reciente del dashboard
ahora se obtiene del historial persistente.

Sprint 8 agrega la tabla `notificacion`. Los reportes y las estadisticas de
administracion se calculan desde los datos existentes sin tablas auxiliares.

Sprint 9 agrega la tabla `invitacion_espacio`. Las invitaciones se crean dentro
de la app para usuarios registrados, expiran en 7 dias y se aceptan o rechazan
desde la bandeja de notificaciones.

## Colaboracion multiusuario - Sprint 9

| Metodo | Endpoint | Descripcion |
| --- | --- | --- |
| `POST` | `/api/espacios/{id}/invitaciones` | Invita un usuario registrado al espacio |
| `GET` | `/api/espacios/{id}/invitaciones` | Lista invitaciones del espacio |
| `GET` | `/api/espacios/{id}/usuarios-disponibles` | Lista usuarios invitables del espacio |
| `POST` | `/api/invitaciones/{id}/aceptar` | Acepta la invitacion del usuario autenticado |
| `POST` | `/api/invitaciones/{id}/rechazar` | Rechaza la invitacion del usuario autenticado |
| `POST` | `/api/invitaciones/{id}/reenviar` | Renueva una invitacion pendiente o expirada |
| `DELETE` | `/api/invitaciones/{id}` | Revoca una invitacion |
| `GET` | `/api/espacios/{id}/miembros` | Lista el equipo del espacio |
| `PATCH` | `/api/espacios/{id}/miembros/{usuarioId}/rol` | Cambia rol interno |
| `DELETE` | `/api/espacios/{id}/miembros/{usuarioId}` | Quita un miembro |

Solo un `LIDER` del espacio o un `ADMIN` gestiona equipo e invitaciones. Los
miembros del proyecto pueden crear tareas y modificar las que tienen
asignadas; solo un lider o administrador puede reasignar tareas ajenas,
eliminarlas o gestionar miembros.

## Endpoints de Sprint 3

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

Ejemplo de creacion de espacio:

```powershell
$headers = @{ Authorization = "Bearer $($login.token)" }
$espacio = Invoke-RestMethod `
  -Method Post `
  -Uri http://localhost:8080/api/espacios `
  -Headers $headers `
  -ContentType "application/json" `
  -Body '{"nombre":"Arquitectura de Software","color":"#6D28D9"}'
```

Ejemplo de creacion de proyecto:

```powershell
Invoke-RestMethod `
  -Method Post `
  -Uri http://localhost:8080/api/proyectos `
  -Headers $headers `
  -ContentType "application/json" `
  -Body "{`"nombre`":`"Sistema FISIHUB`",`"espacioId`":$($espacio.id),`"estado`":`"PLANIFICADO`",`"prioridad`":`"ALTA`"}"
```

## Endpoints de Sprint 4

Todos requieren `Authorization: Bearer <token>`.

| Metodo | Endpoint | Descripcion |
| --- | --- | --- |
| `GET` | `/api/tareas` | Lista tareas accesibles y acepta filtros |
| `GET` | `/api/tareas/mi-trabajo` | Resume el trabajo personal del usuario |
| `POST` | `/api/tareas` | Crea una tarea |
| `GET` | `/api/tareas/{id}` | Obtiene el detalle de una tarea |
| `PUT` | `/api/tareas/{id}` | Edita una tarea |
| `PATCH` | `/api/tareas/{id}/estado` | Cambia el estado |
| `DELETE` | `/api/tareas/{id}` | Elimina una tarea |
| `GET` | `/api/proyectos/{id}/tareas` | Lista tareas de un proyecto |

Filtros disponibles en `GET /api/tareas`:

```text
estado, prioridad, proyectoId, responsableId
```

`GET /api/tareas/mi-trabajo` devuelve:

- resumen personal por estado y urgencia
- tareas prioritarias
- tareas asignadas
- tareas que requieren accion
- proyectos donde el usuario tiene carga activa

Ejemplo:

```powershell
$tarea = Invoke-RestMethod `
  -Method Post `
  -Uri http://localhost:8080/api/tareas `
  -Headers $headers `
  -ContentType "application/json" `
  -Body "{`"titulo`":`"Implementar servicio`",`"proyectoId`":1,`"prioridad`":`"ALTA`"}"

Invoke-RestMethod `
  -Method Patch `
  -Uri "http://localhost:8080/api/tareas/$($tarea.id)/estado" `
  -Headers $headers `
  -ContentType "application/json" `
  -Body '{"estado":"COMPLETADA"}'
```

## Kanban de Sprint 5

La ruta protegida `http://localhost:5173/kanban` permite:

- Seleccionar uno de los proyectos accesibles.
- Ver tareas agrupadas en cinco columnas por estado.
- Arrastrar una tarjeta a otra columna.
- Cambiar estado con un selector si no se usa drag and drop.
- Ver el avance confirmado por el backend despues de cada cambio.

Endpoints reutilizados:

```text
GET /api/tareas
GET /api/proyectos
GET /api/proyectos/{id}
GET /api/proyectos/{id}/tareas
PATCH /api/tareas/{id}/estado
```

Validacion local:

```powershell
cd frontend
npm install
npm run lint
npm run build
npm run dev
```

## Dashboard de Sprint 6

El endpoint requiere `Authorization: Bearer <token>`:

| Metodo | Endpoint | Descripcion |
| --- | --- | --- |
| `GET` | `/api/dashboard/resumen` | Estadisticas y listas del dashboard |

Incluye:

- Cantidad de proyectos activos.
- Tareas pendientes, completadas, vencidas y para hoy.
- Porcentaje promedio de avance de proyectos accesibles.
- Hasta cinco proyectos activos recientes.
- Tareas pendientes para hoy o los proximos tres dias.
- Detalle de tareas vencidas.
- Hasta cinco eventos recientes del historial de proyectos accesibles.

Prueba manual:

```powershell
Invoke-RestMethod `
  -Uri http://localhost:8080/api/dashboard/resumen `
  -Headers @{ Authorization = "Bearer $($login.token)" }
```

## Colaboracion de Sprint 7

Todos los endpoints requieren `Authorization: Bearer <token>`.

| Metodo | Endpoint | Descripcion |
| --- | --- | --- |
| `GET` | `/api/proyectos/{id}/miembros` | Lista miembros y carga activa |
| `POST` | `/api/proyectos/{id}/miembros` | Agrega usuario por correo |
| `PATCH` | `/api/proyectos/{id}/miembros/{usuarioId}/rol` | Cambia rol interno |
| `DELETE` | `/api/proyectos/{id}/miembros/{usuarioId}` | Quita un miembro |
| `GET` | `/api/tareas/{id}/comentarios` | Lista comentarios cronologicamente |
| `POST` | `/api/tareas/{id}/comentarios` | Crea un comentario |
| `DELETE` | `/api/comentarios/{id}` | Elimina un comentario autorizado |
| `GET` | `/api/proyectos/{id}/actividad` | Lista actividad reciente |

Solo un `LIDER` interno o un usuario `ADMIN` puede gestionar miembros. No se
permiten duplicados ni dejar el proyecto sin lider. Los comentarios pueden ser
eliminados por su autor, un lider del proyecto o un administrador.

Ejemplo para agregar un miembro:

```powershell
Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8080/api/proyectos/1/miembros" `
  -Headers $headers `
  -ContentType "application/json" `
  -Body '{"correo":"miembro@ejemplo.com","rol":"MIEMBRO"}'
```

Ejemplo para comentar:

```powershell
Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8080/api/tareas/1/comentarios" `
  -Headers $headers `
  -ContentType "application/json" `
  -Body '{"contenido":"Revision completada"}'
```

## Cierre del MVP - Sprint 8

Todos los endpoints requieren JWT. Los endpoints `/api/admin/**` requieren
ademas el rol global `ADMIN`.

| Metodo | Endpoint | Descripcion |
| --- | --- | --- |
| `GET` | `/api/notificaciones` | Lista avisos y genera vencimientos de manana |
| `PATCH` | `/api/notificaciones/{id}/leida` | Marca un aviso como leido |
| `PATCH` | `/api/notificaciones/leer-todas` | Marca todos como leidos |
| `GET` | `/api/proyectos/{id}/reportes/avance` | Avance y productividad |
| `GET` | `/api/admin/usuarios` | Lista usuarios |
| `PATCH` | `/api/admin/usuarios/{id}/activar` | Activa un usuario |
| `PATCH` | `/api/admin/usuarios/{id}/desactivar` | Desactiva un usuario |
| `GET` | `/api/admin/proyectos` | Lista todos los proyectos |
| `GET` | `/api/admin/estadisticas` | Resumen global |

Las notificaciones se crean al asignar una tarea o agregar un miembro. Los
avisos de tareas que vencen manana se materializan al consultar
`GET /api/notificaciones`, sin WebSockets ni scheduler externo.

Pruebas finales:

```powershell
cd backend
mvn test
mvn spring-boot:run
```

```powershell
cd frontend
npm install
npm run lint
npm run build
npm run dev
```

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

## Recuperacion de cuenta

FISIHUB ahora expone estos endpoints publicos:

| Metodo | Endpoint | Descripcion |
| --- | --- | --- |
| `POST` | `/api/auth/forgot-password` | Solicita enlace de recuperacion |
| `GET` | `/api/auth/reset-password/{token}` | Valida token de recuperacion |
| `POST` | `/api/auth/reset-password` | Actualiza la contrasena |

En local, la API devuelve `previewUrl` para abrir el flujo sin depender de
SMTP. En un despliegue real conviene configurar
`PASSWORD_RESET_EXPOSE_LINK=false`.
