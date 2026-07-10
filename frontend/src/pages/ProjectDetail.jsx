import {
  ArrowLeft,
  CheckCircle2,
  Clock3,
  Columns3,
  Edit3,
  MessageSquare,
  Plus,
} from 'lucide-react'
import { useCallback, useEffect, useMemo, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import ActivityFeed from '../components/members/ActivityFeed'
import TaskCommentsModal from '../components/tasks/TaskCommentsModal'
import TaskFormModal from '../components/tasks/TaskFormModal'
import Badge from '../components/ui/Badge'
import Button from '../components/ui/Button'
import Card from '../components/ui/Card'
import ProgressBar from '../components/ui/ProgressBar'
import Toast from '../components/ui/Toast'
import {
  addProjectMember,
  getProjectActivity,
  updateProjectMemberRole,
} from '../services/miembroService'
import {
  getProyectoDetalle,
} from '../services/proyectoService'
import { getWorkspaceMembers } from '../services/espacioService'
import { createTarea, updateEstadoTarea, updateTarea } from '../services/tareaService'

const projectStatusBadge = {
  PLANIFICADO: 'pendiente',
  EN_PROCESO: 'en-proceso',
  EN_REVISION: 'revision',
  FINALIZADO: 'completada',
  CANCELADO: 'bloqueada',
}

const taskStatusBadge = {
  PENDIENTE: 'pendiente',
  EN_PROCESO: 'en-proceso',
  EN_REVISION: 'revision',
  COMPLETADA: 'completada',
  BLOQUEADA: 'bloqueada',
}

const formatDate = (value) => {
  if (!value) return 'Sin fecha'
  return new Intl.DateTimeFormat('es-PE', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    timeZone: 'UTC',
  }).format(new Date(`${value}T00:00:00Z`))
}

const tabs = [
  ['resumen', 'Resumen'],
  ['tareas', 'Tareas'],
  ['miembros', 'Miembros'],
  ['actividad', 'Actividad'],
]

function TaskHighlightCard({
  task,
  saving,
  onEdit,
  onComments,
  onStatusChange,
  projectId,
}) {
  const overdue = task.fechaLimite
    && task.estado !== 'COMPLETADA'
    && task.fechaLimite < new Date().toISOString().slice(0, 10)

  return (
    <article className="rounded-xl border border-border bg-white p-4">
      <div className="flex items-start justify-between gap-3">
        <div>
          <div className="flex flex-wrap gap-2">
            <Badge value={taskStatusBadge[task.estado]}>
              {task.estado.replaceAll('_', ' ')}
            </Badge>
            <Badge value={task.prioridad.toLowerCase()}>{task.prioridad}</Badge>
            {overdue && <Badge value="bloqueada">Vencida</Badge>}
          </div>
          <Link
            to={`/tareas/${task.id}`}
            state={{ from: `/proyectos/${projectId}` }}
            className="mt-3 block font-bold hover:text-accent hover:underline"
          >
            {task.titulo}
          </Link>
          <p className="mt-1 text-sm text-textMuted">
            {task.descripcion || 'Sin descripcion'}
          </p>
        </div>
        <div className="flex gap-1">
          {task.puedeEditar && (
            <button
              type="button"
              aria-label={`Editar ${task.titulo}`}
              className="rounded-lg p-2 text-textMuted hover:bg-violet-50 hover:text-accent"
              onClick={() => onEdit(task)}
            >
              <Edit3 size={16} />
            </button>
          )}
          <button
            type="button"
            aria-label={`Comentarios de ${task.titulo}`}
            className="rounded-lg p-2 text-textMuted hover:bg-violet-50 hover:text-accent"
            onClick={() => onComments(task)}
          >
            <MessageSquare size={16} />
          </button>
        </div>
      </div>

      <div className="mt-4 flex flex-wrap gap-4 text-xs text-textMuted">
        <span>Responsable: {task.responsableNombre || 'Sin responsable'}</span>
        <span>Vence: {formatDate(task.fechaLimite)}</span>
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
      </div>
    </article>
  )
}

function ProjectDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [detail, setDetail] = useState(null)
  const [workspaceMembers, setWorkspaceMembers] = useState([])
  const [loading, setLoading] = useState(true)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const [activeTab, setActiveTab] = useState('resumen')
  const [taskModalOpen, setTaskModalOpen] = useState(false)
  const [editingTask, setEditingTask] = useState(null)
  const [commentTask, setCommentTask] = useState(null)
  const [memberUserId, setMemberUserId] = useState('')
  const [memberRole, setMemberRole] = useState('MIEMBRO')

  const loadDetail = useCallback(async () => {
    setLoading(true)
    setError('')
    try {
      const response = await getProyectoDetalle(id)
      setDetail(response)
      const workspaceTeam = await getWorkspaceMembers(response.proyecto.espacioId)
      setWorkspaceMembers(workspaceTeam.miembros || [])
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setLoading(false)
    }
  }, [id])

  useEffect(() => {
    loadDetail()
  }, [loadDetail])

  const proyecto = detail?.proyecto
  const resumen = detail?.resumenTareas
  const miembros = useMemo(
    () => detail?.miembros?.miembros || [],
    [detail],
  )
  const actividad = useMemo(
    () => detail?.actividadReciente || [],
    [detail],
  )
  const tareas = useMemo(
    () => detail?.tareasDestacadas || [],
    [detail],
  )

  const availableMembers = useMemo(() => workspaceMembers.filter(
    (member) => member.activo
      && !miembros.some(
        (projectMember) => projectMember.usuarioId === member.usuarioId,
      ),
  ), [miembros, workspaceMembers])

  const criticalTaskCount = useMemo(
    () => tareas.filter((task) =>
      task.estado === 'BLOQUEADA'
      || (task.fechaLimite && task.fechaLimite < new Date().toISOString().slice(0, 10)),
    ).length,
    [tareas],
  )

  const reloadActivity = async () => {
    const data = await getProjectActivity(id)
    setDetail((current) => ({
      ...current,
      actividadReciente: data.slice(0, 8),
    }))
  }

  const saveTask = async (payload, taskId) => {
    setBusy(true)
    setError('')
    try {
      if (taskId) {
        await updateTarea(taskId, payload)
      } else {
        await createTarea(payload)
      }
      setEditingTask(null)
      await loadDetail()
    } catch (requestError) {
      setError(requestError.message)
      throw requestError
    } finally {
      setBusy(false)
    }
  }

  const handleStatusChange = async (taskId, estado) => {
    setBusy(true)
    setError('')
    try {
      await updateEstadoTarea(taskId, estado)
      await loadDetail()
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setBusy(false)
    }
  }

  const handleAddMember = async (event) => {
    event.preventDefault()
    setBusy(true)
    setError('')
    try {
      await addProjectMember(id, {
        usuarioId: Number(memberUserId),
        rol: memberRole,
      })
      setMemberUserId('')
      setMemberRole('MIEMBRO')
      await loadDetail()
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setBusy(false)
    }
  }

  const handleRoleChange = async (userId, role) => {
    setBusy(true)
    setError('')
    try {
      await updateProjectMemberRole(id, userId, role)
      await loadDetail()
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setBusy(false)
    }
  }

  const openProjectEditor = () => {
    navigate('/proyectos', { state: { editProjectId: proyecto.id } })
  }

  if (loading) {
    return (
      <div className="space-y-6">
        <div className="h-40 animate-pulse rounded-xl bg-white" />
        <div className="grid gap-4 lg:grid-cols-3">
          <div className="h-44 animate-pulse rounded-xl bg-white" />
          <div className="h-44 animate-pulse rounded-xl bg-white" />
          <div className="h-44 animate-pulse rounded-xl bg-white" />
        </div>
      </div>
    )
  }

  if (error && !detail) {
    return (
      <div className="space-y-4">
        <Toast
          variant="error"
          title="No se pudo cargar el proyecto"
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
        <Link to="/proyectos" className="inline-flex items-center gap-2 font-medium text-accent hover:underline">
          <ArrowLeft size={16} />
          Volver a proyectos
        </Link>
        <span>/</span>
        <span>{proyecto.nombre}</span>
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
        <div className="flex flex-col justify-between gap-4 lg:flex-row lg:items-start">
          <div>
            <p className="text-sm font-semibold text-accent">{proyecto.espacioNombre}</p>
            <h1 className="mt-1 text-3xl font-extrabold">{proyecto.nombre}</h1>
            <p className="mt-2 max-w-3xl text-sm text-textMuted">
              {proyecto.descripcion || 'Sin descripcion registrada para este proyecto.'}
            </p>
          </div>
          <div className="flex flex-wrap gap-2">
            <Badge value={projectStatusBadge[proyecto.estado]}>
              {proyecto.estado.replaceAll('_', ' ')}
            </Badge>
            <Badge value={proyecto.prioridad.toLowerCase()}>
              {proyecto.prioridad}
            </Badge>
            {proyecto.puedeGestionar && (
              <Button variant="secondary" onClick={openProjectEditor}>
                <Edit3 size={16} />
                Editar proyecto
              </Button>
            )}
            <Button onClick={() => {
              setEditingTask(null)
              setTaskModalOpen(true)
            }}
            >
              <Plus size={16} />
              Nueva tarea
            </Button>
          </div>
        </div>

        <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
          <div className="rounded-xl bg-slate-50 p-4">
            <p className="text-xs font-semibold uppercase tracking-wide text-textMuted">Lider</p>
            <p className="mt-2 font-bold">{proyecto.liderNombre}</p>
          </div>
          <div className="rounded-xl bg-slate-50 p-4">
            <p className="text-xs font-semibold uppercase tracking-wide text-textMuted">Fechas</p>
            <p className="mt-2 text-sm font-medium">
              {formatDate(proyecto.fechaInicio)} - {formatDate(proyecto.fechaFin)}
            </p>
          </div>
          <div className="rounded-xl bg-slate-50 p-4">
            <p className="text-xs font-semibold uppercase tracking-wide text-textMuted">Miembros</p>
            <p className="mt-2 text-2xl font-extrabold">{proyecto.cantidadMiembros}</p>
          </div>
          <div className="rounded-xl bg-slate-50 p-4">
            <p className="text-xs font-semibold uppercase tracking-wide text-textMuted">Alertas</p>
            <p className="mt-2 text-2xl font-extrabold">{criticalTaskCount}</p>
          </div>
        </div>

        <ProgressBar value={proyecto.porcentajeAvance} label="Avance del proyecto" />
      </Card>

      <div className="flex flex-wrap gap-2">
        {tabs.map(([value, label]) => (
          <button
            type="button"
            key={value}
            onClick={() => setActiveTab(value)}
            className={`rounded-full px-4 py-2 text-sm font-semibold transition ${
              activeTab === value
                ? 'bg-accent text-white'
                : 'border border-border bg-white text-textMuted hover:border-accent hover:text-accent'
            }`}
          >
            {label}
          </button>
        ))}
        <Button
          variant="ghost"
          className="ml-auto"
          onClick={() => navigate(`/kanban?proyectoId=${proyecto.id}`)}
        >
          <Columns3 size={16} />
          Abrir Kanban
        </Button>
      </div>

      {(activeTab === 'resumen' || activeTab === 'tareas') && (
        <div className="grid gap-4 lg:grid-cols-3">
          <Card className="p-4">
            <p className="text-xs font-semibold uppercase tracking-wide text-textMuted">Total tareas</p>
            <p className="mt-2 text-3xl font-extrabold">{resumen.total}</p>
          </Card>
          <Card className="p-4">
            <p className="text-xs font-semibold uppercase tracking-wide text-textMuted">Pendientes y en curso</p>
            <p className="mt-2 text-3xl font-extrabold">
              {resumen.pendientes + resumen.enProceso + resumen.enRevision}
            </p>
          </Card>
          <Card className="p-4">
            <p className="text-xs font-semibold uppercase tracking-wide text-textMuted">Bloqueadas o vencidas</p>
            <p className="mt-2 text-3xl font-extrabold text-danger">
              {resumen.bloqueadas + resumen.vencidas}
            </p>
          </Card>
        </div>
      )}

      {activeTab === 'resumen' && (
        <div className="grid gap-6 xl:grid-cols-[minmax(0,1.35fr)_minmax(20rem,0.7fr)]">
          <div className="space-y-6">
            <Card>
              <div className="flex items-center justify-between gap-3">
                <div>
                  <p className="text-sm font-medium text-textMuted">Trabajo inmediato</p>
                  <h2 className="mt-1 text-lg font-bold">Tareas destacadas</h2>
                </div>
                <span className="text-sm text-textMuted">{tareas.length} visibles</span>
              </div>
              {tareas.length === 0 ? (
                <div className="grid place-items-center py-12 text-center">
                  <CheckCircle2 size={36} className="text-success" />
                  <p className="mt-3 font-semibold">No hay tareas destacadas</p>
                </div>
              ) : (
                <div className="mt-5 grid gap-4 lg:grid-cols-2">
                  {tareas.map((task) => (
                    <TaskHighlightCard
                      key={task.id}
                      task={task}
                      saving={busy}
                      projectId={proyecto.id}
                      onEdit={(item) => {
                        setEditingTask(item)
                        setTaskModalOpen(true)
                      }}
                      onComments={setCommentTask}
                      onStatusChange={handleStatusChange}
                    />
                  ))}
                </div>
              )}
            </Card>

            <Card>
              <div className="flex items-center justify-between gap-3">
                <div>
                  <p className="text-sm font-medium text-textMuted">Equipo</p>
                  <h2 className="mt-1 text-lg font-bold">Miembros y carga</h2>
                </div>
                <Button variant="ghost" onClick={() => setActiveTab('miembros')}>
                  Ver equipo
                </Button>
              </div>
              <div className="mt-5 grid gap-3 md:grid-cols-2">
                {miembros.slice(0, 4).map((member) => (
                  <div key={member.usuarioId} className="rounded-xl border border-border p-4">
                    <div className="flex items-start justify-between gap-3">
                      <div>
                        <p className="font-semibold">{member.nombre}</p>
                        <p className="text-sm text-textMuted">{member.correo}</p>
                      </div>
                      <Badge value={member.rol === 'LIDER' ? 'en-proceso' : 'pendiente'}>
                        {member.rol}
                      </Badge>
                    </div>
                    <p className="mt-3 text-sm text-textMuted">
                      {member.tareasActivas} tarea(s) activa(s)
                    </p>
                  </div>
                ))}
              </div>
            </Card>
          </div>

          <Card>
            <div className="flex items-center justify-between gap-3">
              <div>
                <p className="text-sm font-medium text-textMuted">Actividad</p>
                <h2 className="mt-1 text-lg font-bold">Ultimos cambios</h2>
              </div>
              <Button variant="ghost" onClick={() => setActiveTab('actividad')}>
                Ver todo
              </Button>
            </div>
            <div className="mt-5">
              <ActivityFeed items={actividad.slice(0, 5)} />
            </div>
          </Card>
        </div>
      )}

      {activeTab === 'tareas' && (
        <Card>
          <div className="mb-5 flex flex-wrap items-center justify-between gap-3">
            <div>
              <p className="text-sm font-medium text-textMuted">Seguimiento</p>
              <h2 className="mt-1 text-lg font-bold">Tareas criticas y proximas</h2>
            </div>
            <Button onClick={() => {
              setEditingTask(null)
              setTaskModalOpen(true)
            }}>
              <Plus size={16} />
              Nueva tarea
            </Button>
          </div>
          {tareas.length === 0 ? (
            <p className="rounded-xl border border-dashed border-border p-8 text-center text-sm text-textMuted">
              Este proyecto todavia no tiene tareas.
            </p>
          ) : (
            <div className="grid gap-4 lg:grid-cols-2">
              {tareas.map((task) => (
                <TaskHighlightCard
                  key={task.id}
                  task={task}
                  saving={busy}
                  projectId={proyecto.id}
                  onEdit={(item) => {
                    setEditingTask(item)
                    setTaskModalOpen(true)
                  }}
                  onComments={setCommentTask}
                  onStatusChange={handleStatusChange}
                />
              ))}
            </div>
          )}
        </Card>
      )}

      {activeTab === 'miembros' && (
        <div className="grid gap-6 xl:grid-cols-[minmax(0,1.2fr)_minmax(20rem,0.8fr)]">
          <Card>
            <div className="flex items-center justify-between gap-3">
              <div>
                <p className="text-sm font-medium text-textMuted">Equipo del proyecto</p>
                <h2 className="mt-1 text-lg font-bold">Miembros y roles</h2>
              </div>
              <span className="text-sm text-textMuted">{miembros.length} miembro(s)</span>
            </div>

            {detail?.miembros?.puedeGestionar && (
              <form onSubmit={handleAddMember} className="mt-5 grid gap-3 border-b border-border pb-5 md:grid-cols-[1fr_10rem_auto] md:items-end">
                <label className="text-sm font-medium">
                  Miembro del espacio
                  <select
                    value={memberUserId}
                    required
                    onChange={(event) => setMemberUserId(event.target.value)}
                    className="mt-1.5 w-full rounded-lg border border-border bg-white px-3.5 py-2.5 outline-none focus:border-accent"
                  >
                    <option value="">Selecciona un miembro</option>
                    {availableMembers.map((member) => (
                      <option key={member.usuarioId} value={member.usuarioId}>
                        {member.nombre} - {member.correo}
                      </option>
                    ))}
                  </select>
                </label>
                <label className="text-sm font-medium">
                  Rol
                  <select
                    value={memberRole}
                    onChange={(event) => setMemberRole(event.target.value)}
                    className="mt-1.5 w-full rounded-lg border border-border bg-white px-3.5 py-2.5 outline-none focus:border-accent"
                  >
                    <option value="MIEMBRO">Miembro</option>
                    <option value="LIDER">Lider</option>
                  </select>
                </label>
                <Button type="submit" disabled={busy || availableMembers.length === 0}>
                  <Plus size={16} />
                  Agregar
                </Button>
              </form>
            )}

            <div className="mt-5 space-y-3">
              {miembros.map((member) => (
                <div key={member.usuarioId} className="rounded-xl border border-border p-4">
                  <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                    <div>
                      <div className="flex items-center gap-2">
                        <p className="font-semibold">{member.nombre}</p>
                        {member.liderDesignado && (
                          <Badge value="en-proceso">Lider designado</Badge>
                        )}
                        {!member.activo && (
                          <Badge value="bloqueada">Inactivo</Badge>
                        )}
                      </div>
                      <p className="text-sm text-textMuted">{member.correo}</p>
                    </div>
                    <div className="flex items-center gap-3">
                      <span className="text-sm text-textMuted">
                        {member.tareasActivas} tarea(s) activas
                      </span>
                      {detail?.miembros?.puedeGestionar ? (
                        <select
                          value={member.rol}
                          disabled={busy}
                          onChange={(event) =>
                            handleRoleChange(member.usuarioId, event.target.value)}
                          className="rounded-lg border border-border bg-white px-3 py-2 text-sm outline-none focus:border-accent"
                        >
                          <option value="LIDER">Lider</option>
                          <option value="MIEMBRO">Miembro</option>
                        </select>
                      ) : (
                        <Badge value={member.rol === 'LIDER' ? 'en-proceso' : 'pendiente'}>
                          {member.rol}
                        </Badge>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </Card>

          <Card>
            <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-1">
              <div className="rounded-xl bg-slate-50 p-4">
                <p className="text-xs font-semibold uppercase tracking-wide text-textMuted">Sin tareas</p>
                <p className="mt-2 text-2xl font-extrabold text-emerald-700">
                  {miembros.filter((member) => member.tareasActivas === 0).length}
                </p>
              </div>
              <div className="rounded-xl bg-slate-50 p-4">
                <p className="text-xs font-semibold uppercase tracking-wide text-textMuted">Carga alta</p>
                <p className="mt-2 text-2xl font-extrabold text-amber-700">
                  {miembros.filter((member) => member.tareasActivas >= 5).length}
                </p>
              </div>
              <div className="rounded-xl bg-slate-50 p-4">
                <p className="text-xs font-semibold uppercase tracking-wide text-textMuted">Lideres</p>
                <p className="mt-2 text-2xl font-extrabold text-accent">
                  {miembros.filter((member) => member.rol === 'LIDER').length}
                </p>
              </div>
            </div>
          </Card>
        </div>
      )}

      {activeTab === 'actividad' && (
        <Card>
          <div className="flex items-center justify-between gap-3">
            <div>
              <p className="text-sm font-medium text-textMuted">Historial</p>
              <h2 className="mt-1 text-lg font-bold">Actividad reciente del proyecto</h2>
            </div>
            <Button variant="secondary" onClick={reloadActivity}>
              <Clock3 size={16} />
              Actualizar
            </Button>
          </div>
          <div className="mt-5">
            <ActivityFeed items={actividad} />
          </div>
        </Card>
      )}

      <div className="grid gap-4 lg:grid-cols-3">
        <Card className="p-4">
          <p className="text-xs font-semibold uppercase tracking-wide text-textMuted">Pendientes</p>
          <p className="mt-2 text-2xl font-extrabold">{resumen.pendientes}</p>
        </Card>
        <Card className="p-4">
          <p className="text-xs font-semibold uppercase tracking-wide text-textMuted">En revision</p>
          <p className="mt-2 text-2xl font-extrabold">{resumen.enRevision}</p>
        </Card>
        <Card className="p-4">
          <p className="text-xs font-semibold uppercase tracking-wide text-textMuted">Completadas</p>
          <p className="mt-2 text-2xl font-extrabold text-success">{resumen.completadas}</p>
        </Card>
      </div>

      <TaskFormModal
        open={taskModalOpen}
        task={editingTask}
        projects={proyecto ? [proyecto] : []}
        saving={busy}
        title={editingTask ? 'Editar tarea del proyecto' : 'Nueva tarea del proyecto'}
        lockProject
        onClose={() => {
          setTaskModalOpen(false)
          setEditingTask(null)
        }}
        onSave={saveTask}
      />
      <TaskCommentsModal
        task={commentTask}
        onClose={() => {
          setCommentTask(null)
          loadDetail()
        }}
      />
    </div>
  )
}

export default ProjectDetail
