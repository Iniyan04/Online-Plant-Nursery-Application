import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { getPlantById } from '../api/client.js'
import EmptyState from '../components/EmptyState.jsx'
import { DetailSkeleton } from '../components/LoadingBlock.jsx'
import { useAuth } from '../context/AuthContext.jsx'
import PlantChatbot from '../components/PlantChatbot.jsx'

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
    return <DetailSkeleton />
  }

  if (error) {
    return (
      <EmptyState
        icon="🌿"
        eyebrow="Plant detail"
        title="Couldn't find that plant"
        message={error}
        action={<Link className="btn btn-outline" to="/plants">Back to catalog</Link>}
      />
    )
  }

  return (
    <div className="page-fade-in">
      <Link to="/plants" className="btn btn-outline btn-sm back-link">
        Back to catalog
      </Link>

      <div className="detail-shell">
        <div className="detail-media-card">
          {plant.imageUrl ? (
            <img src={plant.imageUrl} alt={plant.commonName} className="detail-hero" />
          ) : (
            <div className="detail-hero-placeholder">🌿</div>
          )}
          <div className="detail-summary-strip">
            <span className="detail-summary-pill">{plant.typeOfPlant || 'Plant'}</span>
            <span className={`detail-summary-pill ${plant.plantsStock > 0 ? '' : 'danger'}`}>
              {plant.plantsStock > 0 ? `${plant.plantsStock} in stock` : 'Out of stock'}
            </span>
          </div>
        </div>

        <div className="detail-card">
          <div className="plant-tag-type">{plant.typeOfPlant || 'Plant'}</div>
          <h1>{plant.commonName}</h1>
          <p className="detail-desc">{plant.plantDescription}</p>

          <div className="detail-specs">
            <div className="detail-spec-card">
              <div className="spec-label">Height</div>
              <div className="spec-value">{plant.plantHeight} cm</div>
            </div>
            <div className="detail-spec-card">
              <div className="spec-label">Spread</div>
              <div className="spec-value">{plant.plantSpread}</div>
            </div>
            <div className="detail-spec-card">
              <div className="spec-label">Bloom time</div>
              <div className="spec-value">{plant.bloomTime}</div>
            </div>
            <div className="detail-spec-card">
              <div className="spec-label">Difficulty</div>
              <div className="spec-value">{plant.difficultyLevel}</div>
            </div>
            <div className="detail-spec-card">
              <div className="spec-label">Ideal temperature</div>
              <div className="spec-value">{plant.temparature}</div>
            </div>
            <div className="detail-spec-card">
              <div className="spec-label">Medicinal / culinary use</div>
              <div className="spec-value">{plant.medicinalOrCulinaryUse || '-'}</div>
            </div>
          </div>

          <div className="detail-price-row">
            <div>
              <div className="spec-label">Price</div>
              <div className="detail-price">₹{plant.plantCost.toFixed(2)}</div>
            </div>
            <div className="detail-status-card">
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

      {/* Floating plant care chatbot — powered by Gemini */}
      <PlantChatbot plant={plant} />
    </div>
  )
}
