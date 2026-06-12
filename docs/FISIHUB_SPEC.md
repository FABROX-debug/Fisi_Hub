# FISIHUB - Especificacion del MVP

Este documento resume la especificacion funcional y visual de FISIHUB. La
fuente original fue incorporada al repositorio como
`docs/Se ha pegado el markdown.md`; se conserva intacta como material de
referencia.

## Producto

FISIHUB es una plataforma web de gestion de proyectos academicos y de software
para equipos universitarios y profesionales pequenos. Busca ofrecer una
experiencia mas simple, visual y directa que herramientas enterprise.

## Stack

- Frontend: React, Vite, TailwindCSS y React Router v6.
- Iconos: Lucide React.
- Estado global previsto: Zustand o Context API.
- Backend: Spring Boot con Java 17+ y arquitectura MVC.
- Persistencia prevista: PostgreSQL.
- Comunicacion prevista: API REST con JSON.
- Autenticacion prevista: JWT y Spring Security.

## Sistema de diseno

| Token | Valor | Uso |
| --- | --- | --- |
| `primary` | `#1E1B4B` | Sidebar y cabeceras |
| `accent` | `#6D28D9` | Acciones y estados activos |
| `accentLight` | `#8B5CF6` | Hover y acentos suaves |
| `surface` | `#F8F7FF` | Fondo principal |
| `card` | `#FFFFFF` | Tarjetas |
| `border` | `#E5E7EB` | Bordes y divisores |
| `textPrimary` | `#111827` | Texto principal |
| `textMuted` | `#6B7280` | Texto secundario |
| `success` | `#10B981` | Exito y completado |
| `warning` | `#F59E0B` | Advertencias y revision |
| `danger` | `#EF4444` | Error, bloqueo y vencimiento |
| `info` | `#3B82F6` | Informacion y proceso |

La barra de progreso con gradiente violeta y shimmer es el elemento visual
distintivo de FISIHUB. El resto de las animaciones debe ser discreto.

## Layout principal

- Sidebar fijo de 240-288 px con fondo `primary`.
- Topbar blanca con borde inferior.
- Contenido sobre fondo `surface`.
- Sidebar colapsable en pantallas pequenas.
- Navegacion agrupada en General, Trabajo, Equipo, Analisis y Sistema.

## Modulos previstos

1. Landing y autenticacion.
2. Dashboard.
3. Espacios y proyectos.
4. Tareas y tablero Kanban.
5. Miembros y actividad.
6. Calendario y notificaciones.
7. Reportes.
8. Perfil y administracion.

## Reglas de implementacion

- Mantener consistencia de estados, prioridades y colores.
- Usar componentes UI reutilizables.
- Mostrar errores futuros mediante toasts, no alertas del navegador.
- Toda accion destructiva futura debe solicitar confirmacion.
- Mantener contraste suficiente y focus visible en elementos interactivos.
- Los datos temporales de interfaz no deben convertirse en mocks permanentes.
- Implementar cada modulo solo cuando corresponda a su sprint.

## Alcance actual

Sprint 1 implementa exclusivamente el sistema de diseno, layout, rutas base,
componentes UI y dashboard visual temporal. No incluye autenticacion, entidades,
persistencia ni endpoints de negocio.

