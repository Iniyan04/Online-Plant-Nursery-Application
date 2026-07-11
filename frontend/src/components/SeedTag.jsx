import { Link } from 'react-router-dom'

export default function SeedTag({ seed }) {
  const lowStock = seed.seedsStock <= 5

  return (
    <Link to={`/seeds/${seed.seedId}`} className="plant-tag">
      <div className="plant-tag-type">{seed.typeOfSeeds || 'Seed'}</div>
      <h3 className="plant-tag-name">{seed.commonName}</h3>
      <div className="plant-tag-meta">
        <span className="plant-tag-cost">₹{seed.seedsCost.toFixed(2)}</span>
        <span className={`plant-tag-stock ${lowStock ? 'low' : ''}`}>
          {seed.seedsStock > 0 ? `${seed.seedsStock} in stock` : 'Out of stock'}
        </span>
      </div>
    </Link>
  )
}
