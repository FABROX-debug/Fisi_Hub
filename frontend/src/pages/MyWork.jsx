import {
  AlertCircle,
  ArrowRight,
  CalendarClock,
  CheckSquare2,
  Columns3,
  FolderKanban,
  MessageSquare,
  UserRound,
} from 'lucide-react'
import { useCallback, useEffect, useMemo, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import TaskCommentsModal from '../components/tasks/TaskCommentsModal'
import Badge from '../components/ui/Badge'
import Button from '../components/ui/Button'
import Card from '../components/ui/Card'
import ProgressBar from '../components/ui/ProgressBar'
import Toast from '../components/ui/Toast'
import { getMiTrabajo, updateEstadoTarea } from '../services/tareaService'

const stateBadge = {
  PENDIENTE: 'pendiente',
  EN_PROCESO: 'en-proceso',
  EN_REVISION: 'revision',
  COMPLETADA: 'completada',
  BLOQUEADA: 'bloqueada',
}

const statCards = [
  {
    key: 'pendientes',
    label: 'Pendientes',
    tone: 'text-warning',
    bg: 'bg-amber-50',
    icon: CheckSquare2,
  },
  {
    key: 'enProceso',
    label: 'En proceso',
    tone: 'text-info',
    bg: 'bg-blue-50',
    icon: ArrowRight,
  },
  {
    key: 'vencidas',
    label: 'Vencidas',
    tone: 'text-danger',
    bg: 'bg-red-50',
    icon: AlertCircle,
  },
  {
    key: 'bloqueadas',
    label: 'Bloqueadas',
    tone: 'text-danger',
    bg: 'bg-rose-50',
    icon: Columns3,
  },
  {
    key: 'paraHoy',
    label: 'Para hoy',
    tone: 'text-accent',
    bg: 'bg-violet-50',
    icon: CalendarClock,
  },
]

const formatDate = (value) => {
  if (!value) return 'Sin fecha'
  return new Intl.DateTimeFormat('es-PE', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    timeZone: 'UTC',
  }).format(new Date(`${value}T00:00:00Z`))
}

function TaskActionCard({
  task,
  saving,
  onComments,
  onOpenKanban,
  onStatusChange,
  returnPath,
}) {
  const overdue = task.fechaLimite
    && task.estado !== 'COMPLETADA'
    && task.fechaLimite < new Date().toISOString().slice(0, 10)

  return (
    <article className="rounded-xl border border-border bg-white p-4">
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0">
          <div className="flex flex-wrap gap-2">
            <Badge value={stateBadge[task.estado]}>
              {task.estado.replaceAll('_', ' ')}
            </Badge>
            <Badge value={task.prioridad.toLowerCase()}>{task.prioridad}</Badge>
            {overdue && <Badge value="bloqueada">Vencida</Badge>}
          </div>
          <Link
            to={`/tareas/${task.id}`}
            state={{ from: returnPath }}
            className="mt-3 block truncate font-bold hover:text-accent hover:underline"
          >
            {task.titulo}
          </Link>
          <Link
            to={`/proyectos/${task.proyectoId}`}
            className="mt-1 inline-flex text-xs text-textMuted hover:text-accent hover:underline"
          >
            {task.proyectoNombre}
          </Link>
          <p className="mt-3 text-sm text-textMuted">
            {task.descripcion || 'Sin descripcion registrada.'}
          </p>
        </div>
        <button
          type="button"
          aria-label={`Comentarios de ${task.titulo}`}
          className="rounded-lg p-2 text-textMuted hover:bg-violet-50 hover:text-accent"
          onClick={() => onComments(task)}
        >
          <MessageSquare size={16} />
        </button>
      </div>

      <div className="mt-4 flex flex-wrap gap-4 text-xs text-textMuted">
        <span className="flex items-center gap-1.5">
          <UserRound size={14} />
          {task.responsableNombre || 'Sin responsable'}
        </span>
        <span className={`${overdue ? 'text-danger' : ''}`}>
          Vence: {formatDate(task.fechaLimite)}
        </span>
      </div>

      <div className="mt-4 flex flex-wrap items-center gap-3">
        <label className="text-xs font-semibold uppercase tracking-wide text-textMuted">
          Estado
          <select
            aria-label={`Cambiar estado de ${task.titulo}`}
            disabled={saving || !task.puedeCambiarEstado}
            value={task.estado}
            onChange={(event) => onStatusChange(task.id, event.target.value)}
            className="mt-1.5 block rounded-lg border border-border bg-white px-3 py-2 text-sm outline-none focus:border-accent"
          >
            <option value="PENDIENTE">Pendiente</option>
            <option value="EN_PROCESO">En proceso</option>
            <option value="EN_REVISION">En revision</option>
            <option value="COMPLETADA">Completada</option>
            <option value="BLOQUEADA">Bloqueada</option>
          </select>
        </label>
        <Button
          variant="ghost"
          className="ml-auto"
          onClick={() => onOpenKanban(task.proyectoId)}
        >
          <Columns3 size={16} />
          Ver Kanban
        </Button>
      </div>
    </article>
  )
}

function MyWork() {
  const navigate = useNavigate()
  const location = useLocation()
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [commentTask, setCommentTask] = useState(null)
  const [filters, setFilters] = useState({
    estado: '',
    proyectoId: '',
  })

  const load = useCallback(async () => {
    setLoading(true)
    setError('')
    try {
      setData(await getMiTrabajo())
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    load()
  }, [load])

  const handleStatusChange = async (taskId, estado) => {
    setSaving(true)
    setError('')
    try {
      await updateEstadoTarea(taskId, estado)
      await load()
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setSaving(false)
    }
  }

  const tareasAsignadas = useMemo(() => data?.tareasAsignadas ?? [], [data])
  const tareasPrioritarias = useMemo(() => data?.tareasPrioritarias ?? [], [data])
  const tareasAccion = useMemo(() => data?.tareasNecesitanAccion ?? [], [data])
  const proyectos = useMemo(() => data?.proyectosConCarga ?? [], [data])
  const resumen = data?.resumen

  const projectOptions = useMemo(() => {
    const map = new Map()
    tareasAsignadas.forEach((task) => {
      map.set(String(task.proyectoId), task.proyectoNombre)
    })
    return [...map.entries()].map(([id, nombre]) => ({ id, nombre }))
  }, [tareasAsignadas])

  const visibleTasks = useMemo(
    () => tareasAsignadas.filter((task) =>
      (!filters.estado || task.estado === filters.estado)
      && (!filters.proyectoId || String(task.proyectoId) === filters.proyectoId)),
    [filters, tareasAsignadas],
  )

  if (loading) {
    return (
      <div className="space-y-6" aria-label="Cargando mi trabajo">
        <div className="h-28 animate-pulse rounded-xl bg-white" />
        <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-5">
          {Array.from({ length: 5 }).map((_, index) => (
            <div key={index} className="h-32 animate-pulse rounded-xl bg-white" />
          ))}
        </div>
        <div className="h-96 animate-pulse rounded-xl bg-white" />
      </div>
    )
  }

  if (error && !data) {
    return (
      <div className="space-y-4">
        <Toast
          variant="error"
          title="No se pudo cargar tu bandeja"
          message={error}
          className="max-w-none"
        />
        <Button onClick={load}>Reintentar</Button>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
        <div>
          <p className="text-sm font-semibold text-accent">Ejecucion personal</p>
          <h1 className="mt-1 text-2xl font-extrabold sm:text-3xl">Mi trabajo</h1>
          <p className="mt-2 text-sm text-textMuted">
            Prioriza tus tareas activas, detecta bloqueos y entra al proyecto correcto sin perder contexto.
          </p>
        </div>
        <div className="flex flex-wrap gap-2">
          <Button variant="secondary" onClick={() => navigate('/tareas')}>
            Ver todas las tareas
          </Button>
          <Button onClick={load}>Actualizar</Button>
        </div>
      </div>

      {error && (
        <button type="button" className="w-full text-left" onClick={() => setError('')}>
          <Toast
            variant="error"
            title="No se pudo completar la operacion"
            message={error}
            className="max-w-none"
          />
        </button>
      )}

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-5">
        {statCards.map((item) => {
          const Icon = item.icon
          return (
            <Card key={item.key} className="p-5">
              <div className="flex items-start justify-between gap-3">
                <div>
                  <p className="text-sm font-medium text-textMuted">{item.label}</p>
                  <p className="mt-3 text-3xl font-extrabold">{resumen?.[item.key] ?? 0}</p>
                </div>
                <span className={`grid h-10 w-10 place-items-center rounded-lg ${item.bg} ${item.tone}`}>
                  <Icon size={20} />
                </span>
              </div>
            </Card>
          )
        })}
      </div>

      <div className="grid gap-6 xl:grid-cols-[minmax(0,1.3fr)_minmax(20rem,0.8fr)]">
        <Card>
          <div className="flex items-center justify-between gap-3">
            <div>
              <p className="text-sm font-medium text-textMuted">Trabajo inmediato</p>
              <h2 className="mt-1 text-lg font-bold">Prioridades del dia</h2>
            </div>
            <span className="text-sm text-textMuted">{tareasPrioritarias.length} visibles</span>
          </div>

          {tareasPrioritarias.length === 0 ? (
            <div className="grid place-items-center py-12 text-center">
              <CheckSquare2 size={38} className="text-success" />
              <p className="mt-3 font-semibold">No tienes prioridades activas</p>
            </div>
          ) : (
            <div className="mt-5 grid gap-4 lg:grid-cols-2">
              {tareasPrioritarias.map((task) => (
                <TaskActionCard
                  key={task.id}
                  task={task}
                  saving={saving}
                  returnPath={location.pathname}
                  onComments={setCommentTask}
                  onOpenKanban={(projectId) => navigate(`/kanban?proyectoId=${projectId}`)}
                  onStatusChange={handleStatusChange}
                />
              ))}
            </div>
          )}
        </Card>

        <Card>
          <div className="flex items-center justify-between gap-3">
            <div>
              <p className="text-sm font-medium text-textMuted">Necesita accion</p>
              <h2 className="mt-1 text-lg font-bold">Bloqueos y vencimientos</h2>
            </div>
            <span className="rounded-full bg-red-100 px-3 py-1 text-sm font-bold text-danger">
              {tareasAccion.length}
            </span>
          </div>

          {tareasAccion.length === 0 ? (
            <div className="grid place-items-center py-12 text-center">
              <AlertCircle size={36} className="text-success" />
              <p className="mt-3 font-semibold">Sin alertas inmediatas</p>
            </div>
          ) : (
            <div className="mt-5 space-y-3">
              {tareasAccion.map((task) => (
                <div key={task.id} className="rounded-xl border border-border p-4">
                  <div className="flex items-start justify-between gap-3">
                    <div className="min-w-0">
                      <Link
                        to={`/tareas/${task.id}`}
                        state={{ from: location.pathname }}
                        className="truncate font-semibold hover:text-accent hover:underline"
                      >
                        {task.titulo}
                      </Link>
                      <Link
                        to={`/proyectos/${task.proyectoId}`}
                        className="mt-1 inline-flex text-xs text-textMuted hover:text-accent hover:underline"
                      >
                        {task.proyectoNombre}
                      </Link>
                    </div>
                    <Badge value={stateBadge[task.estado]}>
                      {task.estado.replaceAll('_', ' ')}
                    </Badge>
                  </div>
                  <div className="mt-3 flex items-center justify-between gap-3 text-sm text-textMuted">
                    <span>Vence: {formatDate(task.fechaLimite)}</span>
                    <Button
                      variant="ghost"
                      className="px-0"
                      onClick={() => setCommentTask(task)}
                    >
                      Ver comentarios
                    </Button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card>
      </div>

      <div className="grid gap-6 xl:grid-cols-[minmax(0,1.2fr)_minmax(20rem,0.9fr)]">
        <Card>
          <div className="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
            <div>
              <p className="text-sm font-medium text-textMuted">Mis tareas</p>
              <h2 className="mt-1 text-lg font-bold">Responsabilidad directa</h2>
            </div>
            <div className="grid gap-3 sm:grid-cols-2">
              <label className="text-sm font-medium">
                Estado
                <select
                  value={filters.estado}
                  onChange={(event) =>
                    setFilters((current) => ({ ...current, estado: event.target.value }))}
                  className="mt-1.5 w-full rounded-lg border border-border bg-white px-3 py-2.5 outline-none focus:border-accent"
                >
                  <option value="">Todos</option>
                  <option value="PENDIENTE">Pendiente</option>
                  <option value="EN_PROCESO">En proceso</option>
                  <option value="EN_REVISION">En revision</option>
                  <option value="COMPLETADA">Completada</option>
                  <option value="BLOQUEADA">Bloqueada</option>
                </select>
              </label>
              <label className="text-sm font-medium">
                Proyecto
                <select
                  value={filters.proyectoId}
                  onChange={(event) =>
                    setFilters((current) => ({ ...current, proyectoId: event.target.value }))}
                  className="mt-1.5 w-full rounded-lg border border-border bg-white px-3 py-2.5 outline-none focus:border-accent"
                >
                  <option value="">Todos</option>
                  {projectOptions.map((project) => (
                    <option key={project.id} value={project.id}>
                      {project.nombre}
                    </option>
                  ))}
                </select>
              </label>
            </div>
          </div>

          {visibleTasks.length === 0 ? (
            <div className="grid place-items-center py-14 text-center">
              <CheckSquare2 size={42} className="text-accent" />
              <h3 className="mt-4 text-lg font-bold">No hay tareas para mostrar</h3>
              <p className="mt-1 max-w-md text-sm text-textMuted">
                Ajusta los filtros o espera nuevas asignaciones.
              </p>
            </div>
          ) : (
            <div className="mt-5 grid gap-4 lg:grid-cols-2">
              {visibleTasks.map((task) => (
                <TaskActionCard
                  key={task.id}
                  task={task}
                  saving={saving}
                  returnPath={location.pathname}
                  onComments={setCommentTask}
                  onOpenKanban={(projectId) => navigate(`/kanban?proyectoId=${projectId}`)}
                  onStatusChange={handleStatusChange}
                />
              ))}
            </div>
          )}
        </Card>

        <Card>
          <div className="flex items-center justify-between gap-3">
            <div>
              <p className="text-sm font-medium text-textMuted">Contexto de proyectos</p>
              <h2 className="mt-1 text-lg font-bold">Donde tienes carga activa</h2>
            </div>
            <Button variant="ghost" onClick={() => navigate('/proyectos')}>
              Ver proyectos
            </Button>
          </div>

          {proyectos.length === 0 ? (
            <div className="grid place-items-center py-12 text-center">
              <FolderKanban size={38} className="text-accent" />
              <p className="mt-3 font-semibold">Sin proyectos con carga activa</p>
            </div>
          ) : (
            <div className="mt-5 space-y-3">
              {proyectos.map((project) => (
                <article key={project.id} className="rounded-xl border border-border p-4">
                  <div className="flex items-start justify-between gap-3">
                    <div>
                      <p className="text-xs font-semibold uppercase tracking-wide text-accent">
                        {project.espacioNombre}
                      </p>
                      <h3 className="mt-1 font-bold">{project.nombre}</h3>
                    </div>
                    <Badge value={project.prioridad.toLowerCase()}>{project.prioridad}</Badge>
                  </div>
                  <div className="mt-3 flex flex-wrap gap-2 text-xs text-textMuted">
                    <span>{project.tareasActivas} tarea(s) activas</span>
                    <span>Vence: {formatDate(project.fechaFin)}</span>
                  </div>
                  <ProgressBar
                    value={project.porcentajeAvance}
                    label="Avance"
                    className="mt-4"
                  />
                  <div className="mt-4 flex flex-wrap gap-2">
                    <Button
                      variant="secondary"
                      className="flex-1"
                      onClick={() => navigate(`/proyectos/${project.id}`)}
                    >
                      Abrir proyecto
                    </Button>
                    <Button
                      variant="ghost"
                      className="flex-1"
                      onClick={() => navigate(`/kanban?proyectoId=${project.id}`)}
                    >
                      <Columns3 size={16} />
                      Kanban
                    </Button>
                  </div>
                </article>
              ))}
            </div>
          )}
        </Card>
      </div>

      <TaskCommentsModal
        task={commentTask}
        onClose={() => {
          setCommentTask(null)
          load()
        }}
      />
    </div>
  )
}

export default MyWork
