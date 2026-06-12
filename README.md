# FISIHUB

FISIHUB es una plataforma web para gestionar proyectos academicos y de
software de forma simple, visual y directa.

## Estado del proyecto

Sprint 0: estructura inicial del frontend, backend y documentacion. Todavia no
se han implementado autenticacion, dashboard, proyectos, tareas, Kanban,
reportes, notificaciones ni administracion.

## Stack

- Frontend: React, Vite y TailwindCSS.
- Backend: Spring Boot, Java 17+, Spring MVC, Spring Data JPA, Validation y
  Spring Security.
- Base de datos: PostgreSQL.
- Comunicacion prevista: API REST con JSON.
- Autenticacion prevista: JWT con Spring Security.

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

Durante el Sprint 0 el backend puede iniciar sin que PostgreSQL este disponible,
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

