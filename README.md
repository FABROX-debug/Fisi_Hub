# FISIHUB

FISIHUB es una plataforma web para gestionar proyectos academicos y de
software de forma simple, visual y directa.

## Estado del proyecto

Sprint 5: tablero Kanban basico por proyecto con tareas reales, drag and drop,
cambio de estado persistente y recalculo del avance.

Todavia no se han implementado comentarios, reportes reales, notificaciones,
dashboard con datos reales ni panel administrativo.

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

## Rutas frontend

| Ruta | Vista |
| --- | --- |
| `/` | Landing publica |
| `/login` | Inicio de sesion |
| `/register` | Registro |
| `/dashboard` | Dashboard temporal protegido |
| `/espacios` | Espacios de trabajo del usuario |
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
