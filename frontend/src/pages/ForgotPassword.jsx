import { useState } from 'react'
import { Link } from 'react-router-dom'
import Button from '../components/ui/Button'
import Input from '../components/ui/Input'
import Toast from '../components/ui/Toast'
import AuthLayout from '../layouts/AuthLayout'
import { forgotPassword } from '../services/authService'

function ForgotPassword() {
  const [correo, setCorreo] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [response, setResponse] = useState(null)

  const handleSubmit = async (event) => {
    event.preventDefault()
    setLoading(true)
    setError('')
    setResponse(null)

    try {
      const data = await forgotPassword({ correo: correo.trim() })
      setResponse(data)
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <AuthLayout
      title="Recupera tu cuenta"
      subtitle="Te enviaremos un enlace para restablecer tu contrasena."
    >
      {error && (
        <Toast
          variant="error"
          title="No se pudo procesar la solicitud"
          message={error}
          className="mb-5 max-w-none"
        />
      )}

      {response && (
        <Toast
          variant="success"
          title="Solicitud registrada"
          message={response.message}
          className="mb-5 max-w-none"
        />
      )}

      <form className="space-y-5" onSubmit={handleSubmit}>
        <Input
          id="forgot-password-correo"
          name="correo"
          type="email"
          label="Correo electronico"
          placeholder="nombre@correo.com"
          autoComplete="email"
          required
          value={correo}
          onChange={(event) => setCorreo(event.target.value)}
        />
        <Button type="submit" className="w-full" disabled={loading}>
          {loading ? 'Enviando...' : 'Enviar enlace'}
        </Button>
      </form>

      {response?.previewUrl && (
        <div className="mt-5 rounded-xl border border-dashed border-accent/30 bg-violet-50 p-4">
          <p className="text-sm font-semibold text-primary">
            Enlace local de desarrollo
          </p>
          <p className="mt-1 text-sm text-textMuted">
            El correo SMTP no es obligatorio en local. Puedes abrir el enlace
            generado directamente.
          </p>
          <a
            href={response.previewUrl}
            className="mt-3 inline-flex text-sm font-semibold text-accent hover:underline"
          >
            Abrir enlace de recuperacion
          </a>
        </div>
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

export default ForgotPassword
