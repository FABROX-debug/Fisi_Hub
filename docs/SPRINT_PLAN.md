# FISIHUB — Plan de Sprints MVP

> **Versión:** 1.0  
> **Referencia:** `docs/FISIHUB_SPEC.md`  
> **Repositorio:** https://github.com/FABROX-debug/Fisi_Hub.git  
> Codex debe leer este archivo y `docs/FISIHUB_SPEC.md` antes de cada sprint.  
> **Regla de oro:** Codex NO avanza al siguiente sprint si no se le pide explícitamente.

---

## RESUMEN EJECUTIVO

| Sprint | Nombre                          | Estado      | Depende de |
|--------|---------------------------------|-------------|------------|
| 0      | Preparación del MVP             | ✅ Completo  | —          |
| 1      | Sistema de diseño y layout base | ✅ Completo  | Sprint 0   |
| 2      | Autenticación y usuarios        | ✅ Completo  | Sprint 1   |
| 3      | Espacios de trabajo y proyectos | ✅ Completo  | Sprint 2   |
| 4      | Tareas                          | ⏳ Pendiente | Sprint 3   |
| 5      | Kanban básico                   | ⏳ Pendiente | Sprint 4   |
| 6      | Dashboard con datos reales      | ⏳ Pendiente | Sprint 5   |
| 7      | Miembros y comentarios          | ⏳ Pendiente | Sprint 6   |
| 8      | Cierre del MVP                  | ⏳ Pendiente | Sprint 7   |

---

## SPRINT 0 — Preparación del MVP

**Estado:** ✅ Completado
**Commit:** `git commit -m "Sprint 0: preparación del MVP"`

### Objetivo
Dejar el proyecto base listo para desarrollo. Sin lógica de negocio, sin CRUD, sin base de datos real.

### Lo que se hizo
- Estructura `frontend/` + `backend/` + `docs/`
- React + Vite + TailwindCSS funcionando
- Spring Boot con arquitectura MVC (paquetes base creados)
- Endpoint `GET /api/health` respondiendo `{"status": "FISIHUB backend funcionando"}`
- `README.md` y `.gitignore` creados
- Test de health passing
- Node, npm, Java, Maven instalados y verificados

### Lo que NO se hizo (correcto)
- Sin login, sin registro, sin JWT
- Sin entidades JPA ni tablas
- Sin PostgreSQL instalado localmente
- Sin CRUD de ningún tipo

### Notas
- PostgreSQL debe instalarse **antes de comenzar Sprint 2**.
- Como alternativa temporal para tests: configurar perfil H2 solo para unit tests.

---

## SPRINT 1 — Sistema de Diseño y Layout Base

**Estado:** ✅ Completado
**Commit esperado:** `git commit -m "Sprint 1: sistema de diseño y layout base"`  
**PostgreSQL requerido:** ❌ NO (aún no)

### Objetivo
Construir el esqueleto visual completo de FISIHUB: layout, componentes UI reutilizables, rutas placeholder y dashboard temporal. Sin ningún dato real ni conexión al backend.

### Historias de usuario
- Como usuario, quiero ver el layout general de la app (sidebar + topbar) para orientarme.
- Como usuario, quiero navegar entre secciones mediante el menú lateral.
- Como desarrollador, quiero tener los componentes base (Button, Badge, Card, etc.) listos para usar en sprints siguientes.

### Tareas frontend
- [ ] Instalar `react-router-dom` v6, `lucide-react`
- [ ] Configurar Tailwind con los colores custom de FISIHUB (ver `FISIHUB_SPEC.md` sección 1.1)
- [ ] Crear `src/layouts/AppLayout.jsx` — sidebar fijo 240px + topbar + content area
- [ ] Crear `src/layouts/Sidebar.jsx` — menú agrupado con secciones: GENERAL / TRABAJO / EQUIPO / ANÁLISIS / SISTEMA
- [ ] Crear `src/layouts/Topbar.jsx` — logo izquierda, búsqueda centro, avatar derecha
- [ ] Crear componentes en `src/components/ui/`:
  - `Button.jsx` — variantes: primary, secondary, danger, ghost
  - `Input.jsx` — con label, placeholder, mensaje de error
  - `Card.jsx` — con shadow y border-radius 12px
  - `Badge.jsx` — estados: pendiente, en-proceso, revision, completada, bloqueada + prioridades
  - `ProgressBar.jsx` — con efecto shimmer violeta (elemento firma del diseño)
  - `Toast.jsx` — éxito/error/info, auto-dismiss 4s, esquina inferior derecha
  - `Avatar.jsx` — con foto o iniciales sobre fondo violeta
  - `Modal.jsx` — overlay + contenedor centrado con cierre por ESC y clic exterior
  - `EmptyState.jsx` — ilustración SVG + mensaje de acción
  - `Skeleton.jsx` — placeholder de carga para cards y listas
- [ ] Crear rutas placeholder en `src/router/index.jsx`:
  - `/` — Landing pública
  - `/dashboard` — Dashboard (temporal, datos hardcodeados)
  - `/proyectos` — Proyectos (vacío con EmptyState)
  - `/tareas` — Tareas (vacío)
  - `/kanban` — Kanban (vacío)
  - `/miembros` — Miembros (vacío)
  - `/reportes` — Reportes (vacío)
  - `/configuracion` — Configuración (vacío)
- [ ] Crear `src/pages/LandingPage.jsx` — hero con gradiente, beneficios, CTA
- [ ] Crear `src/pages/DashboardPage.jsx` — datos hardcodeados: 4 stat cards, lista de proyectos con ProgressBar, actividad reciente

### Tareas backend
- Ninguna en este sprint. El backend ya tiene `GET /api/health` del Sprint 0.

### Tareas de base de datos
- Ninguna. PostgreSQL no se toca en este sprint.

### Qué NO debe hacer Codex en este sprint
- ❌ No implementar login ni registro
- ❌ No tocar Spring Boot ni el backend
- ❌ No crear entidades JPA ni repositorios
- ❌ No conectar el frontend al backend
- ❌ No instalar ni configurar PostgreSQL
- ❌ No implementar drag & drop
- ❌ No agregar lógica de estado global real (solo data hardcoded está bien)

### Criterios de aceptación
- [ ] `npm run dev` levanta el frontend sin errores
- [ ] La barra lateral tiene todos los ítems del menú agrupados
- [ ] El topbar muestra logo + barra de búsqueda + avatar
- [ ] Cada ruta renderiza su componente (aunque sea un EmptyState)
- [ ] El componente `Button` tiene las 4 variantes funcionando
- [ ] El componente `Badge` muestra los 5 estados con sus colores correctos
- [ ] El `ProgressBar` tiene el efecto shimmer visible en el dashboard
- [ ] El `Toast` aparece y desaparece a los 4 segundos
- [ ] El dashboard muestra al menos 4 stat cards y 2 tarjetas de proyecto con barra de progreso
- [ ] La landing page tiene hero con gradiente + sección de beneficios + CTA

### Dependencias
- Requiere Sprint 0 completo ✅

### Comandos de prueba
```bash
cd frontend
npm run dev        # Debe levantar sin errores
npm run build      # Debe compilar sin errores
npm run lint       # Debe pasar sin warnings críticos
```

### Resultado esperado
Una app React navegable, visualmente consistente con la identidad FISIHUB, sin ninguna conexión al backend, con todos los componentes base listos para los sprints de funcionalidad.

---

## SPRINT 2 — Autenticación y Usuarios

**Estado:** ✅ Completado
**Commit esperado:** `git commit -m "Sprint 2: autenticación JWT y gestión de usuarios"`  
**PostgreSQL requerido:** ✅ SÍ — debe estar instalado ANTES de este sprint

### Objetivo
Implementar registro, login y autenticación JWT completa. El usuario puede crear cuenta, iniciar sesión y ver rutas protegidas. El token se almacena en el cliente.

### Historias de usuario
- Como visitante, quiero registrarme con nombre, correo y contraseña.
- Como usuario registrado, quiero iniciar sesión y entrar al dashboard.
- Como sistema, quiero proteger todas las rutas privadas con JWT.

### Tareas frontend
- [ ] Crear `src/pages/RegisterPage.jsx` — layout dos columnas, validación en tiempo real
- [ ] Crear `src/pages/LoginPage.jsx` — layout dos columnas, manejo de errores
- [ ] Crear `src/services/authService.js` — funciones `register()`, `login()`, `logout()`
- [ ] Crear `src/context/AuthContext.jsx` — estado global del usuario autenticado
- [ ] Crear `src/components/ProtectedRoute.jsx` — redirige a `/login` si no hay token
- [ ] Envolver rutas privadas con `ProtectedRoute`
- [ ] Guardar token JWT en `localStorage`
- [ ] Mostrar nombre del usuario en el sidebar y topbar al autenticarse
- [ ] Implementar logout desde el menú del avatar en topbar

### Tareas backend
- [ ] Crear entidad `Usuario` (id, nombre, correo, password, activo, creadoEn)
- [ ] Crear entidad `Rol` (id, nombre) con valores: ADMIN, LIDER, MIEMBRO
- [ ] Crear entidad `UsuarioRol` (usuarioId, rolId)
- [ ] Crear `UsuarioRepository`, `RolRepository`
- [ ] Crear `UsuarioService` con lógica de registro y búsqueda
- [ ] Crear `AuthController` (Boundary) con endpoints:
  - `POST /api/auth/register`
  - `POST /api/auth/login`
  - `GET /api/auth/me` (requiere token)
- [ ] Crear `JwtUtil` para generar y validar tokens
- [ ] Crear `JwtFilter` para interceptar requests con Bearer token
- [ ] Configurar `SecurityConfig` con Spring Security:
  - Rutas públicas: `/api/auth/**`, `/api/health`
  - Rutas privadas: todo lo demás
- [ ] Crear `RegisterRequest`, `LoginRequest`, `AuthResponse` DTOs
- [ ] Hash de contraseñas con BCrypt

### Tareas de base de datos
- [ ] Instalar PostgreSQL localmente si aún no está
- [ ] Crear base de datos: `fisihub_db`
- [ ] Crear usuario de base de datos con permisos
- [ ] Configurar variables de entorno: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`
- [ ] Validar que Spring Boot conecta a PostgreSQL al arrancar (`spring.jpa.hibernate.ddl-auto=update` temporalmente)
- [ ] Las tablas `usuario`, `rol`, `usuario_rol` deben crearse automáticamente
- [ ] Insertar roles iniciales con `data.sql` o `CommandLineRunner`: ADMIN, LIDER, MIEMBRO

### Qué NO debe hacer Codex en este sprint
- ❌ No crear pantallas de proyectos, tareas ni Kanban
- ❌ No implementar recuperación de contraseña por email (fuera del MVP)
- ❌ No crear espacios de trabajo ni proyectos
- ❌ No implementar roles complejos con permisos granulares
- ❌ No agregar refresh tokens

### Criterios de aceptación
- [ ] `POST /api/auth/register` crea un usuario y devuelve token JWT
- [ ] `POST /api/auth/login` con credenciales correctas devuelve token JWT
- [ ] `POST /api/auth/login` con credenciales incorrectas devuelve 401
- [ ] `GET /api/auth/me` con token válido devuelve datos del usuario
- [ ] `GET /api/auth/me` sin token devuelve 401
- [ ] El frontend redirige a `/login` si no hay token
- [ ] Tras login exitoso, el usuario ve el dashboard con su nombre
- [ ] El logout borra el token y redirige a `/login`
- [ ] Las contraseñas están hasheadas en la base de datos (no en texto plano)

### Dependencias
- Sprint 0 ✅ y Sprint 1 ✅ completados
- PostgreSQL instalado y corriendo

### Comandos de prueba
```bash
# Backend
cd backend
./mvnw test

# Prueba manual con curl o Postman
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Fabrizio","correo":"fab@test.com","password":"Test1234"}'

curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"correo":"fab@test.com","password":"Test1234"}'
```

---

## SPRINT 3 — Espacios de Trabajo y Proyectos

**Estado:** ✅ Completado  
**Commit esperado:** `git commit -m "Sprint 3: espacios de trabajo y proyectos CRUD"`  
**PostgreSQL requerido:** ✅ SÍ

### Objetivo
Crear y gestionar espacios de trabajo y proyectos. El usuario autenticado puede crear un espacio, agregar un proyecto, verlo en listado con su avance y estado.

### Historias de usuario
- Como líder, quiero crear un espacio de trabajo para agrupar mis proyectos.
- Como líder, quiero crear proyectos dentro de un espacio con nombre, fechas, prioridad y estado.
- Como miembro, quiero ver los proyectos en los que participo con su porcentaje de avance.

### Tareas frontend
- [ ] Crear `src/pages/EspaciosPage.jsx` — lista de espacios con botón crear
- [ ] Crear modal de crear/editar espacio (nombre, descripción, color)
- [ ] Crear `src/pages/ProyectosPage.jsx` — grid de cards con filtros por estado
- [ ] Crear `ProjectCard.jsx` — con nombre, estado, prioridad, fechas, avatares, ProgressBar
- [ ] Crear modal de crear/editar proyecto (todos los campos del spec)
- [ ] Crear `src/pages/ProyectoDetailPage.jsx` — con tabs: Resumen / Tareas / Miembros
- [ ] Crear `src/services/espacioService.js` y `proyectoService.js`
- [ ] Conectar con API: GET, POST, PUT, DELETE de espacios y proyectos

### Tareas backend
- [ ] Crear entidad `EspacioTrabajo` (id, nombre, descripcion, color, icono, creadoPor, creadoEn)
- [ ] Crear entidad `EspacioMiembro` (espacioId, usuarioId)
- [ ] Crear entidad `Proyecto` (id, nombre, descripcion, fechaInicio, fechaFin, estado, prioridad, porcentajeAvance, espacioId, liderId, creadoEn)
- [ ] Crear entidad `MiembroProyecto` (proyectoId, usuarioId, rolEnProyecto)
- [ ] Crear repositories para cada entidad
- [ ] Crear `EspacioService` y `ProyectoService` con lógica CRUD
- [ ] Crear `EspacioController` y `ProyectoController` (Boundary)
- [ ] Endpoints de espacios: GET all, POST, GET by id, PUT, DELETE, GET proyectos por espacio
- [ ] Endpoints de proyectos: GET all (del usuario), POST, GET by id, PUT, DELETE
- [ ] Endpoint de miembros de proyecto: POST (agregar), DELETE (quitar)
- [ ] Validar que el `porcentajeAvance` se calcula correctamente (0 al crear)
- [ ] Crear DTOs: `EspacioRequest`, `EspacioResponse`, `ProyectoRequest`, `ProyectoResponse`

### Tareas de base de datos
- [ ] Las tablas `espacio_trabajo`, `espacio_miembro`, `proyecto`, `miembro_proyecto` deben crearse automáticamente
- [ ] `proyecto.estado` como ENUM: PLANIFICADO, EN_PROCESO, EN_REVISION, FINALIZADO, CANCELADO
- [ ] `proyecto.prioridad` como ENUM: BAJA, MEDIA, ALTA, URGENTE

### Qué NO debe hacer Codex en este sprint
- ❌ No crear tareas ni Kanban
- ❌ No implementar archivos adjuntos
- ❌ No implementar notificaciones
- ❌ No implementar reportes

### Criterios de aceptación
- [ ] CRUD completo de espacios funciona vía API
- [ ] CRUD completo de proyectos funciona vía API
- [ ] La UI muestra la lista de proyectos con cards correctas
- [ ] Se puede crear un proyecto desde el frontend y verlo en el listado
- [ ] La card de proyecto muestra ProgressBar en 0% al crearse
- [ ] Los filtros por estado funcionan en el listado de proyectos

### Dependencias
- Sprint 2 ✅ (autenticación funcional, JWT en header de cada request)

### Comandos de prueba
```bash
# Verificar tablas
psql -d fisihub_db -c "\dt"

# Test backend
cd backend && ./mvnw test
```

---

## SPRINT 4 — Tareas

**Estado:** ⏳ Pendiente  
**Commit esperado:** `git commit -m "Sprint 4: gestión de tareas CRUD"`  
**PostgreSQL requerido:** ✅ SÍ

### Objetivo
Crear, asignar y gestionar tareas dentro de proyectos. El usuario puede ver sus tareas, filtrarlas y cambiar su estado.

### Historias de usuario
- Como líder, quiero crear tareas dentro de un proyecto y asignar un responsable.
- Como miembro, quiero ver mis tareas, su prioridad y fecha límite.
- Como líder, quiero filtrar tareas por estado, prioridad y responsable.

### Tareas frontend
- [ ] Crear `src/pages/TareasPage.jsx` — vista tabla con columnas: tarea/responsable/prioridad/estado/vence
- [ ] Crear panel lateral `TaskDetailPanel.jsx` — slide-in desde la derecha con todos los campos
- [ ] Crear `TaskRow.jsx` — fila de tabla con badges de estado y prioridad
- [ ] Implementar filtros: estado, prioridad, responsable, fecha
- [ ] Toggle vista: lista ↔ (Kanban se conectará en Sprint 5)
- [ ] Crear `src/services/tareaService.js`
- [ ] Conectar con API: GET, POST, PUT, PATCH estado, DELETE

### Tareas backend
- [ ] Crear entidad `Tarea` (id, titulo, descripcion, responsableId, proyectoId, fechaLimite, estado, prioridad, creadoPor, creadoEn, actualizadoEn)
- [ ] Crear `TareaRepository` con queries para filtrar por proyecto, responsable, estado
- [ ] Crear `TareaService` con lógica CRUD + recalcular `porcentajeAvance` del proyecto al cambiar estado
- [ ] Crear `TareaController` (Boundary) con endpoints:
  - `GET /api/tareas` (filtros opcionales por querystring)
  - `POST /api/tareas`
  - `GET /api/tareas/{id}`
  - `PUT /api/tareas/{id}`
  - `PATCH /api/tareas/{id}/estado`
  - `DELETE /api/tareas/{id}`
  - `GET /api/proyectos/{id}/tareas`
- [ ] Al marcar tarea como COMPLETADA: recalcular y actualizar `porcentajeAvance` del proyecto
- [ ] Crear DTOs: `TareaRequest`, `TareaResponse`, `EstadoRequest`
- [ ] Validaciones: título requerido, fechaLimite no puede ser pasada al crear, responsable debe ser miembro del proyecto

### Tareas de base de datos
- [ ] Tabla `tarea` con ENUMs: `estado` (PENDIENTE, EN_PROCESO, EN_REVISION, COMPLETADA, BLOQUEADA) y `prioridad` (BAJA, MEDIA, ALTA, URGENTE)

### Qué NO debe hacer Codex en este sprint
- ❌ No implementar drag & drop (es Sprint 5)
- ❌ No implementar comentarios (es Sprint 7)
- ❌ No implementar archivos adjuntos
- ❌ No construir el Kanban visual

### Criterios de aceptación
- [ ] Se puede crear una tarea con todos sus campos desde el panel lateral
- [ ] `PATCH /api/tareas/{id}/estado` actualiza el estado y recalcula avance del proyecto
- [ ] Los filtros de la tabla funcionan correctamente
- [ ] Los badges de estado y prioridad usan los colores del spec
- [ ] Al completar una tarea, el `porcentajeAvance` del proyecto se actualiza en la BD

### Dependencias
- Sprint 3 ✅ (proyectos y miembros funcionales)

---

## SPRINT 5 — Kanban Básico

**Estado:** ⏳ Pendiente  
**Commit esperado:** `git commit -m "Sprint 5: tablero Kanban con drag and drop"`  
**PostgreSQL requerido:** ✅ SÍ

### Objetivo
Implementar el tablero Kanban visual por proyecto. Las tareas se muestran como tarjetas en columnas por estado. El usuario puede arrastrar tarjetas entre columnas para cambiar el estado.

### Historias de usuario
- Como usuario, quiero ver las tareas de un proyecto organizadas en columnas por estado.
- Como usuario, quiero arrastrar una tarjeta a otra columna y que su estado se actualice automáticamente.
- Como usuario, quiero hacer clic en una tarjeta y ver su detalle en un panel lateral.

### Tareas frontend
- [ ] Instalar `@dnd-kit/core` y `@dnd-kit/sortable` (librería de drag & drop)
- [ ] Crear `src/pages/KanbanPage.jsx` — layout horizontal con scroll lateral
- [ ] Crear `KanbanColumn.jsx` — columna con título, contador y lista de tarjetas droppable
- [ ] Crear `KanbanCard.jsx` — tarjeta con título, badge prioridad, avatar responsable, fecha, contador de comentarios
- [ ] Implementar drag & drop: al soltar tarjeta en otra columna → `PATCH /api/tareas/{id}/estado`
- [ ] Reutilizar `TaskDetailPanel.jsx` del Sprint 4 al hacer clic en tarjeta
- [ ] Selector de proyecto activo en el Kanban (dropdown o tabs)
- [ ] Animación suave al mover tarjeta (200ms ease-out)
- [ ] Tarjeta URGENTE con borde izquierdo rojo pulsante
- [ ] Columna COMPLETADO con fondo levemente verde

### Tareas backend
- No hay nuevos endpoints en este sprint. Se usa `PATCH /api/tareas/{id}/estado` del Sprint 4.

### Tareas de base de datos
- Ninguna nueva.

### Qué NO debe hacer Codex en este sprint
- ❌ No implementar múltiples tableros dentro del mismo proyecto
- ❌ No implementar columnas personalizadas
- ❌ No implementar reordenamiento dentro de la misma columna (opcional si hay tiempo)

### Criterios de aceptación
- [ ] Las 4 columnas se muestran: PENDIENTE / EN PROCESO / EN REVISIÓN / COMPLETADO
- [ ] Las tarjetas muestran: título, prioridad (badge), responsable (avatar), fecha límite
- [ ] El drag & drop funciona: al soltar, el estado se actualiza vía API
- [ ] El panel lateral se abre al hacer clic en una tarjeta
- [ ] La columna COMPLETADO tiene fondo diferenciado
- [ ] Las tarjetas URGENTE tienen el borde pulsante rojo

### Dependencias
- Sprint 4 ✅ (tareas funcionales con PATCH de estado)

---

## SPRINT 6 — Dashboard con Datos Reales

**Estado:** ⏳ Pendiente  
**Commit esperado:** `git commit -m "Sprint 6: dashboard con datos reales del backend"`  
**PostgreSQL requerido:** ✅ SÍ

### Objetivo
Reemplazar los datos hardcodeados del dashboard por datos reales del backend. El dashboard muestra el estado real del trabajo del usuario autenticado.

### Historias de usuario
- Como usuario, quiero ver en el dashboard cuántos proyectos activos tengo y cuántas tareas pendientes.
- Como usuario, quiero ver las tareas que vencen hoy.
- Como usuario, quiero ver la actividad reciente de mis proyectos.

### Tareas frontend
- [ ] Crear `src/services/dashboardService.js` con función `getDashboardStats()`
- [ ] Conectar las 4 stat cards a datos reales: proyectos activos, pendientes, completadas, vencidas
- [ ] Conectar la lista de proyectos activos a datos reales (con ProgressBar real)
- [ ] Mostrar tareas que vencen hoy o en los próximos 3 días
- [ ] Mostrar las últimas 5 acciones de actividad reciente
- [ ] Manejar estado de carga con Skeleton mientras llega la API
- [ ] Manejar estado vacío con EmptyState si no hay datos

### Tareas backend
- [ ] Crear `DashboardController` (Boundary) con `GET /api/dashboard/stats`
- [ ] Crear `DashboardService` que calcule:
  - Proyectos activos del usuario
  - Tareas pendientes del usuario
  - Tareas completadas del usuario (último mes)
  - Tareas vencidas del usuario
  - Tareas que vencen en los próximos 3 días
  - Últimas 5 acciones registradas en `HistorialActividad` del usuario
- [ ] Crear `HistorialActividad` entidad (id, usuarioId, accion, entidadTipo, entidadId, descripcion, fecha)
- [ ] Registrar actividad en servicios existentes: cuando se crea/completa una tarea, cuando se crea un proyecto
- [ ] Crear `DashboardStatsResponse` DTO

### Tareas de base de datos
- [ ] Tabla `historial_actividad`

### Qué NO debe hacer Codex en este sprint
- ❌ No implementar notificaciones push
- ❌ No implementar calendario
- ❌ No refactorizar sprints anteriores

### Criterios de aceptación
- [ ] `GET /api/dashboard/stats` devuelve datos reales del usuario autenticado
- [ ] Las 4 stat cards muestran números reales
- [ ] La lista de proyectos usa ProgressBar con el porcentaje real de la BD
- [ ] Las tareas de hoy se listan correctamente
- [ ] El feed de actividad reciente muestra las últimas acciones reales

### Dependencias
- Sprint 5 ✅

---

## SPRINT 7 — Miembros y Comentarios

**Estado:** ⏳ Pendiente  
**Commit esperado:** `git commit -m "Sprint 7: gestión de miembros y comentarios en tareas"`  
**PostgreSQL requerido:** ✅ SÍ

### Objetivo
Gestionar miembros de proyectos (agregar/quitar/asignar rol) y permitir comentarios en tareas para facilitar la coordinación del equipo.

### Historias de usuario
- Como líder, quiero agregar miembros a un proyecto y asignarles un rol.
- Como líder, quiero ver la carga de trabajo de cada miembro.
- Como miembro, quiero comentar en una tarea para coordinar con el equipo.

### Tareas frontend
- [ ] Crear `src/pages/MiembrosPage.jsx` — lista con avatar, nombre, rol, tareas asignadas, barra de carga de trabajo
- [ ] Implementar modal de agregar miembro (buscar usuario por nombre/correo)
- [ ] Mostrar badge "Alta carga" si el miembro tiene 5+ tareas activas
- [ ] Agregar sección de comentarios en `TaskDetailPanel.jsx` — lista de comentarios + campo de texto + botón enviar
- [ ] Crear `src/services/comentarioService.js`
- [ ] Mostrar timestamp relativo en comentarios (hace X minutos / hace X horas)

### Tareas backend
- [ ] Crear endpoints de miembros:
  - `POST /api/proyectos/{id}/miembros` — agregar miembro
  - `DELETE /api/proyectos/{id}/miembros/{usuarioId}` — quitar miembro
  - `PATCH /api/proyectos/{id}/miembros/{usuarioId}/rol` — cambiar rol
  - `GET /api/proyectos/{id}/miembros` — listar con carga de trabajo
- [ ] Crear entidad `Comentario` (id, tareaId, autorId, contenido, creadoEn)
- [ ] Crear `ComentarioRepository`, `ComentarioService`, `ComentarioController`
- [ ] Endpoints de comentarios:
  - `GET /api/tareas/{id}/comentarios`
  - `POST /api/tareas/{id}/comentarios`
  - `DELETE /api/comentarios/{id}` (solo el autor o el líder)
- [ ] Crear DTOs: `ComentarioRequest`, `ComentarioResponse`, `MiembroConCargaResponse`

### Tareas de base de datos
- [ ] Tabla `comentario`

### Qué NO debe hacer Codex en este sprint
- ❌ No implementar menciones con @usuario
- ❌ No implementar respuestas anidadas a comentarios
- ❌ No implementar notificaciones push por comentario

### Criterios de aceptación
- [ ] Se puede agregar un miembro a un proyecto buscando por correo
- [ ] La lista de miembros muestra su carga de trabajo real
- [ ] Se puede comentar en una tarea desde el panel lateral
- [ ] Los comentarios se ordenan cronológicamente
- [ ] Solo el autor o el líder puede eliminar un comentario

### Dependencias
- Sprint 6 ✅

---

## SPRINT 8 — Cierre del MVP

**Estado:** ⏳ Pendiente  
**Commit esperado:** `git commit -m "Sprint 8: cierre MVP - notificaciones, reportes, admin y responsive"`  
**PostgreSQL requerido:** ✅ SÍ

### Objetivo
Completar las funcionalidades restantes para el MVP: notificaciones básicas, reporte de avance simple, panel de administración mínimo, diseño responsive y limpieza general del código.

### Historias de usuario
- Como usuario, quiero recibir notificaciones cuando me asignen una tarea.
- Como líder, quiero ver un reporte de avance del proyecto.
- Como administrador, quiero gestionar usuarios del sistema.
- Como usuario, quiero usar FISIHUB desde mi celular.

### Tareas frontend
- [ ] Crear `src/pages/NotificacionesPage.jsx` — lista con filtros y marcar como leído
- [ ] Implementar badge contador de notificaciones no leídas en el topbar
- [ ] Crear `src/pages/ReportesPage.jsx` — reporte de avance con barras de progreso por miembro
- [ ] Crear `src/pages/AdminPage.jsx` — tabla de usuarios con acciones activar/desactivar/cambiar rol (solo ADMIN)
- [ ] Implementar diseño responsive: sidebar colapsable en tablet, menú hamburguesa en móvil
- [ ] Agregar skeleton loaders en todas las vistas que cargan datos
- [ ] Revisar y completar empty states en todas las páginas
- [ ] Auditoría de accesibilidad: focus visible, aria-labels en íconos
- [ ] Limpiar código: eliminar console.log, datos hardcodeados residuales, imports sin usar

### Tareas backend
- [ ] Crear entidad `Notificacion` (id, usuarioId, tipo, mensaje, referenciaId, leida, creadoEn)
- [ ] Crear `NotificacionService` que genere notificaciones al:
  - Asignar una tarea a un usuario
  - Que una tarea venza mañana
  - Agregar un usuario como miembro de proyecto
- [ ] Crear `NotificacionController` con endpoints:
  - `GET /api/notificaciones`
  - `PATCH /api/notificaciones/{id}/leida`
  - `PATCH /api/notificaciones/leer-todas`
- [ ] Crear `ReportesController` con endpoints:
  - `GET /api/proyectos/{id}/reportes/avance`
  - `GET /api/proyectos/{id}/reportes/miembros`
- [ ] Crear `AdminController` con endpoints (solo ADMIN):
  - `GET /api/admin/usuarios`
  - `PATCH /api/admin/usuarios/{id}/activar`
  - `PATCH /api/admin/usuarios/{id}/desactivar`
  - `PATCH /api/admin/usuarios/{id}/rol`
- [ ] Pruebas finales de todos los endpoints críticos
- [ ] Revisar y limpiar logs, manejo de excepciones global con `@ControllerAdvice`

### Tareas de base de datos
- [ ] Tabla `notificacion`
- [ ] Revisión final de todas las tablas e índices básicos

### Qué NO debe hacer Codex en este sprint
- ❌ No implementar WebSockets ni notificaciones en tiempo real
- ❌ No implementar exportación a PDF o Excel
- ❌ No implementar calendario avanzado
- ❌ No implementar recuperación de contraseña por email real

### Criterios de aceptación
- [ ] Las notificaciones aparecen al asignar una tarea
- [ ] El badge de notificaciones muestra el número correcto
- [ ] El reporte de avance muestra porcentaje real y productividad por miembro
- [ ] El panel de admin permite activar/desactivar usuarios
- [ ] La app es usable en móvil (viewport 375px mínimo)
- [ ] No hay datos hardcodeados ni console.log en el código final
- [ ] Todos los endpoints principales tienen manejo de errores correcto

### Dependencias
- Sprint 7 ✅

---

## OBJETIVO FINAL DEL MVP

Al terminar el Sprint 8, el usuario puede:

1. ✅ Registrarse en FISIHUB
2. ✅ Iniciar sesión con JWT
3. ✅ Ver su dashboard con datos reales
4. ✅ Crear y gestionar espacios de trabajo
5. ✅ Crear y gestionar proyectos con estados y prioridades
6. ✅ Crear y asignar tareas con fechas límite
7. ✅ Ver tareas en tablero Kanban y cambiar su estado con drag & drop
8. ✅ Ver el avance real del proyecto en barras de progreso
9. ✅ Comentar en tareas para coordinar el equipo
10. ✅ Gestionar miembros del equipo y ver su carga de trabajo
11. ✅ Recibir notificaciones básicas
12. ✅ Ver reportes de avance simples
13. ✅ Administrar usuarios (rol ADMIN)
14. ✅ Usar la plataforma desde dispositivos móviles

---

*FISIHUB MVP Sprint Plan v1.0*
