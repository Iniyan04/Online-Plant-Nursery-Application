import { useEffect, useMemo, useRef, useState } from 'react'
import { getPlanters } from '../api/client.js'
import EmptyState from '../components/EmptyState.jsx'
import { CatalogSkeleton } from '../components/LoadingBlock.jsx'
import PlanterTag from '../components/PlanterTag.jsx'

const COST_FILTERS = [
  { label: 'All', min: null, max: null },
  { label: 'Under Rs.100', min: 0, max: 99 },
  { label: 'Rs.100-Rs.200', min: 100, max: 200 },
  { label: 'Over Rs.200', min: 201, max: 9999 }
]

const SORT_OPTIONS = [
  { value: 'default', label: 'Featured' },
  { value: 'price-asc', label: 'Price Low to High' },
  { value: 'price-desc', label: 'Price High to Low' },
  { value: 'name-asc', label: 'Name A-Z' }
]

export default function PlanterCatalog() {
  const [planters, setPlanters] = useState([])
  const [activeFilter, setActiveFilter] = useState('All')
  const [search, setSearch] = useState('')
  const [sortBy, setSortBy] = useState('default')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const collectionRef = useRef(null)

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    setError('')

    const filter = COST_FILTERS.find((item) => item.label === activeFilter) ?? COST_FILTERS[0]
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

  const visiblePlanters = useMemo(() => {
    const query = search.trim().toLowerCase()
    let next = planters.filter((planter) => {
      if (!query) return true
      return [
        planter.planterShape,
        `${planter.planterCapacity}L planter`,
        `${planter.planterheight}cm`,
        `${planter.drainageHoles} drainage holes`
      ].some((value) => String(value || '').toLowerCase().includes(query))
    })

    if (sortBy === 'price-asc') {
      next = [...next].sort((a, b) => a.planterCost - b.planterCost)
    } else if (sortBy === 'price-desc') {
      next = [...next].sort((a, b) => b.planterCost - a.planterCost)
    } else if (sortBy === 'name-asc') {
      next = [...next].sort((a, b) => a.planterShape.localeCompare(b.planterShape))
    }

    return next
  }, [planters, search, sortBy])

  return (
    <div className="catalog-shell page-fade-in">
      <section className="catalog-hero catalog-hero-premium">
        <div className="catalog-hero-copy">
          <span className="catalog-badge">Premium planter collection</span>
          <h1 className="page-title">Planters designed to elevate every corner</h1>
          <p className="page-sub">
            Browse nursery-ready planters with clean silhouettes, balanced proportions, and practical sizing for indoor and outdoor spaces.
          </p>
          <div className="catalog-hero-actions">
            <button
              type="button"
              className="btn btn-primary"
              onClick={() => collectionRef.current?.scrollIntoView({ behavior: 'smooth', block: 'start' })}
            >
              Explore Collection
            </button>
          </div>
        </div>
        <div className="hero-stat-card hero-stat-card-premium">
          <span className="hero-stat-label">Available range</span>
          <strong>{loading ? '--' : visiblePlanters.length}</strong>
          <span className="hero-stat-copy">planters visible in this price range</span>
        </div>
      </section>

      <section className="toolbar toolbar-catalog" ref={collectionRef}>
        <div className="catalog-tools">
          <div className="field search-field search-field-catalog">
            <label htmlFor="planterSearch">Search planters</label>
            <input
              id="planterSearch"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search by shape, size, or planter details"
            />
          </div>

          <div className="field sort-field">
            <label htmlFor="planterSort">Sort by</label>
            <select id="planterSort" value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
              {SORT_OPTIONS.map((option) => (
                <option key={option.value} value={option.value}>{option.label}</option>
              ))}
            </select>
          </div>
        </div>

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
      </section>

      {error && <div className="alert alert-error--dark">{error}</div>}
      {loading && <CatalogSkeleton count={8} />}

      {!loading && !error && visiblePlanters.length === 0 && (
        <EmptyState
          icon="Planter"
          eyebrow="No matches"
          title="No planters match this view"
          message="Try another price band or search phrase to explore more planters."
          compact
        />
      )}

      {!loading && visiblePlanters.length > 0 && (
        <div className="product-grid">
          {visiblePlanters.map((planter, index) => (
            <PlanterTag key={planter.planterId} planter={planter} index={index} />
          ))}
        </div>
      )}
    </div>
  )
}
