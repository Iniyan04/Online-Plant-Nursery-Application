import { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { getDashboard } from '../../api/client.js'
import { TableSkeleton } from '../../components/LoadingBlock.jsx'
import { useAuth } from '../../context/AuthContext.jsx'

const DASHBOARD_CARDS = [
  ['Total Customers', 'totalCustomers', 'Customers'],
  ['Total Plants', 'totalPlants', 'Plants'],
  ['Total Seeds', 'totalSeeds', 'Seeds'],
  ['Total Planters', 'totalPlanters', 'Planters'],
  ['Total Orders', 'totalOrders', 'Orders'],
  ['Active Orders', 'activeOrders', 'Active'],
  ['Cancelled Orders', 'cancelledOrders', 'Cancelled']
]

const QUICK_ACTIONS = [
  { title: 'Add Plant', path: '/admin/plants', icon: 'P', copy: 'Create a new plant entry for the nursery catalog.' },
  { title: 'Add Seed', path: '/admin/seeds', icon: 'S', copy: 'Add seed packets and keep seasonal inventory updated.' },
  { title: 'Add Planter', path: '/admin/planters', icon: 'T', copy: 'List new planters, capacities, and stock levels.' },
  { title: 'View Customers', path: '/admin/customers', icon: 'C', copy: 'Review customer records and recent registrations.' }
]

function DashboardMetric({ label, value, icon }) {
  return (
    <div className="stat-card stat-card-premium">
      <div className="stat-icon stat-icon-premium" aria-hidden="true">{icon.slice(0, 1)}</div>
      <div className="spec-label">{label}</div>
      <div className="stat-value stat-value-animated">{value ?? 0}</div>
    </div>
  )
}

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

  const summary = useMemo(() => {
    const totalOrders = dashboard?.totalOrders ?? 0
    const activeOrders = dashboard?.activeOrders ?? 0
    const cancelledOrders = dashboard?.cancelledOrders ?? 0
    const totalInventory = (dashboard?.totalPlants ?? 0) + (dashboard?.totalSeeds ?? 0) + (dashboard?.totalPlanters ?? 0)

    return {
      totalOrders,
      activeOrders,
      cancelledOrders,
      activePercent: totalOrders ? Math.round((activeOrders / totalOrders) * 100) : 0,
      cancelledPercent: totalOrders ? Math.round((cancelledOrders / totalOrders) * 100) : 0,
      inventoryBars: [
        {
          label: 'Plants',
          value: dashboard?.totalPlants ?? 0,
          percent: totalInventory ? Math.round(((dashboard?.totalPlants ?? 0) / totalInventory) * 100) : 0
        },
        {
          label: 'Seeds',
          value: dashboard?.totalSeeds ?? 0,
          percent: totalInventory ? Math.round(((dashboard?.totalSeeds ?? 0) / totalInventory) * 100) : 0
        },
        {
          label: 'Planters',
          value: dashboard?.totalPlanters ?? 0,
          percent: totalInventory ? Math.round(((dashboard?.totalPlanters ?? 0) / totalInventory) * 100) : 0
        }
      ]
    }
  }, [dashboard])

  return (
    <div className="page-fade-in admin-page-shell">
      <div className="dashboard-hero dashboard-hero-premium">
        <div>
          <p className="eyebrow">Admin overview</p>
          <h1 className="page-title">Admin dashboard</h1>
          <p className="page-sub">A polished view of orders, inventory, and customer records across Greenroot Nursery.</p>
        </div>
        <div className="dashboard-hero-panel">
          <span className="hero-stat-label">Order health</span>
          <div className="dashboard-meter">
            <div className="dashboard-meter-track">
              <span className="dashboard-meter-fill" style={{ width: `${summary.activePercent}%` }} />
            </div>
            <div className="dashboard-meter-meta">
              <span>{summary.activePercent}% active</span>
              <span>{summary.cancelledPercent}% cancelled</span>
            </div>
          </div>
        </div>
      </div>

      {error && <div className="alert alert-error--dark">{error}</div>}

      {loading && <TableSkeleton rows={3} columns={4} />}

      {!loading && dashboard && (
        <>
          <div className="dashboard-grid">
            {DASHBOARD_CARDS.map(([label, key, icon]) => (
              <DashboardMetric key={key} label={label} value={dashboard[key] ?? 0} icon={icon} />
            ))}
          </div>

          <div className="dashboard-panel dashboard-panel-quick">
            <div className="dashboard-panel-head">
              <div className="spec-label">Quick actions</div>
              <p className="dashboard-panel-copy">Jump into the most common admin tasks without changing any existing flow.</p>
            </div>
            <div className="quick-actions-grid">
              {QUICK_ACTIONS.map((action) => (
                <Link key={action.title} to={action.path} className="quick-action-card">
                  <span className="quick-action-icon" aria-hidden="true">{action.icon}</span>
                  <strong>{action.title}</strong>
                  <span>{action.copy}</span>
                </Link>
              ))}
            </div>
          </div>

          <div className="dashboard-panel-grid">
            <div className="dashboard-panel">
              <div className="dashboard-panel-head">
                <div className="spec-label">Inventory summary</div>
                <p className="dashboard-panel-copy">Visual stock distribution across the main catalog categories using the existing dashboard counts.</p>
              </div>
              <div className="inventory-bars">
                {summary.inventoryBars.map((item) => (
                  <div className="inventory-bar-row" key={item.label}>
                    <div className="inventory-bar-meta">
                      <span>{item.label}</span>
                      <strong>{item.value}</strong>
                    </div>
                    <div className="inventory-bar-track">
                      <span className="inventory-bar-fill" style={{ width: `${item.percent}%` }} />
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div className="dashboard-panel">
              <div className="dashboard-panel-head">
                <div className="spec-label">Order mix</div>
                <p className="dashboard-panel-copy">A visual breakdown of active versus cancelled orders based on the current backend summary.</p>
              </div>
              <div className="mini-chart">
                <div className="mini-bar-group">
                  <span className="mini-bar-label">Active</span>
                  <div className="mini-bar-track">
                    <span className="mini-bar-fill" style={{ width: `${summary.activePercent}%` }} />
                  </div>
                  <strong>{summary.activeOrders} orders</strong>
                </div>
                <div className="mini-bar-group">
                  <span className="mini-bar-label">Cancelled</span>
                  <div className="mini-bar-track danger">
                    <span className="mini-bar-fill danger" style={{ width: `${summary.cancelledPercent}%` }} />
                  </div>
                  <strong>{summary.cancelledOrders} orders</strong>
                </div>
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  )
}
