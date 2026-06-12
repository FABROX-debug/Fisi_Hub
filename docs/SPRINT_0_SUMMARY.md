# FISIHUB — Resumen Sprint 0

> **Archivo:** `docs/SPRINT_0_SUMMARY.md`  
> **Sprint:** 0 — Preparación del MVP  
> **Estado:** ✅ Completado  
> **Commit:** `git commit -m "Sprint 0: preparación del MVP"`

---

## Objetivo del Sprint 0

Dejar el repositorio del proyecto listo para desarrollo: estructura de carpetas, stack configurado, endpoint de verificación y documentación base. Sin lógica de negocio, sin CRUD, sin base de datos real.

---

## Lo que se completó

### Estructura del proyecto
- Carpeta `frontend/` creada con React + Vite + TailwindCSS
- Carpeta `backend/` creada con Spring Boot (Java 17+)
- Carpeta `docs/` creada para documentación del proyecto
- `.gitignore` creado y configurado para Node, Java/Maven y variables de entorno
- `README.md` creado con descripción del proyecto y referencias a los documentos de docs/

### Frontend
- React instalado con Vite como bundler
- TailwindCSS configurado
- Estructura de carpetas base creada
- `Home.jsx` temporal con texto de verificación: "FISIHUB MVP funcionando" (o equivalente)
- Build verificado: `npm run build` pasa sin errores
- Lint verificado: `npm run lint` pasa sin errores críticos
- `package-lock.json` reproducible incluido
- `npm audit`: 0 vulnerabilidades (o ninguna alta/crítica)

### Backend
- Spring Boot con Java 17+ configurado
- Arquitectura MVC preparada con paquetes base creados:
  - `config/`
  - `controller/`
  - `service/`
  - `repository/`
  - `model/`
  - `dto/`
  - `security/`
- `HealthController` creado (actúa como Boundary de verificación)
- Endpoint de salud funcionando:
  ```
  GET /api/health
  → {"status": "FISIHUB backend funcionando"}
  ```
- Test de health pasando

### Base de datos
- PostgreSQL configurado para usar variables de entorno:
  - `DB_HOST`
  - `DB_PORT`
  - `DB_NAME`
  - `DB_USER`
  - `DB_PASSWORD`
- **PostgreSQL NO está instalado localmente todavía** — esto es correcto para el Sprint 0
- No se crearon tablas, entidades JPA ni migraciones en este sprint

---

## Lo que se verificó como instalado

| Herramienta | Estado         |
|-------------|----------------|
| Node.js     | ✅ Instalado    |
| npm         | ✅ Instalado    |
| Java 17+    | ✅ Instalado    |
| Maven       | ✅ Instalado    |
| Git         | ✅ Instalado    |
| PostgreSQL  | ⚠️ NO instalado — se requiere antes del Sprint 2 |

---

## Repositorio remoto

```
https://github.com/FABROX-debug/Fisi_Hub.git
```

El repositorio remoto está definido y el Sprint 0 está pusheado.

---

## Lo que NO se hizo (correcto)

- ❌ No se implementó login ni registro
- ❌ No se crearon entidades JPA
- ❌ No se configuró Spring Security ni JWT
- ❌ No se crearon tablas en la base de datos
- ❌ No se creó CRUD de ningún tipo
- ❌ No se instaló PostgreSQL localmente
- ❌ No se avanzó al Sprint 1

---

## Deuda técnica conocida al final del Sprint 0

- PostgreSQL debe instalarse antes del Sprint 2.
- El `Home.jsx` del frontend es temporal y será reemplazado en el Sprint 1.
- La estructura de paquetes del backend está vacía (solo creados), se poblarán desde el Sprint 2.

---

## Próximo paso

Ejecutar el **Sprint 1 — Sistema de diseño y layout base**.  
Ver el prompt completo en: `docs/SPRINT_1_PROMPT.md`

---

*FISIHUB Sprint 0 Summary v1.0*
