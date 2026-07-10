import { useEffect } from 'react'
import {
  BrowserRouter,
  Navigate,
  Outlet,
  Route,
  Routes,
} from 'react-router-dom'
import ProtectedRoute from './components/ProtectedRoute'
import AdminRoute from './components/AdminRoute'
import AppLayout from './layouts/AppLayout'
import Dashboard from './pages/Dashboard'
import ForgotPassword from './pages/ForgotPassword'
import Home from './pages/Home'
import Kanban from './pages/Kanban'
import Login from './pages/Login'
import Members from './pages/Members'
import MyWork from './pages/MyWork'
import Projects from './pages/Projects'
import ProjectDetail from './pages/ProjectDetail'
import Register from './pages/Register'
import ResetPassword from './pages/ResetPassword'
import Reports from './pages/Reports'
import Settings from './pages/Settings'
import TaskDetail from './pages/TaskDetail'
import Tasks from './pages/Tasks'
import Workspaces from './pages/Workspaces'
import Notifications from './pages/Notifications'
import Admin from './pages/Admin'
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
      <Route path="/forgot-password" element={<ForgotPassword />} />
      <Route path="/reset-password/:token" element={<ResetPassword />} />

      <Route element={<PublicOnlyRoute />}>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
      </Route>

      <Route element={<ProtectedRoute />}>
        <Route element={<AppLayout />}>
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/espacios" element={<Workspaces />} />
          <Route path="/proyectos" element={<Projects />} />
          <Route path="/proyectos/:id" element={<ProjectDetail />} />
          <Route path="/mi-trabajo" element={<MyWork />} />
          <Route path="/tareas" element={<Tasks />} />
          <Route path="/tareas/:id" element={<TaskDetail />} />
          <Route path="/kanban" element={<Kanban />} />
          <Route path="/miembros" element={<Members />} />
          <Route path="/reportes" element={<Reports />} />
          <Route path="/notificaciones" element={<Notifications />} />
          <Route path="/configuracion" element={<Settings />} />
          <Route element={<AdminRoute />}>
            <Route path="/administracion" element={<Admin />} />
          </Route>
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
