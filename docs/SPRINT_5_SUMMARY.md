# Sprint 5 - Kanban basico

## Estado

Sprint 5 completado. No se implementaron funcionalidades de Sprint 6.

## Alcance implementado

- Tablero protegido en `/kanban` con tareas reales del usuario autenticado.
- Selector de proyecto.
- Columnas `PENDIENTE`, `EN_PROCESO`, `EN_REVISION`, `COMPLETADA` y
  `BLOQUEADA`.
- Tarjetas con titulo, proyecto, prioridad, responsable, fecha limite y estado.
- Drag and drop entre columnas con `@dnd-kit/core`.
- Selector de estado alternativo en cada tarjeta.
- Estados de carga, error, tablero vacio y columnas vacias.
- Indicador visual para prioridad urgente y fondo diferenciado por columna.
- Barra de avance refrescada desde el backend despues de cada cambio.

## Backend reutilizado

No se agregaron entidades, tablas ni endpoints. Se reutilizaron:

- `GET /api/tareas`
- `GET /api/proyectos`
- `GET /api/proyectos/{id}`
- `GET /api/proyectos/{id}/tareas`
- `PATCH /api/tareas/{id}/estado`

La autorizacion por JWT, el control de acceso y el recalculo del porcentaje
permanecen en `TareaService`.

## Pruebas

La prueba de integracion de tareas ahora cubre que el porcentaje del proyecto:

- Aumenta cuando una tarea pasa a `COMPLETADA`.
- Disminuye cuando una tarea sale de `COMPLETADA`.
- Vuelve a aumentar al completarla nuevamente.

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

- Reordenamiento dentro de una misma columna.
- Columnas personalizadas.
- Comentarios y archivos adjuntos.
- Reportes y notificaciones reales.
- Dashboard con datos reales de Sprint 6.
