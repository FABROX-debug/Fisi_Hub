import { useEffect, useState } from 'react'
import Button from '../ui/Button'
import Input from '../ui/Input'
import Modal from '../ui/Modal'
import Toast from '../ui/Toast'

const initialForm = {
  nombre: '',
  descripcion: '',
  color: '#6D28D9',
  icono: 'folder',
}

function WorkspaceFormModal({
  open,
  workspace,
  saving,
  onClose,
  onSave,
}) {
  const [form, setForm] = useState(initialForm)
  const [error, setError] = useState('')

  useEffect(() => {
    setForm(workspace ? {
      nombre: workspace.nombre,
      descripcion: workspace.descripcion || '',
      color: workspace.color,
      icono: workspace.icono,
    } : initialForm)
    setError('')
  }, [workspace, open])

  const submit = async (event) => {
    event.preventDefault()
    if (form.nombre.trim().length < 3) {
      setError('El nombre debe tener al menos 3 caracteres')
      return
    }
    try {
      await onSave({ ...form, nombre: form.nombre.trim() }, workspace?.id)
      onClose()
    } catch (requestError) {
      setError(requestError.message)
    }
  }

  return (
    <Modal
      open={open}
      title={workspace ? 'Editar espacio' : 'Nuevo espacio'}
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
          id="workspace-name"
          label="Nombre del espacio"
          required
          value={form.nombre}
          onChange={(event) =>
            setForm((current) => ({ ...current, nombre: event.target.value }))
          }
        />
        <div>
          <label
            htmlFor="workspace-description"
            className="mb-1.5 block text-sm font-medium"
          >
            Descripcion
          </label>
          <textarea
            id="workspace-description"
            rows="4"
            maxLength="500"
            value={form.descripcion}
            onChange={(event) =>
              setForm((current) => ({
                ...current,
                descripcion: event.target.value,
              }))
            }
            className="w-full rounded-lg border border-border px-3.5 py-2.5 text-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/15"
          />
        </div>
        <div className="grid gap-5 sm:grid-cols-2">
          <Input
            id="workspace-color"
            label="Color"
            type="color"
            value={form.color}
            onChange={(event) =>
              setForm((current) => ({ ...current, color: event.target.value }))
            }
            className="h-11"
          />
          <Input
            id="workspace-icon"
            label="Icono"
            value={form.icono}
            onChange={(event) =>
              setForm((current) => ({ ...current, icono: event.target.value }))
            }
          />
        </div>
        <div className="flex justify-end gap-3">
          <Button type="button" variant="secondary" onClick={onClose}>
            Cancelar
          </Button>
          <Button type="submit" disabled={saving}>
            {saving ? 'Guardando...' : 'Guardar espacio'}
          </Button>
        </div>
      </form>
    </Modal>
  )
}

export default WorkspaceFormModal
