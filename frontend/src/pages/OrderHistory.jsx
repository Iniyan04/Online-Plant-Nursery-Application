import { useEffect, useMemo, useState } from 'react'
import { cancelOrder, getOrdersByCustomer } from '../api/client.js'
import EmptyState from '../components/EmptyState.jsx'
import { TableSkeleton } from '../components/LoadingBlock.jsx'
import { useAuth } from '../context/AuthContext.jsx'

export default function OrderHistory() {
  const { auth } = useAuth()
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [cancellingId, setCancellingId] = useState(null)

  function loadOrders() {
    setLoading(true)
    setError('')
    getOrdersByCustomer(auth.customer.customerId)
      .then(setOrders)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }

  useEffect(loadOrders, [auth.customer.customerId])

  async function handleCancel(order) {
    if (!window.confirm(`Cancel order #${order.bookingOrderId}?`)) return
    setCancellingId(order.bookingOrderId)
    setError('')
    setSuccess('')

    try {
      await cancelOrder(order.bookingOrderId, auth.customer.customerId)
      setSuccess(`Order #${order.bookingOrderId} cancelled successfully.`)
      loadOrders()
    } catch (err) {
      setError(err.message)
    } finally {
      setCancellingId(null)
    }
  }

  const summary = useMemo(() => {
    const active = orders.filter((order) => order.orderStatus?.trim().toLowerCase() !== 'cancelled').length
    const cancelled = orders.length - active
    const spend = orders.reduce((total, order) => total + (Number(order.totalCost) || 0), 0)
    return { active, cancelled, spend }
  }, [orders])

  return (
    <div className="page-fade-in">
      <div className="orders-hero">
        <div>
          <p className="eyebrow">Purchase history</p>
          <h1 className="page-title">My orders</h1>
          <p className="page-sub">Track your purchases, monitor order status, and cancel active orders when needed.</p>
        </div>
        <div className="order-summary-card">
          <div className="order-summary-metric">
            <span>Total orders</span>
            <strong>{loading ? '--' : orders.length}</strong>
          </div>
          <div className="order-summary-grid">
            <div>
              <span>Active</span>
              <strong>{loading ? '--' : summary.active}</strong>
            </div>
            <div>
              <span>Cancelled</span>
              <strong>{loading ? '--' : summary.cancelled}</strong>
            </div>
            <div>
              <span>Total spend</span>
              <strong>{loading ? '--' : `Rs. ${summary.spend.toFixed(2)}`}</strong>
            </div>
          </div>
        </div>
      </div>

      {success && <div className="alert alert-success">{success}</div>}
      {error && <div className="alert alert-error--dark">{error}</div>}

      {loading && <TableSkeleton rows={4} columns={8} />}

      {!loading && !error && orders.length === 0 && (
        <EmptyState
          icon="Orders"
          eyebrow="No purchases yet"
          title="No orders yet"
          message="Your plant, seed, and planter orders will appear here as soon as you place them."
        />
      )}

      {!loading && orders.length > 0 && (
        <div className="table-wrap orders-table-wrap">
          <table className="data-table">
            <thead>
              <tr>
                <th>Order</th>
                <th>Date</th>
                <th>Product</th>
                <th>Type</th>
                <th>Qty</th>
                <th>Total</th>
                <th>Status</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {orders.map((order) => {
                const cancelled = order.orderStatus?.trim().toLowerCase() === 'cancelled'
                const totalCost = Number(order.totalCost)
                return (
                  <tr key={order.bookingOrderId}>
                    <td className="num">#{order.bookingOrderId}</td>
                    <td>{order.orderDate || '-'}</td>
                    <td>{order.itemName || '-'}</td>
                    <td>
                      <span className="table-badge">{order.itemType || '-'}</span>
                    </td>
                    <td className="num">{order.quantity}</td>
                    <td className="num">{Number.isFinite(totalCost) ? `Rs. ${totalCost.toFixed(2)}` : '-'}</td>
                    <td>
                      <span className={`status-badge ${cancelled ? 'status-badge-cancelled' : 'status-badge-active'}`}>
                        {order.orderStatus || 'Active'}
                      </span>
                    </td>
                    <td>
                      <button
                        className="btn btn-danger btn-sm cancel-order-btn"
                        onClick={() => handleCancel(order)}
                        disabled={cancelled || cancellingId === order.bookingOrderId}
                      >
                        {cancellingId === order.bookingOrderId ? 'Cancelling...' : 'Cancel'}
                      </button>
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
