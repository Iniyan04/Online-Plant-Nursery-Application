import { Link } from 'react-router-dom'

export default function PlantTag({ plant, index = 0 }) {
  const lowStock = plant.plantsStock > 0 && plant.plantsStock <= 5
  const outOfStock = plant.plantsStock === 0
  const availabilityLabel = outOfStock ? 'Unavailable' : lowStock ? 'Limited' : 'Available'

  return (
    <Link
      to={`/plants/${plant.plantId}`}
      className="product-card product-card-premium"
      style={{ animationDelay: `${index * 55}ms` }}
    >
      <div className="product-card-wrap">
        {plant.imageUrl ? (
          <img
            src={plant.imageUrl}
            alt={plant.commonName}
            className="product-card-img"
            onError={(e) => {
              e.target.style.display = 'none'
              e.target.nextSibling.style.display = 'flex'
            }}
          />
        ) : null}
        <div
          className="product-card-img-placeholder"
          style={{ display: plant.imageUrl ? 'none' : 'flex' }}
        >
          <span>Plant</span>
        </div>
        <div className="product-card-overlay">
          <span className="product-card-badge product-card-badge-type">{plant.typeOfPlant || 'Plant'}</span>
          <span className={`product-card-badge ${outOfStock ? 'danger' : 'success'}`}>{availabilityLabel}</span>
        </div>
        {outOfStock && <span className="out-of-stock-overlay">Out of stock</span>}
      </div>

      <div className="product-card-body">
        <div className="product-card-meta-row">
          <div className="product-card-type">{plant.typeOfPlant || 'Plant'}</div>
          <span className={`product-card-stock ${lowStock ? 'low' : ''}`}>
            {outOfStock ? 'Out of stock' : lowStock ? `Only ${plant.plantsStock} left` : `${plant.plantsStock} in stock`}
          </span>
        </div>
        <h3 className="product-card-name">{plant.commonName}</h3>
        {plant.plantDescription && (
          <p className="product-card-desc">{plant.plantDescription}</p>
        )}
        <div className="product-card-footer">
          <div className="product-price-stack">
            <span className="product-card-price">Rs. {plant.plantCost.toFixed(2)}</span>
            <span className="product-card-subprice">Nursery tag price</span>
          </div>
          <span className="product-card-cta">View Details</span>
        </div>
      </div>
    </Link>
  )
}
