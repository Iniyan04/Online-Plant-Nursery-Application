import { Link } from 'react-router-dom'

export default function PlanterTag({ planter }) {
  const lowStock = planter.planterStock <= 5

  return (
    <Link to={`/planters/${planter.planterId}`} className="plant-tag">
      <div className="plant-tag-type">{planter.planterShape || 'Planter'}</div>
      <h3 className="plant-tag-name">{planter.planterCapacity}L capacity</h3>
      <div className="plant-tag-meta">
        <span className="plant-tag-cost">₹{planter.planterCost}</span>
        <span className={`plant-tag-stock ${lowStock ? 'low' : ''}`}>
          {planter.planterStock > 0 ? `${planter.planterStock} in stock` : 'Out of stock'}
        </span>
      </div>
    </Link>
  )
}
