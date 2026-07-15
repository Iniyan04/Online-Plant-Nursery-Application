import { useEffect, useState } from 'react'
import { getPlanters } from '../api/client.js'
import PlanterTag from '../components/PlanterTag.jsx'

const COST_FILTERS = [
  { label: 'All', min: null, max: null },
  { label: 'Under ₹100', min: 0, max: 99 },
  { label: '₹100–₹200', min: 100, max: 200 },
  { label: 'Over ₹200', min: 201, max: 9999 }
]

export default function PlanterCatalog() {
  const [planters, setPlanters] = useState([])
  const [activeFilter, setActiveFilter] = useState('All')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    setError('')

    const filter = COST_FILTERS.find((f) => f.label === activeFilter) ?? COST_FILTERS[0]
    getPlanters(filter.min, filter.max)
      .then((data) => {
        if (!cancelled) setPlanters(data)
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
  }, [activeFilter])

  return (
    <div>
      <h1 className="page-title">Planter catalog</h1>
      <p className="page-sub">
        Find the right pot for your plants. Tap a tag to see dimensions and stock.
      </p>

      <div className="toolbar">
        <div className="filter-pills">
          {COST_FILTERS.map((filter) => (
            <button
              key={filter.label}
              className={`pill ${activeFilter === filter.label ? 'active' : ''}`}
              onClick={() => setActiveFilter(filter.label)}
            >
              {filter.label}
            </button>
          ))}
        </div>
      </div>

      {error && <div className="alert alert-error--dark">{error}</div>}

      {loading && (
        <div className="state-block">
          <p className="page-title">Loading planters…</p>
        </div>
      )}

      {!loading && !error && planters.length === 0 && (
        <div className="state-block">
          <p className="page-title">No planters here yet</p>
          <p>Try a different price range, or check back once the admin adds new stock.</p>
        </div>
      )}

      {!loading && planters.length > 0 && (
        <div className="product-grid">
          {planters.map((planter) => (
            <PlanterTag key={planter.planterId} planter={planter} />
          ))}
        </div>
      )}
    </div>
  )
}
