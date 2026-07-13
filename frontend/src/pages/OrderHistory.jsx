import { useEffect, useState } from 'react'
import { cancelOrder, getOrdersByCustomer } from '../api/client.js'
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

  return (
    <div>
      <h1 className="page-title">My orders</h1>
      <p className="page-sub">Track your purchases and cancel active orders when needed.</p>

      {success && <div className="alert alert-success">{success}</div>}
      {error && <div className="alert alert-error--dark">{error}</div>}

      {loading && (
        <div className="state-block">
          <p className="page-title">Loading orders...</p>
        </div>
      )}

      {!loading && !error && orders.length === 0 && (
        <div className="state-block">
          <p className="page-title">No orders yet</p>
          <p>Your plant, seed, and planter orders will appear here.</p>
        </div>
      )}

      {!loading && orders.length > 0 && (
        <div className="table-wrap">
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
                <th></th>
              </tr>
            </thead>
            <tbody>
              {orders.map((order) => {
                const cancelled = order.orderStatus?.trim().toLowerCase() === 'cancelled'
                const totalCost = Number(order.totalCost)
                return (
                  <tr key={order.bookingOrderId}>
                    <td>#{order.bookingOrderId}</td>
                    <td>{order.orderDate || '-'}</td>
                    <td>{order.itemName || '-'}</td>
                    <td>{order.itemType || '-'}</td>
                    <td className="num">{order.quantity}</td>
                    <td className="num">{Number.isFinite(totalCost) ? `Rs. ${totalCost.toFixed(2)}` : '-'}</td>
                    <td>{order.orderStatus || 'Active'}</td>
                    <td>
                      <button
                        className="btn btn-danger btn-sm"
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
