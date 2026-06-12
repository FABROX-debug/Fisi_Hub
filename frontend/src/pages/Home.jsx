import { ArrowRight, CheckSquare2, Network, Users } from 'lucide-react'
import { Link } from 'react-router-dom'

function Home() {
  return (
    <main className="min-h-screen bg-primary text-white">
      <nav className="mx-auto flex max-w-7xl items-center justify-between px-6 py-6">
        <Link to="/" className="flex items-center gap-3 font-extrabold">
          <span className="grid h-10 w-10 place-items-center rounded-xl bg-accent">
            <Network size={22} />
          </span>
          FISIHUB
        </Link>
        <div className="flex items-center gap-3">
          <Link
            to="/login"
            className="rounded-lg px-4 py-2 text-sm font-semibold text-violet-100 hover:bg-white/10"
          >
            Iniciar sesion
          </Link>
          <Link
            to="/register"
            className="rounded-lg bg-white px-4 py-2 text-sm font-semibold text-accent hover:bg-violet-50"
          >
            Crear cuenta
          </Link>
        </div>
      </nav>

      <section className="mx-auto grid max-w-7xl items-center gap-12 px-6 py-20 lg:grid-cols-2 lg:py-28">
        <div>
          <p className="text-sm font-bold uppercase tracking-[0.2em] text-violet-300">
            Gestion simple para equipos reales
          </p>
          <h1 className="mt-5 text-4xl font-extrabold leading-tight sm:text-5xl">
            Tu equipo. Tus proyectos. Todo en un solo lugar.
          </h1>
          <p className="mt-6 max-w-xl text-lg leading-8 text-violet-100">
            Planifica, asigna y controla el avance de tus proyectos academicos
            y de software sin complejidad innecesaria.
          </p>
          <div className="mt-8 flex flex-wrap gap-3">
            <Link
              to="/register"
              className="inline-flex items-center gap-2 rounded-lg bg-white px-5 py-3 font-semibold text-accent hover:bg-violet-50"
            >
              Empezar gratis <ArrowRight size={18} />
            </Link>
            <Link
              to="/login"
              className="rounded-lg border border-white/30 px-5 py-3 font-semibold hover:bg-white/10"
            >
              Ya tengo cuenta
            </Link>
          </div>
        </div>

        <div className="rounded-2xl border border-white/15 bg-white/10 p-6 shadow-2xl backdrop-blur">
          <div className="grid gap-4 sm:grid-cols-2">
            <div className="rounded-xl bg-white p-5 text-textPrimary">
              <CheckSquare2 className="text-accent" />
              <p className="mt-4 font-bold">Trabajo organizado</p>
              <p className="mt-1 text-sm text-textMuted">
                Proyectos y tareas claras para todo el equipo.
              </p>
            </div>
            <div className="rounded-xl bg-white p-5 text-textPrimary">
              <Users className="text-accent" />
              <p className="mt-4 font-bold">Equipo conectado</p>
              <p className="mt-1 text-sm text-textMuted">
                Roles y responsabilidades visibles desde el inicio.
              </p>
            </div>
          </div>
        </div>
      </section>
    </main>
  )
}

export default Home
