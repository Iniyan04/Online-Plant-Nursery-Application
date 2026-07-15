import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { getPlantById } from '../api/client.js'
import { useAuth } from '../context/AuthContext.jsx'

export default function PlantDetail() {
  const { id } = useParams()
  const { auth } = useAuth()
  const [plant, setPlant] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    setError('')

    getPlantById(id)
      .then((data) => {
        if (!cancelled) setPlant(data)
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
        <p className="page-title">Loading plant…</p>
      </div>
    )
  }

  if (error) {
    return (
      <div className="state-block">
        <p className="page-title">Couldn't find that plant</p>
        <p>{error}</p>
        <Link className="btn btn-outline mt-24" to="/plants">
          Back to catalog
        </Link>
      </div>
    )
  }

  return (
    <div>
      <Link to="/plants" className="btn-ghost" style={{ marginBottom: 24, display: 'inline-block' }}>
        ← Back to catalog
      </Link>

      {plant.imageUrl ? (
        <img src={plant.imageUrl} alt={plant.commonName} className="detail-hero" />
      ) : (
        <div className="detail-hero-placeholder">🌿</div>
      )}

      <div className="detail-card">
        <div className="plant-tag-type">{plant.typeOfPlant || 'Plant'}</div>
        <h1>{plant.commonName}</h1>
        <p className="detail-desc">{plant.plantDescription}</p>

        <div className="detail-specs">
          <div>
            <div className="spec-label">Height</div>
            <div className="spec-value">{plant.plantHeight} cm</div>
          </div>
          <div>
            <div className="spec-label">Spread</div>
            <div className="spec-value">{plant.plantSpread}</div>
          </div>
          <div>
            <div className="spec-label">Bloom time</div>
            <div className="spec-value">{plant.bloomTime}</div>
          </div>
          <div>
            <div className="spec-label">Difficulty</div>
            <div className="spec-value">{plant.difficultyLevel}</div>
          </div>
          <div>
            <div className="spec-label">Ideal temperature</div>
            <div className="spec-value">{plant.temparature}</div>
          </div>
          <div>
            <div className="spec-label">Medicinal / culinary use</div>
            <div className="spec-value">{plant.medicinalOrCulinaryUse || '—'}</div>
          </div>
        </div>

        <div className="detail-price-row">
          <div>
            <div className="spec-label">Price</div>
            <div className="detail-price">₹{plant.plantCost.toFixed(2)}</div>
          </div>
          <div style={{ textAlign: 'right' }}>
            <div className="spec-label">Stock</div>
            <div className="spec-value">
              {plant.plantsStock > 0 ? `${plant.plantsStock} available` : 'Out of stock'}
            </div>
          </div>
        </div>

        {plant.plantsStock > 0 && (
          <div className="detail-actions">
            {auth?.role === 'customer' ? (
              <Link to={`/plants/${plant.plantId}/order`} className="btn btn-primary">
                Order plant
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
