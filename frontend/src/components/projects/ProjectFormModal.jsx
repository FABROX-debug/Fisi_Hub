import { useEffect, useState } from 'react'
import Button from '../ui/Button'
import Input from '../ui/Input'
import Modal from '../ui/Modal'
import Toast from '../ui/Toast'

const initialForm = {
  nombre: '',
  descripcion: '',
  espacioId: '',
  fechaInicio: '',
  fechaFin: '',
  estado: 'PLANIFICADO',
  prioridad: 'MEDIA',
}

function ProjectFormModal({
  open,
  project,
  workspaces,
  saving,
  onClose,
  onSave,
}) {
  const [form, setForm] = useState(initialForm)
  const [error, setError] = useState('')

  useEffect(() => {
    setForm(project ? {
      nombre: project.nombre,
      descripcion: project.descripcion || '',
      espacioId: String(project.espacioId),
      fechaInicio: project.fechaInicio || '',
      fechaFin: project.fechaFin || '',
      estado: project.estado,
      prioridad: project.prioridad,
    } : {
      ...initialForm,
      espacioId: workspaces[0] ? String(workspaces[0].id) : '',
    })
    setError('')
  }, [project, workspaces, open])

  const update = (field) => (event) => {
    setForm((current) => ({ ...current, [field]: event.target.value }))
  }

  const submit = async (event) => {
    event.preventDefault()
    if (!form.espacioId) {
      setError('Primero debes crear o seleccionar un espacio')
      return
    }
    if (form.fechaInicio && form.fechaFin && form.fechaFin < form.fechaInicio) {
      setError('La fecha de fin no puede ser anterior a la fecha de inicio')
      return
    }
    try {
      await onSave({
        ...form,
        nombre: form.nombre.trim(),
        espacioId: Number(form.espacioId),
        fechaInicio: form.fechaInicio || null,
        fechaFin: form.fechaFin || null,
      }, project?.id)
      onClose()
    } catch (requestError) {
      setError(requestError.message)
    }
  }

  return (
    <Modal
      open={open}
      title={project ? 'Editar proyecto' : 'Nuevo proyecto'}
      onClose={onClose}
    >
      {error && (
        <Toast
          variant="error"
          title="No se pudo guardar"
          message={error}
          className="mb-5 max-w-none"
        />
      )}
      <form className="space-y-5" onSubmit={submit}>
        <Input
          id="project-name"
          label="Nombre"
          required
          value={form.nombre}
          onChange={update('nombre')}
        />
        <div>
          <label
            htmlFor="project-description"
            className="mb-1.5 block text-sm font-medium"
          >
            Descripcion
          </label>
          <textarea
            id="project-description"
            rows="4"
            maxLength="1000"
            value={form.descripcion}
            onChange={update('descripcion')}
            className="w-full rounded-lg border border-border px-3.5 py-2.5 text-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/15"
          />
        </div>
        <div>
          <label
            htmlFor="project-workspace"
            className="mb-1.5 block text-sm font-medium"
          >
            Espacio
          </label>
          <select
            id="project-workspace"
            required
            disabled={Boolean(project)}
            value={form.espacioId}
            onChange={update('espacioId')}
            className="w-full rounded-lg border border-border bg-white px-3.5 py-2.5 text-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/15 disabled:bg-slate-100"
          >
            <option value="">Selecciona un espacio</option>
            {workspaces.map((workspace) => (
              <option key={workspace.id} value={workspace.id}>
                {workspace.nombre}
              </option>
            ))}
          </select>
        </div>
        <div className="grid gap-5 sm:grid-cols-2">
          <Input
            id="project-start"
            label="Fecha de inicio"
            type="date"
            value={form.fechaInicio}
            onChange={update('fechaInicio')}
          />
          <Input
            id="project-end"
            label="Fecha de fin"
            type="date"
            value={form.fechaFin}
            onChange={update('fechaFin')}
          />
        </div>
        <div className="grid gap-5 sm:grid-cols-2">
          <label className="text-sm font-medium">
            Estado
            <select
              value={form.estado}
              onChange={update('estado')}
              className="mt-1.5 w-full rounded-lg border border-border bg-white px-3.5 py-2.5 outline-none focus:border-accent"
            >
              <option value="PLANIFICADO">Planificado</option>
              <option value="EN_PROCESO">En proceso</option>
              <option value="EN_REVISION">En revision</option>
              <option value="FINALIZADO">Finalizado</option>
              <option value="CANCELADO">Cancelado</option>
            </select>
          </label>
          <label className="text-sm font-medium">
            Prioridad
            <select
              value={form.prioridad}
              onChange={update('prioridad')}
              className="mt-1.5 w-full rounded-lg border border-border bg-white px-3.5 py-2.5 outline-none focus:border-accent"
            >
              <option value="BAJA">Baja</option>
              <option value="MEDIA">Media</option>
              <option value="ALTA">Alta</option>
              <option value="URGENTE">Urgente</option>
            </select>
          </label>
        </div>
        <div className="flex justify-end gap-3">
          <Button type="button" variant="secondary" onClick={onClose}>
            Cancelar
          </Button>
          <Button type="submit" disabled={saving || workspaces.length === 0}>
            {saving ? 'Guardando...' : 'Guardar proyecto'}
          </Button>
        </div>
      </form>
    </Modal>
  )
}

export default ProjectFormModal
