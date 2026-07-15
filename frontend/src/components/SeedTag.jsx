import { Link } from 'react-router-dom'

export default function SeedTag({ seed }) {
  const lowStock = seed.seedsStock > 0 && seed.seedsStock <= 5
  const outOfStock = seed.seedsStock === 0

  return (
    <Link to={`/seeds/${seed.seedId}`} className="product-card">
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
          <span>🌱</span>
        </div>
        {outOfStock && <span className="out-of-stock-overlay">Out of stock</span>}
      </div>

      <div className="product-card-body">
        <div className="product-card-type">{seed.typeOfSeeds || 'Seed'}</div>
        <h3 className="product-card-name">{seed.commonName}</h3>
        {seed.seedsDescription && (
          <p className="product-card-desc">{seed.seedsDescription}</p>
        )}
        <div className="product-card-footer">
          <span className="product-card-price">₹{seed.seedsCost.toFixed(2)}</span>
          <span className={`product-card-stock ${lowStock ? 'low' : ''}`}>
            {outOfStock ? 'Out of stock' : lowStock ? `Only ${seed.seedsStock} left` : `${seed.seedsStock} in stock`}
          </span>
        </div>
      </div>
    </Link>
  )
}
