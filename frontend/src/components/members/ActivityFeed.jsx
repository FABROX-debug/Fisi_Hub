import {
  CheckSquare2,
  FolderPlus,
  MessageSquare,
  RefreshCw,
  UserPlus,
} from 'lucide-react'

const icons = {
  PROYECTO_CREADO: FolderPlus,
  TAREA_CREADA: CheckSquare2,
  ESTADO_TAREA_CAMBIADO: RefreshCw,
  MIEMBRO_AGREGADO: UserPlus,
  COMENTARIO_CREADO: MessageSquare,
}

const formatDateTime = (value) => new Intl.DateTimeFormat('es-PE', {
  dateStyle: 'medium',
  timeStyle: 'short',
}).format(new Date(value))

function ActivityFeed({ items }) {
  if (items.length === 0) {
    return (
      <p className="rounded-xl border border-dashed border-border p-6 text-center text-sm text-textMuted">
        Todavia no hay actividad registrada en este proyecto.
      </p>
    )
  }

  return (
    <div className="space-y-1">
      {items.map((item) => {
        const Icon = icons[item.tipo] || RefreshCw
        return (
          <article key={item.id} className="flex gap-3 rounded-xl p-3 hover:bg-violet-50/50">
            <div className="grid size-9 shrink-0 place-items-center rounded-full bg-violet-100 text-accent">
              <Icon size={17} />
            </div>
            <div className="min-w-0">
              <p className="text-sm font-medium">{item.descripcion}</p>
              <p className="mt-0.5 text-xs text-textMuted">
                {formatDateTime(item.fecha)}
              </p>
            </div>
          </article>
        )
      })}
    </div>
  )
}

export default ActivityFeed
