import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { getPlanterById } from '../api/client.js'
import EmptyState from '../components/EmptyState.jsx'
import { DetailSkeleton } from '../components/LoadingBlock.jsx'
import { useAuth } from '../context/AuthContext.jsx'

export default function PlanterDetail() {
  const { id } = useParams()
  const { auth } = useAuth()
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
    return <DetailSkeleton />
  }

  if (error) {
    return (
      <EmptyState
        icon="🪴"
        eyebrow="Planter detail"
        title="Couldn't find that planter"
        message={error}
        action={<Link className="btn btn-outline" to="/planters">Back to catalog</Link>}
      />
    )
  }

  return (
    <div className="page-fade-in">
      <Link to="/planters" className="btn btn-outline btn-sm back-link">
        Back to catalog
      </Link>

      <div className="detail-shell">
        <div className="detail-media-card">
          {planter.imageUrl ? (
            <img src={planter.imageUrl} alt={`${planter.planterShape} planter`} className="detail-hero" />
          ) : (
            <div className="detail-hero-placeholder">🪴</div>
          )}
          <div className="detail-summary-strip">
            <span className="detail-summary-pill">{planter.planterShape}</span>
            <span className={`detail-summary-pill ${planter.planterStock > 0 ? '' : 'danger'}`}>
              {planter.planterStock > 0 ? `${planter.planterStock} in stock` : 'Out of stock'}
            </span>
          </div>
        </div>

        <div className="detail-card">
          <div className="plant-tag-type">{planter.planterShape}</div>
          <h1>{planter.planterShape} planter</h1>

          <div className="detail-specs">
            <div className="detail-spec-card">
              <div className="spec-label">Height</div>
              <div className="spec-value">{planter.planterheight} cm</div>
            </div>
            <div className="detail-spec-card">
              <div className="spec-label">Capacity</div>
              <div className="spec-value">{planter.planterCapacity} L</div>
            </div>
            <div className="detail-spec-card">
              <div className="spec-label">Drainage holes</div>
              <div className="spec-value">{planter.drainageHoles}</div>
            </div>
            <div className="detail-spec-card">
              <div className="spec-label">Color code</div>
              <div className="spec-value">{planter.planterColor}</div>
            </div>
            {planter.plants && (
              <div className="detail-spec-card">
                <div className="spec-label">Suggested plant</div>
                <div className="spec-value">
                  <Link to={`/plants/${planter.plants.plantId}`}>{planter.plants.commonName}</Link>
                </div>
              </div>
            )}
            {planter.seeds && (
              <div className="detail-spec-card">
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
              <div className="detail-price">₹{Number(planter.planterCost).toFixed(2)}</div>
            </div>
            <div className="detail-status-card">
              <div className="spec-label">Stock</div>
              <div className="spec-value">
                {planter.planterStock > 0 ? `${planter.planterStock} available` : 'Out of stock'}
              </div>
            </div>
          </div>

          {planter.planterStock > 0 && (
            <div className="detail-actions">
              {auth?.role === 'customer' ? (
                <Link to={`/planters/${planter.planterId}/order`} className="btn btn-primary">
                  Order planter
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
    </div>
  )
}
