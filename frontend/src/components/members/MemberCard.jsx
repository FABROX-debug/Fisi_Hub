import { ShieldCheck, Trash2, UserRound } from 'lucide-react'
import Badge from '../ui/Badge'

const initials = (name) => name
  .split(/\s+/)
  .slice(0, 2)
  .map((part) => part[0])
  .join('')
  .toUpperCase()

function MemberCard({
  member,
  canManage,
  busy,
  onRoleChange,
  onRemove,
}) {
  return (
    <article className="rounded-xl border border-border bg-white p-4">
      <div className="flex items-start gap-3">
        <div className="grid size-11 shrink-0 place-items-center rounded-full bg-violet-100 font-bold text-accent">
          {initials(member.nombre)}
        </div>
        <div className="min-w-0 flex-1">
          <div className="flex flex-wrap items-center gap-2">
            <h3 className="truncate font-bold">{member.nombre}</h3>
            {member.liderDesignado && (
              <ShieldCheck
                size={17}
                className="text-accent"
                aria-label="Lider designado"
              />
            )}
          </div>
          <p className="truncate text-sm text-textMuted">{member.correo}</p>
        </div>
        {canManage && !member.liderDesignado && (
          <button
            type="button"
            aria-label={`Quitar a ${member.nombre}`}
            disabled={busy}
            onClick={() => onRemove(member)}
            className="rounded-lg p-2 text-textMuted hover:bg-red-50 hover:text-danger disabled:opacity-50"
          >
            <Trash2 size={17} />
          </button>
        )}
      </div>

      <div className="mt-4 flex flex-wrap items-end justify-between gap-3">
        <div>
          <p className="mb-1 text-xs font-semibold uppercase tracking-wide text-textMuted">
            Rol interno
          </p>
          {canManage ? (
            <select
              aria-label={`Rol de ${member.nombre}`}
              value={member.rol}
              disabled={busy}
              onChange={(event) => onRoleChange(member, event.target.value)}
              className="rounded-lg border border-border bg-white px-3 py-2 text-sm outline-none focus:border-accent"
            >
              <option value="LIDER">Lider</option>
              <option value="MIEMBRO">Miembro</option>
            </select>
          ) : (
            <Badge value={member.rol === 'LIDER' ? 'en-proceso' : 'pendiente'}>
              {member.rol}
            </Badge>
          )}
        </div>
        <div className="text-right">
          <span className="flex items-center gap-1.5 text-sm text-textMuted">
            <UserRound size={15} />
            {member.tareasActivas} tareas activas
          </span>
          {member.tareasActivas >= 5 && (
            <Badge value="alta" className="mt-1">Carga alta</Badge>
          )}
        </div>
      </div>
    </article>
  )
}

export default MemberCard
