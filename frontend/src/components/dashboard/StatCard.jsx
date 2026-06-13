import { createElement } from 'react'
import Card from '../ui/Card'

function StatCard({
  label,
  value,
  detail,
  icon,
  color,
  border,
  background,
}) {
  return (
    <Card className={`border-l-4 p-5 ${border}`}>
      <div className="flex items-start justify-between gap-3">
        <div>
          <p className="text-sm font-medium text-textMuted">{label}</p>
          <p className="mt-3 text-3xl font-extrabold">{value}</p>
          <p className="mt-1 text-xs text-textMuted">{detail}</p>
        </div>
        <span
          className={`grid h-10 w-10 place-items-center rounded-lg ${background} ${color}`}
        >
          {createElement(icon, { size: 20 })}
        </span>
      </div>
    </Card>
  )
}

export default StatCard
