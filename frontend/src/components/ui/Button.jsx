const variants = {
  primary: 'bg-accent text-white hover:bg-violet-800 shadow-sm',
  secondary:
    'border border-border bg-white text-textPrimary hover:bg-slate-50',
  danger: 'bg-danger text-white hover:bg-red-600 shadow-sm',
  ghost: 'bg-transparent text-accent hover:bg-violet-50',
}

function Button({
  children,
  variant = 'primary',
  className = '',
  type = 'button',
  ...props
}) {
  return (
    <button
      type={type}
      className={`inline-flex items-center justify-center gap-2 rounded-lg px-4 py-2.5 text-sm font-semibold transition duration-150 active:scale-[0.97] disabled:cursor-not-allowed disabled:opacity-50 ${variants[variant] ?? variants.primary} ${className}`}
      {...props}
    >
      {children}
    </button>
  )
}

export default Button

