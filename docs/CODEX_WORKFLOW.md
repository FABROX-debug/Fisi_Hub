# FISIHUB — Guía de Trabajo con Codex

> **Archivo:** `docs/CODEX_WORKFLOW.md`  
> Este documento define el protocolo completo para trabajar con Codex como agente de programación en el proyecto FISIHUB. Léelo antes de iniciar cualquier sprint.

---

## ¿QUÉ ES CODEX EN ESTE PROYECTO?

Codex es el agente de programación que implementa el código de FISIHUB sprint por sprint. No es un asistente de chat general: es un ejecutor de tareas específicas y delimitadas.

**Principio fundamental:** Codex hace exactamente lo que el prompt le indica. Si el prompt es vago, Codex implementará de más y romperá la planificación. Si el prompt es claro y acotado, Codex produce código usable y controlable.

---

## ESTRUCTURA DE UN PROMPT PARA CODEX

Cada prompt de sprint debe seguir esta estructura exacta:

```
# Sprint N — [Nombre del sprint]

## Documentos de referencia
Lee estos archivos antes de comenzar:
- docs/FISIHUB_SPEC.md
- docs/SPRINT_PLAN.md
- docs/SPRINT_N_SUMMARY.md (si existe)
- README.md

## Objetivo
[Una sola frase que describe el resultado esperado]

## Contexto
[Estado actual del proyecto. Qué ya existe. Qué funciona.]

## Qué debes implementar
[Lista numerada y específica. Sin ambigüedad.]

## Qué NO debes implementar
[Lista explícita de lo que está fuera del alcance de este sprint]

## Archivos esperados
[Lista de archivos que deben crearse o modificarse]

## Criterios de aceptación
[Lista verificable. Cada ítem debe poder comprobarse con un comando o con la UI]

## Comandos de prueba
[Comandos concretos para verificar que todo funciona]

## Notas técnicas
[Restricciones de arquitectura, librerías permitidas, convenciones de código]
```

---

## CÓMO DAR PROMPTS A CODEX

### ✅ Haz esto

- **Un prompt por sprint.** Nunca combines dos sprints en un mismo prompt.
- **Referencia siempre los archivos de documentación** al inicio del prompt: `docs/FISIHUB_SPEC.md`, `docs/SPRINT_PLAN.md`.
- **Sé explícito en el alcance.** Si no quieres que Codex haga algo, escríbelo en la sección "Qué NO debes implementar".
- **Incluye criterios verificables.** "El endpoint debe devolver 200 con el token JWT" es verificable. "Debe funcionar bien" no lo es.
- **Indica la arquitectura.** Recuérdalo en cada prompt: Controller (Boundary) → Service (Control) → Repository → Entity. Los controllers no tienen lógica de negocio.

### ❌ Evita esto

- No le des a Codex el documento maestro completo y le digas "implementa todo".
- No le pidas dos funcionalidades distintas en un mismo prompt.
- No uses palabras vagas como "mejora", "refactoriza", "optimiza" sin especificar qué y cómo.
- No le pidas que "continúe desde donde quedó" sin darle el contexto actual.
- No asumas que Codex recuerda lo que hizo en el sprint anterior: siempre proporciona el contexto.

---

## CÓMO LIMITAR EL ALCANCE

El alcance de cada sprint se controla mediante tres mecanismos:

### 1. Sección "Qué NO debes implementar"
Esta sección es obligatoria en todo prompt. Escribe explícitamente lo que está fuera del sprint. Ejemplo:

```
## Qué NO debes implementar
- No implementes login ni registro (es el Sprint 2)
- No conectes el frontend al backend en este sprint
- No instales ni configures PostgreSQL
- No implementes drag & drop (es el Sprint 5)
```

### 2. Archivos esperados
Listar exactamente qué archivos deben crearse o modificarse. Si Codex crea algo fuera de esa lista sin justificación, es una señal de que se excedió del alcance.

### 3. Criterios de aceptación concretos
Si los criterios son claros, Codex sabe cuándo parar. No hay razón para implementar más si los criterios ya se cumplen.

---

## CÓMO REVISAR LOS CAMBIOS DE CODEX

Después de que Codex entregue el código de un sprint, sigue este proceso de revisión antes de hacer commit:

### Revisión rápida (5 minutos)
```bash
# Ver qué archivos cambió Codex
git diff --name-only

# Ver el diff completo
git diff

# Ver archivos nuevos no trackeados
git status
```

### Checklist de revisión por sprint

**Frontend:**
- [ ] ¿Los componentes siguen la paleta de colores del spec? (`docs/FISIHUB_SPEC.md` sección 1.1)
- [ ] ¿Los componentes están en las carpetas correctas? (`src/components/ui/`, `src/pages/`, `src/layouts/`)
- [ ] ¿No hay datos hardcodeados que deberían venir del backend?
- [ ] ¿Las rutas están configuradas correctamente en el router?
- [ ] ¿El build pasa sin errores? (`npm run build`)

**Backend:**
- [ ] ¿Los controllers (Boundary) no tienen lógica de negocio?
- [ ] ¿Toda la lógica está en los services (Control)?
- [ ] ¿Los endpoints devuelven DTOs, no entidades directamente?
- [ ] ¿Los tests pasan? (`./mvnw test`)
- [ ] ¿Las nuevas entidades tienen sus migrations o se crean automáticamente?

**General:**
- [ ] ¿Codex implementó algo fuera del alcance del sprint?
- [ ] ¿Hay archivos modificados que no debían tocarse?

---

## CÓMO HACER COMMITS POR SPRINT

Cada sprint tiene un commit específico. Usa este flujo:

```bash
# 1. Revisar qué cambió
git status
git diff --name-only

# 2. Agregar solo los archivos del sprint
git add .

# 3. Commit con el mensaje del sprint
git commit -m "Sprint N: [nombre del sprint]"

# 4. Push a GitHub
git push origin main

# 5. Verificar en GitHub que los cambios llegaron
# https://github.com/FABROX-debug/Fisi_Hub
```

### Convención de mensajes de commit
```
Sprint 0: preparación del MVP
Sprint 1: sistema de diseño y layout base
Sprint 2: autenticación JWT y gestión de usuarios
Sprint 3: espacios de trabajo y proyectos CRUD
Sprint 4: gestión de tareas CRUD
Sprint 5: tablero Kanban con drag and drop
Sprint 6: dashboard con datos reales del backend
Sprint 7: gestión de miembros y comentarios en tareas
Sprint 8: cierre MVP - notificaciones, reportes, admin y responsive
```

---

## CÓMO PROBAR CADA SPRINT

### Pruebas del frontend
```bash
cd frontend
npm run dev          # Levanta el servidor de desarrollo
npm run build        # Verifica que compila sin errores
npm run lint         # Verifica que no hay errores de linting
```

### Pruebas del backend
```bash
cd backend
./mvnw test                    # Corre los tests unitarios
./mvnw spring-boot:run         # Levanta el servidor

# Prueba el endpoint de salud
curl http://localhost:8080/api/health
```

### Pruebas de integración (a partir del Sprint 2)
```bash
# Con Postman o curl, verificar los endpoints del sprint
# Ver docs/SPRINT_PLAN.md para los comandos específicos de cada sprint
```

---

## QUÉ HACER SI CODEX ROMPE ALGO

Si Codex implementa código que rompe algo que ya funcionaba, usa Git para recuperarte:

### Opción 1: Deshacer el último commit (si aún no hiciste push)
```bash
# Volver al estado del sprint anterior
git reset --soft HEAD~1

# Revisar el estado
git status
```

### Opción 2: Revertir a un sprint anterior (si ya hiciste push)
```bash
# Ver el historial de commits
git log --oneline

# Crear una rama de recuperación desde el sprint anterior
git checkout -b recovery/sprint-N <hash-del-commit-del-sprint-anterior>

# Desde esa rama, crea un nuevo branch y continúa
```

### Opción 3: Stash temporal
```bash
# Guardar los cambios temporalmente sin commitear
git stash

# Volver al estado limpio
git stash drop   # Si decides descartar los cambios de Codex
git stash pop    # Si decides recuperarlos
```

### Regla importante
**Nunca hagas `git push --force` a `main` sin consenso del equipo.** Si necesitas reescribir historia, hazlo en una rama separada.

---

## CÓMO USAR GITHUB COMO RESPALDO

El repositorio en `https://github.com/FABROX-debug/Fisi_Hub.git` es el respaldo principal.

**Buenas prácticas:**
- Haz push al final de cada sprint, no durante.
- Nunca trabajes directamente en `main` sin revisar los cambios de Codex.
- Si el equipo tiene varios miembros, usa ramas: `sprint-1`, `sprint-2`, etc., y haz merge a `main` solo cuando el sprint pase la revisión.

```bash
# Flujo con ramas por sprint (recomendado para equipos)
git checkout -b sprint-1
# ... Codex trabaja aquí ...
git add . && git commit -m "Sprint 1: sistema de diseño y layout base"
git push origin sprint-1

# Cuando el sprint pasa revisión:
git checkout main
git merge sprint-1
git push origin main
```

---

## CÓMO EVITAR QUE CODEX IMPLEMENTE MÁS DE LO PEDIDO

Estas son las tres reglas más importantes:

### Regla 1: La sección "Qué NO debes implementar" es obligatoria
Siempre inclúyela. Aunque parezca obvio que Codex no debería hacer algo, escríbelo de todas formas. Codex no sabe qué viene en el siguiente sprint.

### Regla 2: Cierra el alcance con archivos específicos
Si el prompt dice "crea los archivos X, Y y Z", Codex tiene un criterio claro de cuándo terminó. Si el prompt es abierto ("implementa el sistema de autenticación"), Codex puede interpretarlo de forma más amplia de lo deseado.

### Regla 3: El criterio de aceptación es el freno natural
Si los criterios de aceptación son concretos y verificables, Codex para cuando los cumple. Si son vagos, Codex puede continuar añadiendo "mejoras" no solicitadas.

---

## FLUJO COMPLETO DE UN SPRINT (PASO A PASO)

```
1. Revisar el prompt del sprint en docs/SPRINT_N_PROMPT.md
2. Verificar que el sprint anterior está completo y committeado
3. Dar el prompt a Codex
4. Esperar que Codex entregue el código
5. Revisar los cambios con git diff
6. Ejecutar los comandos de prueba del sprint
7. Verificar los criterios de aceptación uno por uno
8. Si hay errores: pedir a Codex que los corrija con un prompt específico
9. Hacer commit y push
10. Actualizar el estado del sprint en docs/SPRINT_PLAN.md
11. Crear docs/SPRINT_N_SUMMARY.md con lo que se hizo
12. Preparar el prompt del siguiente sprint
```

---

*FISIHUB Codex Workflow v1.0*
