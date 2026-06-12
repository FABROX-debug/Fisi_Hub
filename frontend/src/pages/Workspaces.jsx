import { Edit3, FolderOpen, Plus, Trash2, Users } from 'lucide-react'
import { useEffect, useState } from 'react'
import WorkspaceFormModal from '../components/workspaces/WorkspaceFormModal'
import Button from '../components/ui/Button'
import Card from '../components/ui/Card'
import ConfirmDialog from '../components/ui/ConfirmDialog'
import Toast from '../components/ui/Toast'
import useWorkspaceStore from '../store/workspaceStore'

function Workspaces() {
  const {
    espacios,
    loading,
    saving,
    error,
    loadAll,
    saveEspacio,
    removeEspacio,
    clearError,
  } = useWorkspaceStore()
  const [editing, setEditing] = useState(null)
  const [formOpen, setFormOpen] = useState(false)
  const [deleting, setDeleting] = useState(null)

  useEffect(() => {
    loadAll()
  }, [loadAll])

  const openCreate = () => {
    setEditing(null)
    setFormOpen(true)
  }

  const confirmDelete = async () => {
    try {
      await removeEspacio(deleting.id)
      setDeleting(null)
    } catch {
      // The store exposes the API error through the page toast.
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
        <div>
          <p className="text-sm font-semibold text-accent">Organizacion</p>
          <h2 className="text-2xl font-extrabold">Espacios de trabajo</h2>
          <p className="mt-1 text-sm text-textMuted">
            Agrupa proyectos y conserva el contexto de cada equipo.
          </p>
        </div>
        <Button onClick={openCreate}>
          <Plus size={18} />
          Nuevo espacio
        </Button>
      </div>

      {error && (
        <button type="button" onClick={clearError} className="text-left">
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
          <p className="text-sm text-textMuted">Cargando espacios...</p>
        </Card>
      ) : espacios.length === 0 ? (
        <Card className="grid place-items-center py-14 text-center">
          <FolderOpen size={42} className="text-accent" />
          <h3 className="mt-4 text-lg font-bold">Crea tu primer espacio</h3>
          <p className="mt-1 max-w-md text-sm text-textMuted">
            Los proyectos deben pertenecer a un espacio de trabajo.
          </p>
          <Button className="mt-5" onClick={openCreate}>
            Crear espacio
          </Button>
        </Card>
      ) : (
        <div className="grid gap-4 lg:grid-cols-2">
          {espacios.map((workspace) => (
            <Card key={workspace.id} className="flex items-center gap-4">
              <span
                className="grid h-12 w-12 shrink-0 place-items-center rounded-xl text-white"
                style={{ backgroundColor: workspace.color }}
              >
                <FolderOpen size={23} />
              </span>
              <div className="min-w-0 flex-1">
                <h3 className="truncate font-bold">{workspace.nombre}</h3>
                <p className="mt-1 line-clamp-1 text-sm text-textMuted">
                  {workspace.descripcion || 'Sin descripcion'}
                </p>
                <p className="mt-2 flex gap-4 text-xs text-textMuted">
                  <span className="flex items-center gap-1">
                    <Users size={14} /> {workspace.cantidadMiembros} miembro(s)
                  </span>
                  <span>{workspace.cantidadProyectos} proyecto(s)</span>
                </p>
              </div>
              <div className="flex shrink-0 gap-1">
                <button
                  type="button"
                  aria-label={`Editar ${workspace.nombre}`}
                  className="rounded-lg p-2 text-textMuted hover:bg-violet-50 hover:text-accent"
                  onClick={() => {
                    setEditing(workspace)
                    setFormOpen(true)
                  }}
                >
                  <Edit3 size={18} />
                </button>
                <button
                  type="button"
                  aria-label={`Eliminar ${workspace.nombre}`}
                  className="rounded-lg p-2 text-textMuted hover:bg-red-50 hover:text-danger"
                  onClick={() => setDeleting(workspace)}
                >
                  <Trash2 size={18} />
                </button>
              </div>
            </Card>
          ))}
        </div>
      )}

      <WorkspaceFormModal
        open={formOpen}
        workspace={editing}
        saving={saving}
        onClose={() => setFormOpen(false)}
        onSave={saveEspacio}
      />
      <ConfirmDialog
        open={Boolean(deleting)}
        title="Eliminar espacio"
        message="Se eliminaran tambien sus proyectos. Esta accion no se puede deshacer."
        busy={saving}
        onCancel={() => setDeleting(null)}
        onConfirm={confirmDelete}
      />
    </div>
  )
}

export default Workspaces
