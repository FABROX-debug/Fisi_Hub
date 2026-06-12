import { Network } from 'lucide-react'
import { Link } from 'react-router-dom'

function AuthLayout({ children, title, subtitle }) {
  return (
    <main className="grid min-h-screen bg-surface lg:grid-cols-2">
      <section className="hidden bg-gradient-to-br from-primary via-primary to-accent p-12 text-white lg:flex lg:flex-col lg:justify-between">
        <Link to="/" className="flex items-center gap-3 text-xl font-extrabold">
          <span className="grid h-11 w-11 place-items-center rounded-xl bg-white/15">
            <Network size={24} />
          </span>
          FISIHUB
        </Link>
        <div className="max-w-lg">
          <p className="text-sm font-bold uppercase tracking-[0.2em] text-violet-300">
            Organizacion para equipos
          </p>
          <blockquote className="mt-5 text-4xl font-extrabold leading-tight">
            El trabajo en equipo empieza con organizacion.
          </blockquote>
          <p className="mt-5 text-violet-100">
            Gestiona proyectos academicos y de software de forma visual,
            directa y segura.
          </p>
        </div>
        <p className="text-sm text-violet-300">FISIHUB MVP - Sprint 2</p>
      </section>

      <section className="flex items-center justify-center px-5 py-10 sm:px-8">
        <div className="w-full max-w-md">
          <Link
            to="/"
            className="mb-8 flex items-center gap-2 font-extrabold text-primary lg:hidden"
          >
            <Network size={24} className="text-accent" />
            FISIHUB
          </Link>
          <h1 className="text-3xl font-extrabold tracking-tight">{title}</h1>
          <p className="mt-2 text-textMuted">{subtitle}</p>
          <div className="mt-8">{children}</div>
        </div>
      </section>
    </main>
  )
}

export default AuthLayout

