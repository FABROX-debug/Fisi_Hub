# FISIHUB — Alcance del MVP

> **Archivo:** `docs/MVP_SCOPE.md`  
> Define qué entra y qué no entra en el MVP de FISIHUB.  
> Este documento es la referencia de control de alcance para todo el equipo y para Codex.

---

## OBJETIVO DEL MVP

El MVP de FISIHUB tiene un objetivo concreto: **que un equipo pequeño pueda gestionar proyectos reales de principio a fin**, con las funcionalidades mínimas necesarias para que eso sea posible.

No es una lista de tareas. Es una plataforma donde el equipo puede organizarse, comunicarse y medir el avance real del proyecto.

---

## ✅ DENTRO DEL MVP

### Autenticación y usuarios
- Registro de usuario con nombre, correo y contraseña
- Inicio de sesión con JWT
- Cierre de sesión
- Perfil de usuario básico (ver nombre, correo, rol)
- Roles del sistema: ADMIN, LIDER, MIEMBRO
- Protección de rutas privadas con JWT

### Espacios de trabajo
- Crear, editar y eliminar espacios de trabajo
- Agregar y quitar miembros de un espacio
- Ver los proyectos asociados a cada espacio

### Proyectos
- Crear, editar y eliminar proyectos
- Asignar líder, fechas de inicio/fin, prioridad y estado
- Ver lista de proyectos con filtros por estado
- Ver detalle del proyecto con tabs: Resumen / Tareas / Miembros
- Cálculo automático del porcentaje de avance en base a las tareas completadas

### Tareas
- Crear, editar y eliminar tareas dentro de proyectos
- Asignar responsable, fecha límite, estado y prioridad
- Cambiar el estado de una tarea
- Ver lista de tareas con filtros por estado, prioridad y responsable
- Historial simple de cambios de estado

### Tablero Kanban
- Visualización de tareas en columnas por estado: Pendiente / En Proceso / En Revisión / Completado
- Arrastrar tarjetas entre columnas para cambiar el estado (drag & drop)
- Panel lateral de detalle de tarea al hacer clic en una tarjeta

### Miembros
- Agregar y quitar miembros de un proyecto
- Cambiar el rol de un miembro dentro del proyecto
- Ver la carga de trabajo de cada miembro (número de tareas activas)

### Comentarios
- Comentar en tareas
- Ver comentarios ordenados cronológicamente
- Eliminar comentarios propios (o cualquiera si eres líder)

### Dashboard
- Stat cards: proyectos activos, tareas pendientes, tareas completadas, tareas vencidas
- Lista de proyectos activos con su porcentaje de avance
- Tareas que vencen hoy o en los próximos 3 días
- Feed de actividad reciente (últimas 5 acciones)

### Notificaciones
- Notificación al ser asignado a una tarea
- Notificación cuando una tarea vence mañana
- Notificación al ser agregado a un proyecto
- Marcar notificaciones como leídas (individual y todas)
- Badge con contador en el topbar

### Reportes
- Reporte de avance del proyecto: total de tareas, completadas, pendientes, vencidas
- Productividad por miembro: tareas completadas por persona
- Porcentaje de avance general del proyecto

### Panel de administración
- Ver todos los usuarios del sistema
- Activar y desactivar usuarios
- Cambiar el rol de un usuario

### Diseño e interfaz
- Identidad visual completa: paleta de colores FISIHUB, tipografía, iconografía (Lucide)
- Layout: sidebar fijo + topbar + área de contenido
- Landing page pública con hero, beneficios y CTA
- Páginas de login y registro con diseño dos columnas
- Componentes UI reutilizables: Button, Input, Card, Badge, ProgressBar con shimmer, Toast, Avatar, Modal, EmptyState, Skeleton
- Diseño responsive (móvil y tablet)
- Accesibilidad básica: contraste AA, focus visible, aria-labels

---

## ❌ FUERA DEL MVP (versión futura)

Estas funcionalidades **no se implementarán** en el MVP. No deben incluirse en ningún sprint ni en ningún prompt para Codex.

### Comunicación y archivos
- Archivos adjuntos en tareas o proyectos
- Chat interno del equipo
- Menciones con @usuario en comentarios
- Respuestas anidadas a comentarios

### Notificaciones avanzadas
- Notificaciones en tiempo real (WebSockets)
- Notificaciones por correo electrónico
- Configuración de preferencias de notificación

### Autenticación avanzada
- Recuperación de contraseña real por correo electrónico (envío de email)
- Autenticación con Google u otros proveedores (OAuth)
- Tokens de refresco (refresh tokens)
- Autenticación de dos factores (2FA)

### Reportes avanzados
- Exportar reportes a PDF
- Exportar reportes a Excel
- Gráficos avanzados (líneas de tiempo, burndown chart)
- Tiempo estimado vs. tiempo real por tarea

### Calendario
- Vista de calendario con entregas y eventos
- Integración con calendarios externos (Google Calendar, Outlook)
- Creación de eventos desde el calendario

### Funcionalidades de proyecto avanzadas
- Subtareas o tareas anidadas
- Dependencias entre tareas
- Tableros personalizados (columnas con nombres propios)
- Sprints o iteraciones dentro de un proyecto
- Etiquetas personalizadas en tareas

### Infraestructura y escala
- Multiempresa (múltiples organizaciones)
- Subdominios por organización
- Permisos granulares (más allá de ADMIN/LIDER/MIEMBRO)
- Auditoría avanzada con logs completos
- Modo offline

### Integraciones externas
- Integración con GitHub o GitLab
- Integración con Slack o Discord
- API pública documentada (Swagger completo)
- Webhooks salientes

### Plataformas adicionales
- App móvil nativa (React Native o Flutter)
- Extensión de navegador
- Aplicación de escritorio (Electron)

### Inteligencia artificial
- Asistente IA dentro del sistema
- Sugerencias automáticas de asignación de tareas
- Generación de reportes con IA
- Estimación automática de tiempo con ML

---

## CRITERIO DE DECISIÓN

Si durante el desarrollo surge una duda sobre si algo entra en el MVP, aplica este criterio:

> **¿Un equipo de 4 personas puede gestionar un proyecto real de inicio a fin sin esta funcionalidad?**
>
> - Si la respuesta es **SÍ**: la funcionalidad está fuera del MVP.
> - Si la respuesta es **NO**: la funcionalidad puede entrar, pero debe justificarse y planificarse en el sprint correcto.

---

## ESTADO DEL ALCANCE

| Área                    | Estado en MVP  |
|-------------------------|----------------|
| Autenticación           | ✅ Incluida     |
| Espacios de trabajo     | ✅ Incluida     |
| Proyectos               | ✅ Incluida     |
| Tareas                  | ✅ Incluida     |
| Kanban                  | ✅ Incluida     |
| Miembros                | ✅ Incluida     |
| Comentarios             | ✅ Incluida     |
| Dashboard               | ✅ Incluida     |
| Notificaciones básicas  | ✅ Incluida     |
| Reportes simples        | ✅ Incluida     |
| Panel de admin          | ✅ Incluida     |
| Archivos adjuntos       | ❌ Fuera del MVP |
| Calendario              | ❌ Fuera del MVP |
| Notificaciones por email| ❌ Fuera del MVP |
| Exportar PDF/Excel      | ❌ Fuera del MVP |
| WebSockets              | ❌ Fuera del MVP |
| App móvil nativa        | ❌ Fuera del MVP |
| OAuth / 2FA             | ❌ Fuera del MVP |
| Integraciones externas  | ❌ Fuera del MVP |
| IA generativa           | ❌ Fuera del MVP |

---

*FISIHUB MVP Scope v1.0*
