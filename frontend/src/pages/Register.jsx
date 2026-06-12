import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import Button from '../components/ui/Button'
import Input from '../components/ui/Input'
import Toast from '../components/ui/Toast'
import AuthLayout from '../layouts/AuthLayout'
import useAuthStore from '../store/authStore'

const initialForm = {
  nombre: '',
  correo: '',
  password: '',
  confirmPassword: '',
}

function validate(form) {
  const errors = {}
  if (form.nombre.trim().length < 3) {
    errors.nombre = 'El nombre debe tener al menos 3 caracteres'
  }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.correo)) {
    errors.correo = 'Ingresa un correo valido'
  }
  if (
    form.password.length < 8 ||
    !/[A-Z]/.test(form.password) ||
    !/\d/.test(form.password)
  ) {
    errors.password =
      'Usa al menos 8 caracteres, una mayuscula y un numero'
  }
  if (form.confirmPassword !== form.password) {
    errors.confirmPassword = 'Las contrasenas no coinciden'
  }
  return errors
}

function Register() {
  const navigate = useNavigate()
  const register = useAuthStore((state) => state.register)
  const loading = useAuthStore((state) => state.loading)
  const [form, setForm] = useState(initialForm)
  const [errors, setErrors] = useState({})
  const [requestError, setRequestError] = useState('')

  const handleChange = (event) => {
    const nextForm = {
      ...form,
      [event.target.name]: event.target.value,
    }
    setForm(nextForm)
    setErrors(validate(nextForm))
    setRequestError('')
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    const nextErrors = validate(form)
    setErrors(nextErrors)
    if (Object.keys(nextErrors).length > 0) return

    try {
      await register({
        nombre: form.nombre.trim(),
        correo: form.correo.trim(),
        password: form.password,
      })
      navigate('/dashboard', { replace: true })
    } catch (error) {
      setRequestError(error.message)
      if (error.validationErrors) {
        setErrors((current) => ({
          ...current,
          ...error.validationErrors,
        }))
      }
    }
  }

  return (
    <AuthLayout
      title="Crea tu cuenta"
      subtitle="Empieza a organizar tus proyectos con un perfil seguro."
    >
      {requestError && (
        <Toast
          variant="error"
          title="No se pudo crear la cuenta"
          message={requestError}
          className="mb-5 max-w-none"
        />
      )}

      <form className="space-y-5" onSubmit={handleSubmit} noValidate>
        <Input
          id="register-nombre"
          name="nombre"
          label="Nombre completo"
          placeholder="Fabrizio Huaytalla"
          autoComplete="name"
          required
          value={form.nombre}
          error={errors.nombre}
          onChange={handleChange}
        />
        <Input
          id="register-correo"
          name="correo"
          type="email"
          label="Correo electronico"
          placeholder="nombre@correo.com"
          autoComplete="email"
          required
          value={form.correo}
          error={errors.correo}
          onChange={handleChange}
        />
        <Input
          id="register-password"
          name="password"
          type="password"
          label="Contrasena"
          placeholder="Minimo 8 caracteres"
          autoComplete="new-password"
          required
          value={form.password}
          error={errors.password}
          onChange={handleChange}
        />
        <Input
          id="register-confirm-password"
          name="confirmPassword"
          type="password"
          label="Confirmar contrasena"
          placeholder="Repite tu contrasena"
          autoComplete="new-password"
          required
          value={form.confirmPassword}
          error={errors.confirmPassword}
          onChange={handleChange}
        />
        <Button type="submit" className="w-full" disabled={loading}>
          {loading ? 'Creando cuenta...' : 'Registrarme'}
        </Button>
      </form>

      <p className="mt-6 text-center text-sm text-textMuted">
        Ya tienes cuenta?{' '}
        <Link to="/login" className="font-semibold text-accent hover:underline">
          Inicia sesion
        </Link>
      </p>
    </AuthLayout>
  )
}

export default Register

