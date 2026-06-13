import { MessageSquare, Send, Trash2 } from 'lucide-react'
import { useEffect, useState } from 'react'
import {
  createTaskComment,
  deleteComment,
  getTaskComments,
} from '../../services/comentarioService'
import Button from '../ui/Button'
import ConfirmDialog from '../ui/ConfirmDialog'
import Modal from '../ui/Modal'
import Toast from '../ui/Toast'

const formatDateTime = (value) => new Intl.DateTimeFormat('es-PE', {
  dateStyle: 'medium',
  timeStyle: 'short',
}).format(new Date(value))

const initials = (name) => name
  .split(/\s+/)
  .slice(0, 2)
  .map((part) => part[0])
  .join('')
  .toUpperCase()

function TaskCommentsModal({ task, onClose }) {
  const [comments, setComments] = useState([])
  const [content, setContent] = useState('')
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [deleting, setDeleting] = useState(null)

  useEffect(() => {
    if (!task) return
    const load = async () => {
      setLoading(true)
      setError('')
      try {
        setComments(await getTaskComments(task.id))
      } catch (requestError) {
        setError(requestError.message)
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [task])

  const submit = async (event) => {
    event.preventDefault()
    if (!content.trim()) {
      setError('Escribe un comentario antes de enviarlo')
      return
    }
    setSaving(true)
    setError('')
    try {
      const created = await createTaskComment(task.id, content.trim())
      setComments((current) => [...current, created])
      setContent('')
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setSaving(false)
    }
  }

  const confirmDelete = async () => {
    setSaving(true)
    setError('')
    try {
      await deleteComment(deleting.id)
      setComments((current) => current.filter((item) => item.id !== deleting.id))
      setDeleting(null)
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <>
      <Modal
        open={Boolean(task)}
        title={task ? `Comentarios: ${task.titulo}` : 'Comentarios'}
        onClose={onClose}
      >
        {error && (
          <button type="button" className="mb-4 w-full text-left" onClick={() => setError('')}>
            <Toast
              variant="error"
              title="No se pudo completar la operacion"
              message={error}
              className="max-w-none"
            />
          </button>
        )}

        <div className="max-h-80 space-y-4 overflow-y-auto pr-1">
          {loading ? (
            <p className="text-sm text-textMuted">Cargando comentarios...</p>
          ) : comments.length === 0 ? (
            <div className="grid place-items-center rounded-xl border border-dashed border-border py-10 text-center">
              <MessageSquare size={32} className="text-accent" />
              <p className="mt-3 font-semibold">Sin comentarios</p>
              <p className="text-sm text-textMuted">Inicia la conversacion de esta tarea.</p>
            </div>
          ) : comments.map((comment) => (
            <article key={comment.id} className="flex gap-3 rounded-xl bg-slate-50 p-4">
              <div className="grid size-9 shrink-0 place-items-center rounded-full bg-violet-100 text-xs font-bold text-accent">
                {initials(comment.autorNombre)}
              </div>
              <div className="min-w-0 flex-1">
                <div className="flex items-start justify-between gap-3">
                  <div>
                    <p className="text-sm font-bold">{comment.autorNombre}</p>
                    <p className="text-xs text-textMuted">{formatDateTime(comment.creadoEn)}</p>
                  </div>
                  {comment.puedeEliminar && (
                    <button
                      type="button"
                      aria-label="Eliminar comentario"
                      onClick={() => setDeleting(comment)}
                      className="rounded-lg p-1.5 text-textMuted hover:bg-red-50 hover:text-danger"
                    >
                      <Trash2 size={16} />
                    </button>
                  )}
                </div>
                <p className="mt-2 whitespace-pre-wrap text-sm leading-6">{comment.contenido}</p>
              </div>
            </article>
          ))}
        </div>

        <form onSubmit={submit} className="mt-5 border-t border-border pt-5">
          <label htmlFor="comment-content" className="mb-1.5 block text-sm font-semibold">
            Nuevo comentario
          </label>
          <textarea
            id="comment-content"
            rows="3"
            maxLength="2000"
            value={content}
            onChange={(event) => setContent(event.target.value)}
            placeholder="Escribe un comentario..."
            className="w-full resize-none rounded-lg border border-border px-3.5 py-2.5 text-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/15"
          />
          <div className="mt-3 flex justify-end">
            <Button type="submit" disabled={saving || !content.trim()}>
              <Send size={17} />
              {saving ? 'Enviando...' : 'Comentar'}
            </Button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        open={Boolean(deleting)}
        title="Eliminar comentario"
        message="El comentario se eliminara de forma permanente."
        busy={saving}
        onCancel={() => setDeleting(null)}
        onConfirm={confirmDelete}
      />
    </>
  )
}

export default TaskCommentsModal
