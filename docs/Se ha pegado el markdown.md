# FISIHUB — Prompt Maestro de Diseño y Desarrollo

> Documento de especificación completo para la creación de la plataforma FISIHUB.  
> Usa este prompt como guía absoluta de diseño, arquitectura, flujos y componentes.

---

## 0. CONTEXTO GENERAL

Crea **FISIHUB**, una plataforma web de gestión de proyectos académicos y de software, orientada a equipos universitarios y profesionales pequeños. Es el equivalente a un ClickUp/Jira más humano, visual y directo, sin la complejidad innecesaria de plataformas enterprise.

**Stack técnico:**
- **Frontend:** React + Vite + TailwindCSS
- **Backend:** Spring Boot (Java 17+) — arquitectura MVC
- **Patrón:** Boundary / Control / Entity
- **Base de datos:** PostgreSQL (relacional)
- **Comunicación:** API REST (JSON)
- **Autenticación:** JWT + Spring Security
- **Estado global frontend:** Zustand o Context API
- **Routing:** React Router v6

---

## 1. IDENTIDAD VISUAL — SISTEMA DE DISEÑO

### 1.1 Paleta de colores

La identidad visual de FISIHUB se basa en un sistema de azules profundos con acentos en violeta eléctrico y toques de ámbar para urgencias. Transmite **confianza técnica + energía de equipo joven**.

```
PRIMARY       #1E1B4B   →  Azul marino oscuro (sidebar, cabeceras)
ACCENT        #6D28D9   →  Violeta eléctrico (botones primarios, CTAs, links activos)
ACCENT-LIGHT  #8B5CF6   →  Violeta suave (hover states, badges)
SURFACE       #F8F7FF   →  Blanco violáceo (fondo principal de pantallas)
CARD          #FFFFFF   →  Blanco puro (tarjetas y paneles)
BORDER        #E5E7EB   →  Gris claro (bordes y divisores)
TEXT-PRIMARY  #111827   →  Negro suave (títulos y cuerpo)
TEXT-MUTED    #6B7280   →  Gris medio (subtítulos, metadatos)
SUCCESS       #10B981   →  Verde esmeralda (completado, éxito)
WARNING       #F59E0B   →  Ámbar (prioridad alta, fechas próximas)
DANGER        #EF4444   →  Rojo (urgente, vencido, eliminar)
INFO          #3B82F6   →  Azul cielo (notificaciones, información)
```

**Gradiente de firma:** `linear-gradient(135deg, #1E1B4B 0%, #6D28D9 100%)`  
Usado en: pantalla de inicio, sidebar superior, avatar default, ilustraciones hero.

### 1.2 Tipografía

```
DISPLAY   →  "Inter" 700–800  →  Títulos principales, nombres de proyectos
BODY      →  "Inter" 400–500  →  Texto corriente, descripciones
MONO      →  "JetBrains Mono" 400  →  IDs, fechas, badges de estado, código
```

**Escala tipográfica:**
```
xs   →  11px / 0.75rem   (metadatos, timestamps)
sm   →  13px / 0.875rem  (labels, placeholders)
base →  15px / 0.9375rem (cuerpo principal)
lg   →  18px / 1.125rem  (subtítulos de sección)
xl   →  22px / 1.375rem  (títulos de página)
2xl  →  28px / 1.75rem   (estadísticas del dashboard)
3xl  →  36px / 2.25rem   (hero heading)
```

### 1.3 Bordes, sombras y espaciado

```
border-radius BASE    →  8px   (inputs, tags)
border-radius CARD    →  12px  (tarjetas, modales)
border-radius PILL    →  9999px (badges de estado, avatares)

shadow-sm   →  0 1px 3px rgba(0,0,0,0.08)
shadow-md   →  0 4px 12px rgba(0,0,0,0.10)
shadow-lg   →  0 8px 24px rgba(0,0,0,0.14)
shadow-glow →  0 0 20px rgba(109,40,217,0.25)  (elemento firma en hover)

spacing base →  4px (usa múltiplos: 4, 8, 12, 16, 24, 32, 48, 64)
```

### 1.4 Iconografía

Usa **Lucide React** como librería única de iconos. Tamaño base: 18px. Stroke width: 1.75.

### 1.5 Elemento firma (The Signature)

La **barra de progreso de proyecto** tiene un efecto de brillo deslizante (shimmer) con gradiente violeta cuando el porcentaje avanza en tiempo real. Es el único elemento con animación llamativa: el resto del sistema es quieto y preciso. Esto refuerza que lo que importa es el **avance real del trabajo**.

---

## 2. LAYOUT Y ESTRUCTURA GLOBAL

### 2.1 Layout principal (autenticado)

```
┌─────────────────────────────────────────────────────────┐
│  TOPBAR  [Logo FISIHUB]    [Búsqueda global]   [🔔] [Avatar] │
├──────────┬──────────────────────────────────────────────┤
│          │                                              │
│ SIDEBAR  │              CONTENT AREA                   │
│ 240px    │              flex-1, overflow-y: auto        │
│ fijo     │                                              │
│          │                                              │
└──────────┴──────────────────────────────────────────────┘
```

**Sidebar (fondo: #1E1B4B):**
- Logo FISIHUB arriba (con ícono de nodo de red / hub)
- Menú de navegación con secciones agrupadas
- Usuario activo + rol en el fondo del sidebar
- Hover: fondo violeta semitransparente + borde izquierdo violeta 3px
- Item activo: fondo `rgba(109,40,217,0.2)` + texto blanco + borde izquierdo `#6D28D9`

**Menú lateral:**
```
──── GENERAL ────
  🏠 Dashboard
  📋 Mis Tareas
  🗓️ Calendario

──── TRABAJO ────
  🗂️ Espacios
  📁 Proyectos
  🏗️ Tablero Kanban

──── EQUIPO ────
  👥 Miembros
  💬 Actividad

──── ANÁLISIS ────
  📊 Reportes

──── SISTEMA ────
  🔔 Notificaciones
  ⚙️ Configuración
  🛡️ Administración  (solo admin)
```

**Topbar (fondo: #FFFFFF, border-bottom: #E5E7EB):**
- Izquierda: Nombre de sección activa (breadcrumb sencillo)
- Centro: Barra de búsqueda global (busca proyectos, tareas, miembros)
- Derecha: Ícono notificaciones (badge contador), avatar con menú desplegable

---

## 3. PANTALLA DE INICIO (Landing Page)

Página pública antes de autenticarse. Diseño de una sola página dividida en secciones.

### Hero Section
```
Background: gradiente #1E1B4B → #6D28D9 (diagonal 135°)
Texto: blanco

Headline (3xl bold):
  "Tu equipo. Tus proyectos.
   Todo en un solo lugar."

Subheadline (lg, opacity 0.8):
  "FISIHUB te ayuda a planificar, asignar y controlar
   el avance real de cualquier proyecto."

CTA Buttons:
  [Empezar gratis →]  fondo blanco, texto violeta  (principal)
  [Ver demo]           borde blanco, texto blanco    (secundario)

Ilustración derecha:
  SVG animado de un tablero Kanban esquemático, tarjetas moviéndose
  entre columnas (animación loop suave, 4s)
```

### Beneficios (3 columnas, fondo blanco)
```
Tarjetas con ícono grande en violeta, título bold, descripción muted

  [📋 Planifica]         [🎯 Asigna]           [📈 Controla]
  Divide proyectos       Reparte tareas         Mide el avance
  en tareas claras       con responsables       con reportes reales
  y fechas reales.       definidos.             y tablero visual.
```

### Cómo funciona (steps horizontales)
```
① Crea tu espacio   →   ② Agrega proyectos   →   ③ Asigna tareas   →   ④ Controla el avance
```

### CTA final
```
Background: #1E1B4B
"¿Listo para organizarte?"
[Crear cuenta gratuita]
```

### Footer
```
Logo FISIHUB — Links: Inicio / Funciones / Contacto — © 2025 FISIHUB
```

---

## 4. AUTENTICACIÓN

### 4.1 Página de Registro

Layout: dos columnas (50/50).

**Columna izquierda:** panel decorativo con gradiente violeta + ilustración del dashboard + quote:
> "El trabajo en equipo empieza con organización."

**Columna derecha:** formulario blanco, padding generoso.

```
Logo FISIHUB (pequeño, arriba)
Título: "Crea tu cuenta"
Subtítulo: "Es gratis. Sin tarjeta."

Campos:
  [Nombre completo        ]
  [Correo electrónico     ]
  [Contraseña             ] 👁️
  [Confirmar contraseña   ] 👁️

Validaciones en tiempo real (debajo del campo, texto rojo sm):
  - Nombre: mínimo 3 caracteres
  - Correo: formato válido
  - Contraseña: mínimo 8 caracteres, 1 mayúscula, 1 número
  - Confirmar: debe coincidir

[Registrarme →]  botón primario, ancho completo

"¿Ya tienes cuenta? Inicia sesión"
```

### 4.2 Página de Inicio de Sesión

Mismo layout dos columnas.

```
Título: "Bienvenido de nuevo"
Subtítulo: "Ingresa a tu equipo"

Campos:
  [Correo electrónico     ]
  [Contraseña             ] 👁️

Link: "¿Olvidaste tu contraseña?"

[Ingresar →]  botón primario

"¿No tienes cuenta? Regístrate"
```

### 4.3 Recuperación de contraseña

Página simple: campo de correo + botón "Enviar enlace de recuperación".  
Confirmación: mensaje de éxito con ícono de sobre.

---

## 5. DASHBOARD PRINCIPAL

La pantalla más importante. Al ingresar, el usuario ve su resumen completo.

### 5.1 Header de bienvenida
```
"Hola, Fabrizio 👋"
"Tienes 3 tareas pendientes para hoy. Hay 1 tarea vencida."
```
Texto personalizado según datos reales. Si no hay tareas: "Todo al día. Buen trabajo."

### 5.2 Tarjetas de estadísticas (4 en fila)

```
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│  📁 Proyectos   │  │  📋 Pendientes  │  │  ✅ Completadas │  │  🚨 Vencidas   │
│                 │  │                 │  │                 │  │                 │
│      6          │  │      8          │  │      24         │  │      2          │
│  activos        │  │  esta semana    │  │  este mes       │  │  requieren att. │
└─────────────────┘  └─────────────────┘  └─────────────────┘  └─────────────────┘
```
Cada tarjeta tiene: borde izquierdo de color (azul/ámbar/verde/rojo), fondo blanco, sombra suave.

### 5.3 Sección principal (dos columnas: 60% / 40%)

**Columna izquierda:**

**Mis proyectos activos** — lista de tarjetas de proyecto:
```
┌──────────────────────────────────────────────────────────┐
│ 📁 Sistema FISIHUB                        EN PROCESO  🟡 │
│ Fecha de entrega: 20 jun 2025                            │
│ Líder: Fabrizio H.          3 miembros                   │
│ ████████████████░░░░░░░░  70%                           │
└──────────────────────────────────────────────────────────┘
```
La barra de progreso usa el efecto shimmer violeta (elemento firma).

**Mis tareas para hoy** — lista compacta:
```
○  Crear diagrama de clases      ALTA    vence hoy
○  Revisar mockups UI            MEDIA   vence mañana
✓  Documentar endpoints API      BAJA    completada
```

**Columna derecha:**

**Actividad reciente** — feed tipo timeline:
```
● Fabrizio creó la tarea "Revisar mockups"       hace 2h
● María marcó "Login API" como completada         hace 4h
● Carlos comentó en "Diagrama ER"                ayer
● Se agregó a Luis como miembro del proyecto      ayer
```

**Próximas entregas** — mini calendario:
```
Hoy       ↦ Crear diagrama ER
Mañana    ↦ Entregar informe de avance
Viernes   ↦ Revisión del módulo de autenticación
```

---

## 6. ESPACIOS DE TRABAJO

### Lista de espacios
```
┌───────────────────────────────────────────────┐
│  🗂️ Arquitectura de Software   4 proyectos   [→] │
│     3 miembros · Activo                          │
└───────────────────────────────────────────────┘
┌───────────────────────────────────────────────┐
│  🗂️ Proyecto Final              2 proyectos   [→] │
│     5 miembros · En proceso                      │
└───────────────────────────────────────────────┘
```

Botón: **[+ Nuevo espacio]** — abre modal.

### Modal: Crear/Editar espacio
```
Nombre del espacio:    [___________________]
Descripción:          [___________________]
Color/ícono:          🔵 🟢 🟣 🟡 🔴  (selector visual)
Miembros:             [buscar por nombre o correo]
                      [Añadir]

[Cancelar]   [Crear espacio]
```

### Vista interna de un espacio
- Header con nombre, color, miembros (avatares apilados)
- Grid de proyectos del espacio con sus avances
- Botón **[+ Nuevo proyecto]**

---

## 7. GESTIÓN DE PROYECTOS

### Lista de proyectos

Cabecera con filtros:
```
[Todos] [En proceso] [Planificado] [Finalizado]    [🔍 Buscar]   [+ Nuevo proyecto]
```

Cards de proyecto en grid (3 columnas en desktop):
```
┌──────────────────────────────────┐
│  🔵 EN PROCESO          ALTA ↑  │
│                                  │
│  Sistema FISIHUB                 │
│  Plataforma de gestión de...     │
│                                  │
│  Inicio: 01 may · Fin: 20 jun   │
│                                  │
│  [F] [M] [C] +2                  │  ← avatares de miembros
│                                  │
│  ████████████░░░░  70%           │
│                                  │
│  12 tareas · 3 pendientes       │
└──────────────────────────────────┘
```

### Vista detallada de proyecto

Tabs internos:
```
[Resumen]  [Tablero]  [Tareas]  [Miembros]  [Archivos]  [Reportes]
```

**Tab Resumen:**
- Header con nombre, estado, prioridad, fechas, líder, descripción
- Barra de progreso grande con shimmer
- Estadísticas rápidas: tareas totales / completadas / pendientes / vencidas
- Últimas actividades del proyecto

### Modal: Crear/Editar proyecto
```
Nombre:              [_____________________]
Descripción:         [_____________________]
Espacio:             [▾ Seleccionar espacio]
Líder:               [▾ Seleccionar miembro]
Fecha de inicio:     [📅 dd/mm/aaaa]
Fecha de entrega:    [📅 dd/mm/aaaa]
Prioridad:           [○ Baja] [○ Media] [● Alta] [○ Urgente]
Estado inicial:      [▾ Planificado]
Miembros:            [buscar y añadir]

[Cancelar]   [Crear proyecto]
```

---

## 8. GESTIÓN DE TAREAS

### Lista de tareas (vista tabla)

```
┌──────────────────────────────────────────────────────────────────────────────┐
│  TAREA                   RESPONSABLE  PRIORIDAD  ESTADO       VENCE         │
├──────────────────────────────────────────────────────────────────────────────┤
│  ○ Crear modelo de clases  Fabrizio    🔴 ALTA    En proceso   15 jun       │
│  ○ Revisar mockups         María       🟡 MEDIA   Pendiente    18 jun       │
│  ✓ Diseñar endpoints API   Carlos      🟢 BAJA    Completada   12 jun       │
│  ⚠ Documentar módulo auth  Luis        🔴 URGENTE Bloqueada    10 jun       │
└──────────────────────────────────────────────────────────────────────────────┘
```

Filtros: `[Estado ▾] [Prioridad ▾] [Responsable ▾] [Fecha ▾]`  
Vista dual: **[≡ Lista] [⊞ Kanban]** (toggle)

### Modal: Crear/Editar tarea

Panel lateral derecho (slide-in desde la derecha, no modal centrado):
```
┌────────────────────────────────┐
│  Nueva tarea              [✕] │
├────────────────────────────────┤
│  Título                        │
│  [________________________]    │
│                                │
│  Descripción                   │
│  [________________________]    │
│  [________________________]    │
│                                │
│  Responsable   [▾ Asignar]    │
│  Proyecto      [▾ Seleccionar] │
│  Prioridad     [▾ Alta]       │
│  Estado        [▾ Pendiente]  │
│  Fecha límite  [📅]           │
│                                │
│  Etiquetas     [+ agregar]    │
│                                │
│  Archivos adjuntos             │
│  [📎 Subir archivo]           │
│                                │
│  Comentarios                   │
│  [Escribe un comentario...]   │
│  [Enviar]                     │
│                                │
│  Historial de cambios ▾        │
│                                │
│         [Guardar tarea]       │
└────────────────────────────────┘
```

### Estados de tarea (badges visuales)
```
Pendiente   → fondo gris,   texto gris oscuro
En proceso  → fondo azul,   texto azul oscuro
En revisión → fondo ámbar,  texto ámbar oscuro
Completada  → fondo verde,  texto verde oscuro
Bloqueada   → fondo rojo,   texto rojo oscuro
```

### Prioridades (badges visuales)
```
Baja     → ↓  gris
Media    → →  azul
Alta     → ↑  naranja
Urgente  → !! rojo parpadeante (animación pulse sutil)
```

---

## 9. TABLERO KANBAN

Pantalla completa horizontal con scroll lateral si hay muchas columnas.

```
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│  ⬜ PENDIENTE   │  │  🔵 EN PROCESO  │  │  🟡 EN REVISIÓN │  │  ✅ COMPLETADO  │
│  4 tareas       │  │  3 tareas       │  │  2 tareas       │  │  8 tareas       │
├─────────────────┤  ├─────────────────┤  ├─────────────────┤  ├─────────────────┤
│                 │  │                 │  │                 │  │                 │
│ ┌─────────────┐ │  │ ┌─────────────┐ │  │ ┌─────────────┐ │  │ ┌─────────────┐ │
│ │ Crear UML   │ │  │ │ Diseñar UI  │ │  │ │ Revisar API │ │  │ │ Setup DB    │ │
│ │ 🔴 ALTA    │ │  │ │ 🟡 MEDIA   │ │  │ │ 🔴 ALTA    │ │  │ │ ✓ BAJA     │ │
│ │ [F] 15 jun  │ │  │ │ [M] 18 jun  │ │  │ │ [C] 10 jun  │ │  │ │ [L] 05 jun  │ │
│ │ 💬 2       │ │  │ │ 💬 0       │ │  │ │ 💬 5       │ │  │ │ 💬 1       │ │
│ └─────────────┘ │  │ └─────────────┘ │  │ └─────────────┘ │  │ └─────────────┘ │
│                 │  │                 │  │                 │  │                 │
│ [+ Agregar]     │  │ [+ Agregar]     │  │                 │  │                 │
└─────────────────┘  └─────────────────┘  └─────────────────┘  └─────────────────┘
```

**Comportamiento del Kanban:**
- Drag & drop entre columnas (React DnD o dnd-kit)
- Al soltar: actualiza estado en backend vía PATCH `/api/tareas/{id}`
- Animación suave al mover tarjeta (transición 200ms ease)
- Tarjeta con prioridad URGENTE tiene borde izquierdo rojo pulsante
- Columna COMPLETADO tiene fondo levemente verde

**Tarjeta Kanban expandida (hover):**
Al hacer hover sobre una tarjeta, se eleva (shadow-lg + translate-y -2px).  
Al hacer clic: abre el panel lateral de detalle de tarea.

---

## 10. MIEMBROS DEL EQUIPO

### Vista de miembros de un proyecto

```
┌───────────────────────────────────────────────────────┐
│  [Avatar] Fabrizio Huaytalla    Líder de proyecto     │
│           fabrizio@email.com    4 tareas asignadas    │
│           ████░░░░  4/8 tareas completadas            │
│                                              [▾ Rol]  │
└───────────────────────────────────────────────────────┘
```

**Vista de carga de trabajo** — gráfico de barras horizontal:
```
Fabrizio  ████████  4 tareas
María     ██████    3 tareas
Carlos    ██████████  5 tareas  ← carga alta (barra naranja)
Luis      ████      2 tareas
```

Si un miembro tiene 5+ tareas activas: badge "Alta carga" en naranja.

---

## 11. CALENDARIO

Vista mensual con navegación de meses.

```
  ◀ Mayo 2025                                        Junio 2025 ▶

  Lun   Mar   Mié   Jue   Vie   Sáb   Dom
   1     2     3     4     5     6     7
              [●Tarea A]
   8     9    10    11    12    13    14
                    [●Entrega X]      [🔴 VENCIDA]
  15    16    17    18    19    20    21
  [●Tarea B]        [●Tarea C]  [📁 Proyecto Y - entrega]
```

- Punto violeta: tarea con fecha límite
- Punto rojo: tarea vencida
- Borde azul: entrega de proyecto
- Clic en evento: abre panel lateral con detalle
- Vista alternativa: **[📅 Mes] [📋 Semana] [📆 Día]**

---

## 12. NOTIFICACIONES

### Panel de notificaciones (dropdown desde topbar)

```
🔔 Notificaciones                      [Marcar todo como leído]
─────────────────────────────────────────────────────
🟣  Te asignaron: "Revisar mockups UI"
    Proyecto FISIHUB · hace 10 min

🟣  Carlos comentó tu tarea "Diseñar endpoints"
    "Ya revisé los endpoints, falta documentar..." · hace 1h

⚪  María completó "Setup de base de datos"
    Proyecto FISIHUB · hace 3h

⚪  Tu tarea "Crear UML" vence mañana
    15 junio 2025 · hace 5h

    [Ver todas las notificaciones →]
```

Punto morado = no leída. Punto blanco = leída.

### Página completa de notificaciones

Filtros: `[Todas] [No leídas] [Tareas] [Proyectos] [Comentarios]`

---

## 13. REPORTES

### Resumen general del proyecto

```
Sistema FISIHUB — Reporte de avance
Generado: 12 junio 2025

  Avance general: 70%
  ███████████████░░░░░░░  

  ┌──────────────┬───────────────────────┐
  │ Total tareas │ 20                    │
  │ Completadas  │ 14   ███████████████  │
  │ Pendientes   │  4   ████             │
  │ En proceso   │  2   ██               │
  │ Vencidas     │  1   █                │
  └──────────────┴───────────────────────┘
```

### Productividad por miembro (gráfico de barras)
```
Fabrizio   █████████  9 tareas completadas
María      ███████    7 tareas completadas
Carlos     ████████   8 tareas completadas
Luis       ██         2 tareas completadas
```

### Sección de exportación
```
[📥 Exportar PDF]   [📊 Exportar Excel]   [🖨️ Imprimir]
```

---

## 14. PERFIL DE USUARIO

```
┌─────────────────────────────────────────────────┐
│                                                 │
│   [Avatar 80px]   Fabrizio Huaytalla           │
│                   fabrizio@email.com            │
│                   Líder de proyecto             │
│                                                 │
│   [Editar perfil]   [Cambiar contraseña]       │
│                                                 │
├─────────────────────────────────────────────────┤
│  Proyectos activos (3)                          │
│  · Sistema FISIHUB — 70%                        │
│  · App Móvil UPC — 45%                         │
│  · API REST Backend — 90%                       │
├─────────────────────────────────────────────────┤
│  Mis tareas (4)                                 │
│  · Crear diagrama de clases — Alta — vence hoy  │
│  · Revisar mockups — Media — vence mañana       │
├─────────────────────────────────────────────────┤
│  Actividad reciente                             │
│  · Completó "Setup DB"                hace 1d  │
│  · Comentó en "Diseño UI"             hace 2d  │
└─────────────────────────────────────────────────┘
```

---

## 15. PANEL DE ADMINISTRACIÓN

Solo visible para usuarios con rol ADMIN.

### Vista de usuarios
```
USUARIO              EMAIL                ROL           ESTADO    ACCIONES
Fabrizio H.          f@email.com          Líder         ✅ Activo  [Editar] [Desactivar]
María G.             m@email.com          Miembro       ✅ Activo  [Editar] [Desactivar]
Carlos R.            c@email.com          Miembro       ❌ Inactivo [Activar]
```

Filtros: `[Todos] [Activos] [Inactivos]`  
Búsqueda por nombre o correo.

### Vista de todos los proyectos
- Tabla con: nombre, espacio, líder, estado, avance, miembros
- Puede acceder a cualquier proyecto sin ser miembro

### Estadísticas del sistema
```
Total usuarios:     12
Total proyectos:     8
Tareas completadas: 94
Tasa de completitud: 78%
```

---

## 16. COMPONENTES UI REUTILIZABLES

### Botones
```jsx
// Primario
<Button variant="primary">Crear proyecto</Button>
// bg-violet-700, text-white, hover:bg-violet-800, rounded-lg, px-4 py-2

// Secundario
<Button variant="secondary">Cancelar</Button>
// border border-gray-300, text-gray-700, hover:bg-gray-50

// Peligro
<Button variant="danger">Eliminar</Button>
// bg-red-500, text-white, hover:bg-red-600

// Ghost
<Button variant="ghost">Ver detalle</Button>
// text-violet-600, hover:bg-violet-50
```

### Badges de estado
```jsx
<Badge status="pendiente" />   // gris
<Badge status="en-proceso" />  // azul
<Badge status="revision" />    // ámbar
<Badge status="completada" />  // verde
<Badge status="bloqueada" />   // rojo
```

### Inputs
```jsx
// Siempre con label arriba, placeholder descriptivo
// Error: borde rojo + mensaje debajo en rojo sm
// Focus: borde violeta + sombra glow sutil
```

### Avatares
```jsx
// Con foto: imagen circular
// Sin foto: iniciales en fondo violeta
// Grupo de avatares: apilados con overlap de -8px
```

### Progress Bar
```jsx
// Base: fondo gris claro
// Fill: gradiente violeta con shimmer animado
// Porcentaje mostrado a la derecha
```

### Toast / Alertas
```jsx
// Éxito:  borde izquierdo verde + ícono check
// Error:  borde izquierdo rojo + ícono X
// Info:   borde izquierdo azul + ícono i
// Posición: esquina inferior derecha, auto-dismiss 4s
```

---

## 17. ENDPOINTS API REST (REFERENCIA BACKEND)

```
# AUTH
POST   /api/auth/register
POST   /api/auth/login
POST   /api/auth/logout
POST   /api/auth/forgot-password

# USUARIOS
GET    /api/usuarios
GET    /api/usuarios/{id}
PUT    /api/usuarios/{id}
DELETE /api/usuarios/{id}

# ESPACIOS
GET    /api/espacios
POST   /api/espacios
GET    /api/espacios/{id}
PUT    /api/espacios/{id}
DELETE /api/espacios/{id}
GET    /api/espacios/{id}/proyectos

# PROYECTOS
GET    /api/proyectos
POST   /api/proyectos
GET    /api/proyectos/{id}
PUT    /api/proyectos/{id}
DELETE /api/proyectos/{id}
GET    /api/proyectos/{id}/tareas
GET    /api/proyectos/{id}/miembros
GET    /api/proyectos/{id}/reportes

# TAREAS
GET    /api/tareas
POST   /api/tareas
GET    /api/tareas/{id}
PUT    /api/tareas/{id}
PATCH  /api/tareas/{id}/estado
DELETE /api/tareas/{id}

# MIEMBROS
POST   /api/proyectos/{id}/miembros
DELETE /api/proyectos/{id}/miembros/{usuarioId}
PATCH  /api/proyectos/{id}/miembros/{usuarioId}/rol

# COMENTARIOS
GET    /api/tareas/{id}/comentarios
POST   /api/tareas/{id}/comentarios
DELETE /api/comentarios/{id}

# ARCHIVOS
POST   /api/tareas/{id}/archivos
DELETE /api/archivos/{id}

# NOTIFICACIONES
GET    /api/notificaciones
PATCH  /api/notificaciones/{id}/leida
PATCH  /api/notificaciones/leer-todas

# REPORTES
GET    /api/proyectos/{id}/reportes/avance
GET    /api/proyectos/{id}/reportes/miembros
```

---

## 18. ENTIDADES DE BASE DE DATOS

```sql
-- Usuarios y roles
Usuario      (id, nombre, correo, password_hash, foto_url, activo, creado_en)
Rol          (id, nombre)  → ADMIN, LIDER, MIEMBRO
UsuarioRol   (usuario_id, rol_id)

-- Estructura de trabajo
EspacioTrabajo  (id, nombre, descripcion, color, icono, creado_por, creado_en)
EspacioMiembro  (espacio_id, usuario_id)

-- Proyectos
Proyecto     (id, nombre, descripcion, fecha_inicio, fecha_fin, estado, prioridad,
              porcentaje_avance, espacio_id, lider_id, creado_en)
MiembroProyecto (proyecto_id, usuario_id, rol_en_proyecto)

-- Tareas
Tarea        (id, titulo, descripcion, responsable_id, proyecto_id,
              fecha_limite, estado, prioridad, creado_por, creado_en, actualizado_en)

-- Comunicación y archivos
Comentario   (id, tarea_id, autor_id, contenido, creado_en)
Archivo      (id, nombre, url, tipo, tarea_id, subido_por, subido_en)

-- Sistema
Notificacion     (id, usuario_id, tipo, mensaje, referencia_id, leida, creado_en)
HistorialActividad (id, usuario_id, accion, entidad_tipo, entidad_id, descripcion, fecha)
```

---

## 19. ESTADOS Y FLUJOS

### Flujo de una tarea en Kanban
```
PENDIENTE → EN PROCESO → EN REVISIÓN → COMPLETADA
                ↓
            BLOQUEADA (puede retornar a EN PROCESO)
```

### Flujo de un proyecto
```
PLANIFICADO → EN PROCESO → EN REVISIÓN → FINALIZADO
                                          ↓
                                      CANCELADO (desde cualquier estado)
```

### Cálculo de avance de proyecto
```
porcentaje_avance = (tareas_completadas / total_tareas) * 100
```
Se recalcula en backend cada vez que una tarea cambia de estado.

---

## 20. COMPORTAMIENTOS Y MICROINTERACCIONES

- **Drag & drop en Kanban:** animación fluida (200ms ease-out). Tarjeta "fantasma" mientras se arrastra.
- **Barra de progreso:** al actualizar, anima de valor anterior a valor nuevo (transición 600ms ease-in-out + shimmer).
- **Notificaciones:** badge con contador en topbar. Pulse sutil en el ícono si hay no leídas.
- **Modal/panel lateral:** fade-in + slide desde la derecha (250ms). Overlay oscuro detrás.
- **Hover en cards:** elevación suave (box-shadow + translate-y -2px, 150ms).
- **Botones:** scale(0.97) en clic activo. Spinner interno en estados de carga.
- **Empty states:** ilustración SVG + mensaje de acción ("Crea tu primer proyecto →").
- **Skeletons de carga:** en cards y tablas mientras carga la API.
- **Formularios:** validación en tiempo real (onChange), no solo al submit.
- **Responsive:** sidebar colapsable en tablet. Menú hamburguesa en móvil.

---

## 21. INSTRUCCIONES FINALES DE IMPLEMENTACIÓN

1. Comienza por el **sistema de diseño**: crea el archivo de tokens de color, tipografía y componentes base.
2. Implementa **autenticación completa** antes de cualquier otra pantalla.
3. Construye las pantallas en este orden:  
   Landing → Login/Registro → Dashboard → Proyectos → Tareas → Kanban → Miembros → Reportes → Admin
4. Conecta el frontend con el backend desde el inicio: no hagas mocks internos permanentes.
5. El **tablero Kanban** es la funcionalidad más visible: dale prioridad visual y de QA.
6. Las **barras de progreso con shimmer** son el elemento firma: impleméntalas en todas las vistas donde aparezca un porcentaje.
7. Mantén consistencia absoluta en los colores de estado a través de toda la app.
8. Toda acción destructiva (eliminar proyecto, quitar miembro) debe pedir **confirmación modal**.
9. Los errores de API deben mostrarse como **toasts informativos**, no como alertas del navegador.
10. **Accesibilidad mínima:** contraste AA, focus visible en todos los interactivos, aria-labels en íconos sin texto.

---

*FISIHUB — Prompt Maestro v1.0 | Diseñado para equipos que hacen cosas reales.*