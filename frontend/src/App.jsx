import { useEffect } from 'react'
import {
  BrowserRouter,
  Navigate,
  Outlet,
  Route,
  Routes,
} from 'react-router-dom'
import ProtectedRoute from './components/ProtectedRoute'
import AppLayout from './layouts/AppLayout'
import Dashboard from './pages/Dashboard'
import Home from './pages/Home'
import Kanban from './pages/Kanban'
import Login from './pages/Login'
import Members from './pages/Members'
import Projects from './pages/Projects'
import Register from './pages/Register'
import Reports from './pages/Reports'
import Settings from './pages/Settings'
import Tasks from './pages/Tasks'
import useAuthStore from './store/authStore'

function PublicOnlyRoute() {
  const { token, initialized } = useAuthStore()

  if (!initialized) {
    return (
      <div className="grid min-h-screen place-items-center bg-surface text-sm text-textMuted">
        Verificando sesion...
      </div>
    )
  }

  return token ? <Navigate to="/dashboard" replace /> : <Outlet />
}

function AppRoutes() {
  const initialize = useAuthStore((state) => state.initialize)

  useEffect(() => {
    initialize()
  }, [initialize])

  return (
    <Routes>
      <Route path="/" element={<Home />} />

      <Route element={<PublicOnlyRoute />}>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
      </Route>

      <Route element={<ProtectedRoute />}>
        <Route element={<AppLayout />}>
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/proyectos" element={<Projects />} />
          <Route path="/tareas" element={<Tasks />} />
          <Route path="/kanban" element={<Kanban />} />
          <Route path="/miembros" element={<Members />} />
          <Route path="/reportes" element={<Reports />} />
          <Route path="/configuracion" element={<Settings />} />
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}

function App() {
  return (
    <BrowserRouter>
      <AppRoutes />
    </BrowserRouter>
  )
}

export default App
