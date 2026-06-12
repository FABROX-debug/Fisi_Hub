import { X } from 'lucide-react'
import { useEffect } from 'react'

function Modal({ open, title, children, onClose, className = '' }) {
  useEffect(() => {
    if (!open) return undefined

    const handleKeyDown = (event) => {
      if (event.key === 'Escape') onClose()
    }
    document.addEventListener('keydown', handleKeyDown)
    return () => document.removeEventListener('keydown', handleKeyDown)
  }, [open, onClose])

  if (!open) return null

  return (
    <div
      className="fixed inset-0 z-[80] grid place-items-center bg-slate-950/55 p-4"
      role="presentation"
      onMouseDown={(event) => {
        if (event.target === event.currentTarget) onClose()
      }}
    >
      <section
        role="dialog"
        aria-modal="true"
        aria-label={title}
        className={`max-h-[90vh] w-full max-w-2xl overflow-y-auto rounded-2xl bg-white shadow-xl ${className}`}
      >
        <header className="sticky top-0 z-10 flex items-center justify-between border-b border-border bg-white px-6 py-4">
          <h2 className="text-lg font-bold">{title}</h2>
          <button
            type="button"
            aria-label="Cerrar"
            className="rounded-lg p-2 text-textMuted hover:bg-slate-100"
            onClick={onClose}
          >
            <X size={20} />
          </button>
        </header>
        <div className="p-6">{children}</div>
      </section>
    </div>
  )
}

export default Modal
