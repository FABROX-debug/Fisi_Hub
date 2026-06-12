# Sprint 1 — Prompt para Codex

> **Archivo:** `docs/SPRINT_1_PROMPT.md`  
> Este archivo contiene el prompt listo para entregar a Codex.  
> Copia el contenido desde "INICIO DEL PROMPT" hasta "FIN DEL PROMPT" y dáselo a Codex.  
> **No avances al Sprint 2 hasta que este sprint esté completo y committeado.**

---

<!-- ============================================================ -->
<!-- INICIO DEL PROMPT — Copia desde aquí                         -->
<!-- ============================================================ -->

# Sprint 1 — Sistema de diseño y layout base

## Documentos de referencia

Lee estos archivos ANTES de escribir cualquier código:

- `docs/FISIHUB_SPEC.md` — especificación completa de diseño y funcionalidad
- `docs/SPRINT_PLAN.md` — plan de sprints del MVP
- `docs/MVP_SCOPE.md` — qué entra y qué no entra en el MVP
- `docs/SPRINT_0_SUMMARY.md` — qué se completó en el Sprint 0
- `README.md` — descripción general del proyecto

## Objetivo

Construir el esqueleto visual completo de FISIHUB: layout base (sidebar + topbar), componentes UI reutilizables, rutas placeholder y un dashboard temporal con datos hardcodeados. **Sin conexión al backend. Sin lógica real. Solo la estructura visual.**

## Contexto actual del proyecto

El Sprint 0 está completo. Existe:

- `frontend/` — React + Vite + TailwindCSS funcionando, con un `Home.jsx` temporal
- `backend/` — Spring Boot con `GET /api/health` funcionando, sin tocar en este sprint
- `docs/` — documentación del proyecto (no modificar en este sprint)

El frontend tiene build y lint limpios. No hay rutas configuradas todavía.

## Stack permitido en este sprint

- React + Vite
- TailwindCSS
- `react-router-dom` v6
- `lucide-react`
- CSS modules o clases Tailwind (no styled-components ni emotion)

**No instalar ninguna otra dependencia sin justificación.**

## Identidad visual (obligatorio)

Lee la sección 1 de `docs/FISIHUB_SPEC.md` para los valores exactos. Resumen:

```
Colores principales (configura en tailwind.config.js):
  primary:      #1E1B4B   (sidebar, cabeceras)
  accent:       #6D28D9   (botones primarios, links activos)
  accent-light: #8B5CF6   (hover states, badges)
  surface:      #F8F7FF   (fondo principal)
  card:         #FFFFFF   (tarjetas)

Tipografía: "Inter" para display y body (Google Fonts)
Iconografía: Lucide React, tamaño 18px, strokeWidth 1.75
```

## Qué debes implementar

### 1. Configuración de Tailwind

Extiende `tailwind.config.js` con los colores custom de FISIHUB:
- `primary`, `accent`, `accent-light`, `surface`, `card`
- Agrega la fuente "Inter" desde Google Fonts
- Agrega "JetBrains Mono" como fuente mono

### 2. Layout principal (`src/layouts/`)

**`AppLayout.jsx`**
- Sidebar fijo 240px a la izquierda
- Topbar horizontal arriba (altura 64px)
- Área de contenido: `flex-1`, `overflow-y: auto`, fondo `surface` (#F8F7FF)
- El layout solo se muestra en rutas privadas (no en la landing)

**`Sidebar.jsx`**
- Fondo: `primary` (#1E1B4B)
- Logo FISIHUB arriba (texto + ícono de red de nodos de Lucide)
- Menú agrupado con estas secciones y estos ítems (ver `FISIHUB_SPEC.md` sección 2):
  ```
  ── GENERAL ──
    Dashboard      (ícono: LayoutDashboard)
    Mis Tareas     (ícono: CheckSquare)
    Calendario     (ícono: Calendar)

  ── TRABAJO ──
    Espacios       (ícono: Grid)
    Proyectos      (ícono: Folder)
    Tablero        (ícono: Columns)

  ── EQUIPO ──
    Miembros       (ícono: Users)
    Actividad      (ícono: Activity)

  ── ANÁLISIS ──
    Reportes       (ícono: BarChart2)

  ── SISTEMA ──
    Notificaciones (ícono: Bell)
    Configuración  (ícono: Settings)
  ```
- Item activo: fondo `rgba(109,40,217,0.2)` + borde izquierdo 3px `#6D28D9` + texto blanco
- Item hover: fondo `rgba(109,40,217,0.1)` + texto blanco
- Nombre de usuario y rol en la parte inferior del sidebar (datos hardcodeados: "Fabrizio H." / "Líder de proyecto")

**`Topbar.jsx`**
- Fondo: blanco, borde inferior `#E5E7EB`
- Izquierda: nombre de la sección activa (breadcrumb simple, texto del link activo del sidebar)
- Centro: input de búsqueda con ícono Search (placeholder: "Buscar proyectos, tareas...")
- Derecha: ícono Bell (sin funcionalidad) + Avatar circular con iniciales "FH" en fondo violeta

### 3. Componentes UI reutilizables (`src/components/ui/`)

**`Button.jsx`** — Props: `variant` (primary | secondary | danger | ghost), `size` (sm | md | lg), `loading` (boolean), `disabled`, `onClick`, `children`
- `primary`: bg-violet-700, text-white, hover:bg-violet-800
- `secondary`: border border-gray-300, text-gray-700, hover:bg-gray-50
- `danger`: bg-red-500, text-white, hover:bg-red-600
- `ghost`: text-violet-600, hover:bg-violet-50
- `loading`: muestra spinner interno, deshabilita el botón
- Todos: `rounded-lg`, transición 150ms, `scale(0.97)` en click activo

**`Input.jsx`** — Props: `label`, `placeholder`, `error`, `type`, `value`, `onChange`
- Label arriba, en negrita sm
- Borde gris por defecto, borde violeta en focus con shadow glow sutil
- Borde rojo + mensaje de error debajo cuando `error` tiene contenido

**`Card.jsx`** — Props: `children`, `className`, `onClick`
- Fondo blanco, border-radius 12px, `shadow-sm`
- Hover (si tiene onClick): `shadow-md` + `translateY(-2px)`, transición 150ms

**`Badge.jsx`** — Props: `status` | `priority`
- Por estado:
  - `pendiente`: bg-gray-100, text-gray-700
  - `en-proceso`: bg-blue-100, text-blue-700
  - `revision`: bg-amber-100, text-amber-700
  - `completada`: bg-emerald-100, text-emerald-700
  - `bloqueada`: bg-red-100, text-red-700
- Por prioridad:
  - `baja`: flecha ↓, gris
  - `media`: flecha →, azul
  - `alta`: flecha ↑, naranja
  - `urgente`: !! rojo con animación `animate-pulse`
- Forma: `rounded-full`, `text-xs`, `font-medium`, `px-2.5 py-0.5`

**`ProgressBar.jsx`** — Props: `value` (0-100), `showLabel` (boolean)
- Base: fondo gris claro `bg-gray-100`, `rounded-full`, altura 8px
- Fill: gradiente `#6D28D9 → #8B5CF6`, `rounded-full`
- Efecto shimmer animado (elemento firma del diseño):
  ```css
  @keyframes shimmer {
    0% { background-position: -200% 0; }
    100% { background-position: 200% 0; }
  }
  /* El fill usa background-size: 200% y la animación shimmer */
  ```
- Si `showLabel`: muestra el porcentaje a la derecha del contenedor

**`Toast.jsx`** — Props: `type` (success | error | info), `message`, `onClose`
- Posición: fijo, esquina inferior derecha, `z-50`
- Borde izquierdo 4px: verde (success), rojo (error), azul (info)
- Ícono correspondiente de Lucide (Check, X, Info)
- Auto-dismiss: desaparece automáticamente a los 4 segundos
- También tiene botón X para cerrar manualmente

**`Avatar.jsx`** — Props: `name`, `src`, `size` (sm | md | lg)
- Con `src`: imagen circular
- Sin `src`: iniciales (primera letra del nombre y del apellido) sobre fondo violeta (`#6D28D9`)
- Tamaños: sm=24px, md=32px, lg=40px

**`Modal.jsx`** — Props: `isOpen`, `onClose`, `title`, `children`
- Overlay oscuro `bg-black/50`
- Contenedor blanco centrado, `rounded-xl`, `shadow-lg`, padding 24px
- Título + botón X para cerrar
- Cierra al presionar ESC o al hacer clic en el overlay
- Animación fade-in al abrir (150ms)

**`EmptyState.jsx`** — Props: `icon`, `title`, `description`, `actionLabel`, `onAction`
- SVG o ícono de Lucide grande (64px) en color violeta
- Título bold + descripción muted
- Botón de acción primario si `onAction` está definido

**`Skeleton.jsx`** — Props: `width`, `height`, `className`
- Fondo gris claro con animación pulse
- Para usar como placeholder mientras cargan datos

### 4. Router y rutas (`src/router/index.jsx`)

Configura React Router v6 con estas rutas:

- `/` → `LandingPage` (pública, sin sidebar/topbar)
- `/dashboard` → `DashboardPage` (con AppLayout)
- `/proyectos` → `ProyectosPage` (con AppLayout)
- `/tareas` → `TareasPage` (con AppLayout)
- `/kanban` → `KanbanPage` (con AppLayout)
- `/miembros` → `MiembrosPage` (con AppLayout)
- `/reportes` → `ReportesPage` (con AppLayout)
- `/configuracion` → `ConfiguracionPage` (con AppLayout)

### 5. Páginas (`src/pages/`)

**`LandingPage.jsx`** — Página pública. No usar AppLayout.

Secciones (ver `FISIHUB_SPEC.md` sección 3):

*Hero:*
- Fondo: gradiente `#1E1B4B → #6D28D9` (135°)
- Headline: "Tu equipo. Tus proyectos. Todo en un solo lugar."
- Subheadline: "FISIHUB te ayuda a planificar, asignar y controlar el avance real de cualquier proyecto."
- Dos botones: [Empezar gratis →] (blanco/violeta) y [Ver demo] (outline blanco)
- Lado derecho: representación visual simple (SVG o div estilizado) de un tablero Kanban

*Beneficios (3 columnas):*
- [📋 Planifica] [🎯 Asigna] [📈 Controla]
- Fondo blanco, ícono violeta grande, título bold, descripción muted

*Cómo funciona (4 steps en fila):*
- ① Crea tu espacio → ② Agrega proyectos → ③ Asigna tareas → ④ Controla el avance

*CTA final:*
- Fondo `#1E1B4B`, texto blanco, botón [Crear cuenta gratuita]

*Footer:*
- Logo FISIHUB, links, copyright

**`DashboardPage.jsx`** — Datos hardcodeados. Es el corazón visual del Sprint 1.

Debe mostrar:
- Header: "Hola, Fabrizio 👋" + subtítulo "Tienes 3 tareas pendientes para hoy."
- 4 stat cards en fila: Proyectos activos (6), Pendientes (8), Completadas (24), Vencidas (2)
  - Cada card con borde izquierdo de color, icono, número grande, label pequeño
- Sección izquierda (60%):
  - "Mis proyectos activos" con 2 tarjetas usando `Card` + `Badge` de estado + `ProgressBar` (70% y 45%)
  - "Mis tareas para hoy": lista de 3 ítems con checkbox visual, prioridad y fecha
- Sección derecha (40%):
  - "Actividad reciente": 4 ítems en feed tipo timeline con punto violeta
  - "Próximas entregas": 3 ítems con fecha y nombre de tarea

**Páginas placeholder** — Las siguientes páginas deben existir pero solo mostrar un `EmptyState`:
- `ProyectosPage.jsx` — "Aún no tienes proyectos. Crea tu primer proyecto →"
- `TareasPage.jsx` — "No hay tareas asignadas todavía."
- `KanbanPage.jsx` — "Selecciona un proyecto para ver su tablero."
- `MiembrosPage.jsx` — "No hay miembros en este equipo todavía."
- `ReportesPage.jsx` — "Crea proyectos y tareas para ver reportes aquí."
- `ConfiguracionPage.jsx` — "Configuración (próximamente)"

## Qué NO debes implementar

- ❌ No implementes login, registro ni autenticación de ningún tipo
- ❌ No toques el backend ni `backend/` en ninguna forma
- ❌ No instales ni configures PostgreSQL
- ❌ No conectes el frontend al backend
- ❌ No implementes estado global real (Zustand, Context) — solo props y useState local
- ❌ No implementes drag & drop en el Kanban (es Sprint 5)
- ❌ No implementes funcionalidad real en la búsqueda del topbar
- ❌ No implementes funcionalidad en el ícono de Bell del topbar
- ❌ No implementes el calendario
- ❌ No crees el panel de admin
- ❌ No agregues nuevas dependencias sin justificación explícita

## Archivos que deben crearse o modificarse

```
frontend/
├── tailwind.config.js             (modificar: colores custom + fuentes)
├── index.html                     (modificar: importar Inter y JetBrains Mono de Google Fonts)
├── src/
│   ├── main.jsx                   (modificar: envolver con BrowserRouter)
│   ├── index.css                  (modificar: variables CSS si es necesario)
│   ├── router/
│   │   └── index.jsx              (crear: rutas de la app)
│   ├── layouts/
│   │   ├── AppLayout.jsx          (crear)
│   │   ├── Sidebar.jsx            (crear)
│   │   └── Topbar.jsx             (crear)
│   ├── components/
│   │   └── ui/
│   │       ├── Button.jsx         (crear)
│   │       ├── Input.jsx          (crear)
│   │       ├── Card.jsx           (crear)
│   │       ├── Badge.jsx          (crear)
│   │       ├── ProgressBar.jsx    (crear)
│   │       ├── Toast.jsx          (crear)
│   │       ├── Avatar.jsx         (crear)
│   │       ├── Modal.jsx          (crear)
│   │       ├── EmptyState.jsx     (crear)
│   │       └── Skeleton.jsx       (crear)
│   └── pages/
│       ├── LandingPage.jsx        (crear o reemplazar Home.jsx)
│       ├── DashboardPage.jsx      (crear)
│       ├── ProyectosPage.jsx      (crear: EmptyState)
│       ├── TareasPage.jsx         (crear: EmptyState)
│       ├── KanbanPage.jsx         (crear: EmptyState)
│       ├── MiembrosPage.jsx       (crear: EmptyState)
│       ├── ReportesPage.jsx       (crear: EmptyState)
│       └── ConfiguracionPage.jsx  (crear: EmptyState)
```

No debes crear ni modificar ningún archivo fuera de `frontend/`.

## Criterios de aceptación

Al terminar este sprint, deben cumplirse todos estos puntos:

- [ ] `npm run dev` levanta el servidor sin errores en consola
- [ ] `npm run build` compila sin errores
- [ ] `npm run lint` pasa sin warnings críticos
- [ ] La ruta `/` muestra la landing page con hero, beneficios, steps y CTA
- [ ] La ruta `/dashboard` muestra el layout completo: sidebar + topbar + contenido
- [ ] La barra lateral muestra todos los grupos y todos los ítems del menú
- [ ] El ítem activo del sidebar tiene el borde violeta y el fondo diferenciado
- [ ] La topbar muestra el nombre de la sección activa, la barra de búsqueda y el avatar
- [ ] El componente `Button` renderiza las 4 variantes (primary, secondary, danger, ghost)
- [ ] El componente `Badge` muestra los 5 estados de tarea con sus colores correctos
- [ ] El componente `ProgressBar` muestra el efecto shimmer animado en el dashboard
- [ ] El componente `Toast` aparece y desaparece automáticamente a los 4 segundos
- [ ] El componente `Modal` se abre y cierra correctamente (ESC + clic exterior + botón X)
- [ ] El dashboard muestra 4 stat cards con números y colores correctos
- [ ] El dashboard muestra 2 tarjetas de proyecto con ProgressBar al 70% y 45%
- [ ] El dashboard muestra el feed de actividad reciente con los 4 ítems
- [ ] Las páginas placeholder muestran EmptyState con mensaje descriptivo
- [ ] Los colores del sidebar coinciden con `#1E1B4B` (fondo) y `#6D28D9` (activo)
- [ ] La fuente "Inter" se carga correctamente

## Comandos de prueba

```bash
cd frontend

# Instalar dependencias si no están instaladas
npm install

# Instalar las dependencias del sprint
npm install react-router-dom lucide-react

# Verificar que el servidor levanta
npm run dev

# Verificar que compila
npm run build

# Verificar lint
npm run lint
```

## Notas técnicas importantes

1. **Arquitectura de carpetas:** Usa la estructura descrita en "Archivos que deben crearse". No crees carpetas adicionales sin justificación.

2. **No uses styled-components ni CSS-in-JS.** Usa solo clases de Tailwind. Para el efecto shimmer del ProgressBar, puedes usar un `<style>` tag en el componente o agregar la animación custom en `tailwind.config.js`.

3. **El DashboardPage usa datos hardcodeados.** Está bien. No uses `fetch`, `axios` ni ninguna llamada HTTP en este sprint.

4. **El sidebar debe usar `NavLink` de react-router-dom** para detectar automáticamente el ítem activo con la prop `className`.

5. **El AppLayout** debe aplicarse a todas las rutas excepto `/`. La landing page no tiene sidebar ni topbar.

6. **Consistencia de naming:** Usa PascalCase para componentes, camelCase para funciones y variables, kebab-case para clases CSS custom.

7. **No instales librerías de gráficos** (recharts, chart.js, etc.) — no se necesitan en este sprint.

---

<!-- FIN DEL PROMPT — Hasta aquí                                  -->
<!-- ============================================================ -->

---

## Notas para el equipo (no para Codex)

**Antes de dar este prompt a Codex, verifica:**
- [ ] El Sprint 0 está committeado y pusheado en GitHub
- [ ] `frontend/` tiene React + Vite + Tailwind funcionando
- [ ] `backend/` tiene `GET /api/health` respondiendo
- [ ] `docs/FISIHUB_SPEC.md` existe en el repositorio

**Después de que Codex entregue el código:**
1. Ejecuta `npm run dev` y verifica visualmente todas las páginas
2. Verifica cada criterio de aceptación de la lista
3. Si algo falla, da a Codex un prompt específico de corrección (no el prompt completo de nuevo)
4. Cuando todo pase: `git add . && git commit -m "Sprint 1: sistema de diseño y layout base" && git push origin main`
5. Crea `docs/SPRINT_1_SUMMARY.md` con lo que se completó
6. Prepara el prompt del Sprint 2

---

*FISIHUB Sprint 1 Prompt v1.0*
