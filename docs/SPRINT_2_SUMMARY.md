# Sprint 2 - Autenticacion y Usuarios

## Estado

Completado.

## Alcance implementado

### Backend

- Entidades JPA `Usuario`, `Rol` y `UsuarioRol`.
- Repositorios para las tres entidades.
- Roles iniciales `ADMIN`, `LIDER` y `MIEMBRO`.
- Registro de usuarios con rol `MIEMBRO`.
- Passwords protegidos con BCrypt.
- Login con Spring Security.
- Generacion y validacion de JWT.
- Filtro stateless para tokens Bearer.
- Endpoints:
  - `POST /api/auth/register`
  - `POST /api/auth/login`
  - `GET /api/auth/me`
- Respuestas mediante DTOs; las entidades no se exponen.
- Manejo JSON de errores de validacion, conflicto y autenticacion.
- CORS para el frontend local en `http://localhost:5173`.

### Frontend

- Paginas publicas `/login` y `/register`.
- Landing publica en `/`.
- `authService.js` para comunicacion con la API.
- `authStore.js` con Zustand.
- Token persistido en `localStorage`.
- Restauracion de sesion mediante `/api/auth/me`.
- `ProtectedRoute.jsx` para rutas privadas.
- Nombre y rol del usuario en sidebar y topbar.
- Nombre del usuario autenticado en dashboard.
- Logout desde el menu del avatar.

## Seguridad

- `/api/health`, registro y login son publicos.
- `/api/auth/me` y futuras rutas `/api/**` requieren JWT.
- La sesion del servidor es stateless.
- `JWT_SECRET` es obligatorio y debe tener al menos 32 caracteres.
- No se implementaron refresh tokens, OAuth ni recuperacion por correo.

## Pruebas automatizadas

El perfil `test` usa H2 en memoria y valida:

- Inicializacion de los tres roles.
- Registro y asignacion del rol `MIEMBRO`.
- Password almacenado como hash BCrypt.
- Login correcto.
- Login incorrecto con HTTP 401.
- `/api/auth/me` con token valido.
- `/api/auth/me` sin token con HTTP 401.
- Continuidad de `/api/health`.

## Base de datos

En Sprint 2 se usa temporalmente:

```properties
spring.jpa.hibernate.ddl-auto=update
```

PostgreSQL debe estar activo y las credenciales deben suministrarse mediante
variables de entorno. No se versionan contrasenas.

## Fuera de alcance

No se implementaron espacios, proyectos, tareas, Kanban, reportes,
notificaciones ni funcionalidades de Sprint 3.

