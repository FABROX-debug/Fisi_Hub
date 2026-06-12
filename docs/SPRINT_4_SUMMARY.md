# Sprint 4 - Gestion de Tareas

## Estado

Completado.

## Backend

- Entidad JPA `Tarea`.
- Enums `EstadoTarea` y `PrioridadTarea`.
- Repository, DTOs, service y controllers separados por capas.
- CRUD de tareas protegido con JWT.
- Listado global del usuario y listado por proyecto.
- Filtros por estado, prioridad, proyecto y responsable.
- Responsable opcional validado contra `MiembroProyecto`.
- Proyecto de la tarea inmutable durante la edicion.
- Fecha limite validada para impedir fechas pasadas.
- Recalculo automatico del avance del proyecto en crear, editar, cambiar
  estado y eliminar.
- Avance calculado con redondeo entero:
  `completadas / total * 100`; sin tareas equivale a `0`.

## Endpoints

- `GET /api/tareas`
- `POST /api/tareas`
- `GET /api/tareas/{id}`
- `PUT /api/tareas/{id}`
- `PATCH /api/tareas/{id}/estado`
- `DELETE /api/tareas/{id}`
- `GET /api/proyectos/{id}/tareas`

Todos requieren JWT y respetan la membresia del proyecto.

## Frontend

- Vista protegida `/tareas` conectada al backend.
- Tabla responsive con proyecto, responsable, prioridad, estado y fecha.
- Modal para crear y editar tareas.
- Cambio de estado desde la tabla.
- Eliminacion con confirmacion.
- Filtros por estado, prioridad y proyecto.
- Estado global con Zustand y servicio `tareaService.js`.
- Errores mediante `Toast`; no se usa `alert()`.

## Pruebas

- JWT obligatorio.
- Defaults `PENDIENTE` y `MEDIA`.
- CRUD y filtros.
- Responsable miembro y rechazo de usuario externo.
- Rechazo de fecha pasada.
- Aislamiento entre usuarios.
- Recalculo del avance `0`, `50` y `100`.
- Regresion de autenticacion, salud, espacios y proyectos.

## Fuera de alcance

No se implementaron drag and drop, Kanban funcional, comentarios, archivos,
notificaciones, reportes ni funcionalidades del Sprint 5.
