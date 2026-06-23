import { ShieldCheck, Trash2, UserRound } from 'lucide-react'
import { useState } from 'react'
import Badge from '../ui/Badge'
import Button from '../ui/Button'
import Input from '../ui/Input'

const initials = (name) => name
  .split(/\s+/)
  .slice(0, 2)
  .map((part) => part[0])
  .join('')
  .toUpperCase()

function MemberCard({
  member,
  canManage,
  busy,
  onRoleChange,
  onRemove,
  onQuickTaskCreate,
  quickTaskSaving,
}) {
  const [quickOpen, setQuickOpen] = useState(false)
  const [quickTitle, setQuickTitle] = useState('')
  const [quickPriority, setQuickPriority] = useState('MEDIA')
  const [quickDueDate, setQuickDueDate] = useState('')
  const [quickError, setQuickError] = useState('')

  const isHighLoad = member.tareasActivas >= 5
  const hasNoTasks = member.tareasActivas === 0

  const resetQuickForm = () => {
    setQuickOpen(false)
    setQuickTitle('')
    setQuickPriority('MEDIA')
    setQuickDueDate('')
    setQuickError('')
  }

  const handleQuickSubmit = async (event) => {
    event.preventDefault()
    const title = quickTitle.trim()
    if (!title) {
      setQuickError('Escribe un titulo corto para la tarea')
      return
    }

    try {
      await onQuickTaskCreate(member, {
        titulo: title,
        prioridad: quickPriority,
        fechaLimite: quickDueDate || null,
      })
      resetQuickForm()
    } catch (requestError) {
      setQuickError(requestError.message)
    }
  }

  return (
    <article className="rounded-xl border border-border bg-white p-4">
      <div className="flex items-start gap-3">
        <div className="grid size-11 shrink-0 place-items-center rounded-full bg-violet-100 font-bold text-accent">
          {initials(member.nombre)}
        </div>
        <div className="min-w-0 flex-1">
          <div className="flex flex-wrap items-center gap-2">
            <h3 className="truncate font-bold">{member.nombre}</h3>
            {member.liderDesignado && (
              <ShieldCheck
                size={17}
                className="text-accent"
                aria-label="Lider designado"
              />
            )}
          </div>
          <p className="truncate text-sm text-textMuted">{member.correo}</p>
        </div>
        {canManage && !member.liderDesignado && (
          <button
            type="button"
            aria-label={`Quitar a ${member.nombre}`}
            disabled={busy}
            onClick={() => onRemove(member)}
            className="rounded-lg p-2 text-textMuted hover:bg-red-50 hover:text-danger disabled:opacity-50"
          >
            <Trash2 size={17} />
          </button>
        )}
      </div>

      <div className="mt-4 flex flex-wrap items-end justify-between gap-3">
        <div>
          <p className="mb-1 text-xs font-semibold uppercase tracking-wide text-textMuted">
            Rol interno
          </p>
          {canManage ? (
            <select
              aria-label={`Rol de ${member.nombre}`}
              value={member.rol}
              disabled={busy}
              onChange={(event) => onRoleChange(member, event.target.value)}
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
        <div className="text-right">
          <span className="flex items-center gap-1.5 text-sm text-textMuted">
            <UserRound size={15} />
            {member.tareasActivas} tareas activas
          </span>
          {hasNoTasks && (
            <Badge value="completada" className="mt-1">Sin tareas</Badge>
          )}
          {isHighLoad && (
            <Badge value="alta" className="mt-1">Carga alta</Badge>
          )}
          {!member.activo && (
            <Badge value="bloqueada" className="mt-1">Cuenta inactiva</Badge>
          )}
        </div>
      </div>

      {canManage && member.activo && onQuickTaskCreate && (
        <div className="mt-3 space-y-3">
          <Button
            type="button"
            variant="secondary"
            disabled={busy || quickTaskSaving}
            className="w-full"
            onClick={() => {
              if (quickOpen) {
                resetQuickForm()
                return
              }
              setQuickOpen(true)
              setQuickError('')
            }}
          >
            {quickOpen ? 'Cancelar tarea rapida' : 'Crear tarea rapida'}
          </Button>

          {quickOpen && (
            <form
              className="space-y-3 rounded-xl border border-violet-100 bg-violet-50/50 p-3"
              onSubmit={handleQuickSubmit}
            >
              <Input
                id={`quick-task-${member.usuarioId}`}
                label="Titulo"
                required
                maxLength="180"
                value={quickTitle}
                onChange={(event) => setQuickTitle(event.target.value)}
              />
              <div className="grid gap-3 sm:grid-cols-2">
                <label className="text-sm font-medium">
                  Prioridad
                  <select
                    value={quickPriority}
                    onChange={(event) => setQuickPriority(event.target.value)}
                    className="mt-1.5 w-full rounded-lg border border-border bg-white px-3 py-2.5 text-sm outline-none focus:border-accent"
                  >
                    <option value="BAJA">Baja</option>
                    <option value="MEDIA">Media</option>
                    <option value="ALTA">Alta</option>
                    <option value="URGENTE">Urgente</option>
                  </select>
                </label>
                <Input
                  id={`quick-task-date-${member.usuarioId}`}
                  label="Fecha limite"
                  type="date"
                  min={new Date().toISOString().slice(0, 10)}
                  value={quickDueDate}
                  onChange={(event) => setQuickDueDate(event.target.value)}
                />
              </div>
              {quickError && (
                <p className="text-sm text-danger">{quickError}</p>
              )}
              <div className="flex justify-end gap-2">
                <Button
                  type="button"
                  variant="secondary"
                  disabled={quickTaskSaving}
                  onClick={resetQuickForm}
                >
                  Cancelar
                </Button>
                <Button type="submit" disabled={busy || quickTaskSaving}>
                  {quickTaskSaving ? 'Creando...' : 'Crear tarea'}
                </Button>
              </div>
            </form>
          )}
        </div>
      )}
    </article>
  )
}

export default MemberCard
