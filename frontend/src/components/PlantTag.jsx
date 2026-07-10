import { Link } from 'react-router-dom'

export default function PlantTag({ plant }) {
  const lowStock = plant.plantsStock <= 5

  return (
    <Link to={`/plants/${plant.plantId}`} className="plant-tag">
      <div className="plant-tag-type">{plant.typeOfPlant || 'Plant'}</div>
      <h3 className="plant-tag-name">{plant.commonName}</h3>
      <div className="plant-tag-meta">
        <span className="plant-tag-cost">₹{plant.plantCost.toFixed(2)}</span>
        <span className={`plant-tag-stock ${lowStock ? 'low' : ''}`}>
          {plant.plantsStock > 0 ? `${plant.plantsStock} in stock` : 'Out of stock'}
        </span>
      </div>
    </Link>
  )
}
