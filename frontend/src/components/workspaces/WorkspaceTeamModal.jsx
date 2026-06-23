import { MailPlus, RefreshCw, Trash2, UserMinus } from 'lucide-react'
import { useEffect, useState } from 'react'
import {
  getWorkspaceMembers,
  removeWorkspaceMember,
  updateWorkspaceMemberRole,
} from '../../services/espacioService'
import {
  createWorkspaceInvitation,
  getAvailableUsers,
  getWorkspaceInvitations,
  resendInvitation,
  revokeInvitation,
} from '../../services/invitacionService'
import Badge from '../ui/Badge'
import Button from '../ui/Button'
import ConfirmDialog from '../ui/ConfirmDialog'
import Modal from '../ui/Modal'
import Toast from '../ui/Toast'

const invitationBadge = {
  PENDIENTE: 'pendiente',
  ACEPTADA: 'completada',
  EXPIRADA: 'bloqueada',
  REVOCADA: 'revision',
}

const initials = (name) =>
  name
    .split(/\s+/)
    .slice(0, 2)
    .map((part) => part[0])
    .join('')
    .toUpperCase()

function WorkspaceTeamModal({ workspace, onClose }) {
  const [team, setTeam] = useState(null)
  const [invitations, setInvitations] = useState([])
  const [availableUsers, setAvailableUsers] = useState([])
  const [form, setForm] = useState({ usuarioId: '', rol: 'MIEMBRO' })
  const [loading, setLoading] = useState(false)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [removing, setRemoving] = useState(null)

  const load = async () => {
    if (!workspace) return
    setLoading(true)
    setError('')
    try {
      const members = await getWorkspaceMembers(workspace.id)
      setTeam(members)
      if (members.puedeGestionar) {
        const [invs, available] = await Promise.all([
          getWorkspaceInvitations(workspace.id),
          getAvailableUsers(workspace.id),
        ])
        setInvitations(invs)
        setAvailableUsers(available)
      } else {
        setInvitations([])
        setAvailableUsers([])
      }
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [workspace]) // eslint-disable-line react-hooks/exhaustive-deps

  const invite = async (event) => {
    event.preventDefault()
    if (!form.usuarioId) return
    setBusy(true)
    setError('')
    setSuccess('')
    try {
      const invitado = availableUsers.find(
        (u) => String(u.id) === String(form.usuarioId),
      )
      await createWorkspaceInvitation(workspace.id, {
        usuarioId: Number(form.usuarioId),
        rol: form.rol,
      })
      setForm({ usuarioId: '', rol: 'MIEMBRO' })
      setSuccess(
        `Invitacion enviada a ${invitado?.nombre ?? 'el usuario'} correctamente.`,
      )
      await load()
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setBusy(false)
    }
  }

  const changeRole = async (member, role) => {
    setBusy(true)
    setError('')
    try {
      await updateWorkspaceMemberRole(workspace.id, member.usuarioId, role)
      await load()
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
      await removeWorkspaceMember(workspace.id, removing.usuarioId)
      setRemoving(null)
      await load()
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setBusy(false)
    }
  }

  const resend = async (invitation) => {
    setBusy(true)
    setError('')
    setSuccess('')
    try {
      await resendInvitation(invitation.id)
      setSuccess(`Invitacion reenviada a ${invitation.usuarioNombre}.`)
      await load()
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setBusy(false)
    }
  }

  const revoke = async (invitation) => {
    setBusy(true)
    setError('')
    try {
      await revokeInvitation(invitation.id)
      await load()
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setBusy(false)
    }
  }

  return (
    <>
      <Modal
        open={Boolean(workspace)}
        title={workspace ? `Equipo de ${workspace.nombre}` : 'Equipo'}
        onClose={onClose}
      >
        <div className="space-y-5">
          {error && (
            <Toast
              variant="error"
              title="No se pudo completar la operacion"
              message={error}
              className="max-w-none"
            />
          )}
          {success && (
            <Toast
              variant="success"
              title="Equipo actualizado"
              message={success}
              className="max-w-none"
            />
          )}

          {/* ── Formulario de invitación in-app ── */}
          {team?.puedeGestionar && (
            <form
              onSubmit={invite}
              className="rounded-xl border border-violet-100 bg-violet-50/50 p-4"
            >
              <h3 className="flex items-center gap-2 font-bold">
                <MailPlus size={18} className="text-accent" />
                Invitar miembro
              </h3>

              {availableUsers.length === 0 ? (
                <p className="mt-3 text-sm text-textMuted">
                  No hay usuarios disponibles para invitar. Los demas ya son
                  miembros o tienen una invitacion pendiente.
                </p>
              ) : (
                <div className="mt-3 grid gap-3 sm:grid-cols-[1fr_9rem_auto] sm:items-end">
                  {/* Selector de usuarios disponibles */}
                  <label className="text-sm font-medium">
                    Usuario
                    <select
                      id="workspace-invite-user"
                      value={form.usuarioId}
                      required
                      onChange={(event) =>
                        setForm((current) => ({
                          ...current,
                          usuarioId: event.target.value,
                        }))
                      }
                      className="mt-1.5 w-full rounded-lg border border-border bg-white px-3 py-2.5 text-sm outline-none focus:border-accent"
                    >
                      <option value="">Selecciona un usuario...</option>
                      {availableUsers.map((user) => (
                        <option key={user.id} value={user.id}>
                          {user.nombre} — {user.correo}
                        </option>
                      ))}
                    </select>
                  </label>

                  {/* Selector de rol */}
                  <label className="text-sm font-medium">
                    Rol
                    <select
                      value={form.rol}
                      onChange={(event) =>
                        setForm((current) => ({
                          ...current,
                          rol: event.target.value,
                        }))
                      }
                      className="mt-1.5 w-full rounded-lg border border-border bg-white px-3 py-2.5 text-sm outline-none focus:border-accent"
                    >
                      <option value="MIEMBRO">Miembro</option>
                      <option value="LIDER">Lider</option>
                    </select>
                  </label>

                  <Button type="submit" disabled={busy || !form.usuarioId}>
                    Invitar
                  </Button>
                </div>
              )}
            </form>
          )}

          {/* ── Lista de miembros ── */}
          {loading ? (
            <p className="text-sm text-textMuted">Cargando equipo...</p>
          ) : (
            <section>
              <h3 className="font-bold">Miembros activos</h3>
              <div className="mt-3 space-y-2">
                {team?.miembros.map((member) => (
                  <article
                    key={member.usuarioId}
                    className="flex flex-col gap-3 rounded-xl border border-border p-3 sm:flex-row sm:items-center"
                  >
                    <div className="grid size-10 shrink-0 place-items-center rounded-full bg-violet-100 font-bold text-accent">
                      {initials(member.nombre)}
                    </div>
                    <div className="min-w-0 flex-1">
                      <p className="truncate font-semibold">{member.nombre}</p>
                      <p className="truncate text-xs text-textMuted">
                        {member.correo} · {member.tareasActivas} tareas activas
                      </p>
                    </div>
                    <Badge value={member.activo ? 'completada' : 'bloqueada'}>
                      {member.activo ? 'ACTIVO' : 'INACTIVO'}
                    </Badge>
                    {team.puedeGestionar && (
                      <>
                        <select
                          aria-label={`Rol de ${member.nombre}`}
                          value={member.rol}
                          disabled={busy}
                          onChange={(event) =>
                            changeRole(member, event.target.value)
                          }
                          className="rounded-lg border border-border bg-white px-3 py-2 text-sm outline-none focus:border-accent"
                        >
                          <option value="LIDER">Lider</option>
                          <option value="MIEMBRO">Miembro</option>
                        </select>
                        <button
                          type="button"
                          aria-label={`Quitar a ${member.nombre}`}
                          disabled={busy}
                          onClick={() => setRemoving(member)}
                          className="rounded-lg p-2 text-textMuted hover:bg-red-50 hover:text-danger"
                        >
                          <UserMinus size={17} />
                        </button>
                      </>
                    )}
                  </article>
                ))}
              </div>
            </section>
          )}

          {/* ── Lista de invitaciones ── */}
          {team?.puedeGestionar && (
            <section>
              <h3 className="font-bold">Invitaciones</h3>
              {invitations.length === 0 ? (
                <p className="mt-2 text-sm text-textMuted">
                  No hay invitaciones registradas.
                </p>
              ) : (
                <div className="mt-3 space-y-2">
                  {invitations.map((invitation) => (
                    <article
                      key={invitation.id}
                      className="flex flex-col gap-2 rounded-xl border border-border p-3 sm:flex-row sm:items-center"
                    >
                      <div className="grid size-9 shrink-0 place-items-center rounded-full bg-violet-100 font-bold text-accent text-sm">
                        {initials(invitation.usuarioNombre)}
                      </div>
                      <div className="min-w-0 flex-1">
                        <p className="truncate font-semibold">
                          {invitation.usuarioNombre}
                        </p>
                        <p className="text-xs text-textMuted">
                          Rol {invitation.rol} · vence{' '}
                          {new Date(invitation.expiraEn).toLocaleDateString('es-PE')}
                        </p>
                      </div>
                      <Badge value={invitationBadge[invitation.estado]}>
                        {invitation.estado}
                      </Badge>
                      {['PENDIENTE', 'EXPIRADA'].includes(invitation.estado) && (
                        <button
                          type="button"
                          aria-label={`Reenviar invitacion a ${invitation.usuarioNombre}`}
                          disabled={busy}
                          onClick={() => resend(invitation)}
                          className="rounded-lg p-2 text-textMuted hover:bg-violet-50 hover:text-accent"
                        >
                          <RefreshCw size={17} />
                        </button>
                      )}
                      {invitation.estado !== 'ACEPTADA' &&
                        invitation.estado !== 'REVOCADA' && (
                          <button
                            type="button"
                            aria-label={`Revocar invitacion de ${invitation.usuarioNombre}`}
                            disabled={busy}
                            onClick={() => revoke(invitation)}
                            className="rounded-lg p-2 text-textMuted hover:bg-red-50 hover:text-danger"
                          >
                            <Trash2 size={17} />
                          </button>
                        )}
                    </article>
                  ))}
                </div>
              )}
            </section>
          )}
        </div>
      </Modal>

      <ConfirmDialog
        open={Boolean(removing)}
        title="Quitar miembro del espacio"
        message={`Se quitara a ${removing?.nombre || 'este usuario'}. Primero debe estar fuera de todos los proyectos del espacio.`}
        busy={busy}
        onCancel={() => setRemoving(null)}
        onConfirm={confirmRemove}
      />
    </>
  )
}

export default WorkspaceTeamModal
