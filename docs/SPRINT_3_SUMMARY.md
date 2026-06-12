# Sprint 3 - Espacios de Trabajo y Proyectos

## Estado

Completado.

## Backend

- Entidades `EspacioTrabajo`, `EspacioMiembro`, `Proyecto` y
  `MiembroProyecto`.
- Enums de estado, prioridad y roles internos de membresia.
- Repositories, DTOs, services y controllers separados por capas.
- CRUD REST de espacios y proyectos.
- Listado de proyectos global y por espacio.
- Aislamiento de datos por membresia del usuario autenticado.
- Creador del espacio asociado automaticamente como `LIDER`.
- Creador del proyecto asociado automaticamente como `LIDER`.
- Avance inicial fijo en `0`; no se calcula con tareas en este sprint.
- Validacion de fechas y manejo JSON de errores `400`, `403` y `404`.

## Endpoints

- `GET /api/espacios`
- `POST /api/espacios`
- `GET /api/espacios/{id}`
- `PUT /api/espacios/{id}`
- `DELETE /api/espacios/{id}`
- `GET /api/espacios/{id}/proyectos`
- `GET /api/proyectos`
- `POST /api/proyectos`
- `GET /api/proyectos/{id}`
- `PUT /api/proyectos/{id}`
- `DELETE /api/proyectos/{id}`

Todos los endpoints requieren JWT.

## Frontend

- Ruta protegida `/espacios`.
- Vista de espacios con alta, edicion y eliminacion confirmada.
- Vista de proyectos conectada al backend.
- Alta, edicion y eliminacion confirmada de proyectos.
- Filtros por estado.
- Cards con espacio, estado, prioridad, fechas, miembros y ProgressBar.
- Store Zustand compartido para espacios y proyectos.
- Errores mostrados con `Toast`; no se usa `alert()`.

## Pruebas

- JWT obligatorio para espacios y proyectos.
- CRUD de espacios y proyectos.
- Liderazgo automatico del creador.
- Avance inicial en `0`.
- Asociacion del proyecto con su espacio.
- Aislamiento entre usuarios.
- Continuidad de autenticacion y `/api/health`.

## Fuera de alcance

No se implementaron tareas, Kanban funcional, drag and drop, comentarios,
archivos, reportes, notificaciones ni funcionalidades del Sprint 4.
