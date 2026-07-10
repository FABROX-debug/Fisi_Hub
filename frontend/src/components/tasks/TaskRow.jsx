import {
  CalendarDays,
  Edit3,
  MessageSquare,
  Trash2,
  UserRound,
} from 'lucide-react'
import { Link } from 'react-router-dom'
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
    year: 'numeric',
    timeZone: 'UTC',
  }).format(new Date(`${value}T00:00:00Z`))
}

function TaskRow({
  task,
  saving,
  onEdit,
  onDelete,
  onComments,
  onStatusChange,
}) {
  return (
    <tr className="border-b border-border last:border-0 hover:bg-violet-50/40">
      <td className="min-w-64 px-4 py-4">
        <Link
          to={`/tareas/${task.id}`}
          state={{ from: '/tareas' }}
          className="font-semibold hover:text-accent hover:underline"
        >
          {task.titulo}
        </Link>
        <Link
          to={`/proyectos/${task.proyectoId}`}
          className="mt-1 inline-flex text-xs text-textMuted hover:text-accent hover:underline"
        >
          {task.proyectoNombre}
        </Link>
      </td>
      <td className="px-4 py-4">
        <span className="flex items-center gap-2 text-sm text-textMuted">
          <UserRound size={16} />
          {task.responsableNombre || 'Sin responsable'}
        </span>
      </td>
      <td className="px-4 py-4">
        <Badge value={task.prioridad.toLowerCase()}>{task.prioridad}</Badge>
      </td>
      <td className="px-4 py-4">
        <div className="flex flex-col gap-2">
          <Badge value={stateBadge[task.estado]}>
            {task.estado.replaceAll('_', ' ')}
          </Badge>
          <select
            aria-label={`Cambiar estado de ${task.titulo}`}
            disabled={saving || !task.puedeCambiarEstado}
            value={task.estado}
            onChange={(event) => onStatusChange(task.id, event.target.value)}
            className="rounded-lg border border-border bg-white px-2 py-1.5 text-xs outline-none focus:border-accent"
          >
            <option value="PENDIENTE">Pendiente</option>
            <option value="EN_PROCESO">En proceso</option>
            <option value="EN_REVISION">En revision</option>
            <option value="COMPLETADA">Completada</option>
            <option value="BLOQUEADA">Bloqueada</option>
          </select>
        </div>
      </td>
      <td className="px-4 py-4">
        <span className="flex items-center gap-2 whitespace-nowrap text-sm text-textMuted">
          <CalendarDays size={16} />
          {formatDate(task.fechaLimite)}
        </span>
      </td>
      <td className="px-4 py-4">
        <div className="flex justify-end gap-1">
          <button
            type="button"
            aria-label={`Comentarios de ${task.titulo}`}
            className="rounded-lg p-2 text-textMuted hover:bg-violet-50 hover:text-accent"
            onClick={() => onComments(task)}
          >
            <MessageSquare size={17} />
          </button>
          {task.puedeEditar && (
            <button
              type="button"
              aria-label={`Editar ${task.titulo}`}
              className="rounded-lg p-2 text-textMuted hover:bg-violet-50 hover:text-accent"
              onClick={() => onEdit(task)}
            >
              <Edit3 size={17} />
            </button>
          )}
          {task.puedeEliminar && (
            <button
              type="button"
              aria-label={`Eliminar ${task.titulo}`}
              className="rounded-lg p-2 text-textMuted hover:bg-red-50 hover:text-danger"
              onClick={() => onDelete(task)}
            >
              <Trash2 size={17} />
            </button>
          )}
        </div>
      </td>
    </tr>
  )
}

export default TaskRow
