import { BarChart3, CheckCircle2, Clock3, ListTodo } from 'lucide-react'
import { useEffect, useState } from 'react'
import Card from '../components/ui/Card'
import ProgressBar from '../components/ui/ProgressBar'
import Toast from '../components/ui/Toast'
import { getProyectos } from '../services/proyectoService'
import { getProjectProgressReport } from '../services/reporteService'

function Reports() {
  const [projects, setProjects] = useState([])
  const [projectId, setProjectId] = useState('')
  const [report, setReport] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const loadProjects = async () => {
      try {
        const result = await getProyectos()
        setProjects(result)
        setProjectId(result[0] ? String(result[0].id) : '')
      } catch (requestError) {
        setError(requestError.message)
      } finally {
        setLoading(false)
      }
    }
    loadProjects()
  }, [])

  useEffect(() => {
    if (!projectId) {
      setReport(null)
      return
    }
    const loadReport = async () => {
      setLoading(true)
      setError('')
      try {
        setReport(await getProjectProgressReport(projectId))
      } catch (requestError) {
        setError(requestError.message)
      } finally {
        setLoading(false)
      }
    }
    loadReport()
  }, [projectId])

  const stats = report ? [
    ['Total tareas', report.totalTareas, <ListTodo size={24} />, 'text-info'],
    ['Completadas', report.tareasCompletadas, <CheckCircle2 size={24} />, 'text-success'],
    ['Pendientes', report.tareasPendientes, <Clock3 size={24} />, 'text-warning'],
    ['Vencidas', report.tareasVencidas, <BarChart3 size={24} />, 'text-danger'],
  ] : []
  const maxCompleted = report
    ? Math.max(1, ...report.productividadMiembros.map(
      (member) => member.tareasCompletadas,
    ))
    : 1

  return (
    <div className="space-y-6">
      <div>
        <p className="text-sm font-semibold text-accent">Analisis</p>
        <h1 className="text-2xl font-extrabold">Reporte de avance</h1>
        <p className="mt-1 text-sm text-textMuted">
          Estado real y productividad del equipo por proyecto.
        </p>
      </div>

      <Card>
        <label className="text-sm font-semibold">
          Proyecto
          <select
            value={projectId}
            disabled={loading || projects.length === 0}
            onChange={(event) => setProjectId(event.target.value)}
            className="mt-1.5 w-full rounded-lg border border-border bg-white px-3.5 py-2.5 outline-none focus:border-accent"
          >
            {projects.length === 0 && <option value="">Sin proyectos disponibles</option>}
            {projects.map((project) => (
              <option key={project.id} value={project.id}>{project.nombre}</option>
            ))}
          </select>
        </label>
      </Card>

      {error && (
        <Toast
          variant="error"
          title="No se pudo cargar el reporte"
          message={error}
          className="max-w-none"
        />
      )}

      {loading ? (
        <div className="grid gap-4 sm:grid-cols-2">
          {[0, 1, 2, 3].map((item) => (
            <div key={item} className="h-32 animate-pulse rounded-xl bg-white" />
          ))}
        </div>
      ) : !report ? (
        <Card className="grid place-items-center py-14 text-center">
          <BarChart3 size={42} className="text-accent" />
          <h2 className="mt-4 text-lg font-bold">No hay datos para reportar</h2>
          <p className="mt-1 text-sm text-textMuted">
            Crea un proyecto para consultar su avance.
          </p>
        </Card>
      ) : (
        <>
          <Card>
            <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
              <div>
                <p className="text-sm text-textMuted">Proyecto seleccionado</p>
                <h2 className="text-xl font-bold">{report.proyectoNombre}</h2>
              </div>
              <div className="w-full sm:max-w-md">
                <ProgressBar
                  value={report.porcentajeAvance}
                  label="Avance general"
                />
              </div>
            </div>
          </Card>

          <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
            {stats.map(([label, value, icon, color]) => (
              <Card key={label}>
                <span className={color}>{icon}</span>
                <p className="mt-4 text-3xl font-extrabold">{value}</p>
                <p className="text-sm text-textMuted">{label}</p>
              </Card>
            ))}
          </div>

          <div className="grid gap-6 xl:grid-cols-2">
            <Card>
              <h2 className="text-lg font-bold">Distribucion por estado</h2>
              <div className="mt-5 space-y-4">
                {[
                  ['Pendientes', report.tareasPendientes, 'bg-slate-500'],
                  ['En proceso', report.tareasEnProceso, 'bg-info'],
                  ['En revision', report.tareasEnRevision, 'bg-warning'],
                  ['Completadas', report.tareasCompletadas, 'bg-success'],
                  ['Bloqueadas', report.tareasBloqueadas, 'bg-danger'],
                ].map(([label, value, color]) => (
                  <div key={label}>
                    <div className="mb-1 flex justify-between text-sm">
                      <span>{label}</span>
                      <span className="font-semibold">{value}</span>
                    </div>
                    <div className="h-2 overflow-hidden rounded-full bg-slate-100">
                      <div
                        className={`h-full rounded-full ${color}`}
                        style={{
                          width: `${report.totalTareas
                            ? (value / report.totalTareas) * 100
                            : 0}%`,
                        }}
                      />
                    </div>
                  </div>
                ))}
              </div>
            </Card>

            <Card>
              <h2 className="text-lg font-bold">Productividad por miembro</h2>
              {report.productividadMiembros.length === 0 ? (
                <p className="mt-5 text-sm text-textMuted">
                  No hay miembros para mostrar.
                </p>
              ) : (
                <div className="mt-5 space-y-4">
                  {report.productividadMiembros.map((member) => (
                    <div key={member.usuarioId}>
                      <div className="mb-1 flex justify-between gap-3 text-sm">
                        <span className="truncate font-medium">{member.nombre}</span>
                        <span className="whitespace-nowrap text-textMuted">
                          {member.tareasCompletadas}/{member.tareasAsignadas} completadas
                        </span>
                      </div>
                      <div className="h-3 overflow-hidden rounded-full bg-slate-100">
                        <div
                          className="h-full rounded-full bg-gradient-to-r from-primary to-accent"
                          style={{
                            width: `${(member.tareasCompletadas / maxCompleted) * 100}%`,
                          }}
                        />
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </Card>
          </div>
        </>
      )}
    </div>
  )
}

export default Reports
