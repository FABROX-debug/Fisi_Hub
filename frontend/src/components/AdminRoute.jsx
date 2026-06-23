import { Navigate, Outlet } from 'react-router-dom'
import useAuthStore from '../store/authStore'

function AdminRoute() {
  const user = useAuthStore((state) => state.user)
  return user?.roles?.includes('ADMIN')
    ? <Outlet />
    : <Navigate to="/dashboard" replace />
}

export default AdminRoute
