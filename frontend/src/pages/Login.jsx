import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import Button from '../components/ui/Button'
import Input from '../components/ui/Input'
import Toast from '../components/ui/Toast'
import AuthLayout from '../layouts/AuthLayout'
import useAuthStore from '../store/authStore'

function Login() {
  const navigate = useNavigate()
  const location = useLocation()
  const login = useAuthStore((state) => state.login)
  const loading = useAuthStore((state) => state.loading)
  const [form, setForm] = useState({ correo: '', password: '' })
  const [error, setError] = useState('')

  const handleChange = (event) => {
    setForm((current) => ({
      ...current,
      [event.target.name]: event.target.value,
    }))
    setError('')
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    try {
      await login(form)
      const destination = location.state?.from?.pathname || '/dashboard'
      navigate(destination, { replace: true })
    } catch (requestError) {
      setError(requestError.message)
    }
  }

  return (
    <AuthLayout
      title="Bienvenido de nuevo"
      subtitle="Ingresa a tu equipo y continua organizando el trabajo."
    >
      {error && (
        <Toast
          variant="error"
          title="No se pudo iniciar sesion"
          message={error}
          className="mb-5 max-w-none"
        />
      )}

      <form className="space-y-5" onSubmit={handleSubmit}>
        <Input
          id="login-correo"
          name="correo"
          type="email"
          label="Correo electronico"
          placeholder="nombre@correo.com"
          autoComplete="email"
          required
          value={form.correo}
          onChange={handleChange}
        />
        <Input
          id="login-password"
          name="password"
          type="password"
          label="Contrasena"
          placeholder="Tu contrasena"
          autoComplete="current-password"
          required
          value={form.password}
          onChange={handleChange}
        />
        <div className="flex justify-end">
          <Link
            to="/forgot-password"
            className="text-sm font-medium text-accent hover:underline"
          >
            Olvide mi contrasena
          </Link>
        </div>
        <Button type="submit" className="w-full" disabled={loading}>
          {loading ? 'Ingresando...' : 'Ingresar'}
        </Button>
      </form>

      <p className="mt-6 text-center text-sm text-textMuted">
        No tienes cuenta?{' '}
        <Link
          to="/register"
          className="font-semibold text-accent hover:underline"
        >
          Registrate
        </Link>
      </p>
    </AuthLayout>
  )
}

export default Login
