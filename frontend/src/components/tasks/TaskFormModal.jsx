import { useEffect, useMemo, useState } from 'react'
import Button from '../ui/Button'
import Input from '../ui/Input'
import Modal from '../ui/Modal'
import Toast from '../ui/Toast'
import { getProjectMembers } from '../../services/miembroService'
import useAuthStore from '../../store/authStore'

const initialForm = {
  titulo: '',
  descripcion: '',
  proyectoId: '',
  responsableId: '',
  fechaLimite: '',
  estado: 'PENDIENTE',
  prioridad: 'MEDIA',
}

const toText = (value) => (value == null ? '' : String(value))

const createFormState = ({ task, projects, initialValues }) => {
  if (task?.id) {
    return {
      titulo: task.titulo,
      descripcion: task.descripcion || '',
      proyectoId: toText(task.proyectoId),
      responsableId: toText(task.responsableId),
      fechaLimite: task.fechaLimite || '',
      estado: task.estado,
      prioridad: task.prioridad,
    }
  }

  return {
    ...initialForm,
    titulo: initialValues?.titulo || '',
    descripcion: initialValues?.descripcion || '',
    proyectoId: toText(initialValues?.proyectoId || projects[0]?.id || ''),
    responsableId: toText(initialValues?.responsableId),
    fechaLimite: initialValues?.fechaLimite || '',
    estado: initialValues?.estado || initialForm.estado,
    prioridad: initialValues?.prioridad || initialForm.prioridad,
  }
}

function TaskFormModal({
  open,
  task,
  initialValues,
  projects,
  saving,
  title,
  lockProject = false,
  lockAssignee = false,
  onClose,
  onSave,
}) {
  const [form, setForm] = useState(initialForm)
  const [error, setError] = useState('')
  const [members, setMembers] = useState([])
  const [loadingMembers, setLoadingMembers] = useState(false)
  const user = useAuthStore((state) => state.user)
  const isEditing = Boolean(task?.id)
  const today = new Date().toISOString().slice(0, 10)

  useEffect(() => {
    setForm(createFormState({ task, projects, initialValues }))
    setError('')
  }, [initialValues, open, projects, task])

  const selectedProject = useMemo(
    () => projects.find((project) => String(project.id) === form.proyectoId),
    [form.proyectoId, projects],
  )

  useEffect(() => {
    if (!open || !form.proyectoId) {
      setMembers([])
      return
    }
    let active = true
    setLoadingMembers(true)
    getProjectMembers(form.proyectoId)
      .then((response) => {
        if (active) setMembers(response.miembros.filter((member) => member.activo))
      })
      .catch((requestError) => {
        if (active) setError(requestError.message)
      })
      .finally(() => {
        if (active) setLoadingMembers(false)
      })
    return () => {
      active = false
    }
  }, [form.proyectoId, open])

  const assignableMembers = useMemo(() => {
    if (task?.puedeReasignar || selectedProject?.puedeGestionar) return members
    return members.filter((member) => member.usuarioId === user?.id)
  }, [members, selectedProject, task, user])

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
      const unchangedPastDueDate = isEditing
        && task?.fechaLimite
        && task.fechaLimite < today
        && form.fechaLimite === task.fechaLimite
      if (form.fechaLimite < today && !unchangedPastDueDate) {
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
        task?.id
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
      title={title || (isEditing ? 'Editar tarea' : 'Nueva tarea')}
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
              disabled={isEditing || lockProject}
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
              disabled={lockAssignee || (isEditing && !task.puedeReasignar)}
              onChange={update('responsableId')}
              className="mt-1.5 w-full rounded-lg border border-border bg-white px-3.5 py-2.5 outline-none focus:border-accent disabled:bg-slate-100"
            >
              <option value="">
                {loadingMembers ? 'Cargando miembros...' : 'Sin responsable'}
              </option>
              {assignableMembers.map((member) => (
                <option key={member.usuarioId} value={member.usuarioId}>
                  {member.nombre} - {member.rol}
                </option>
              ))}
            </select>
          </label>
        </div>
        <div className="grid gap-5 sm:grid-cols-3">
          <Input
            id="task-due-date"
            label="Fecha limite"
            type="date"
            min={isEditing && form.fechaLimite && form.fechaLimite < today
              ? undefined
              : today}
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
