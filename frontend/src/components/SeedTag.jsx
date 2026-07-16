import { Link } from 'react-router-dom'

export default function SeedTag({ seed, index = 0 }) {
  const lowStock = seed.seedsStock > 0 && seed.seedsStock <= 5
  const outOfStock = seed.seedsStock === 0
  const availabilityLabel = outOfStock ? 'Unavailable' : lowStock ? 'Limited' : 'Available'

  return (
    <Link
      to={`/seeds/${seed.seedId}`}
      className="product-card product-card-premium"
      style={{ animationDelay: `${index * 55}ms` }}
    >
      <div className="product-card-wrap">
        {seed.imageUrl ? (
          <img
            src={seed.imageUrl}
            alt={seed.commonName}
            className="product-card-img"
            onError={(e) => {
              e.target.style.display = 'none'
              e.target.nextSibling.style.display = 'flex'
            }}
          />
        ) : null}
        <div
          className="product-card-img-placeholder"
          style={{ display: seed.imageUrl ? 'none' : 'flex' }}
        >
          <span>Seed</span>
        </div>
        <div className="product-card-overlay">
          <span className="product-card-badge product-card-badge-type">{seed.typeOfSeeds || 'Seed'}</span>
          <span className={`product-card-badge ${outOfStock ? 'danger' : 'success'}`}>{availabilityLabel}</span>
        </div>
        {outOfStock && <span className="out-of-stock-overlay">Out of stock</span>}
      </div>

      <div className="product-card-body">
        <div className="product-card-meta-row">
          <div className="product-card-type">{seed.typeOfSeeds || 'Seed'}</div>
          <span className={`product-card-stock ${lowStock ? 'low' : ''}`}>
            {outOfStock ? 'Out of stock' : lowStock ? `Only ${seed.seedsStock} left` : `${seed.seedsStock} in stock`}
          </span>
        </div>
        <h3 className="product-card-name">{seed.commonName}</h3>
        {seed.seedsDescription && (
          <p className="product-card-desc">{seed.seedsDescription}</p>
        )}
        <div className="product-card-footer">
          <div className="product-price-stack">
            <span className="product-card-price">Rs. {seed.seedsCost.toFixed(2)}</span>
            <span className="product-card-subprice">Per packet</span>
          </div>
          <span className="product-card-cta">View Details</span>
        </div>
      </div>
    </Link>
  )
}
