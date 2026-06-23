import { useDraggable } from '@dnd-kit/core'
import {
  CalendarDays,
  GripVertical,
  UserRound,
} from 'lucide-react'
import Badge from '../ui/Badge'

const stateBadge = {
  PENDIENTE: 'pendiente',
  EN_PROCESO: 'en-proceso',
  EN_REVISION: 'revision',
  COMPLETADA: 'completada',
  BLOQUEADA: 'bloqueada',
}

const formatDate = (value) => {
  if (!value) return 'Sin fecha limite'
  return new Intl.DateTimeFormat('es-PE', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    timeZone: 'UTC',
  }).format(new Date(`${value}T00:00:00Z`))
}

function KanbanCard({
  task,
  disabled = false,
  onStatusChange,
}) {
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    isDragging,
  } = useDraggable({
    id: String(task.id),
    data: { task },
    disabled: disabled || !task.puedeCambiarEstado,
  })
  const style = transform
    ? { transform: `translate3d(${transform.x}px, ${transform.y}px, 0)` }
    : undefined
  const urgentClasses = task.prioridad === 'URGENTE'
    ? 'relative before:absolute before:inset-y-3 before:left-0 before:w-1 before:animate-pulse before:rounded-r-full before:bg-danger'
    : ''

  return (
    <article
      ref={setNodeRef}
      style={style}
      className={`rounded-xl border border-border bg-white p-4 shadow-sm transition duration-200 hover:-translate-y-0.5 hover:shadow-md ${urgentClasses} ${isDragging ? 'opacity-30' : ''}`}
    >
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0">
          <p className="text-xs font-semibold uppercase tracking-wide text-accent">
            {task.proyectoNombre}
          </p>
          <h3 className="mt-1 break-words font-bold text-textPrimary">
            {task.titulo}
          </h3>
        </div>
        <button
          type="button"
          aria-label={`Arrastrar ${task.titulo}`}
          disabled={disabled || !task.puedeCambiarEstado}
          className="shrink-0 cursor-grab rounded-lg p-1.5 text-textMuted hover:bg-violet-50 hover:text-accent active:cursor-grabbing disabled:cursor-not-allowed"
          {...attributes}
          {...listeners}
        >
          <GripVertical size={18} />
        </button>
      </div>

      <div className="mt-3 flex flex-wrap gap-2">
        <Badge value={task.prioridad.toLowerCase()}>
          {task.prioridad}
        </Badge>
        <Badge value={stateBadge[task.estado]}>
          {task.estado.replaceAll('_', ' ')}
        </Badge>
      </div>

      <div className="mt-4 space-y-2 text-xs text-textMuted">
        <p className="flex items-center gap-2">
          <UserRound size={15} />
          {task.responsableNombre || 'Sin responsable'}
        </p>
        <p className="flex items-center gap-2">
          <CalendarDays size={15} />
          {formatDate(task.fechaLimite)}
        </p>
      </div>

      <label className="mt-4 block text-xs font-semibold text-textMuted">
        Cambiar estado
        <select
          aria-label={`Cambiar estado de ${task.titulo}`}
          disabled={disabled || !task.puedeCambiarEstado}
          value={task.estado}
          onChange={(event) => onStatusChange(task.id, event.target.value)}
          className="mt-1.5 w-full rounded-lg border border-border bg-white px-2.5 py-2 text-xs text-textPrimary outline-none focus:border-accent focus:ring-2 focus:ring-violet-100"
        >
          <option value="PENDIENTE">Pendiente</option>
          <option value="EN_PROCESO">En proceso</option>
          <option value="EN_REVISION">En revision</option>
          <option value="COMPLETADA">Completada</option>
          <option value="BLOQUEADA">Bloqueada</option>
        </select>
      </label>
    </article>
  )
}

export default KanbanCard
