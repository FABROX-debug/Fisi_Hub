import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import AppLayout from './layouts/AppLayout'
import Dashboard from './pages/Dashboard'
import Home from './pages/Home'
import Kanban from './pages/Kanban'
import Members from './pages/Members'
import Projects from './pages/Projects'
import Reports from './pages/Reports'
import Settings from './pages/Settings'
import Tasks from './pages/Tasks'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<AppLayout />}>
          <Route index element={<Home />} />
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="proyectos" element={<Projects />} />
          <Route path="tareas" element={<Tasks />} />
          <Route path="kanban" element={<Kanban />} />
          <Route path="miembros" element={<Members />} />
          <Route path="reportes" element={<Reports />} />
          <Route path="configuracion" element={<Settings />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App
