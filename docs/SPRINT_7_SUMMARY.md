# Sprint 7 - Miembros, comentarios e historial basico

## Estado

Sprint 7 completado. No se implementaron funcionalidades de Sprint 8.

## Backend

Se completo la gestion de `MiembroProyecto` con roles internos `LIDER` y
`MIEMBRO`. Un lider interno o un usuario con rol global `ADMIN` puede agregar,
cambiar el rol y quitar miembros. Se impiden miembros duplicados, eliminar al
lider designado, dejar el proyecto sin lider y quitar usuarios con tareas
asignadas.

Se agregaron las entidades:

- `Comentario`: tarea, autor, contenido y fecha de creacion.
- `HistorialActividad`: proyecto, actor, tipo, descripcion y fecha.

El historial registra:

- Creacion de proyecto.
- Creacion de tarea.
- Cambio de estado de tarea.
- Miembro agregado.
- Comentario creado.

Los controllers devuelven DTOs y validan que el usuario tenga acceso al
proyecto. Los comentarios vacios se rechazan y solo pueden eliminarse por su
autor, un lider interno del proyecto o un administrador.

## Endpoints

Todos requieren JWT:

```text
GET    /api/proyectos/{id}/miembros
POST   /api/proyectos/{id}/miembros
PATCH  /api/proyectos/{id}/miembros/{usuarioId}/rol
DELETE /api/proyectos/{id}/miembros/{usuarioId}

GET    /api/tareas/{id}/comentarios
POST   /api/tareas/{id}/comentarios
DELETE /api/comentarios/{id}

GET    /api/proyectos/{id}/actividad
```

`GET /api/dashboard/resumen` reutiliza ahora el historial persistente para
devolver hasta cinco eventos recientes de proyectos accesibles.

## Frontend

- `/miembros` permite seleccionar proyecto, listar el equipo y su carga activa.
- Los lideres pueden agregar usuarios por correo, cambiar roles y quitar
  miembros con confirmacion.
- Se muestra actividad reciente real por proyecto.
- La tabla de tareas incorpora un panel de comentarios.
- Se pueden crear y eliminar comentarios autorizados.
- Loading, errores y estados vacios se presentan con componentes visuales, sin
  `alert()`.

## Pruebas

La prueba de integracion de colaboracion verifica JWT, aislamiento por
proyecto, miembros duplicados, ultimo lider, permisos de gestion, comentarios,
moderacion, actividad y revocacion de acceso.

Comandos:

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

## Fuera de alcance

- Chat o WebSockets.
- Menciones y respuestas anidadas.
- Archivos adjuntos.
- Notificaciones y reportes reales.
- Funcionalidades de Sprint 8.
