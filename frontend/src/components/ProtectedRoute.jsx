import { Navigate, Outlet, useLocation } from 'react-router-dom'
import useAuthStore from '../store/authStore'

function ProtectedRoute() {
  const location = useLocation()
  const { token, initialized } = useAuthStore()

  if (!initialized) {
    return (
      <div className="grid min-h-screen place-items-center bg-surface text-sm text-textMuted">
        Verificando sesion...
      </div>
    )
  }

  if (!token) {
    return <Navigate to="/login" state={{ from: location }} replace />
  }

  return <Outlet />
}

export default ProtectedRoute

