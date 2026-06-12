import Card from '../components/ui/Card'

function Home() {
  return (
    <Card>
      <p className="text-sm font-semibold uppercase tracking-widest text-accent">
        Sprint 1
      </p>
      <h2 className="mt-2 text-2xl font-bold">FISIHUB funcionando</h2>
      <p className="mt-2 text-textMuted">
        La base visual del MVP esta lista. Usa la navegacion lateral para
        recorrer las rutas iniciales.
      </p>
    </Card>
  )
}

export default Home
