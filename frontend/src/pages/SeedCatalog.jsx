import { useEffect, useState } from 'react'
import { getSeeds } from '../api/client.js'
import SeedTag from '../components/SeedTag.jsx'

const TYPE_FILTERS = ['All', 'Herb', 'Vegetable', 'Flower', 'Fruit']

export default function SeedCatalog() {
  const [seeds, setSeeds] = useState([])
  const [activeType, setActiveType] = useState('All')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    setError('')

    getSeeds(activeType === 'All' ? undefined : activeType)
      .then((data) => {
        if (!cancelled) setSeeds(data)
      })
      .catch((err) => {
        if (!cancelled) setError(err.message)
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })

    return () => {
      cancelled = true
    }
  }, [activeType])

  return (
    <div>
      <h1 className="page-title">Seed catalog</h1>
      <p className="page-sub">
        Browse seed packets ready to sow. Tap a tag to see growing details and place an order.
      </p>

      <div className="toolbar">
        <div className="filter-pills">
          {TYPE_FILTERS.map((type) => (
            <button
              key={type}
              className={`pill ${activeType === type ? 'active' : ''}`}
              onClick={() => setActiveType(type)}
            >
              {type}
            </button>
          ))}
        </div>
      </div>

      {error && <div className="alert alert-error--dark">{error}</div>}

      {loading && (
        <div className="state-block">
          <p className="page-title">Loading seeds…</p>
        </div>
      )}

      {!loading && !error && seeds.length === 0 && (
        <div className="state-block">
          <p className="page-title">No seeds here yet</p>
          <p>Try a different filter, or check back once the admin adds new stock.</p>
        </div>
      )}

      {!loading && seeds.length > 0 && (
        <div className="product-grid">
          {seeds.map((seed) => (
            <SeedTag key={seed.seedId} seed={seed} />
          ))}
        </div>
      )}
    </div>
  )
}
