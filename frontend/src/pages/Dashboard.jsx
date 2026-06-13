import {
  AlertCircle,
  ArrowRight,
  CalendarCheck2,
  CheckCircle2,
  CheckSquare2,
  FolderKanban,
} from 'lucide-react'
import { useCallback, useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import DashboardProjectCard from '../components/dashboard/DashboardProjectCard'
import DashboardTaskItem from '../components/dashboard/DashboardTaskItem'
import StatCard from '../components/dashboard/StatCard'
import Button from '../components/ui/Button'
import Card from '../components/ui/Card'
import ProgressBar from '../components/ui/ProgressBar'
import Toast from '../components/ui/Toast'
import { getDashboardResumen } from '../services/dashboardService'
import useAuthStore from '../store/authStore'

const statConfig = [
  {
    key: 'totalProyectosActivos',
    label: 'Proyectos activos',
    detail: 'en seguimiento',
    icon: FolderKanban,
    color: 'text-info',
    border: 'border-l-info',
    background: 'bg-blue-50',
  },
  {
    key: 'tareasPendientes',
    label: 'Tareas pendientes',
    detail: 'por completar',
    icon: CheckSquare2,
    color: 'text-warning',
    border: 'border-l-warning',
    background: 'bg-amber-50',
  },
  {
    key: 'tareasCompletadas',
    label: 'Completadas',
    detail: 'en tus proyectos',
    icon: CheckCircle2,
    color: 'text-success',
    border: 'border-l-success',
    background: 'bg-emerald-50',
  },
  {
    key: 'tareasVencidas',
    label: 'Vencidas',
    detail: 'requieren atencion',
    icon: AlertCircle,
    color: 'text-danger',
    border: 'border-l-danger',
    background: 'bg-red-50',
  },
]

function Dashboard() {
  const user = useAuthStore((state) => state.user)
  const navigate = useNavigate()
  const [summary, setSummary] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const loadSummary = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      setSummary(await getDashboardResumen())
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    loadSummary()
  }, [loadSummary])

  const greetingDetail = useMemo(() => {
    if (!summary) return ''
    if (summary.tareasParaHoy > 0 && summary.tareasVencidas > 0) {
      return `Tienes ${summary.tareasParaHoy} tarea(s) para hoy y ${summary.tareasVencidas} vencida(s).`
    }
    if (summary.tareasParaHoy > 0) {
      return `Tienes ${summary.tareasParaHoy} tarea(s) con fecha limite hoy.`
    }
    if (summary.tareasVencidas > 0) {
      return `Tienes ${summary.tareasVencidas} tarea(s) vencida(s) que requieren atencion.`
    }
    return 'Todo al dia. No tienes tareas para hoy ni vencidas.'
  }, [summary])

  if (loading) {
    return (
      <div className="space-y-6" aria-label="Cargando dashboard">
        <div className="h-24 animate-pulse rounded-xl bg-white" />
        <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
          {[0, 1, 2, 3].map((item) => (
            <div
              key={item}
              className="h-36 animate-pulse rounded-xl bg-white"
            />
          ))}
        </div>
        <div className="h-80 animate-pulse rounded-xl bg-white" />
      </div>
    )
  }

  if (error) {
    return (
      <div className="space-y-4">
        <Toast
          variant="error"
          title="No se pudo cargar el dashboard"
          message={error}
          className="max-w-none"
        />
        <Button onClick={loadSummary}>Reintentar</Button>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div>
        <p className="text-sm font-medium text-accent">Resumen de trabajo</p>
        <h1 className="mt-1 text-2xl font-extrabold tracking-tight sm:text-3xl">
          Hola, {user?.nombre ?? 'Usuario'}
        </h1>
        <p className="mt-2 text-textMuted">{greetingDetail}</p>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {statConfig.map((stat) => (
          <StatCard
            key={stat.key}
            {...stat}
            value={summary[stat.key]}
          />
        ))}
      </div>

      <div className="grid gap-6 xl:grid-cols-[minmax(0,1.35fr)_minmax(19rem,0.65fr)]">
        <div className="space-y-6">
          <Card>
            <div className="flex items-center justify-between gap-4">
              <div>
                <p className="text-sm font-medium text-textMuted">
                  Seguimiento
                </p>
                <h2 className="mt-1 text-lg font-bold">
                  Proyectos activos recientes
                </h2>
              </div>
              <Button
                variant="ghost"
                onClick={() => navigate('/proyectos')}
              >
                Ver proyectos
                <ArrowRight size={16} />
              </Button>
            </div>

            {summary.proyectosActivosRecientes.length === 0 ? (
              <div className="grid place-items-center py-12 text-center">
                <FolderKanban size={38} className="text-accent" />
                <h3 className="mt-3 font-bold">No hay proyectos activos</h3>
                <p className="mt-1 max-w-sm text-sm text-textMuted">
                  Los proyectos planificados o en curso apareceran aqui.
                </p>
              </div>
            ) : (
              <div className="mt-5 space-y-3">
                {summary.proyectosActivosRecientes.map((project) => (
                  <DashboardProjectCard
                    key={project.id}
                    project={project}
                  />
                ))}
              </div>
            )}
          </Card>

          <Card>
            <div className="flex items-center justify-between gap-4">
              <div>
                <p className="text-sm font-medium text-textMuted">
                  Proximos 3 dias
                </p>
                <h2 className="mt-1 text-lg font-bold">
                  Tareas proximas
                </h2>
              </div>
              <Button variant="ghost" onClick={() => navigate('/tareas')}>
                Ver tareas
                <ArrowRight size={16} />
              </Button>
            </div>

            {summary.tareasProximas.length === 0 ? (
              <div className="grid place-items-center py-10 text-center">
                <CalendarCheck2 size={36} className="text-success" />
                <h3 className="mt-3 font-bold">Sin entregas proximas</h3>
                <p className="mt-1 text-sm text-textMuted">
                  No hay tareas pendientes con fecha en los proximos 3 dias.
                </p>
              </div>
            ) : (
              <div className="mt-5 grid gap-3 md:grid-cols-2">
                {summary.tareasProximas.map((task) => (
                  <DashboardTaskItem key={task.id} task={task} />
                ))}
              </div>
            )}
          </Card>
        </div>

        <div className="space-y-6">
          <Card>
            <p className="text-sm font-medium text-textMuted">
              Avance general
            </p>
            <h2 className="mt-1 text-lg font-bold">Promedio de proyectos</h2>
            <ProgressBar
              value={summary.porcentajePromedioAvance}
              label="Avance promedio"
              className="mt-6"
            />
          </Card>

          <Card>
            <div className="flex items-center justify-between gap-3">
              <div>
                <p className="text-sm font-medium text-textMuted">Atencion</p>
                <h2 className="mt-1 text-lg font-bold">Tareas vencidas</h2>
              </div>
              <span className="rounded-full bg-red-100 px-3 py-1 font-mono text-sm font-bold text-danger">
                {summary.tareasVencidas}
              </span>
            </div>

            {summary.tareasVencidasDetalle.length === 0 ? (
              <div className="py-9 text-center">
                <CheckCircle2
                  size={36}
                  className="mx-auto text-success"
                />
                <p className="mt-3 font-semibold">No hay tareas vencidas</p>
              </div>
            ) : (
              <div className="mt-5 space-y-3">
                {summary.tareasVencidasDetalle.map((task) => (
                  <DashboardTaskItem
                    key={task.id}
                    task={task}
                    overdue
                  />
                ))}
              </div>
            )}
          </Card>
        </div>
      </div>
    </div>
  )
}

export default Dashboard
