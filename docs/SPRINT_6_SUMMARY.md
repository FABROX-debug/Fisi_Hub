# Sprint 6 - Dashboard con datos reales

## Estado

Sprint 6 completado. No se implementaron funcionalidades de Sprint 7.

## Backend

Se agrego el endpoint protegido:

- `GET /api/dashboard/resumen`

El service calcula exclusivamente sobre proyectos donde participa el usuario:

- Proyectos activos, excluyendo `FINALIZADO` y `CANCELADO`.
- Tareas pendientes y completadas.
- Tareas vencidas no completadas.
- Tareas con fecha limite hoy.
- Promedio de avance de proyectos accesibles.
- Hasta cinco proyectos activos recientes.
- Tareas pendientes para hoy o los proximos tres dias.
- Hasta ocho tareas vencidas para mostrar en el dashboard.

La respuesta usa DTOs y no expone entidades. `actividadReciente` se devuelve
como lista vacia porque aun no existe historial persistente y no se agrego una
tabla fuera del alcance solicitado.

## Frontend

- `dashboardService.js` consume el resumen autenticado.
- Las cuatro estadisticas usan valores reales.
- Los proyectos muestran estado, prioridad, lider, fecha y progreso real.
- Se muestran tareas proximas y vencidas.
- El saludo usa el nombre del usuario autenticado.
- Existen estados de carga, error, proyectos vacios y tareas vacias.
- No quedan cifras temporales como fuente principal.

## Pruebas

La prueba de integracion verifica:

- Rechazo sin JWT.
- Estadisticas calculadas.
- Proyectos activos y finalizados.
- Tareas de hoy, proximas, completadas y vencidas.
- Aislamiento de datos entre usuarios.
- Respuesta vacia de actividad reciente.

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

- Historial persistente de actividad.
- Comentarios y gestion de miembros.
- Notificaciones reales.
- Reportes avanzados y exportaciones.
- Funcionalidades de Sprint 7.
