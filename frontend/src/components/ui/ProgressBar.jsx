function ProgressBar({ value = 0, label = 'Progreso', className = '' }) {
  const percentage = Math.min(100, Math.max(0, Number(value) || 0))

  return (
    <div className={className}>
      <div className="mb-2 flex items-center justify-between gap-4 text-sm">
        <span className="font-medium text-textPrimary">{label}</span>
        <span className="font-mono font-semibold text-accent">
          {percentage}%
        </span>
      </div>
      <div
        className="h-3 overflow-hidden rounded-full bg-slate-200"
        role="progressbar"
        aria-label={label}
        aria-valuemin="0"
        aria-valuemax="100"
        aria-valuenow={percentage}
      >
        <div
          className="relative h-full overflow-hidden rounded-full bg-gradient-to-r from-primary via-accent to-accentLight transition-[width] duration-700 ease-out"
          style={{ width: `${percentage}%` }}
        >
          <span className="absolute inset-0 animate-shimmer bg-gradient-to-r from-transparent via-white/40 to-transparent" />
        </div>
      </div>
    </div>
  )
}

export default ProgressBar

