import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { getPlanterById, orderPlanter } from '../api/client.js'
import { useAuth } from '../context/AuthContext.jsx'

const PAYMENT_MODES = ['UPI', 'Card', 'Cash']

export default function OrderPlanter() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { auth } = useAuth()
  const [planter, setPlanter] = useState(null)
  const [quantity, setQuantity] = useState(1)
  const [transactionMode, setTransactionMode] = useState('UPI')
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(null)

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    setError('')

    getPlanterById(id)
      .then((data) => {
        if (!cancelled) setPlanter(data)
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
  }, [id])

  const total = planter ? planter.planterCost * quantity : 0

  async function handleSubmit(e) {
    e.preventDefault()
    setSubmitting(true)
    setError('')
    try {
      const order = await orderPlanter(
        auth.customer.customerId,
        planter.planterId,
        quantity,
        transactionMode
      )
      setSuccess(order)
    } catch (err) {
      setError(err.message)
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) {
    return (
      <div className="state-block">
        <p className="page-title">Loading checkout...</p>
      </div>
    )
  }

  if (error && !planter) {
    return (
      <div className="state-block">
        <p className="page-title">Could not load planter</p>
        <p>{error}</p>
        <Link className="btn btn-outline mt-24" to="/planters">
          Back to catalog
        </Link>
      </div>
    )
  }

  if (success) {
    return (
      <div className="form-card checkout-card">
        <p className="eyebrow">Order placed</p>
        <h1 className="page-title">Thank you!</h1>
        <p className="page-sub">
          Your planter order <strong>#{success.bookingOrderId}</strong> has been confirmed.
          Total: Rs. {success.totalCost.toFixed(2)} via {success.transactionMode}.
        </p>
        <div className="checkout-actions">
          <Link to="/orders" className="btn btn-outline">View orders</Link>
          <button className="btn btn-primary" onClick={() => navigate('/planters')}>
            Continue shopping
          </button>
        </div>
      </div>
    )
  }

  return (
    <div>
      <Link to={`/planters/${id}`} className="btn-ghost" style={{ marginBottom: 24, display: 'inline-block' }}>
        Back to planter
      </Link>

      <div className="form-card checkout-card">
        <p className="eyebrow">Checkout</p>
        <h1 className="page-title">Order {planter.planterShape} planter</h1>
        <p className="page-sub">
          Rs. {planter.planterCost.toFixed(2)} each - {planter.planterStock} in stock
        </p>

        {error && <div className="alert alert-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="field">
            <label htmlFor="quantity">Quantity</label>
            <input
              id="quantity"
              type="number"
              min="1"
              max={planter.planterStock}
              value={quantity}
              onChange={(e) => setQuantity(Math.min(planter.planterStock, Math.max(1, Number(e.target.value) || 1)))}
              required
            />
          </div>

          <div className="field">
            <label htmlFor="transactionMode">Payment method</label>
            <select
              id="transactionMode"
              value={transactionMode}
              onChange={(e) => setTransactionMode(e.target.value)}
              required
            >
              {PAYMENT_MODES.map((mode) => (
                <option key={mode} value={mode}>{mode}</option>
              ))}
            </select>
          </div>

          <div className="checkout-total">
            <span>Total</span>
            <span className="checkout-total-amount">Rs. {total.toFixed(2)}</span>
          </div>

          <button
            type="submit"
            className="btn btn-primary btn-block"
            disabled={submitting || quantity > planter.planterStock}
          >
            {submitting ? 'Placing order...' : 'Place order'}
          </button>
        </form>
      </div>
    </div>
  )
}
