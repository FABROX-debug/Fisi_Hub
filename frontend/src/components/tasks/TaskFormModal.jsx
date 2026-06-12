import { useEffect, useMemo, useState } from 'react'
import Button from '../ui/Button'
import Input from '../ui/Input'
import Modal from '../ui/Modal'
import Toast from '../ui/Toast'

const initialForm = {
  titulo: '',
  descripcion: '',
  proyectoId: '',
  responsableId: '',
  fechaLimite: '',
  estado: 'PENDIENTE',
  prioridad: 'MEDIA',
}

function TaskFormModal({
  open,
  task,
  projects,
  saving,
  onClose,
  onSave,
}) {
  const [form, setForm] = useState(initialForm)
  const [error, setError] = useState('')

  useEffect(() => {
    setForm(task ? {
      titulo: task.titulo,
      descripcion: task.descripcion || '',
      proyectoId: String(task.proyectoId),
      responsableId: task.responsableId
        ? String(task.responsableId)
        : '',
      fechaLimite: task.fechaLimite || '',
      estado: task.estado,
      prioridad: task.prioridad,
    } : {
      ...initialForm,
      proyectoId: projects[0] ? String(projects[0].id) : '',
    })
    setError('')
  }, [open, projects, task])

  const selectedProject = useMemo(
    () => projects.find((project) => String(project.id) === form.proyectoId),
    [form.proyectoId, projects],
  )

  const update = (field) => (event) => {
    const value = event.target.value
    setForm((current) => ({
      ...current,
      [field]: value,
      ...(field === 'proyectoId' ? { responsableId: '' } : {}),
    }))
  }

  const submit = async (event) => {
    event.preventDefault()
    if (!form.proyectoId) {
      setError('Selecciona un proyecto')
      return
    }
    if (form.fechaLimite) {
      const today = new Date().toISOString().slice(0, 10)
      if (form.fechaLimite < today) {
        setError('La fecha limite no puede estar en el pasado')
        return
      }
    }

    const commonPayload = {
      titulo: form.titulo.trim(),
      descripcion: form.descripcion.trim() || null,
      responsableId: form.responsableId
        ? Number(form.responsableId)
        : null,
      fechaLimite: form.fechaLimite || null,
      estado: form.estado,
      prioridad: form.prioridad,
    }

    try {
      await onSave(
        task
          ? commonPayload
          : { ...commonPayload, proyectoId: Number(form.proyectoId) },
        task?.id,
      )
      onClose()
    } catch (requestError) {
      setError(requestError.message)
    }
  }

  return (
    <Modal
      open={open}
      title={task ? 'Editar tarea' : 'Nueva tarea'}
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
          id="task-title"
          label="Titulo"
          required
          maxLength="180"
          value={form.titulo}
          onChange={update('titulo')}
        />
        <div>
          <label
            htmlFor="task-description"
            className="mb-1.5 block text-sm font-medium"
          >
            Descripcion
          </label>
          <textarea
            id="task-description"
            rows="4"
            maxLength="2000"
            value={form.descripcion}
            onChange={update('descripcion')}
            className="w-full rounded-lg border border-border px-3.5 py-2.5 text-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/15"
          />
        </div>
        <div className="grid gap-5 sm:grid-cols-2">
          <label className="text-sm font-medium">
            Proyecto
            <select
              id="task-project"
              required
              disabled={Boolean(task)}
              value={form.proyectoId}
              onChange={update('proyectoId')}
              className="mt-1.5 w-full rounded-lg border border-border bg-white px-3.5 py-2.5 outline-none focus:border-accent disabled:bg-slate-100"
            >
              <option value="">Selecciona un proyecto</option>
              {projects.map((project) => (
                <option key={project.id} value={project.id}>
                  {project.nombre}
                </option>
              ))}
            </select>
          </label>
          <label className="text-sm font-medium">
            Responsable
            <select
              id="task-assignee"
              value={form.responsableId}
              onChange={update('responsableId')}
              className="mt-1.5 w-full rounded-lg border border-border bg-white px-3.5 py-2.5 outline-none focus:border-accent"
            >
              <option value="">Sin responsable</option>
              {selectedProject && (
                <option value={selectedProject.liderId}>
                  {selectedProject.liderNombre}
                </option>
              )}
            </select>
          </label>
        </div>
        <div className="grid gap-5 sm:grid-cols-3">
          <Input
            id="task-due-date"
            label="Fecha limite"
            type="date"
            min={new Date().toISOString().slice(0, 10)}
            value={form.fechaLimite}
            onChange={update('fechaLimite')}
          />
          <label className="text-sm font-medium">
            Estado
            <select
              id="task-status"
              value={form.estado}
              onChange={update('estado')}
              className="mt-1.5 w-full rounded-lg border border-border bg-white px-3.5 py-2.5 outline-none focus:border-accent"
            >
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
              id="task-priority"
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
          <Button variant="secondary" onClick={onClose}>
            Cancelar
          </Button>
          <Button type="submit" disabled={saving || projects.length === 0}>
            {saving ? 'Guardando...' : 'Guardar tarea'}
          </Button>
        </div>
      </form>
    </Modal>
  )
}

export default TaskFormModal
