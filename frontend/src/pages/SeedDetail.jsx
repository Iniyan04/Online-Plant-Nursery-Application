import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { getSeedById } from '../api/client.js'
import { useAuth } from '../context/AuthContext.jsx'

export default function SeedDetail() {
  const { id } = useParams()
  const { auth } = useAuth()
  const [seed, setSeed] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    setError('')

    getSeedById(id)
      .then((data) => {
        if (!cancelled) setSeed(data)
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
  }, [id])

  if (loading) {
    return (
      <div className="state-block">
        <p className="page-title">Loading seed…</p>
      </div>
    )
  }

  if (error) {
    return (
      <div className="state-block">
        <p className="page-title">Couldn't find that seed</p>
        <p>{error}</p>
        <Link className="btn btn-outline mt-24" to="/seeds">
          Back to catalog
        </Link>
      </div>
    )
  }

  const inStock = seed.seedsStock > 0

  return (
    <div>
      <Link to="/seeds" className="btn-ghost" style={{ marginBottom: 24, display: 'inline-block' }}>
        ← Back to catalog
      </Link>

      <div className="detail-card">
        <div className="plant-tag-type">{seed.typeOfSeeds || 'Seed'}</div>
        <h1>{seed.commonName}</h1>
        <p className="detail-desc">{seed.seedsDescription}</p>

        <div className="detail-specs">
          <div>
            <div className="spec-label">Bloom time</div>
            <div className="spec-value">{seed.bloomTime}</div>
          </div>
          <div>
            <div className="spec-label">Watering</div>
            <div className="spec-value">{seed.watering}</div>
          </div>
          <div>
            <div className="spec-label">Difficulty</div>
            <div className="spec-value">{seed.difficultyLevel}</div>
          </div>
          <div>
            <div className="spec-label">Ideal temperature</div>
            <div className="spec-value">{seed.temparature}</div>
          </div>
          <div>
            <div className="spec-label">Seeds per packet</div>
            <div className="spec-value">{seed.seedsPerPacket}</div>
          </div>
        </div>

        <div className="detail-price-row">
          <div>
            <div className="spec-label">Price</div>
            <div className="detail-price">₹{seed.seedsCost.toFixed(2)}</div>
          </div>
          <div style={{ textAlign: 'right' }}>
            <div className="spec-label">Stock</div>
            <div className="spec-value">
              {inStock ? `${seed.seedsStock} available` : 'Out of stock'}
            </div>
          </div>
        </div>

        {inStock && (
          <div className="detail-actions">
            {auth?.role === 'customer' ? (
              <Link to={`/seeds/${seed.seedId}/order`} className="btn btn-primary">
                Order seeds
              </Link>
            ) : (
              <Link to="/login" className="btn btn-primary">
                Log in to order
              </Link>
            )}
          </div>
        )}
      </div>
    </div>
  )
}
