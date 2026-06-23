import {
  Bell,
  CheckCheck,
  CheckCircle2,
  CircleUserRound,
  ClipboardCheck,
  FolderKanban,
  Mail,
  XCircle,
} from 'lucide-react'
import { useEffect, useMemo, useState } from 'react'
import Button from '../components/ui/Button'
import Card from '../components/ui/Card'
import Toast from '../components/ui/Toast'
import {
  acceptInvitation,
  rejectInvitation,
} from '../services/invitacionService'
import useNotificationStore from '../store/notificationStore'

const iconByType = {
  ASIGNACION_TAREA: ClipboardCheck,
  TAREA_VENCE_MANANA: Bell,
  MIEMBRO_PROYECTO: FolderKanban,
  INVITACION_ESPACIO: Mail,
}

const formatDate = (value) =>
  new Intl.DateTimeFormat('es-PE', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(value))

const invitationStatusMessage = {
  ACEPTADA: 'Esta invitacion ya fue aceptada.',
  EXPIRADA: 'Esta invitacion expiro y ya no acepta acciones.',
  REVOCADA: 'Esta invitacion ya no esta disponible.',
}

/** Card especial para invitaciones in-app con botones Aceptar / Rechazar */
function InvitacionCard({ item, onAction, saving }) {
  const [busy, setBusy] = useState(false)
  const [result, setResult] = useState(null) // 'aceptada' | 'rechazada'
  const [actionError, setActionError] = useState('')
  const actionable = item.invitacionEstado === 'PENDIENTE' && !result

  const handleAccept = async () => {
    setBusy(true)
    setActionError('')
    try {
      await acceptInvitation(item.referenciaId)
      setResult('aceptada')
      onAction()
    } catch (err) {
      setActionError(err.message)
    } finally {
      setBusy(false)
    }
  }

  const handleReject = async () => {
    setBusy(true)
    setActionError('')
    try {
      await rejectInvitation(item.referenciaId)
      setResult('rechazada')
      onAction()
    } catch (err) {
      setActionError(err.message)
    } finally {
      setBusy(false)
    }
  }

  return (
    <div
      className={`flex w-full flex-col gap-4 rounded-xl border p-4 text-left shadow-sm transition sm:flex-row sm:items-start ${
        item.leida
          ? 'border-border bg-white'
          : 'border-violet-200 bg-violet-50'
      }`}
    >
      {/* Ícono */}
      <span className="relative grid size-11 shrink-0 place-items-center rounded-full bg-white text-accent shadow-sm">
        <Mail size={20} />
        {!item.leida && (
          <span className="absolute right-0 top-0 size-2.5 rounded-full bg-accent ring-2 ring-white" />
        )}
      </span>

      {/* Contenido */}
      <div className="min-w-0 flex-1">
        <p className="font-semibold">{item.mensaje}</p>
        <p className="mt-1 text-xs text-textMuted">{formatDate(item.creadoEn)}</p>

        {actionError && (
          <p className="mt-2 text-xs font-medium text-danger">{actionError}</p>
        )}

        {result === 'aceptada' && (
          <div className="mt-3 flex items-center gap-2 rounded-lg bg-emerald-50 px-3 py-2 text-sm font-medium text-emerald-700">
            <CheckCircle2 size={16} />
            Invitacion aceptada — ya eres miembro del espacio.
          </div>
        )}
        {result === 'rechazada' && (
          <div className="mt-3 flex items-center gap-2 rounded-lg bg-red-50 px-3 py-2 text-sm font-medium text-red-600">
            <XCircle size={16} />
            Invitacion rechazada.
          </div>
        )}

        {!actionable && !result && !actionError
          && invitationStatusMessage[item.invitacionEstado] && (
          <div className="mt-3 rounded-lg bg-slate-100 px-3 py-2 text-sm text-slate-700">
            {invitationStatusMessage[item.invitacionEstado]}
          </div>
        )}

        {!actionable && !result && !actionError && !item.invitacionEstado && (
          <div className="mt-3 rounded-lg bg-slate-100 px-3 py-2 text-sm text-slate-700">
            Esta invitacion ya no esta disponible.
          </div>
        )}

        {actionable && (
          <div className="mt-3 flex gap-2">
            <Button
              type="button"
              disabled={busy || saving}
              onClick={handleAccept}
              className="text-sm"
            >
              <CheckCircle2 size={15} />
              {busy ? 'Procesando...' : 'Aceptar'}
            </Button>
            <Button
              type="button"
              variant="secondary"
              disabled={busy || saving}
              onClick={handleReject}
              className="text-sm"
            >
              <XCircle size={15} />
              Rechazar
            </Button>
          </div>
        )}
      </div>
    </div>
  )
}

function Notifications() {
  const {
    notifications,
    loading,
    error,
    load,
    markRead,
    markAllRead,
  } = useNotificationStore()
  const [filter, setFilter] = useState('TODAS')
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    load().catch(() => {})
  }, [load])

  const unread = notifications.filter((item) => !item.leida).length
  const visible = useMemo(
    () =>
      filter === 'NO_LEIDAS'
        ? notifications.filter((item) => !item.leida)
        : notifications,
    [filter, notifications],
  )

  const readOne = async (item) => {
    if (item.leida) return
    setSaving(true)
    try {
      await markRead(item.id)
    } catch {
      // The shared store exposes the request error in the page toast.
    } finally {
      setSaving(false)
    }
  }

  const readAll = async () => {
    setSaving(true)
    try {
      await markAllRead()
    } catch {
      // The shared store exposes the request error in the page toast.
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-end">
        <div>
          <p className="text-sm font-semibold text-accent">Centro de avisos</p>
          <h1 className="text-2xl font-extrabold">Notificaciones</h1>
          <p className="mt-1 text-sm text-textMuted">
            Invitaciones, asignaciones, vencimientos y nuevos proyectos.
          </p>
        </div>
        <Button
          variant="secondary"
          disabled={saving || unread === 0}
          onClick={readAll}
        >
          <CheckCheck size={17} />
          Marcar todas como leidas
        </Button>
      </div>

      <div className="flex gap-2">
        {[
          ['TODAS', `Todas (${notifications.length})`],
          ['NO_LEIDAS', `No leidas (${unread})`],
        ].map(([value, label]) => (
          <Button
            key={value}
            variant={filter === value ? 'primary' : 'secondary'}
            onClick={() => setFilter(value)}
          >
            {label}
          </Button>
        ))}
      </div>

      {error && (
        <Toast
          variant="error"
          title="No se pudieron cargar las notificaciones"
          message={error}
          className="max-w-none"
        />
      )}

      {loading ? (
        <div className="space-y-3" aria-label="Cargando notificaciones">
          {[0, 1, 2].map((item) => (
            <div key={item} className="h-24 animate-pulse rounded-xl bg-white" />
          ))}
        </div>
      ) : visible.length === 0 ? (
        <Card className="grid place-items-center py-14 text-center">
          <Bell size={42} className="text-accent" />
          <h2 className="mt-4 text-lg font-bold">Sin notificaciones</h2>
          <p className="mt-1 text-sm text-textMuted">
            {filter === 'NO_LEIDAS'
              ? 'No tienes avisos pendientes de lectura.'
              : 'Los avisos importantes apareceran aqui.'}
          </p>
        </Card>
      ) : (
        <div className="space-y-3">
          {visible.map((item) => {
            // Las invitaciones tienen su propio card interactivo
            if (item.tipo === 'INVITACION_ESPACIO') {
              return (
                <InvitacionCard
                  key={item.id}
                  item={item}
                  saving={saving}
                  onAction={() => {
                    if (!item.leida) {
                      readOne(item).catch(() => {})
                    }
                    load().catch(() => {})
                  }}
                />
              )
            }

            const Icon = iconByType[item.tipo] || CircleUserRound
            return (
              <button
                key={item.id}
                type="button"
                disabled={saving}
                onClick={() => readOne(item)}
                className={`flex w-full gap-4 rounded-xl border p-4 text-left shadow-sm transition hover:-translate-y-0.5 hover:shadow-md ${
                  item.leida
                    ? 'border-border bg-white'
                    : 'border-violet-200 bg-violet-50'
                }`}
              >
                <span className="relative grid size-11 shrink-0 place-items-center rounded-full bg-white text-accent shadow-sm">
                  <Icon size={20} />
                  {!item.leida && (
                    <span className="absolute right-0 top-0 size-2.5 rounded-full bg-accent ring-2 ring-white" />
                  )}
                </span>
                <span className="min-w-0 flex-1">
                  <span className="block font-semibold">{item.mensaje}</span>
                  <span className="mt-1 block text-xs text-textMuted">
                    {formatDate(item.creadoEn)}
                  </span>
                </span>
              </button>
            )
          })}
        </div>
      )}
    </div>
  )
}

export default Notifications
