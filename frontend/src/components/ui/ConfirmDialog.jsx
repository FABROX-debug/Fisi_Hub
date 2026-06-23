import Button from './Button'
import Modal from './Modal'

function ConfirmDialog({
  open,
  title,
  message,
  busy,
  confirmLabel = 'Eliminar',
  onCancel,
  onConfirm,
}) {
  return (
    <Modal open={open} title={title} onClose={onCancel} className="max-w-md">
      <p className="text-sm leading-6 text-textMuted">{message}</p>
      <div className="mt-6 flex justify-end gap-3">
        <Button variant="secondary" onClick={onCancel}>
          Cancelar
        </Button>
        <Button variant="danger" disabled={busy} onClick={onConfirm}>
          {busy ? 'Procesando...' : confirmLabel}
        </Button>
      </div>
    </Modal>
  )
}

export default ConfirmDialog
