import { Link } from 'react-router-dom'

export default function PlanterTag({ planter, index = 0 }) {
  const lowStock = planter.planterStock > 0 && planter.planterStock <= 5
  const outOfStock = planter.planterStock === 0
  const availabilityLabel = outOfStock ? 'Unavailable' : lowStock ? 'Limited' : 'Available'

  return (
    <Link
      to={`/planters/${planter.planterId}`}
      className="product-card product-card-premium"
      style={{ animationDelay: `${index * 55}ms` }}
    >
      <div className="product-card-wrap">
        {planter.imageUrl ? (
          <img
            src={planter.imageUrl}
            alt={`${planter.planterShape} planter`}
            className="product-card-img"
            onError={(e) => {
              e.target.style.display = 'none'
              e.target.nextSibling.style.display = 'flex'
            }}
          />
        ) : null}
        <div
          className="product-card-img-placeholder"
          style={{ display: planter.imageUrl ? 'none' : 'flex' }}
        >
          <span>Planter</span>
        </div>
        <div className="product-card-overlay">
          <span className="product-card-badge product-card-badge-type">{planter.planterShape || 'Planter'}</span>
          <span className={`product-card-badge ${outOfStock ? 'danger' : 'success'}`}>{availabilityLabel}</span>
        </div>
        {outOfStock && <span className="out-of-stock-overlay">Out of stock</span>}
      </div>

      <div className="product-card-body">
        <div className="product-card-meta-row">
          <div className="product-card-type">{planter.planterShape || 'Planter'}</div>
          <span className={`product-card-stock ${lowStock ? 'low' : ''}`}>
            {outOfStock ? 'Out of stock' : lowStock ? `Only ${planter.planterStock} left` : `${planter.planterStock} in stock`}
          </span>
        </div>
        <h3 className="product-card-name">{planter.planterCapacity}L Planter</h3>
        <p className="product-card-desc">
          {planter.planterheight}cm tall · {planter.drainageHoles} drainage holes
        </p>
        <div className="product-card-footer">
          <div className="product-price-stack">
            <span className="product-card-price">Rs. {Number(planter.planterCost).toFixed(2)}</span>
            <span className="product-card-subprice">Premium pot finish</span>
          </div>
          <span className="product-card-cta">View Details</span>
        </div>
      </div>
    </Link>
  )
}
