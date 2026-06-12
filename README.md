# FISIHUB

FISIHUB es una plataforma web para gestionar proyectos academicos y de
software de forma simple, visual y directa.

## Estado del proyecto

Sprint 1: sistema de diseno, layout principal, rutas iniciales, componentes UI
reutilizables y dashboard visual temporal.

Todavia no se han implementado autenticacion, JWT, CRUD, Kanban funcional,
reportes reales, notificaciones, administracion ni entidades de base de datos.

## Stack

- Frontend: React, Vite y TailwindCSS.
- Backend: Spring Boot, Java 17+, Spring MVC, Spring Data JPA, Validation y
  Spring Security.
- Base de datos: PostgreSQL.
- Comunicacion prevista: API REST con JSON.
- Autenticacion prevista: JWT con Spring Security.

## Componentes del Sprint 1

- Layout responsive con sidebar fijo, topbar y area de contenido.
- Navegacion agrupada con iconos de Lucide React.
- Componentes UI: `Button`, `Input`, `Card`, `Badge`, `ProgressBar` y `Toast`.
- Barra de progreso con gradiente y shimmer violeta.
- Dashboard temporal con estadisticas y badges de ejemplo.
- Paginas placeholder sin logica de negocio.

## Rutas frontend

| Ruta | Vista |
| --- | --- |
| `/` | Inicio |
| `/dashboard` | Dashboard temporal |
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

## Ejecutar el backend

Configura las variables de entorno y ejecuta Spring Boot:

```powershell
$env:DB_HOST="localhost"
$env:DB_PORT="5432"
$env:DB_NAME="fisihub"
$env:DB_USER="postgres"
$env:DB_PASSWORD="postgres"
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

Durante el Sprint 1 el backend puede iniciar sin que PostgreSQL este disponible,
porque aun no existen entidades ni operaciones de persistencia. Las variables
ya dejan preparada la conexion para los siguientes sprints.

## Variables de entorno

| Variable | Descripcion | Valor por defecto |
| --- | --- | --- |
| `DB_HOST` | Host de PostgreSQL | `localhost` |
| `DB_PORT` | Puerto de PostgreSQL | `5432` |
| `DB_NAME` | Nombre de la base de datos | `fisihub` |
| `DB_USER` | Usuario de PostgreSQL | `postgres` |
| `DB_PASSWORD` | Contrasena de PostgreSQL | `postgres` |

No se deben versionar credenciales reales ni archivos `.env`.
