import { CalendarClock, UserRound } from 'lucide-react'
import Badge from '../ui/Badge'

const stateBadge = {
  PENDIENTE: 'pendiente',
  EN_PROCESO: 'en-proceso',
  EN_REVISION: 'revision',
  COMPLETADA: 'completada',
  BLOQUEADA: 'bloqueada',
}

const formatDate = (value) => {
  if (!value) return 'Sin fecha'
  return new Intl.DateTimeFormat('es-PE', {
    day: '2-digit',
    month: 'short',
    timeZone: 'UTC',
  }).format(new Date(`${value}T00:00:00Z`))
}

function DashboardTaskItem({ task, overdue = false }) {
  return (
    <article className="rounded-xl border border-border bg-white p-4">
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0">
          <p className="truncate font-semibold">{task.titulo}</p>
          <p className="mt-1 text-xs text-textMuted">{task.proyectoNombre}</p>
        </div>
        <Badge value={task.prioridad.toLowerCase()}>
          {task.prioridad}
        </Badge>
      </div>

      <div className="mt-3 flex flex-wrap items-center gap-2">
        <Badge value={stateBadge[task.estado]}>
          {task.estado.replaceAll('_', ' ')}
        </Badge>
        <span
          className={`flex items-center gap-1 text-xs font-medium ${overdue ? 'text-danger' : 'text-textMuted'}`}
        >
          <CalendarClock size={14} />
          {overdue ? 'Vencio ' : 'Vence '}
          {formatDate(task.fechaLimite)}
        </span>
      </div>

      <p className="mt-3 flex items-center gap-1.5 text-xs text-textMuted">
        <UserRound size={14} />
        {task.responsableNombre || 'Sin responsable'}
      </p>
    </article>
  )
}

export default DashboardTaskItem
