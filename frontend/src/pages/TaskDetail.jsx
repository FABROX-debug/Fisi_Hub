import {
  AlertCircle,
  ArrowLeft,
  CalendarDays,
  CheckCircle2,
  Columns3,
  Edit3,
  FolderKanban,
  MessageSquare,
  RefreshCw,
  UserRound,
} from 'lucide-react'
import { useCallback, useEffect, useMemo, useState } from 'react'
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom'
import ActivityFeed from '../components/members/ActivityFeed'
import TaskCommentsModal from '../components/tasks/TaskCommentsModal'
import TaskFormModal from '../components/tasks/TaskFormModal'
import Badge from '../components/ui/Badge'
import Button from '../components/ui/Button'
import Card from '../components/ui/Card'
import ProgressBar from '../components/ui/ProgressBar'
import Toast from '../components/ui/Toast'
import {
  getTareaDetalle,
  updateEstadoTarea,
  updateTarea,
} from '../services/tareaService'

const stateBadge = {
  PENDIENTE: 'pendiente',
  EN_PROCESO: 'en-proceso',
  EN_REVISION: 'revision',
  COMPLETADA: 'completada',
  BLOQUEADA: 'bloqueada',
}

const alertConfig = [
  { key: 'vencida', label: 'Vencida', value: 'bloqueada' },
  { key: 'venceHoy', label: 'Vence hoy', value: 'revision' },
  { key: 'bloqueada', label: 'Bloqueada', value: 'bloqueada' },
  { key: 'sinResponsable', label: 'Sin responsable', value: 'pendiente' },
  { key: 'requiereAtencion', label: 'Requiere atencion', value: 'alta' },
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

const formatDateTime = (value) => new Intl.DateTimeFormat('es-PE', {
  dateStyle: 'medium',
  timeStyle: 'short',
}).format(new Date(value))

function TaskDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const location = useLocation()
  const [detail, setDetail] = useState(null)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [editing, setEditing] = useState(false)
  const [commentsOpen, setCommentsOpen] = useState(false)

  const loadDetail = useCallback(async () => {
    setLoading(true)
    setError('')
    try {
      setDetail(await getTareaDetalle(id))
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setLoading(false)
    }
  }, [id])

  useEffect(() => {
    loadDetail()
  }, [loadDetail])

  const task = detail?.tarea
  const project = detail?.proyecto
  const comments = detail?.comentarios ?? []
  const activity = detail?.actividad ?? []
  const alerts = detail?.alertas
  const activeAlerts = useMemo(
    () => alertConfig.filter((item) => alerts?.[item.key]),
    [alerts],
  )
  const returnTo = location.state?.from || '/tareas'

  const handleStatusChange = async (estado) => {
    setSaving(true)
    setError('')
    try {
      await updateEstadoTarea(id, estado)
      await loadDetail()
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setSaving(false)
    }
  }

  const handleSave = async (payload, taskId) => {
    setSaving(true)
    setError('')
    try {
      await updateTarea(taskId, payload)
      setEditing(false)
      await loadDetail()
    } catch (requestError) {
      setError(requestError.message)
      throw requestError
    } finally {
      setSaving(false)
    }
  }

  if (loading) {
    return (
      <div className="space-y-6">
        <div className="h-28 animate-pulse rounded-xl bg-white" />
        <div className="grid gap-4 lg:grid-cols-3">
          <div className="h-56 animate-pulse rounded-xl bg-white lg:col-span-2" />
          <div className="h-56 animate-pulse rounded-xl bg-white" />
        </div>
        <div className="h-72 animate-pulse rounded-xl bg-white" />
      </div>
    )
  }

  if (error && !detail) {
    return (
      <div className="space-y-4">
        <Toast
          variant="error"
          title="No se pudo cargar la tarea"
          message={error}
          className="max-w-none"
        />
        <Button onClick={loadDetail}>Reintentar</Button>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center gap-3 text-sm text-textMuted">
        <button
          type="button"
          className="inline-flex items-center gap-2 font-medium text-accent hover:underline"
          onClick={() => navigate(returnTo)}
        >
          <ArrowLeft size={16} />
          Volver
        </button>
        <span>/</span>
        <Link
          to={`/proyectos/${project.id}`}
          className="font-medium text-accent hover:underline"
        >
          {project.nombre}
        </Link>
        <span>/</span>
        <span>{task.titulo}</span>
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

      <Card className="space-y-5">
        <div className="flex flex-col gap-4 lg:flex-row lg:items-start lg:justify-between">
          <div>
            <p className="text-sm font-semibold text-accent">{project.espacioNombre}</p>
            <h1 className="mt-1 text-3xl font-extrabold">{task.titulo}</h1>
            <p className="mt-2 max-w-3xl text-sm text-textMuted">
              {task.descripcion || 'Sin descripcion registrada para esta tarea.'}
            </p>
          </div>
          <div className="flex flex-wrap gap-2">
            <Badge value={stateBadge[task.estado]}>
              {task.estado.replaceAll('_', ' ')}
            </Badge>
            <Badge value={task.prioridad.toLowerCase()}>{task.prioridad}</Badge>
            {task.puedeEditar && (
              <Button variant="secondary" onClick={() => setEditing(true)}>
                <Edit3 size={16} />
                Editar
              </Button>
            )}
            <Button variant="ghost" onClick={() => setCommentsOpen(true)}>
              <MessageSquare size={16} />
              Comentarios
            </Button>
          </div>
        </div>

        {activeAlerts.length > 0 && (
          <div className="flex flex-wrap gap-2">
            {activeAlerts.map((alert) => (
              <Badge key={alert.key} value={alert.value}>
                {alert.label}
              </Badge>
            ))}
          </div>
        )}

        <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
          <div className="rounded-xl bg-slate-50 p-4">
            <p className="text-xs font-semibold uppercase tracking-wide text-textMuted">
              Responsable
            </p>
            <p className="mt-2 font-bold">
              {task.responsableNombre || 'Sin responsable'}
            </p>
          </div>
          <div className="rounded-xl bg-slate-50 p-4">
            <p className="text-xs font-semibold uppercase tracking-wide text-textMuted">
              Creador
            </p>
            <p className="mt-2 font-bold">{task.creadoPorNombre}</p>
          </div>
          <div className="rounded-xl bg-slate-50 p-4">
            <p className="text-xs font-semibold uppercase tracking-wide text-textMuted">
              Fecha limite
            </p>
            <p className="mt-2 font-bold">{formatDate(task.fechaLimite)}</p>
          </div>
          <div className="rounded-xl bg-slate-50 p-4">
            <p className="text-xs font-semibold uppercase tracking-wide text-textMuted">
              Ultima actualizacion
            </p>
            <p className="mt-2 text-sm font-medium">
              {formatDateTime(task.actualizadoEn)}
            </p>
          </div>
        </div>

        <div className="grid gap-4 lg:grid-cols-[minmax(0,1fr)_auto] lg:items-end">
          <div>
            <ProgressBar
              value={project.porcentajeAvance}
              label="Avance del proyecto"
            />
          </div>
          <label className="text-sm font-medium">
            Cambiar estado
            <select
              value={task.estado}
              disabled={saving || !task.puedeCambiarEstado}
              onChange={(event) => handleStatusChange(event.target.value)}
              className="mt-1.5 w-full rounded-lg border border-border bg-white px-3 py-2.5 outline-none focus:border-accent"
            >
              <option value="PENDIENTE">Pendiente</option>
              <option value="EN_PROCESO">En proceso</option>
              <option value="EN_REVISION">En revision</option>
              <option value="COMPLETADA">Completada</option>
              <option value="BLOQUEADA">Bloqueada</option>
            </select>
          </label>
        </div>

        <div className="flex flex-wrap gap-2">
          <Button variant="secondary" onClick={() => navigate(`/proyectos/${project.id}`)}>
            <FolderKanban size={16} />
            Abrir proyecto
          </Button>
          <Button variant="ghost" onClick={() => navigate(`/kanban?proyectoId=${project.id}`)}>
            <Columns3 size={16} />
            Abrir Kanban
          </Button>
        </div>
      </Card>

      <div className="grid gap-6 xl:grid-cols-[minmax(0,1.2fr)_minmax(22rem,0.8fr)]">
        <Card>
          <div className="flex items-center justify-between gap-3">
            <div>
              <p className="text-sm font-medium text-textMuted">Contexto operativo</p>
              <h2 className="mt-1 text-lg font-bold">Resumen de atencion</h2>
            </div>
            <span className="rounded-full bg-violet-50 px-3 py-1 text-sm font-semibold text-accent">
              {activeAlerts.length} alerta(s)
            </span>
          </div>

          <div className="mt-5 grid gap-3 sm:grid-cols-2">
            <div className="rounded-xl border border-border p-4">
              <div className="flex items-center gap-2 text-danger">
                <AlertCircle size={18} />
                <p className="font-semibold">Riesgos</p>
              </div>
              <p className="mt-3 text-sm text-textMuted">
                {alerts?.vencida
                  ? 'La tarea ya vencio.'
                  : alerts?.bloqueada
                    ? 'La tarea esta bloqueada.'
                    : alerts?.venceHoy
                      ? 'La tarea vence hoy.'
                      : 'Sin riesgo inmediato detectado.'}
              </p>
            </div>
            <div className="rounded-xl border border-border p-4">
              <div className="flex items-center gap-2 text-info">
                <UserRound size={18} />
                <p className="font-semibold">Responsabilidad</p>
              </div>
              <p className="mt-3 text-sm text-textMuted">
                {alerts?.sinResponsable
                  ? 'Necesita asignacion para avanzar con claridad.'
                  : `Responsable actual: ${task.responsableNombre}.`}
              </p>
            </div>
            <div className="rounded-xl border border-border p-4">
              <div className="flex items-center gap-2 text-warning">
                <RefreshCw size={18} />
                <p className="font-semibold">Estado</p>
              </div>
              <p className="mt-3 text-sm text-textMuted">
                {task.estado === 'EN_REVISION'
                  ? 'La tarea esta esperando revision.'
                  : `Estado actual: ${task.estado.replaceAll('_', ' ')}.`}
              </p>
            </div>
            <div className="rounded-xl border border-border p-4">
              <div className="flex items-center gap-2 text-success">
                <CheckCircle2 size={18} />
                <p className="font-semibold">Proyecto</p>
              </div>
              <p className="mt-3 text-sm text-textMuted">
                {project.nombre} avanza en {project.porcentajeAvance}%.
              </p>
            </div>
          </div>
        </Card>

        <Card>
          <div className="flex items-center justify-between gap-3">
            <div>
              <p className="text-sm font-medium text-textMuted">Comentarios</p>
              <h2 className="mt-1 text-lg font-bold">Conversacion reciente</h2>
            </div>
            <Button variant="ghost" onClick={() => setCommentsOpen(true)}>
              Ver todos
            </Button>
          </div>

          {comments.length === 0 ? (
            <p className="mt-5 rounded-xl border border-dashed border-border p-6 text-center text-sm text-textMuted">
              Sin comentarios en esta tarea.
            </p>
          ) : (
            <div className="mt-5 space-y-3">
              {comments.slice(-3).reverse().map((comment) => (
                <article key={comment.id} className="rounded-xl border border-border p-4">
                  <div className="flex items-center justify-between gap-3">
                    <p className="font-semibold">{comment.autorNombre}</p>
                    <span className="text-xs text-textMuted">
                      {formatDateTime(comment.creadoEn)}
                    </span>
                  </div>
                  <p className="mt-2 whitespace-pre-wrap text-sm text-textMuted">
                    {comment.contenido}
                  </p>
                </article>
              ))}
            </div>
          )}
        </Card>
      </div>

      <Card>
        <div className="flex items-center justify-between gap-3">
          <div>
            <p className="text-sm font-medium text-textMuted">Actividad relacionada</p>
            <h2 className="mt-1 text-lg font-bold">Cambios y seguimiento</h2>
          </div>
        </div>
        <div className="mt-5">
          <ActivityFeed items={activity} />
        </div>
      </Card>

      <TaskFormModal
        open={editing}
        task={task}
        projects={project ? [{ id: project.id, nombre: project.nombre }] : []}
        saving={saving}
        title="Editar tarea"
        lockProject
        onClose={() => setEditing(false)}
        onSave={handleSave}
      />
      <TaskCommentsModal
        task={commentsOpen ? task : null}
        onClose={() => {
          setCommentsOpen(false)
          loadDetail()
        }}
      />
    </div>
  )
}

export default TaskDetail
