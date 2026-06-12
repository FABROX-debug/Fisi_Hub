import {
  AlertCircle,
  CheckCircle2,
  CheckSquare2,
  FolderKanban,
} from 'lucide-react'
import Badge from '../components/ui/Badge'
import Card from '../components/ui/Card'
import ProgressBar from '../components/ui/ProgressBar'

const stats = [
  {
    label: 'Proyectos activos',
    value: 6,
    detail: 'en seguimiento',
    icon: FolderKanban,
    color: 'text-info',
    border: 'border-l-info',
    background: 'bg-blue-50',
  },
  {
    label: 'Tareas pendientes',
    value: 8,
    detail: 'esta semana',
    icon: CheckSquare2,
    color: 'text-warning',
    border: 'border-l-warning',
    background: 'bg-amber-50',
  },
  {
    label: 'Completadas',
    value: 24,
    detail: 'este mes',
    icon: CheckCircle2,
    color: 'text-success',
    border: 'border-l-success',
    background: 'bg-emerald-50',
  },
  {
    label: 'Vencidas',
    value: 2,
    detail: 'requieren atencion',
    icon: AlertCircle,
    color: 'text-danger',
    border: 'border-l-danger',
    background: 'bg-red-50',
  },
]

function Dashboard() {
  return (
    <div className="space-y-6">
      <div>
        <p className="text-sm font-medium text-accent">Resumen de trabajo</p>
        <h2 className="mt-1 text-2xl font-extrabold tracking-tight sm:text-3xl">
          Hola, Fabrizio
        </h2>
        <p className="mt-2 text-textMuted">
          Esta es una vista temporal del dashboard del MVP.
        </p>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {stats.map((stat) => {
          const Icon = stat.icon
          return (
            <Card
              key={stat.label}
              className={`border-l-4 p-5 ${stat.border}`}
            >
              <div className="flex items-start justify-between gap-3">
                <div>
                  <p className="text-sm font-medium text-textMuted">
                    {stat.label}
                  </p>
                  <p className="mt-3 text-3xl font-extrabold">{stat.value}</p>
                  <p className="mt-1 text-xs text-textMuted">{stat.detail}</p>
                </div>
                <span
                  className={`grid h-10 w-10 place-items-center rounded-lg ${stat.background} ${stat.color}`}
                >
                  <Icon size={20} />
                </span>
              </div>
            </Card>
          )
        })}
      </div>

      <div className="grid gap-6 lg:grid-cols-[minmax(0,1.4fr)_minmax(280px,0.6fr)]">
        <Card>
          <div className="flex flex-wrap items-start justify-between gap-4">
            <div>
              <p className="text-sm font-medium text-textMuted">
                Proyecto destacado
              </p>
              <h3 className="mt-1 text-xl font-bold">Sistema FISIHUB</h3>
              <p className="mt-2 text-sm text-textMuted">
                Base visual y estructura inicial del gestor de proyectos.
              </p>
            </div>
            <div className="flex flex-wrap gap-2">
              <Badge value="en-proceso" />
              <Badge value="alta" />
            </div>
          </div>

          <ProgressBar
            value={70}
            label="Avance del proyecto"
            className="mt-8"
          />

          <div className="mt-6 grid gap-3 border-t border-border pt-5 text-sm sm:grid-cols-3">
            <div>
              <p className="text-textMuted">Estado</p>
              <p className="mt-1 font-semibold">En proceso</p>
            </div>
            <div>
              <p className="text-textMuted">Prioridad</p>
              <p className="mt-1 font-semibold">Alta</p>
            </div>
            <div>
              <p className="text-textMuted">Responsable</p>
              <p className="mt-1 font-semibold">Fabrizio</p>
            </div>
          </div>
        </Card>

        <Card>
          <p className="text-sm font-medium text-textMuted">
            Estados disponibles
          </p>
          <h3 className="mt-1 text-lg font-bold">Badges de ejemplo</h3>
          <div className="mt-5 flex flex-wrap gap-2">
            <Badge value="pendiente" />
            <Badge value="en-proceso" />
            <Badge value="revision" />
            <Badge value="completada" />
            <Badge value="bloqueada" />
          </div>
          <p className="mt-6 text-sm font-medium text-textMuted">Prioridades</p>
          <div className="mt-3 flex flex-wrap gap-2">
            <Badge value="baja" />
            <Badge value="media" />
            <Badge value="alta" />
            <Badge value="urgente" />
          </div>
        </Card>
      </div>
    </div>
  )
}

export default Dashboard

