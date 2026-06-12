import {
  Activity,
  Bell,
  CalendarDays,
  ChartNoAxesCombined,
  CheckSquare2,
  FolderKanban,
  FolderOpen,
  Gauge,
  LayoutDashboard,
  Network,
  Settings,
  ShieldCheck,
  Users,
  X,
} from 'lucide-react'
import { NavLink } from 'react-router-dom'
import useAuthStore from '../../store/authStore'

const navigation = [
  {
    label: 'General',
    items: [
      { label: 'Dashboard', to: '/dashboard', icon: LayoutDashboard },
      { label: 'Mis Tareas', to: '/tareas', icon: CheckSquare2 },
      { label: 'Calendario', icon: CalendarDays },
    ],
  },
  {
    label: 'Trabajo',
    items: [
      { label: 'Espacios', to: '/espacios', icon: FolderOpen },
      { label: 'Proyectos', to: '/proyectos', icon: FolderKanban },
      { label: 'Tablero Kanban', to: '/kanban', icon: Gauge },
    ],
  },
  {
    label: 'Equipo',
    items: [
      { label: 'Miembros', to: '/miembros', icon: Users },
      { label: 'Actividad', icon: Activity },
    ],
  },
  {
    label: 'Analisis',
    items: [
      { label: 'Reportes', to: '/reportes', icon: ChartNoAxesCombined },
    ],
  },
  {
    label: 'Sistema',
    items: [
      { label: 'Notificaciones', icon: Bell },
      { label: 'Configuracion', to: '/configuracion', icon: Settings },
      { label: 'Administracion', icon: ShieldCheck },
    ],
  },
]

const itemClasses =
  'flex w-full items-center gap-3 border-l-2 px-5 py-2.5 text-sm transition-colors'

function Sidebar({ open, onClose }) {
  const user = useAuthStore((state) => state.user)
  const initials = user?.nombre
    ?.split(/\s+/)
    .slice(0, 2)
    .map((part) => part[0])
    .join('')
    .toUpperCase() || 'FH'
  const primaryRole = user?.roles?.[0] ?? 'MIEMBRO'

  return (
    <>
      {open && (
        <button
          type="button"
          aria-label="Cerrar navegacion"
          className="fixed inset-0 z-40 bg-slate-950/50 md:hidden"
          onClick={onClose}
        />
      )}

      <aside
        className={`fixed inset-y-0 left-0 z-50 flex w-72 flex-col bg-primary text-white shadow-xl transition-transform duration-200 md:translate-x-0 ${
          open ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        <div className="flex h-20 items-center justify-between border-b border-white/10 px-6">
          <NavLink
            to="/"
            className="flex items-center gap-3"
            onClick={onClose}
          >
            <span className="grid h-10 w-10 place-items-center rounded-xl bg-accent shadow-glow">
              <Network size={22} strokeWidth={1.75} />
            </span>
            <span>
              <span className="block text-lg font-extrabold tracking-wide">
                FISIHUB
              </span>
              <span className="block text-[11px] text-violet-200">
                Gestion de proyectos
              </span>
            </span>
          </NavLink>
          <button
            type="button"
            className="rounded-lg p-2 text-violet-200 hover:bg-white/10 md:hidden"
            aria-label="Cerrar menu"
            onClick={onClose}
          >
            <X size={20} />
          </button>
        </div>

        <nav className="flex-1 overflow-y-auto py-5" aria-label="Principal">
          {navigation.map((group) => (
            <div className="mb-5" key={group.label}>
              <p className="mb-2 px-6 text-[10px] font-bold uppercase tracking-[0.2em] text-violet-300">
                {group.label}
              </p>
              <div className="space-y-1">
                {group.items.map((item) => {
                  const Icon = item.icon

                  if (!item.to) {
                    return (
                      <span
                        key={item.label}
                        className={`${itemClasses} cursor-not-allowed border-transparent text-violet-300/60`}
                        title="Disponible en un sprint futuro"
                      >
                        <Icon size={18} strokeWidth={1.75} />
                        {item.label}
                      </span>
                    )
                  }

                  return (
                    <NavLink
                      key={item.label}
                      to={item.to}
                      onClick={onClose}
                      className={({ isActive }) =>
                        `${itemClasses} ${
                          isActive
                            ? 'border-accentLight bg-accent/30 text-white'
                            : 'border-transparent text-violet-100 hover:border-accentLight hover:bg-white/10 hover:text-white'
                        }`
                      }
                    >
                      <Icon size={18} strokeWidth={1.75} />
                      {item.label}
                    </NavLink>
                  )
                })}
              </div>
            </div>
          ))}
        </nav>

        <div className="border-t border-white/10 p-4">
          <div className="flex items-center gap-3 rounded-xl bg-white/5 p-3">
            <div className="grid h-10 w-10 place-items-center rounded-full bg-gradient-to-br from-accentLight to-accent text-sm font-bold">
              {initials}
            </div>
            <div className="min-w-0">
              <p className="truncate text-sm font-semibold">
                {user?.nombre ?? 'Usuario'}
              </p>
              <p className="text-xs text-violet-300">{primaryRole}</p>
            </div>
          </div>
        </div>
      </aside>
    </>
  )
}

export default Sidebar
