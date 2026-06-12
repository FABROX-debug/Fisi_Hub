import { CalendarDays, Edit3, Trash2, Users } from 'lucide-react'
import Badge from '../ui/Badge'
import Card from '../ui/Card'
import ProgressBar from '../ui/ProgressBar'

const statusBadge = {
  PLANIFICADO: 'pendiente',
  EN_PROCESO: 'en-proceso',
  EN_REVISION: 'revision',
  FINALIZADO: 'completada',
  CANCELADO: 'bloqueada',
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

function ProjectCard({ project, onEdit, onDelete }) {
  return (
    <Card className="flex h-full flex-col transition hover:-translate-y-0.5 hover:shadow-md">
      <div className="flex items-start justify-between gap-3">
        <div className="flex flex-wrap gap-2">
          <Badge value={statusBadge[project.estado]}>
            {project.estado.replaceAll('_', ' ')}
          </Badge>
          <Badge value={project.prioridad.toLowerCase()}>
            {project.prioridad}
          </Badge>
        </div>
        <div className="flex gap-1">
          <button
            type="button"
            aria-label={`Editar ${project.nombre}`}
            className="rounded-lg p-2 text-textMuted hover:bg-violet-50 hover:text-accent"
            onClick={() => onEdit(project)}
          >
            <Edit3 size={17} />
          </button>
          <button
            type="button"
            aria-label={`Eliminar ${project.nombre}`}
            className="rounded-lg p-2 text-textMuted hover:bg-red-50 hover:text-danger"
            onClick={() => onDelete(project)}
          >
            <Trash2 size={17} />
          </button>
        </div>
      </div>

      <p className="mt-4 text-xs font-semibold uppercase tracking-wide text-accent">
        {project.espacioNombre}
      </p>
      <h3 className="mt-1 text-lg font-bold">{project.nombre}</h3>
      <p className="mt-2 line-clamp-2 min-h-10 text-sm text-textMuted">
        {project.descripcion || 'Sin descripcion'}
      </p>

      <div className="mt-5 grid grid-cols-2 gap-3 text-xs text-textMuted">
        <span className="flex items-center gap-1.5">
          <CalendarDays size={15} />
          {formatDate(project.fechaInicio)}
        </span>
        <span className="flex items-center justify-end gap-1.5">
          <Users size={15} />
          {project.cantidadMiembros} miembro(s)
        </span>
      </div>
      <p className="mt-2 text-xs text-textMuted">
        Entrega: {formatDate(project.fechaFin)}
      </p>

      <ProgressBar
        value={project.porcentajeAvance}
        label="Avance"
        className="mt-auto pt-5"
      />
    </Card>
  )
}

export default ProjectCard
