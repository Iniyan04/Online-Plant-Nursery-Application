import { useEffect, useMemo, useRef, useState } from 'react'
import { getPlants } from '../api/client.js'
import EmptyState from '../components/EmptyState.jsx'
import { CatalogSkeleton } from '../components/LoadingBlock.jsx'
import PlantTag from '../components/PlantTag.jsx'

const TYPE_FILTERS = ['All', 'Indoor', 'Outdoor', 'Flowering', 'Succulent']
const SORT_OPTIONS = [
  { value: 'default', label: 'Featured' },
  { value: 'price-asc', label: 'Price Low to High' },
  { value: 'price-desc', label: 'Price High to Low' },
  { value: 'name-asc', label: 'Name A-Z' }
]

export default function PlantCatalog() {
  const [plants, setPlants] = useState([])
  const [activeType, setActiveType] = useState('All')
  const [search, setSearch] = useState('')
  const [sortBy, setSortBy] = useState('default')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const collectionRef = useRef(null)

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

  const visiblePlants = useMemo(() => {
    const query = search.trim().toLowerCase()
    let next = plants.filter((plant) => {
      if (!query) return true
      return [
        plant.commonName,
        plant.typeOfPlant,
        plant.plantDescription
      ].some((value) => String(value || '').toLowerCase().includes(query))
    })

    if (sortBy === 'price-asc') {
      next = [...next].sort((a, b) => a.plantCost - b.plantCost)
    } else if (sortBy === 'price-desc') {
      next = [...next].sort((a, b) => b.plantCost - a.plantCost)
    } else if (sortBy === 'name-asc') {
      next = [...next].sort((a, b) => a.commonName.localeCompare(b.commonName))
    }

    return next
  }, [plants, search, sortBy])

  return (
    <div className="catalog-shell page-fade-in">
      <section className="catalog-hero catalog-hero-premium">
        <div className="catalog-hero-copy">
          <span className="catalog-badge">Curated greenhouse picks</span>
          <h1 className="page-title">Plants that make your space feel alive</h1>
          <p className="page-sub">
            Discover indoor favorites, flowering accents, and easy-care greens selected for a calm, premium nursery shopping experience.
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
          <span className="hero-stat-label">Live catalog</span>
          <strong>{loading ? '--' : visiblePlants.length}</strong>
          <span className="hero-stat-copy">plants currently matching your view</span>
        </div>
      </section>

      <section className="toolbar toolbar-catalog" ref={collectionRef}>
        <div className="catalog-tools">
          <div className="field search-field search-field-catalog">
            <label htmlFor="plantSearch">Search plants</label>
            <input
              id="plantSearch"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search by name, type, or description"
            />
          </div>

          <div className="field sort-field">
            <label htmlFor="plantSort">Sort by</label>
            <select id="plantSort" value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
              {SORT_OPTIONS.map((option) => (
                <option key={option.value} value={option.value}>{option.label}</option>
              ))}
            </select>
          </div>
        </div>

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
      </section>

      {error && <div className="alert alert-error--dark">{error}</div>}
      {loading && <CatalogSkeleton count={8} />}

      {!loading && !error && visiblePlants.length === 0 && (
        <EmptyState
          icon="Plant"
          eyebrow="No matches"
          title="No plants match this view"
          message="Try a different search term or switch the current filter to discover more nursery picks."
          compact
        />
      )}

      {!loading && visiblePlants.length > 0 && (
        <div className="product-grid">
          {visiblePlants.map((plant, index) => (
            <PlantTag key={plant.plantId} plant={plant} index={index} />
          ))}
        </div>
      )}
    </div>
  )
}
