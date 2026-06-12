import { useDroppable } from '@dnd-kit/core'
import KanbanCard from './KanbanCard'

const columnStyles = {
  PENDIENTE: 'bg-slate-100/80',
  EN_PROCESO: 'bg-blue-50/80',
  EN_REVISION: 'bg-amber-50/80',
  COMPLETADA: 'bg-emerald-50/80',
  BLOQUEADA: 'bg-red-50/80',
}

function KanbanColumn({
  id,
  title,
  tasks,
  saving,
  onStatusChange,
}) {
  const { isOver, setNodeRef } = useDroppable({ id })

  return (
    <section
      ref={setNodeRef}
      className={`flex min-h-[30rem] w-[18rem] shrink-0 flex-col rounded-2xl border p-3 transition-colors lg:w-auto lg:min-w-[17rem] ${columnStyles[id]} ${isOver ? 'border-accent ring-2 ring-violet-200' : 'border-border'}`}
    >
      <header className="flex items-center justify-between px-1 pb-3">
        <h2 className="text-sm font-extrabold uppercase tracking-wide">
          {title}
        </h2>
        <span className="rounded-full bg-white px-2.5 py-1 font-mono text-xs font-bold text-textMuted shadow-sm">
          {tasks.length}
        </span>
      </header>

      <div className="flex flex-1 flex-col gap-3">
        {tasks.length === 0 ? (
          <div className="grid min-h-28 place-items-center rounded-xl border border-dashed border-slate-300 bg-white/60 px-4 text-center text-xs text-textMuted">
            Arrastra una tarea aqui o cambia su estado.
          </div>
        ) : (
          tasks.map((task) => (
            <KanbanCard
              key={task.id}
              task={task}
              disabled={saving}
              onStatusChange={onStatusChange}
            />
          ))
        )}
      </div>
    </section>
  )
}

export default KanbanColumn
