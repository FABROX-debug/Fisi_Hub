import { FolderKanban, Plus } from 'lucide-react'
import { useEffect, useMemo, useState } from 'react'
import ProjectCard from '../components/projects/ProjectCard'
import ProjectFormModal from '../components/projects/ProjectFormModal'
import Button from '../components/ui/Button'
import Card from '../components/ui/Card'
import ConfirmDialog from '../components/ui/ConfirmDialog'
import Toast from '../components/ui/Toast'
import useWorkspaceStore from '../store/workspaceStore'

function Projects() {
  const {
    espacios,
    proyectos,
    loading,
    saving,
    error,
    loadAll,
    saveProyecto,
    removeProyecto,
    clearError,
  } = useWorkspaceStore()
  const [filter, setFilter] = useState('TODOS')
  const [editing, setEditing] = useState(null)
  const [formOpen, setFormOpen] = useState(false)
  const [deleting, setDeleting] = useState(null)

  useEffect(() => {
    loadAll()
  }, [loadAll])

  const visibleProjects = useMemo(
    () => filter === 'TODOS'
      ? proyectos
      : proyectos.filter((project) => project.estado === filter),
    [filter, proyectos],
  )
  const manageableSpaces = useMemo(
    () => espacios.filter((workspace) => workspace.puedeGestionar),
    [espacios],
  )

  const confirmDelete = async () => {
    try {
      await removeProyecto(deleting.id)
      setDeleting(null)
    } catch {
      // The page toast renders the store error.
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
        <div>
          <p className="text-sm font-semibold text-accent">Trabajo activo</p>
          <h2 className="text-2xl font-extrabold">Proyectos</h2>
          <p className="mt-1 text-sm text-textMuted">
            Consulta estado, prioridad, fechas y avance de tus proyectos.
          </p>
        </div>
        <Button
          disabled={manageableSpaces.length === 0}
          title={manageableSpaces.length === 0
            ? 'Necesitas liderar un espacio para crear proyectos'
            : undefined}
          onClick={() => {
            setEditing(null)
            setFormOpen(true)
          }}
        >
          <Plus size={18} />
          Nuevo proyecto
        </Button>
      </div>

      <div className="flex flex-wrap gap-2">
        {[
          ['TODOS', 'Todos'],
          ['PLANIFICADO', 'Planificados'],
          ['EN_PROCESO', 'En proceso'],
          ['EN_REVISION', 'En revision'],
          ['FINALIZADO', 'Finalizados'],
        ].map(([value, label]) => (
          <button
            type="button"
            key={value}
            onClick={() => setFilter(value)}
            className={`rounded-full px-4 py-2 text-sm font-semibold transition ${
              filter === value
                ? 'bg-accent text-white'
                : 'border border-border bg-white text-textMuted hover:border-accent hover:text-accent'
            }`}
          >
            {label}
          </button>
        ))}
      </div>

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
          <p className="text-sm text-textMuted">Cargando proyectos...</p>
        </Card>
      ) : visibleProjects.length === 0 ? (
        <Card className="grid place-items-center py-14 text-center">
          <FolderKanban size={42} className="text-accent" />
          <h3 className="mt-4 text-lg font-bold">No hay proyectos para mostrar</h3>
          <p className="mt-1 text-sm text-textMuted">
            {espacios.length === 0
              ? 'Crea un espacio de trabajo antes de registrar un proyecto.'
              : 'Crea un proyecto o cambia el filtro seleccionado.'}
          </p>
        </Card>
      ) : (
        <div className="grid gap-5 md:grid-cols-2 xl:grid-cols-3">
          {visibleProjects.map((project) => (
            <ProjectCard
              key={project.id}
              project={project}
              onEdit={(item) => {
                setEditing(item)
                setFormOpen(true)
              }}
              onDelete={setDeleting}
            />
          ))}
        </div>
      )}

      <ProjectFormModal
        open={formOpen}
        project={editing}
        workspaces={manageableSpaces}
        saving={saving}
        onClose={() => setFormOpen(false)}
        onSave={saveProyecto}
      />
      <ConfirmDialog
        open={Boolean(deleting)}
        title="Eliminar proyecto"
        message="El proyecto se eliminara de forma permanente."
        busy={saving}
        onCancel={() => setDeleting(null)}
        onConfirm={confirmDelete}
      />
    </div>
  )
}

export default Projects
