import { useEffect, useState } from 'react'
import { getPlants } from '../api/client.js'
import PlantTag from '../components/PlantTag.jsx'

// Common plant types seen in the seed data — the filter is still driven by
// whatever the backend returns via ?type=, this is just a friendly shortlist.
const TYPE_FILTERS = ['All', 'Indoor', 'Outdoor', 'Flowering', 'Succulent']

export default function PlantCatalog() {
  const [plants, setPlants] = useState([])
  const [activeType, setActiveType] = useState('All')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    setError('')

    getPlants(activeType === 'All' ? undefined : activeType)
      .then((data) => {
        if (!cancelled) setPlants(data)
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
      <h1 className="page-title">Plant catalog</h1>
      <p className="page-sub">
        Browse everything currently in stock at the nursery. Tap a tag to see full details.
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
          <p className="page-title">Loading the greenhouse…</p>
        </div>
      )}

      {!loading && !error && plants.length === 0 && (
        <div className="state-block">
          <p className="page-title">No plants here yet</p>
          <p>Try a different filter, or check back once the admin adds new stock.</p>
        </div>
      )}

      {!loading && plants.length > 0 && (
        <div className="product-grid">
          {plants.map((plant) => (
            <PlantTag key={plant.plantId} plant={plant} />
          ))}
        </div>
      )}
    </div>
  )
}
