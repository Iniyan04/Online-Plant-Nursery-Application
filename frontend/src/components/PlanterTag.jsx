import { Link } from 'react-router-dom'

export default function PlanterTag({ planter }) {
  const lowStock = planter.planterStock > 0 && planter.planterStock <= 5
  const outOfStock = planter.planterStock === 0

  return (
    <Link to={`/planters/${planter.planterId}`} className="product-card">
      <div className="product-card-wrap">
        {planter.imageUrl ? (
          <img
            src={planter.imageUrl}
            alt={`${planter.planterShape} Planter`}
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
          <span>🪴</span>
        </div>
        {outOfStock && <span className="out-of-stock-overlay">Out of stock</span>}
      </div>

      <div className="product-card-body">
        <div className="product-card-type">{planter.planterShape || 'Planter'}</div>
        <h3 className="product-card-name">{planter.planterCapacity}L Planter</h3>
        <p className="product-card-desc">
          {planter.planterheight}cm tall · {planter.drainageHoles} drainage holes
        </p>
        <div className="product-card-footer">
          <span className="product-card-price">₹{planter.planterCost}</span>
          <span className={`product-card-stock ${lowStock ? 'low' : ''}`}>
            {outOfStock ? 'Out of stock' : lowStock ? `Only ${planter.planterStock} left` : `${planter.planterStock} in stock`}
          </span>
        </div>
      </div>
    </Link>
  )
}
