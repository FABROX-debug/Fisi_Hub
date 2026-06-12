import { CheckSquare2, Plus } from 'lucide-react'
import { useEffect, useMemo, useState } from 'react'
import TaskFormModal from '../components/tasks/TaskFormModal'
import TaskRow from '../components/tasks/TaskRow'
import Button from '../components/ui/Button'
import Card from '../components/ui/Card'
import ConfirmDialog from '../components/ui/ConfirmDialog'
import Toast from '../components/ui/Toast'
import useTaskStore from '../store/taskStore'

function Tasks() {
  const {
    tareas,
    proyectos,
    loading,
    saving,
    error,
    loadData,
    saveTask,
    changeStatus,
    removeTask,
    clearError,
  } = useTaskStore()
  const [filters, setFilters] = useState({
    estado: '',
    prioridad: '',
    proyectoId: '',
  })
  const [editing, setEditing] = useState(null)
  const [formOpen, setFormOpen] = useState(false)
  const [deleting, setDeleting] = useState(null)

  useEffect(() => {
    loadData()
  }, [loadData])

  const visibleTasks = useMemo(
    () => tareas.filter((task) =>
      (!filters.estado || task.estado === filters.estado)
      && (!filters.prioridad || task.prioridad === filters.prioridad)
      && (!filters.proyectoId
        || String(task.proyectoId) === filters.proyectoId)),
    [filters, tareas],
  )

  const updateFilter = (field) => (event) => {
    setFilters((current) => ({ ...current, [field]: event.target.value }))
  }

  const confirmDelete = async () => {
    try {
      await removeTask(deleting.id)
      setDeleting(null)
    } catch {
      // The store error is rendered as a toast.
    }
  }

  const handleStatusChange = async (id, status) => {
    try {
      await changeStatus(id, status)
    } catch {
      // The store error is rendered as a toast.
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
        <div>
          <p className="text-sm font-semibold text-accent">Ejecucion</p>
          <h2 className="text-2xl font-extrabold">Tareas</h2>
          <p className="mt-1 text-sm text-textMuted">
            Organiza responsables, prioridades, estados y fechas limite.
          </p>
        </div>
        <Button
          disabled={proyectos.length === 0}
          title={proyectos.length === 0
            ? 'Crea primero un proyecto'
            : undefined}
          onClick={() => {
            setEditing(null)
            setFormOpen(true)
          }}
        >
          <Plus size={18} />
          Nueva tarea
        </Button>
      </div>

      <Card className="grid gap-4 md:grid-cols-3">
        <label className="text-sm font-medium">
          Estado
          <select
            value={filters.estado}
            onChange={updateFilter('estado')}
            className="mt-1.5 w-full rounded-lg border border-border bg-white px-3.5 py-2.5 outline-none focus:border-accent"
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
          Prioridad
          <select
            value={filters.prioridad}
            onChange={updateFilter('prioridad')}
            className="mt-1.5 w-full rounded-lg border border-border bg-white px-3.5 py-2.5 outline-none focus:border-accent"
          >
            <option value="">Todas</option>
            <option value="BAJA">Baja</option>
            <option value="MEDIA">Media</option>
            <option value="ALTA">Alta</option>
            <option value="URGENTE">Urgente</option>
          </select>
        </label>
        <label className="text-sm font-medium">
          Proyecto
          <select
            value={filters.proyectoId}
            onChange={updateFilter('proyectoId')}
            className="mt-1.5 w-full rounded-lg border border-border bg-white px-3.5 py-2.5 outline-none focus:border-accent"
          >
            <option value="">Todos</option>
            {proyectos.map((project) => (
              <option key={project.id} value={project.id}>
                {project.nombre}
              </option>
            ))}
          </select>
        </label>
      </Card>

      {error && (
        <button type="button" onClick={clearError} className="w-full text-left">
          <Toast
            variant="error"
            title="No se pudo completar la operacion"
            message={error}
            className="max-w-none"
          />
        </button>
      )}

      {loading ? (
        <Card>
          <p className="text-sm text-textMuted">Cargando tareas...</p>
        </Card>
      ) : visibleTasks.length === 0 ? (
        <Card className="grid place-items-center py-14 text-center">
          <CheckSquare2 size={42} className="text-accent" />
          <h3 className="mt-4 text-lg font-bold">No hay tareas para mostrar</h3>
          <p className="mt-1 max-w-md text-sm text-textMuted">
            {proyectos.length === 0
              ? 'Crea un proyecto antes de registrar tareas.'
              : 'Crea una tarea o cambia los filtros seleccionados.'}
          </p>
        </Card>
      ) : (
        <Card className="overflow-hidden p-0 sm:p-0">
          <div className="overflow-x-auto">
            <table className="w-full text-left">
              <thead className="bg-slate-50 text-xs uppercase tracking-wide text-textMuted">
                <tr>
                  <th className="px-4 py-3">Tarea</th>
                  <th className="px-4 py-3">Responsable</th>
                  <th className="px-4 py-3">Prioridad</th>
                  <th className="px-4 py-3">Estado</th>
                  <th className="px-4 py-3">Vence</th>
                  <th className="px-4 py-3 text-right">Acciones</th>
                </tr>
              </thead>
              <tbody>
                {visibleTasks.map((task) => (
                  <TaskRow
                    key={task.id}
                    task={task}
                    saving={saving}
                    onEdit={(item) => {
                      setEditing(item)
                      setFormOpen(true)
                    }}
                    onDelete={setDeleting}
                    onStatusChange={handleStatusChange}
                  />
                ))}
              </tbody>
            </table>
          </div>
        </Card>
      )}

      <TaskFormModal
        open={formOpen}
        task={editing}
        projects={proyectos}
        saving={saving}
        onClose={() => setFormOpen(false)}
        onSave={saveTask}
      />
      <ConfirmDialog
        open={Boolean(deleting)}
        title="Eliminar tarea"
        message="La tarea se eliminara de forma permanente y el avance del proyecto se recalculara."
        busy={saving}
        onCancel={() => setDeleting(null)}
        onConfirm={confirmDelete}
      />
    </div>
  )
}

export default Tasks
