import { AlertTriangle, CheckCircle2, Info, XCircle } from 'lucide-react'

const variants = {
  success: {
    icon: CheckCircle2,
    classes: 'border-l-success',
    iconClasses: 'text-success',
  },
  error: {
    icon: XCircle,
    classes: 'border-l-danger',
    iconClasses: 'text-danger',
  },
  info: {
    icon: Info,
    classes: 'border-l-info',
    iconClasses: 'text-info',
  },
  warning: {
    icon: AlertTriangle,
    classes: 'border-l-warning',
    iconClasses: 'text-warning',
  },
}

function Toast({
  variant = 'info',
  title,
  message,
  className = '',
}) {
  const config = variants[variant] ?? variants.info
  const Icon = config.icon

  return (
    <div
      role="status"
      className={`flex max-w-sm gap-3 rounded-xl border border-border border-l-4 bg-white p-4 shadow-md ${config.classes} ${className}`}
    >
      <Icon className={`mt-0.5 shrink-0 ${config.iconClasses}`} size={20} />
      <div>
        {title && <p className="text-sm font-semibold">{title}</p>}
        {message && <p className="mt-0.5 text-sm text-textMuted">{message}</p>}
      </div>
    </div>
  )
}

export default Toast

