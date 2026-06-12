const styles = {
  pendiente: 'bg-slate-100 text-slate-700',
  'en-proceso': 'bg-blue-100 text-blue-700',
  revision: 'bg-amber-100 text-amber-800',
  completada: 'bg-emerald-100 text-emerald-700',
  bloqueada: 'bg-red-100 text-red-700',
  baja: 'bg-slate-100 text-slate-600',
  media: 'bg-blue-100 text-blue-700',
  alta: 'bg-amber-100 text-amber-800',
  urgente: 'bg-red-100 text-red-700',
}

const labels = {
  pendiente: 'Pendiente',
  'en-proceso': 'En proceso',
  revision: 'En revision',
  completada: 'Completada',
  bloqueada: 'Bloqueada',
  baja: 'Baja',
  media: 'Media',
  alta: 'Alta',
  urgente: 'Urgente',
}

function Badge({ value = 'pendiente', children, className = '' }) {
  return (
    <span
      className={`inline-flex items-center rounded-full px-2.5 py-1 font-mono text-[11px] font-semibold uppercase tracking-wide ${styles[value] ?? styles.pendiente} ${className}`}
    >
      {children ?? labels[value] ?? value}
    </span>
  )
}

export default Badge

