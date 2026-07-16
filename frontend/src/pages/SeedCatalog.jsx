import { useEffect, useMemo, useRef, useState } from 'react'
import { getSeeds } from '../api/client.js'
import EmptyState from '../components/EmptyState.jsx'
import { CatalogSkeleton } from '../components/LoadingBlock.jsx'
import SeedTag from '../components/SeedTag.jsx'

const TYPE_FILTERS = ['All', 'Herb', 'Vegetable', 'Flower', 'Fruit']
const SORT_OPTIONS = [
  { value: 'default', label: 'Featured' },
  { value: 'price-asc', label: 'Price Low to High' },
  { value: 'price-desc', label: 'Price High to Low' },
  { value: 'name-asc', label: 'Name A-Z' }
]

export default function SeedCatalog() {
  const [seeds, setSeeds] = useState([])
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

  const visibleSeeds = useMemo(() => {
    const query = search.trim().toLowerCase()
    let next = seeds.filter((seed) => {
      if (!query) return true
      return [
        seed.commonName,
        seed.typeOfSeeds,
        seed.seedsDescription
      ].some((value) => String(value || '').toLowerCase().includes(query))
    })

    if (sortBy === 'price-asc') {
      next = [...next].sort((a, b) => a.seedsCost - b.seedsCost)
    } else if (sortBy === 'price-desc') {
      next = [...next].sort((a, b) => b.seedsCost - a.seedsCost)
    } else if (sortBy === 'name-asc') {
      next = [...next].sort((a, b) => a.commonName.localeCompare(b.commonName))
    }

    return next
  }, [search, seeds, sortBy])

  return (
    <div className="catalog-shell page-fade-in">
      <section className="catalog-hero catalog-hero-premium">
        <div className="catalog-hero-copy">
          <span className="catalog-badge">Seasonal sowing collection</span>
          <h1 className="page-title">Seed packs ready for your next growing season</h1>
          <p className="page-sub">
            Explore herb, flower, fruit, and vegetable seeds selected to help you grow a thriving nursery corner at home.
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
          <span className="hero-stat-label">Ready to sow</span>
          <strong>{loading ? '--' : visibleSeeds.length}</strong>
          <span className="hero-stat-copy">seed packs in your current selection</span>
        </div>
      </section>

      <section className="toolbar toolbar-catalog" ref={collectionRef}>
        <div className="catalog-tools">
          <div className="field search-field search-field-catalog">
            <label htmlFor="seedSearch">Search seeds</label>
            <input
              id="seedSearch"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search by name, type, or growing notes"
            />
          </div>

          <div className="field sort-field">
            <label htmlFor="seedSort">Sort by</label>
            <select id="seedSort" value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
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

      {!loading && !error && visibleSeeds.length === 0 && (
        <EmptyState
          icon="Seed"
          eyebrow="No matches"
          title="No seeds match this view"
          message="Try adjusting the current filter or search term to see more seed packets."
          compact
        />
      )}

      {!loading && visibleSeeds.length > 0 && (
        <div className="product-grid">
          {visibleSeeds.map((seed, index) => (
            <SeedTag key={seed.seedId} seed={seed} index={index} />
          ))}
        </div>
      )}
    </div>
  )
}
