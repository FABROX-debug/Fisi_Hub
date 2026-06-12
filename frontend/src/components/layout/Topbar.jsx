import { Bell, LogOut, Menu, Search } from 'lucide-react'
import { useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import useAuthStore from '../../store/authStore'

const sectionTitles = {
  '/': 'Inicio',
  '/dashboard': 'Dashboard',
  '/proyectos': 'Proyectos',
  '/tareas': 'Mis Tareas',
  '/kanban': 'Tablero Kanban',
  '/miembros': 'Miembros',
  '/reportes': 'Reportes',
  '/configuracion': 'Configuracion',
}

function Topbar({ onMenuClick }) {
  const { pathname } = useLocation()
  const navigate = useNavigate()
  const [menuOpen, setMenuOpen] = useState(false)
  const user = useAuthStore((state) => state.user)
  const logout = useAuthStore((state) => state.logout)
  const title = sectionTitles[pathname] ?? 'FISIHUB'
  const initials = user?.nombre
    ?.split(/\s+/)
    .slice(0, 2)
    .map((part) => part[0])
    .join('')
    .toUpperCase() || 'FH'

  const handleLogout = () => {
    logout()
    navigate('/login', { replace: true })
  }

  return (
    <header className="sticky top-0 z-30 flex h-20 items-center gap-4 border-b border-border bg-white px-4 sm:px-6 lg:px-8">
      <button
        type="button"
        aria-label="Abrir navegacion"
        className="rounded-lg p-2 text-textMuted hover:bg-violet-50 hover:text-accent md:hidden"
        onClick={onMenuClick}
      >
        <Menu size={22} />
      </button>

      <div className="min-w-0">
        <p className="text-xs font-medium text-textMuted">FISIHUB</p>
        <h1 className="truncate text-lg font-bold sm:text-xl">{title}</h1>
      </div>

      <div className="ml-auto hidden w-full max-w-md items-center sm:flex">
        <div className="relative w-full">
          <Search
            size={18}
            className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-textMuted"
          />
          <input
            type="search"
            aria-label="Busqueda visual"
            placeholder="Buscar proyectos, tareas o miembros"
            className="w-full rounded-lg border border-border bg-slate-50 py-2.5 pl-10 pr-4 text-sm outline-none transition focus:border-accent focus:bg-white focus:ring-2 focus:ring-accent/15"
          />
        </div>
      </div>

      <button
        type="button"
        aria-label="Notificaciones"
        title="Vista visual, disponible en un sprint futuro"
        className="relative rounded-lg p-2.5 text-textMuted hover:bg-violet-50 hover:text-accent"
      >
        <Bell size={20} />
        <span className="absolute right-2 top-2 h-2 w-2 rounded-full bg-danger ring-2 ring-white" />
      </button>

      <div className="relative">
        <button
          type="button"
          className="grid h-10 w-10 shrink-0 place-items-center rounded-full bg-gradient-to-br from-primary to-accent text-sm font-bold text-white"
          aria-label={`Abrir menu de ${user?.nombre ?? 'usuario'}`}
          aria-expanded={menuOpen}
          onClick={() => setMenuOpen((current) => !current)}
        >
          {initials}
        </button>
        {menuOpen && (
          <div className="absolute right-0 mt-2 w-56 rounded-xl border border-border bg-white p-2 shadow-lg">
            <div className="border-b border-border px-3 py-2">
              <p className="truncate text-sm font-semibold">{user?.nombre}</p>
              <p className="truncate text-xs text-textMuted">{user?.correo}</p>
            </div>
            <button
              type="button"
              className="mt-1 flex w-full items-center gap-2 rounded-lg px-3 py-2 text-sm text-danger hover:bg-red-50"
              onClick={handleLogout}
            >
              <LogOut size={17} />
              Cerrar sesion
            </button>
          </div>
        )}
      </div>
    </header>
  )
}

export default Topbar
