import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { getPlanterById } from '../api/client.js'

export default function PlanterDetail() {
  const { id } = useParams()
  const [planter, setPlanter] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    setError('')

    getPlanterById(id)
      .then((data) => {
        if (!cancelled) setPlanter(data)
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
        <p className="page-title">Loading planter…</p>
      </div>
    )
  }

  if (error) {
    return (
      <div className="state-block">
        <p className="page-title">Couldn't find that planter</p>
        <p>{error}</p>
        <Link className="btn btn-outline mt-24" to="/planters">
          Back to catalog
        </Link>
      </div>
    )
  }

  return (
    <div>
      <Link to="/planters" className="btn-ghost" style={{ marginBottom: 24, display: 'inline-block' }}>
        ← Back to catalog
      </Link>

      <div className="detail-card">
        <div className="plant-tag-type">{planter.planterShape}</div>
        <h1>{planter.planterShape} planter</h1>

        <div className="detail-specs">
          <div>
            <div className="spec-label">Height</div>
            <div className="spec-value">{planter.planterheight} cm</div>
          </div>
          <div>
            <div className="spec-label">Capacity</div>
            <div className="spec-value">{planter.planterCapacity} L</div>
          </div>
          <div>
            <div className="spec-label">Drainage holes</div>
            <div className="spec-value">{planter.drainageHoles}</div>
          </div>
          <div>
            <div className="spec-label">Color code</div>
            <div className="spec-value">{planter.planterColor}</div>
          </div>
          {planter.plants && (
            <div>
              <div className="spec-label">Suggested plant</div>
              <div className="spec-value">
                <Link to={`/plants/${planter.plants.plantId}`}>{planter.plants.commonName}</Link>
              </div>
            </div>
          )}
          {planter.seeds && (
            <div>
              <div className="spec-label">Suggested seed</div>
              <div className="spec-value">
                <Link to={`/seeds/${planter.seeds.seedId}`}>{planter.seeds.commonName}</Link>
              </div>
            </div>
          )}
        </div>

        <div className="detail-price-row">
          <div>
            <div className="spec-label">Price</div>
            <div className="detail-price">₹{planter.planterCost}</div>
          </div>
          <div style={{ textAlign: 'right' }}>
            <div className="spec-label">Stock</div>
            <div className="spec-value">
              {planter.planterStock > 0 ? `${planter.planterStock} available` : 'Out of stock'}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
