import {
  closestCorners,
  DndContext,
  DragOverlay,
  KeyboardSensor,
  PointerSensor,
  useSensor,
  useSensors,
} from '@dnd-kit/core'
import { Columns3 } from 'lucide-react'
import { useEffect, useMemo, useState } from 'react'
import KanbanColumn from '../components/kanban/KanbanColumn'
import Badge from '../components/ui/Badge'
import Card from '../components/ui/Card'
import ProgressBar from '../components/ui/ProgressBar'
import Toast from '../components/ui/Toast'
import useTaskStore from '../store/taskStore'

const columns = [
  { id: 'PENDIENTE', title: 'Pendiente' },
  { id: 'EN_PROCESO', title: 'En proceso' },
  { id: 'EN_REVISION', title: 'En revision' },
  { id: 'COMPLETADA', title: 'Completada' },
  { id: 'BLOQUEADA', title: 'Bloqueada' },
]

function Kanban() {
  const {
    tareas,
    proyectos,
    loading,
    saving,
    error,
    loadData,
    changeStatus,
    clearError,
  } = useTaskStore()
  const [projectId, setProjectId] = useState('')
  const [activeTask, setActiveTask] = useState(null)
  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: { distance: 8 },
    }),
    useSensor(KeyboardSensor),
  )

  useEffect(() => {
    loadData()
  }, [loadData])

  useEffect(() => {
    if (proyectos.length === 0) {
      setProjectId('')
      return
    }
    const projectStillExists = proyectos.some(
      (project) => String(project.id) === projectId,
    )
    if (!projectStillExists) setProjectId(String(proyectos[0].id))
  }, [projectId, proyectos])

  const selectedProject = useMemo(
    () => proyectos.find((project) => String(project.id) === projectId),
    [projectId, proyectos],
  )
  const projectTasks = useMemo(
    () => tareas.filter((task) => String(task.proyectoId) === projectId),
    [projectId, tareas],
  )

  const handleStatusChange = async (taskId, status) => {
    const task = tareas.find((item) => item.id === taskId)
    if (!task || task.estado === status) return
    try {
      await changeStatus(taskId, status)
    } catch {
      // The store error is rendered as a toast.
    }
  }

  const handleDragStart = ({ active }) => {
    setActiveTask(active.data.current?.task ?? null)
  }

  const handleDragEnd = async ({ active, over }) => {
    setActiveTask(null)
    if (!over) return
    await handleStatusChange(Number(active.id), String(over.id))
  }

  return (
    <div className="space-y-6">
      <div>
        <p className="text-sm font-semibold text-accent">Flujo visual</p>
        <h1 className="text-2xl font-extrabold">Tablero Kanban</h1>
        <p className="mt-1 text-sm text-textMuted">
          Arrastra las tareas entre columnas o usa el selector de estado.
        </p>
      </div>

      <Card className="grid gap-5 lg:grid-cols-[minmax(0,1fr)_minmax(18rem,0.7fr)]">
        <label className="text-sm font-semibold">
          Proyecto
          <select
            value={projectId}
            onChange={(event) => setProjectId(event.target.value)}
            disabled={loading || proyectos.length === 0}
            className="mt-1.5 w-full rounded-lg border border-border bg-white px-3.5 py-2.5 outline-none focus:border-accent focus:ring-2 focus:ring-violet-100"
          >
            {proyectos.length === 0 && (
              <option value="">Sin proyectos disponibles</option>
            )}
            {proyectos.map((project) => (
              <option key={project.id} value={project.id}>
                {project.nombre}
              </option>
            ))}
          </select>
        </label>
        {selectedProject && (
          <ProgressBar
            value={selectedProject.porcentajeAvance}
            label={`Avance de ${selectedProject.nombre}`}
          />
        )}
      </Card>

      {error && (
        <button type="button" onClick={clearError} className="w-full text-left">
          <Toast
            variant="error"
            title="No se pudo actualizar el tablero"
            message={error}
            className="max-w-none"
          />
        </button>
      )}

      {loading ? (
        <Card>
          <p className="text-sm text-textMuted">Cargando tablero...</p>
        </Card>
      ) : proyectos.length === 0 ? (
        <Card className="grid place-items-center py-14 text-center">
          <Columns3 size={42} className="text-accent" />
          <h2 className="mt-4 text-lg font-bold">No hay proyectos</h2>
          <p className="mt-1 max-w-md text-sm text-textMuted">
            Crea un proyecto y sus tareas para comenzar a usar el Kanban.
          </p>
        </Card>
      ) : (
        <DndContext
          sensors={sensors}
          collisionDetection={closestCorners}
          onDragStart={handleDragStart}
          onDragCancel={() => setActiveTask(null)}
          onDragEnd={handleDragEnd}
        >
          <div className="grid grid-flow-col auto-cols-max gap-4 overflow-x-auto pb-4 lg:auto-cols-fr">
            {columns.map((column) => (
              <KanbanColumn
                key={column.id}
                id={column.id}
                title={column.title}
                tasks={projectTasks.filter(
                  (task) => task.estado === column.id,
                )}
                saving={saving}
                onStatusChange={handleStatusChange}
              />
            ))}
          </div>
          <DragOverlay>
            {activeTask ? (
              <div className="w-72 rotate-2 rounded-xl border border-border bg-white p-4 shadow-xl">
                <p className="text-xs font-semibold uppercase tracking-wide text-accent">
                  {activeTask.proyectoNombre}
                </p>
                <h3 className="mt-1 font-bold">{activeTask.titulo}</h3>
                <Badge
                  value={activeTask.prioridad.toLowerCase()}
                  className="mt-3"
                >
                  {activeTask.prioridad}
                </Badge>
              </div>
            ) : null}
          </DragOverlay>
        </DndContext>
      )}
    </div>
  )
}

export default Kanban
