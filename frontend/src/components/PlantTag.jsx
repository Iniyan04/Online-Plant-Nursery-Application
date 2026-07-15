import { Link } from 'react-router-dom'

export default function PlantTag({ plant }) {
  const lowStock = plant.plantsStock > 0 && plant.plantsStock <= 5
  const outOfStock = plant.plantsStock === 0

  return (
    <Link to={`/plants/${plant.plantId}`} className="product-card">
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
          <span>🌿</span>
        </div>
        {outOfStock && <span className="out-of-stock-overlay">Out of stock</span>}
      </div>

      <div className="product-card-body">
        <div className="product-card-type">{plant.typeOfPlant || 'Plant'}</div>
        <h3 className="product-card-name">{plant.commonName}</h3>
        {plant.plantDescription && (
          <p className="product-card-desc">{plant.plantDescription}</p>
        )}
        <div className="product-card-footer">
          <span className="product-card-price">₹{plant.plantCost.toFixed(2)}</span>
          <span className={`product-card-stock ${lowStock ? 'low' : ''}`}>
            {outOfStock ? 'Out of stock' : lowStock ? `Only ${plant.plantsStock} left` : `${plant.plantsStock} in stock`}
          </span>
        </div>
      </div>
    </Link>
  )
}
