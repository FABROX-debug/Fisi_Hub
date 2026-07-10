import { CalendarDays, UserRound } from 'lucide-react'
import Badge from '../ui/Badge'
import ProgressBar from '../ui/ProgressBar'

const statusBadge = {
  PLANIFICADO: 'pendiente',
  EN_PROCESO: 'en-proceso',
  EN_REVISION: 'revision',
  FINALIZADO: 'completada',
  CANCELADO: 'bloqueada',
}

const formatDate = (value) => {
  if (!value) return 'Sin fecha de entrega'
  return new Intl.DateTimeFormat('es-PE', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    timeZone: 'UTC',
  }).format(new Date(`${value}T00:00:00Z`))
}

function DashboardProjectCard({ project, onOpen }) {
  return (
    <article
      className={`rounded-xl border border-border bg-white p-4 transition hover:-translate-y-0.5 hover:shadow-md ${onOpen ? 'cursor-pointer' : ''}`}
      onClick={onOpen}
    >
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wide text-accent">
            {project.espacioNombre}
          </p>
          <h3 className="mt-1 font-bold">{project.nombre}</h3>
        </div>
        <div className="flex flex-wrap gap-2">
          <Badge value={statusBadge[project.estado]}>
            {project.estado.replaceAll('_', ' ')}
          </Badge>
          <Badge value={project.prioridad.toLowerCase()}>
            {project.prioridad}
          </Badge>
        </div>
      </div>

      <div className="mt-4 flex flex-wrap gap-x-5 gap-y-2 text-xs text-textMuted">
        <span className="flex items-center gap-1.5">
          <CalendarDays size={15} />
          {formatDate(project.fechaFin)}
        </span>
        <span className="flex items-center gap-1.5">
          <UserRound size={15} />
          {project.liderNombre}
        </span>
      </div>

      <ProgressBar
        value={project.porcentajeAvance}
        label="Avance"
        className="mt-4"
      />
    </article>
  )
}

export default DashboardProjectCard
