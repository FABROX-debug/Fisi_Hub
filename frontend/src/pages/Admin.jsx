import {
  CheckCircle2,
  FolderKanban,
  ListChecks,
  ShieldCheck,
  UsersRound,
} from 'lucide-react'
import { useEffect, useState } from 'react'
import Button from '../components/ui/Button'
import Card from '../components/ui/Card'
import ConfirmDialog from '../components/ui/ConfirmDialog'
import Toast from '../components/ui/Toast'
import {
  activateUser,
  deactivateUser,
  getAdminProjects,
  getAdminStats,
  getAdminUsers,
} from '../services/adminService'
import useAuthStore from '../store/authStore'

function Admin() {
  const currentUser = useAuthStore((state) => state.user)
  const [users, setUsers] = useState([])
  const [projects, setProjects] = useState([])
  const [stats, setStats] = useState(null)
  const [loading, setLoading] = useState(true)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const [target, setTarget] = useState(null)

  useEffect(() => {
    const load = async () => {
      try {
        const [userData, projectData, statData] = await Promise.all([
          getAdminUsers(),
          getAdminProjects(),
          getAdminStats(),
        ])
        setUsers(userData)
        setProjects(projectData)
        setStats(statData)
      } catch (requestError) {
        setError(requestError.message)
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [])

  const toggleUser = async () => {
    setBusy(true)
    setError('')
    try {
      const updated = target.activo
        ? await deactivateUser(target.id)
        : await activateUser(target.id)
      setUsers((current) => current.map((user) =>
        user.id === updated.id ? updated : user))
      setStats((current) => ({
        ...current,
        usuariosActivos: current.usuariosActivos + (updated.activo ? 1 : -1),
      }))
      setTarget(null)
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setBusy(false)
    }
  }

  const statCards = stats ? [
    ['Usuarios', stats.totalUsuarios, <UsersRound size={24} />, 'text-info'],
    ['Proyectos', stats.totalProyectos, <FolderKanban size={24} />, 'text-accent'],
    ['Tareas completadas', stats.tareasCompletadas, <CheckCircle2 size={24} />, 'text-success'],
    ['Tasa completitud', `${stats.tasaCompletitud}%`, <ListChecks size={24} />, 'text-warning'],
  ] : []

  return (
    <div className="space-y-6">
      <div>
        <p className="text-sm font-semibold text-accent">Sistema</p>
        <h1 className="text-2xl font-extrabold">Administracion</h1>
        <p className="mt-1 text-sm text-textMuted">
          Usuarios, proyectos y estadisticas globales del MVP.
        </p>
      </div>

      {error && (
        <Toast
          variant="error"
          title="No se pudo completar la operacion"
          message={error}
          className="max-w-none"
        />
      )}

      {loading ? (
        <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
          {[0, 1, 2, 3].map((item) => (
            <div key={item} className="h-32 animate-pulse rounded-xl bg-white" />
          ))}
        </div>
      ) : (
        <>
          <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
            {statCards.map(([label, value, icon, color]) => (
              <Card key={label}>
                <span className={color}>{icon}</span>
                <p className="mt-4 text-3xl font-extrabold">{value}</p>
                <p className="text-sm text-textMuted">{label}</p>
              </Card>
            ))}
          </div>

          <Card className="overflow-hidden p-0 sm:p-0">
            <div className="border-b border-border p-5">
              <h2 className="text-lg font-bold">Usuarios</h2>
              <p className="text-sm text-textMuted">
                {stats?.usuariosActivos || 0} activos de {users.length}
              </p>
            </div>
            <div className="overflow-x-auto">
              <table className="w-full min-w-[720px] text-left">
                <thead className="bg-slate-50 text-xs uppercase text-textMuted">
                  <tr>
                    <th className="px-5 py-3">Usuario</th>
                    <th className="px-5 py-3">Roles</th>
                    <th className="px-5 py-3">Estado</th>
                    <th className="px-5 py-3 text-right">Accion</th>
                  </tr>
                </thead>
                <tbody>
                  {users.map((user) => (
                    <tr key={user.id} className="border-t border-border">
                      <td className="px-5 py-4">
                        <p className="font-semibold">{user.nombre}</p>
                        <p className="text-sm text-textMuted">{user.correo}</p>
                      </td>
                      <td className="px-5 py-4 text-sm">{user.roles.join(', ')}</td>
                      <td className="px-5 py-4">
                        <span className={user.activo ? 'text-success' : 'text-danger'}>
                          {user.activo ? 'Activo' : 'Inactivo'}
                        </span>
                      </td>
                      <td className="px-5 py-4 text-right">
                        <Button
                          variant={user.activo ? 'danger' : 'secondary'}
                          disabled={user.id === currentUser?.id}
                          onClick={() => setTarget(user)}
                        >
                          {user.activo ? 'Desactivar' : 'Activar'}
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </Card>

          <Card className="overflow-hidden p-0 sm:p-0">
            <div className="border-b border-border p-5">
              <h2 className="text-lg font-bold">Proyectos del sistema</h2>
            </div>
            {projects.length === 0 ? (
              <div className="grid place-items-center py-12 text-center">
                <ShieldCheck size={38} className="text-accent" />
                <p className="mt-3 font-semibold">No hay proyectos registrados</p>
              </div>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full min-w-[760px] text-left">
                  <thead className="bg-slate-50 text-xs uppercase text-textMuted">
                    <tr>
                      <th className="px-5 py-3">Proyecto</th>
                      <th className="px-5 py-3">Lider</th>
                      <th className="px-5 py-3">Estado</th>
                      <th className="px-5 py-3">Miembros</th>
                      <th className="px-5 py-3">Avance</th>
                    </tr>
                  </thead>
                  <tbody>
                    {projects.map((project) => (
                      <tr key={project.id} className="border-t border-border">
                        <td className="px-5 py-4">
                          <p className="font-semibold">{project.nombre}</p>
                          <p className="text-sm text-textMuted">{project.espacioNombre}</p>
                        </td>
                        <td className="px-5 py-4 text-sm">{project.liderNombre}</td>
                        <td className="px-5 py-4 text-sm">{project.estado.replaceAll('_', ' ')}</td>
                        <td className="px-5 py-4 text-sm">{project.totalMiembros}</td>
                        <td className="px-5 py-4 font-semibold">{project.porcentajeAvance}%</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </Card>
        </>
      )}

      <ConfirmDialog
        open={Boolean(target)}
        title={target?.activo ? 'Desactivar usuario' : 'Activar usuario'}
        message={`${target?.nombre || 'El usuario'} ${target?.activo
          ? 'no podra iniciar sesion mientras permanezca inactivo.'
          : 'recuperara el acceso al sistema.'}`}
        busy={busy}
        confirmLabel={target?.activo ? 'Desactivar' : 'Activar'}
        onCancel={() => setTarget(null)}
        onConfirm={toggleUser}
      />
    </div>
  )
}

export default Admin
