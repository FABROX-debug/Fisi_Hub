import { Plus, UsersRound } from 'lucide-react'
import { useCallback, useEffect, useMemo, useState } from 'react'
import ActivityFeed from '../components/members/ActivityFeed'
import MemberCard from '../components/members/MemberCard'
import Badge from '../components/ui/Badge'
import Button from '../components/ui/Button'
import Card from '../components/ui/Card'
import ConfirmDialog from '../components/ui/ConfirmDialog'
import Toast from '../components/ui/Toast'
import {
  addProjectMember,
  getProjectActivity,
  getProjectMembers,
  removeProjectMember,
  updateProjectMemberRole,
} from '../services/miembroService'
import { getProyectos } from '../services/proyectoService'
import { getWorkspaceMembers } from '../services/espacioService'
import { createTarea } from '../services/tareaService'

function Members() {
  const [projects, setProjects] = useState([])
  const [projectId, setProjectId] = useState('')
  const [memberData, setMemberData] = useState(null)
  const [activity, setActivity] = useState([])
  const [workspaceMembers, setWorkspaceMembers] = useState([])
  const [userId, setUserId] = useState('')
  const [role, setRole] = useState('MIEMBRO')
  const [roleFilter, setRoleFilter] = useState('TODOS')
  const [loadFilter, setLoadFilter] = useState('TODOS')
  const [loading, setLoading] = useState(true)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const [removing, setRemoving] = useState(null)
  const [savingTaskMemberId, setSavingTaskMemberId] = useState(null)

  const teamMembers = useMemo(() => memberData?.miembros || [], [memberData])

  const loadProjectContext = useCallback(async (
    currentProjectId,
    currentProjects = projects,
  ) => {
    const project = currentProjects.find(
      (item) => String(item.id) === String(currentProjectId),
    )

    if (!project) {
      setMemberData(null)
      setActivity([])
      setWorkspaceMembers([])
      return
    }

    const [members, recentActivity, workspaceTeam] = await Promise.all([
      getProjectMembers(currentProjectId),
      getProjectActivity(currentProjectId),
      getWorkspaceMembers(project.espacioId),
    ])

    setMemberData(members)
    setActivity(recentActivity)
    setWorkspaceMembers(workspaceTeam.miembros)
    setUserId('')
  }, [projects])

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
        await loadProjectContext(projectId, projects)
      } catch (requestError) {
        setError(requestError.message)
      } finally {
        setLoading(false)
      }
    }

    loadCollaboration()
  }, [loadProjectContext, projectId, projects])

  const addMember = async (event) => {
    event.preventDefault()
    setBusy(true)
    setError('')
    try {
      await addProjectMember(projectId, {
        usuarioId: Number(userId),
        rol: role,
      })
      await loadProjectContext(projectId)
      setUserId('')
      setRole('MIEMBRO')
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setBusy(false)
    }
  }

  const handleQuickTaskCreate = async (member, payload) => {
    setSavingTaskMemberId(member.usuarioId)
    setError('')
    try {
      await createTarea({
        ...payload,
        proyectoId: Number(projectId),
        responsableId: member.usuarioId,
      })
      await loadProjectContext(projectId)
    } finally {
      setSavingTaskMemberId(null)
    }
  }

  const availableMembers = workspaceMembers.filter(
    (member) => member.activo
      && !memberData?.miembros.some(
        (projectMember) => projectMember.usuarioId === member.usuarioId,
      ),
  )

  const teamStats = useMemo(() => ({
    total: teamMembers.length,
    withoutTasks: teamMembers.filter(
      (member) => member.activo && member.tareasActivas === 0,
    ).length,
    highLoad: teamMembers.filter((member) => member.tareasActivas >= 5).length,
    leaders: teamMembers.filter((member) => member.rol === 'LIDER').length,
    active: teamMembers.filter((member) => member.activo).length,
  }), [teamMembers])

  const visibleMembers = useMemo(
    () => teamMembers.filter((member) => {
      const matchesRole = roleFilter === 'TODOS' || member.rol === roleFilter
      const matchesLoad =
        loadFilter === 'TODOS'
        || (loadFilter === 'SIN_TAREAS' && member.tareasActivas === 0)
        || (loadFilter === 'CON_TAREAS' && member.tareasActivas > 0)
        || (loadFilter === 'CARGA_ALTA' && member.tareasActivas >= 5)

      return matchesRole && matchesLoad
    }),
    [loadFilter, roleFilter, teamMembers],
  )

  const changeRole = async (member, nextRole) => {
    setBusy(true)
    setError('')
    try {
      await updateProjectMemberRole(projectId, member.usuarioId, nextRole)
      await loadProjectContext(projectId)
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
      await loadProjectContext(projectId)
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
          Gestiona el equipo, detecta carga desigual y crea trabajo rapido desde cada miembro.
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
                {availableMembers.length === 0 ? (
                  <p className="mt-4 text-sm text-textMuted">
                    Todos los miembros activos del espacio ya participan en este proyecto.
                  </p>
                ) : (
                  <form onSubmit={addMember} className="mt-4 grid gap-3 sm:grid-cols-[1fr_10rem_auto] sm:items-end">
                    <label className="text-sm font-medium">
                      Miembro del espacio
                      <select
                        value={userId}
                        required
                        onChange={(event) => setUserId(event.target.value)}
                        className="mt-1.5 w-full rounded-lg border border-border bg-white px-3.5 py-2.5 outline-none focus:border-accent"
                      >
                        <option value="">Selecciona un miembro</option>
                        {availableMembers.map((member) => (
                          <option key={member.usuarioId} value={member.usuarioId}>
                            {member.nombre} · {member.correo}
                          </option>
                        ))}
                      </select>
                    </label>
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
                    <Button
                      type="submit"
                      disabled={busy || availableMembers.length === 0}
                    >
                      <Plus size={17} />
                      Agregar
                    </Button>
                  </form>
                )}
              </Card>
            )}

            <section>
              <div className="mb-3 flex items-center justify-between">
                <h2 className="text-lg font-bold">Equipo del proyecto</h2>
                <span className="text-sm text-textMuted">
                  {visibleMembers.length} de {teamStats.total} miembros
                </span>
              </div>

              <div className="mb-4 grid gap-3 md:grid-cols-4">
                <Card className="p-4">
                  <p className="text-xs font-semibold uppercase tracking-wide text-textMuted">
                    Sin tareas
                  </p>
                  <p className="mt-2 text-2xl font-extrabold text-emerald-700">
                    {teamStats.withoutTasks}
                  </p>
                </Card>
                <Card className="p-4">
                  <p className="text-xs font-semibold uppercase tracking-wide text-textMuted">
                    Carga alta
                  </p>
                  <p className="mt-2 text-2xl font-extrabold text-amber-700">
                    {teamStats.highLoad}
                  </p>
                </Card>
                <Card className="p-4">
                  <p className="text-xs font-semibold uppercase tracking-wide text-textMuted">
                    Lideres
                  </p>
                  <p className="mt-2 text-2xl font-extrabold text-accent">
                    {teamStats.leaders}
                  </p>
                </Card>
                <Card className="p-4">
                  <p className="text-xs font-semibold uppercase tracking-wide text-textMuted">
                    Miembros activos
                  </p>
                  <p className="mt-2 text-2xl font-extrabold">
                    {teamStats.active}
                  </p>
                </Card>
              </div>

              <Card className="mb-4 grid gap-4 md:grid-cols-2">
                <label className="text-sm font-medium">
                  Filtrar por rol
                  <select
                    value={roleFilter}
                    onChange={(event) => setRoleFilter(event.target.value)}
                    className="mt-1.5 w-full rounded-lg border border-border bg-white px-3.5 py-2.5 outline-none focus:border-accent"
                  >
                    <option value="TODOS">Todos</option>
                    <option value="LIDER">Lideres</option>
                    <option value="MIEMBRO">Miembros</option>
                  </select>
                </label>
                <label className="text-sm font-medium">
                  Filtrar por carga
                  <select
                    value={loadFilter}
                    onChange={(event) => setLoadFilter(event.target.value)}
                    className="mt-1.5 w-full rounded-lg border border-border bg-white px-3.5 py-2.5 outline-none focus:border-accent"
                  >
                    <option value="TODOS">Todas</option>
                    <option value="SIN_TAREAS">Sin tareas</option>
                    <option value="CON_TAREAS">Con tareas</option>
                    <option value="CARGA_ALTA">Carga alta</option>
                  </select>
                </label>
              </Card>

              <div className="mb-4 flex flex-wrap gap-2">
                {roleFilter !== 'TODOS' && (
                  <Badge value="en-proceso">Rol: {roleFilter}</Badge>
                )}
                {loadFilter !== 'TODOS' && (
                  <Badge value="revision">Carga: {loadFilter.replace('_', ' ')}</Badge>
                )}
                {teamStats.withoutTasks > 0 && (
                  <Badge value="completada">
                    {teamStats.withoutTasks} sin tareas
                  </Badge>
                )}
                {teamStats.highLoad > 0 && (
                  <Badge value="alta">
                    {teamStats.highLoad} con carga alta
                  </Badge>
                )}
              </div>

              <div className="grid gap-4 lg:grid-cols-2">
                {visibleMembers.map((member) => (
                  <MemberCard
                    key={member.usuarioId}
                    member={member}
                    canManage={memberData.puedeGestionar}
                    busy={busy}
                    onRoleChange={changeRole}
                    onRemove={setRemoving}
                    onQuickTaskCreate={handleQuickTaskCreate}
                    quickTaskSaving={savingTaskMemberId === member.usuarioId}
                  />
                ))}
              </div>

              {visibleMembers.length === 0 && (
                <Card className="mt-4 grid place-items-center py-10 text-center">
                  <h3 className="text-lg font-bold">Sin coincidencias</h3>
                  <p className="mt-1 text-sm text-textMuted">
                    Ajusta los filtros para ver otros miembros del proyecto.
                  </p>
                </Card>
              )}
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
