import { useEffect, useState } from 'react'
import { getDashboard } from '../../api/client.js'
import { useAuth } from '../../context/AuthContext.jsx'

const DASHBOARD_CARDS = [
  ['Total Customers', 'totalCustomers'],
  ['Total Plants', 'totalPlants'],
  ['Total Seeds', 'totalSeeds'],
  ['Total Planters', 'totalPlanters'],
  ['Total Orders', 'totalOrders'],
  ['Active Orders', 'activeOrders'],
  ['Cancelled Orders', 'cancelledOrders']
]

export default function AdminDashboard() {
  const { auth } = useAuth()
  const [dashboard, setDashboard] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    setError('')

    getDashboard(auth.admin)
      .then((data) => {
        if (!cancelled) setDashboard(data)
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
  }, [auth.admin.username, auth.admin.password])

  return (
    <div>
      <h1 className="page-title">Admin dashboard</h1>
      <p className="page-sub">A quick view of catalog, customer, and order activity.</p>

      {error && <div className="alert alert-error--dark">{error}</div>}

      {loading && (
        <div className="state-block">
          <p className="page-title">Loading dashboard...</p>
        </div>
      )}

      {!loading && dashboard && (
        <div className="dashboard-grid">
          {DASHBOARD_CARDS.map(([label, key]) => (
            <div className="stat-card" key={key}>
              <div className="spec-label">{label}</div>
              <div className="stat-value">{dashboard[key] ?? 0}</div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
