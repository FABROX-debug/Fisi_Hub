import { Plus, UsersRound } from 'lucide-react'
import { useEffect, useState } from 'react'
import ActivityFeed from '../components/members/ActivityFeed'
import MemberCard from '../components/members/MemberCard'
import Button from '../components/ui/Button'
import Card from '../components/ui/Card'
import ConfirmDialog from '../components/ui/ConfirmDialog'
import Input from '../components/ui/Input'
import Toast from '../components/ui/Toast'
import {
  addProjectMember,
  getProjectActivity,
  getProjectMembers,
  removeProjectMember,
  updateProjectMemberRole,
} from '../services/miembroService'
import { getProyectos } from '../services/proyectoService'

function Members() {
  const [projects, setProjects] = useState([])
  const [projectId, setProjectId] = useState('')
  const [memberData, setMemberData] = useState(null)
  const [activity, setActivity] = useState([])
  const [email, setEmail] = useState('')
  const [role, setRole] = useState('MIEMBRO')
  const [loading, setLoading] = useState(true)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const [removing, setRemoving] = useState(null)

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
      setMemberData(null)
      setActivity([])
      return
    }
    const loadCollaboration = async () => {
      setLoading(true)
      setError('')
      try {
        const [members, recentActivity] = await Promise.all([
          getProjectMembers(projectId),
          getProjectActivity(projectId),
        ])
        setMemberData(members)
        setActivity(recentActivity)
      } catch (requestError) {
        setError(requestError.message)
      } finally {
        setLoading(false)
      }
    }
    loadCollaboration()
  }, [projectId])

  const addMember = async (event) => {
    event.preventDefault()
    setBusy(true)
    setError('')
    try {
      await addProjectMember(projectId, { correo: email.trim(), rol: role })
      const [members, recentActivity] = await Promise.all([
        getProjectMembers(projectId),
        getProjectActivity(projectId),
      ])
      setMemberData(members)
      setActivity(recentActivity)
      setEmail('')
      setRole('MIEMBRO')
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setBusy(false)
    }
  }

  const changeRole = async (member, nextRole) => {
    setBusy(true)
    setError('')
    try {
      await updateProjectMemberRole(projectId, member.usuarioId, nextRole)
      setMemberData(await getProjectMembers(projectId))
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setBusy(false)
    }
  }

  const confirmRemove = async () => {
    setBusy(true)
    setError('')
    try {
      await removeProjectMember(projectId, removing.usuarioId)
      setRemoving(null)
      setMemberData(await getProjectMembers(projectId))
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="space-y-6">
      <div>
        <p className="text-sm font-semibold text-accent">Colaboracion</p>
        <h1 className="text-2xl font-extrabold">Miembros y actividad</h1>
        <p className="mt-1 text-sm text-textMuted">
          Gestiona el equipo y revisa los eventos recientes de cada proyecto.
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
        <button type="button" className="w-full text-left" onClick={() => setError('')}>
          <Toast
            variant="error"
            title="No se pudo completar la operacion"
            message={error}
            className="max-w-none"
          />
        </button>
      )}

      {loading ? (
        <Card><p className="text-sm text-textMuted">Cargando colaboracion...</p></Card>
      ) : projects.length === 0 ? (
        <Card className="grid place-items-center py-14 text-center">
          <UsersRound size={42} className="text-accent" />
          <h2 className="mt-4 text-lg font-bold">No hay proyectos</h2>
          <p className="mt-1 text-sm text-textMuted">
            Crea un proyecto antes de gestionar sus miembros.
          </p>
        </Card>
      ) : (
        <div className="grid gap-6 xl:grid-cols-[minmax(0,1.45fr)_minmax(20rem,0.75fr)]">
          <div className="space-y-5">
            {memberData?.puedeGestionar && (
              <Card>
                <h2 className="font-bold">Agregar miembro</h2>
                <form onSubmit={addMember} className="mt-4 grid gap-3 sm:grid-cols-[1fr_10rem_auto] sm:items-end">
                  <Input
                    id="member-email"
                    type="email"
                    label="Correo del usuario"
                    placeholder="usuario@correo.com"
                    value={email}
                    required
                    onChange={(event) => setEmail(event.target.value)}
                  />
                  <label className="text-sm font-medium">
                    Rol
                    <select
                      value={role}
                      onChange={(event) => setRole(event.target.value)}
                      className="mt-1.5 w-full rounded-lg border border-border bg-white px-3.5 py-2.5 outline-none focus:border-accent"
                    >
                      <option value="MIEMBRO">Miembro</option>
                      <option value="LIDER">Lider</option>
                    </select>
                  </label>
                  <Button type="submit" disabled={busy}>
                    <Plus size={17} />
                    Agregar
                  </Button>
                </form>
              </Card>
            )}

            <section>
              <div className="mb-3 flex items-center justify-between">
                <h2 className="text-lg font-bold">Equipo del proyecto</h2>
                <span className="text-sm text-textMuted">
                  {memberData?.miembros.length || 0} miembros
                </span>
              </div>
              <div className="grid gap-4 lg:grid-cols-2">
                {memberData?.miembros.map((member) => (
                  <MemberCard
                    key={member.usuarioId}
                    member={member}
                    canManage={memberData.puedeGestionar}
                    busy={busy}
                    onRoleChange={changeRole}
                    onRemove={setRemoving}
                  />
                ))}
              </div>
            </section>
          </div>

          <Card>
            <h2 className="mb-3 text-lg font-bold">Actividad reciente</h2>
            <ActivityFeed items={activity} />
          </Card>
        </div>
      )}

      <ConfirmDialog
        open={Boolean(removing)}
        title="Quitar miembro"
        message={`Se quitara a ${removing?.nombre || 'este usuario'} del proyecto. No podra acceder a sus tareas ni actividad.`}
        busy={busy}
        onCancel={() => setRemoving(null)}
        onConfirm={confirmRemove}
      />
    </div>
  )
}

export default Members
