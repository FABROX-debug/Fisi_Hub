import Card from './Card'

function PagePlaceholder({ title, description }) {
  return (
    <Card>
      <p className="text-sm font-semibold uppercase tracking-widest text-accent">
        Vista inicial
      </p>
      <h2 className="mt-2 text-2xl font-bold">{title}</h2>
      <p className="mt-2 max-w-2xl text-textMuted">{description}</p>
    </Card>
  )
}

export default PagePlaceholder

