import { useEffect, useMemo, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import Button from '../components/ui/Button'
import Input from '../components/ui/Input'
import Toast from '../components/ui/Toast'
import AuthLayout from '../layouts/AuthLayout'
import {
  resetPassword,
  validateResetToken,
} from '../services/authService'

function validatePasswords(password, confirmPassword) {
  const errors = {}

  if (password.length < 8 || !/[A-Z]/.test(password) || !/\d/.test(password)) {
    errors.password =
      'Usa al menos 8 caracteres, una mayuscula y un numero'
  }

  if (confirmPassword !== password) {
    errors.confirmPassword = 'Las contrasenas no coinciden'
  }

  return errors
}

function ResetPassword() {
  const { token = '' } = useParams()
  const navigate = useNavigate()
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [tokenInfo, setTokenInfo] = useState(null)
  const [form, setForm] = useState({
    password: '',
    confirmPassword: '',
  })
  const [errors, setErrors] = useState({})

  useEffect(() => {
    let active = true

    async function loadToken() {
      setLoading(true)
      setError('')
      try {
        const response = await validateResetToken(token)
        if (active) {
          setTokenInfo(response)
        }
      } catch (requestError) {
        if (active) {
          setError(requestError.message)
        }
      } finally {
        if (active) {
          setLoading(false)
        }
      }
    }

    if (token) {
      loadToken()
    } else {
      setError('El enlace de recuperacion no es valido')
      setLoading(false)
    }

    return () => {
      active = false
    }
  }, [token])

  const helperText = useMemo(() => {
    if (!tokenInfo?.correo) return null
    return `Actualizaras la contrasena de ${tokenInfo.correo}.`
  }, [tokenInfo])

  const handleChange = (event) => {
    const nextForm = {
      ...form,
      [event.target.name]: event.target.value,
    }
    setForm(nextForm)
    setErrors(validatePasswords(nextForm.password, nextForm.confirmPassword))
    setSuccess('')
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    const nextErrors = validatePasswords(form.password, form.confirmPassword)
    setErrors(nextErrors)
    if (Object.keys(nextErrors).length > 0) return

    setSubmitting(true)
    setError('')
    setSuccess('')

    try {
      const response = await resetPassword({
        token,
        password: form.password,
      })
      setSuccess(response.message)
      setTimeout(() => {
        navigate('/login', { replace: true })
      }, 1200)
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <AuthLayout
      title="Restablece tu contrasena"
      subtitle="Define una nueva contrasena segura para volver a entrar."
    >
      {loading && (
        <Toast
          variant="info"
          title="Validando enlace"
          message="Estamos comprobando que el token siga vigente."
          className="mb-5 max-w-none"
        />
      )}

      {error && (
        <Toast
          variant="error"
          title="No se puede usar este enlace"
          message={error}
          className="mb-5 max-w-none"
        />
      )}

      {success && (
        <Toast
          variant="success"
          title="Contrasena actualizada"
          message={success}
          className="mb-5 max-w-none"
        />
      )}

      {!loading && !error && (
        <form className="space-y-5" onSubmit={handleSubmit} noValidate>
          {helperText && (
            <p className="rounded-xl bg-violet-50 px-4 py-3 text-sm text-textMuted">
              {helperText}
            </p>
          )}
          <Input
            id="reset-password"
            name="password"
            type="password"
            label="Nueva contrasena"
            placeholder="Minimo 8 caracteres"
            autoComplete="new-password"
            required
            value={form.password}
            error={errors.password}
            onChange={handleChange}
          />
          <Input
            id="reset-confirm-password"
            name="confirmPassword"
            type="password"
            label="Confirmar contrasena"
            placeholder="Repite tu nueva contrasena"
            autoComplete="new-password"
            required
            value={form.confirmPassword}
            error={errors.confirmPassword}
            onChange={handleChange}
          />
          <Button type="submit" className="w-full" disabled={submitting}>
            {submitting ? 'Guardando...' : 'Actualizar contrasena'}
          </Button>
        </form>
      )}

      <p className="mt-6 text-center text-sm text-textMuted">
        Volver a{' '}
        <Link to="/login" className="font-semibold text-accent hover:underline">
          iniciar sesion
        </Link>
      </p>
    </AuthLayout>
  )
}

export default ResetPassword
